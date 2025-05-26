import argparse
import torch
import torchvision.models as models
from torchvision import transforms
from PIL import Image
import json
import base64
import io
import os
import re
from flask import Flask, request, jsonify
from config.paths import get_valid_model_path

app = Flask(__name__)

# 定义模型名称常量
MODEL_ALEXNET = 'alexnet'
MODEL_VGG16 = 'vgg16'
MODEL_RESNET50 = 'resnet50'

# 全局变量（模型和标签）
model = None
labels = None
device = torch.device("cuda:0" if torch.cuda.is_available() else "cpu")


# 初始化模型
def initialize_model(model_name):
    global model
    input_size = 224  # 默认输入尺寸

    if model_name == MODEL_ALEXNET:
        model = models.alexnet(pretrained=False)
    elif model_name == MODEL_VGG16:
        model = models.vgg16(pretrained=False)
    elif model_name == MODEL_RESNET50:
        model = models.resnet50(pretrained=False)
    else:
        raise ValueError("Invalid model name")

    model = model.to(device)
    model.eval()  # 切换到评估模式
    return input_size


# 加载ImageNet标签
def load_imagenet_labels(label_path='imagenet_class_index.json'):
    with open(label_path) as f:
        labels = json.load(f)
    return {int(k): v[1] for k, v in labels.items()}


# Base64解码为PIL图像
def decode_base64_to_image(base64_str):
    image_data = base64.b64decode(base64_str)
    image = Image.open(io.BytesIO(image_data))
    return image


# 图像预处理
def preprocess_image(image, input_size):
    transform = transforms.Compose([
        transforms.Resize(256),
        transforms.CenterCrop(input_size),
        transforms.ToTensor(),
        transforms.Normalize(mean=[0.485, 0.456, 0.406],
                             std=[0.229, 0.224, 0.225]),
    ])
    image = transform(image).unsqueeze(0)  # 添加batch维度
    return image


def detect_model_type(filename):
    """
    更健壮的模型类型检测
    :param filename: 模型文件名
    :return: 模型类型字符串
    """
    filename = filename.lower()

    # 使用正则表达式进行更精确的匹配
    patterns = [
        (r'vgg16', 'vgg16'),
        (r'resnet50', 'resnet50'),
        (r'mobilenet-v3', 'mobilenet_v3_small')
    ]

    for pattern, model_type in patterns:
        if re.search(pattern, filename):
            return model_type

    # 如果没有匹配到已知模式
    supported = ', '.join([t for _, t in patterns])

    raise ValueError(f"Unsupported model type. Supported types: {supported}")




# 预测函数
def predict(image_tensor,model_path, topk=5):
    model_type = detect_model_type(model_path)
    if model_type == "vgg16":
        model = models.vgg16(pretrained=False)
    elif model_type == "resnet50":
        model = models.resnet50(pretrained=False)
    elif model_type == "mobilenet_v3_small":
        model = models.mobilenet_v3_small(pretrained=False)
    else:
        raise ValueError("该格式的模型文件暂不支持！")



    model_pa = get_valid_model_path(model_path)
    if not os.path.exists(model_pa):
        raise FileNotFoundError(f"模型文件 {model_pa} 不存在！")

    pretrained_dict = torch.load(model_pa)

    # 应用到模型
    model.load_state_dict(pretrained_dict)
    model.to(device)
    model.eval()  # 切换到评估模式

    labels = load_imagenet_labels()

    with torch.no_grad():
        image_tensor = image_tensor.to(device)
        outputs = model(image_tensor)
        probabilities = torch.nn.functional.softmax(outputs[0], dim=0)
        top_probs, top_indices = torch.topk(probabilities, topk)

    results = []
    if labels is None:
        raise ValueError("标签未初始化！请检查 load_imagenet_labels() 函数")
    for i in range(topk):
        idx = top_indices[i].item()
        results.append({
            "labels": labels[idx],
            "confidence": round(top_probs[i].item() * 100, 2)
        })

    print(results)
    return results





# 主函数：启动Flask应用
if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('--model', type=str, required=True,
                        choices=[MODEL_ALEXNET, MODEL_VGG16, MODEL_RESNET50],
                        help='Model name (alexnet, vgg16, or resnet50)')
    args = parser.parse_args()

    # 初始化模型和标签
    initialize_model(args.model)
    labels = load_imagenet_labels()
    print(f"Model {args.model} loaded, running on {device}")

    # 启动Flask
    app.run(host='0.0.0.0', port=5000)