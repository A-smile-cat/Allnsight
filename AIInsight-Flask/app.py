import os

from flask import Flask, request, jsonify
import base64
from segPredict import mock_image_segmentation
from clsPredict import decode_base64_to_image,preprocess_image,predict
from flask_sqlalchemy import SQLAlchemy
import pymysql
app = Flask(__name__)

connection = pymysql.connect(
    host='localhost',  # 数据库主机名
    port=3306,  # 数据库端口号，默认为3306
    user='root',  # 数据库用户名
    passwd='root',  # 数据库密码
    db='demo',  # 数据库名称
    charset='utf8'  # 字符编码
)

cursor = connection.cursor()


@app.route('/segProcess', methods=['POST'])
def segProcess():
    data = request.get_json()
    if not data or 'image' not in data:
        return jsonify({"error": "No base64 image provided"}), 400

    image_base64 = data['image']
    model_id = data['model']
    sql = "select storage_path from models where model_id = %s"
    args = (model_id,)
    cursor.execute(sql,args)
    res = cursor.fetchone()
    model_path = os.path.basename(res[0])

    # 直接从请求中获取Base64图像，不存储任何文件
    result = mock_image_segmentation(image_base64, model_path)

    # 返回结果但不存储
    return jsonify({
        "maskImage": result["mask"],
        "overlayImage": result["overlay"]
    })


# Flask路由：预测接口
@app.route('/clsProcess', methods=['POST'])
def predict_api():
    data = request.get_json()
    if not data or 'image' not in data:
        return jsonify({"error": "No base64 image provided"}), 400

    image_base64 = data['image']
    model_id = data['model']
    sql = "select storage_path from models where model_id = %s"
    args = (model_id,)
    cursor.execute(sql,args)
    res = cursor.fetchone()
    model_path = os.path.basename(res[0])
    print(model_path)
    # 去除可能存在的前缀
    if image_base64.startswith('data:image'):
        # 找到逗号的位置
        comma_index = image_base64.find(',')
        if comma_index != -1:
            image_base64 = image_base64[comma_index + 1:]

    try:
        # Base64解码和预处理
        image = decode_base64_to_image(image_base64)
        if image is None:
            return jsonify({"error": "Invalid image data"}), 400

        image_tensor = preprocess_image(image, input_size=224)  # 所有模型输入尺寸相同


        #预测并返回结果
        predictions = predict(image_tensor,model_path,topk=1)
        return jsonify({
            "labels": predictions[0]["labels"],
            "confidence": predictions[0]["confidence"]
        })
        # 预测并返回所有结果
        # predictions = predict(image_tensor)
        # return jsonify({
        #     "predictions": predictions  # 返回完整预测列表
        # })
    except Exception as e:
        return jsonify({"error": str(e)}), 500




if __name__ == "__main__":
    app.run(port=5050)

#
# from flask import Flask
#
# app = Flask(__name__)
#
# @app.route('/')
# def hello_world():
#     return 'Hello, World!'
#
# if __name__ == '__main__':
#     app.run(debug=True)