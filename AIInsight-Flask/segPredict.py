# predict.py
import numpy as np
import torch
from torchvision import transforms
from PIL import Image
import matplotlib.pyplot as plt
from train_fcn import build_fcn
import io
import base64
import os
from config.paths import get_valid_model_path
os.environ['KMP_DUPLICATE_LIB_OK'] = 'True'



def predict(
        model: torch.nn.Module,
        image_path: str,
        transform: transforms.Compose,
        device: str = "cuda" if torch.cuda.is_available() else "cpu",
        save_path: str = None
):
    """预测单张图像并可视化结果"""
    # 加载图像
    image = Image.open(image_path).convert("RGB")
    image_tensor = transform(image).unsqueeze(0).to(device)

    # 推理
    model.eval()
    with torch.no_grad():
        output = model(image_tensor)

        # 如果模型输出是字典，提取键为 "out" 的张量
        if isinstance(output, dict):
            output = output["out"]

        # 获取预测结果
        pred = torch.argmax(output, dim=1).squeeze().cpu().numpy()

    # 可视化
    plt.figure(figsize=(10, 5))
    plt.subplot(1, 2, 1)
    plt.imshow(image)
    plt.title("Input Image")
    plt.subplot(1, 2, 2)
    plt.imshow(pred, cmap="gray")
    plt.title("Predicted Mask")

    if save_path:
        plt.savefig(save_path)  # 保存预测结果
    plt.show()

    return pred


def mock_image_segmentation(image_base64: str, model_path):
    # 初始化
    device = torch.device("cuda" if torch.cuda.is_available() else "cpu")

    model = build_fcn(num_classes=2).to(device)
    # model_path = "../models/resnet50-0676ba61.pth"

    model_path = get_valid_model_path(model_path)
    model.load_state_dict(torch.load(model_path))  # 加载训练好的权重

    # 数据预处理（需与训练时一致）
    transform = transforms.Compose([
        transforms.Resize((256, 256)),
        transforms.ToTensor(),
    ])
    # 去除可能存在的前缀
    if image_base64.startswith('data:image'):
        # 找到逗号的位置
        comma_index = image_base64.find(',')
        if comma_index != -1:
            image_base64 = image_base64[comma_index + 1:]

    # 将 Base64 编码的图像转换为 PIL 图像
    image_data = base64.b64decode(image_base64)
    original_image = Image.open(io.BytesIO(image_data)).convert("RGB")
    image = original_image.resize((256, 256))
    image_tensor = transform(image).unsqueeze(0).to(device)

    # 推理
    model.eval()
    with torch.no_grad():
        output = model(image_tensor)

        # 如果模型输出是字典，提取键为 "out" 的张量
        if isinstance(output, dict):
            output = output["out"]

        # 获取预测结果
        pred = torch.argmax(output, dim=1).squeeze().cpu().numpy()


    # 将预测结果转换为与原始图像相同大小的掩码图像
    mask = Image.fromarray((pred * 255).astype(np.uint8)).resize(original_image.size)

    # 为掩码添加颜色
    colored_mask = Image.new('RGBA', original_image.size, (0, 0, 0, 0))
    colored_mask.paste((255, 0, 0, 128), mask=mask)

    # 将掩码与原始图像叠加
    overlay = Image.alpha_composite(original_image.convert('RGBA'), colored_mask)

    # 将掩码图像转换为 Base64 编码
    buffer_mask = io.BytesIO()
    mask.save(buffer_mask, format='png')
    buffer_mask.seek(0)
    mask_image_base64 = base64.b64encode(buffer_mask.read()).decode('utf-8')

    # 将叠加图像转换为 Base64 编码
    buffer_overlay = io.BytesIO()
    overlay.save(buffer_overlay, format='png')
    buffer_overlay.seek(0)
    overlay_image_base64 = base64.b64encode(buffer_overlay.read()).decode('utf-8')

    return {
        "mask": mask_image_base64,
        "overlay": overlay_image_base64
    }

#
if __name__ == "__main__":

    # 示例用法

    # 初始化
    device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
    model = build_fcn(num_classes=2).to(device)
    model.load_state_dict(torch.load("best_fcn_river.pth"))  # 加载训练好的权重

    # 数据预处理（需与训练时一致）
    transform = transforms.Compose([
        transforms.Resize((256, 256)),
        transforms.ToTensor(),
    ])

    # 预测单张图像
    # predict(
    #     model=model,
    #     image_path="./river_data/test/src/TT3.png",
    #     transform=transform,
    #     save_path="prediction_result3.png"
    # )


    with open("./river_data/test/src/TT3.png","rb") as image_file:
        image_base64 = base64.b64encode(image_file.read()).decode('utf-8')

    result = mock_image_segmentation(image_base64,None)
    print(result)

    # 解码 Base64 图像
    mask_image_data = base64.b64decode(result["mask"])
    overlay_image_data = base64.b64decode(result["overlay"])

    mask_image = Image.open(io.BytesIO(mask_image_data))
    overlay_image = Image.open(io.BytesIO(overlay_image_data))

    # 可视化结果
    plt.figure(figsize=(15, 5))

    # 显示原始图像
    original_image = Image.open("./river_data/test/src/TT3.png")
    plt.subplot(1, 3, 1)
    plt.imshow(original_image)
    plt.title("Original Image")
    plt.axis('off')

    # 显示掩码图像
    plt.subplot(1, 3, 2)
    plt.imshow(mask_image, cmap="gray")
    plt.title("Mask Image")
    plt.axis('off')

    # 显示叠加图像
    plt.subplot(1, 3, 3)
    plt.imshow(overlay_image)
    plt.title("Overlay Image")
    plt.axis('off')

    plt.show()