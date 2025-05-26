# config/paths.py
import os
from pathlib import Path

# 获取项目根目录（向上两级：Flask -> project-root）
BASE_DIR = Path(__file__).resolve().parent.parent
PROJECT_ROOT = BASE_DIR.parent  # 向上到AIInsight根目录

SHARED_DATA_DIR = PROJECT_ROOT / "SharedData"
# 子目录路径
MODEL_DIR = SHARED_DATA_DIR / "ModelFiles"
DATASET_DIR = SHARED_DATA_DIR / "Datasets"
# 自动创建目录（添加详细错误处理）
try:
    os.makedirs(MODEL_DIR, exist_ok=True)
    os.makedirs(DATASET_DIR, exist_ok=True)
except Exception as e:
    raise RuntimeError(f"无法创建共享目录: {e}")

# 添加路径验证方法
def validate_path(path: Path) -> Path:
    """验证路径是否存在"""
    if not path.exists():
        raise FileNotFoundError(f"路径不存在: {path}")
    return path

# 带验证的路径获取方法
def get_valid_model_path(model_name: str) -> Path:
    """获取已验证的模型路径"""
    path = MODEL_DIR / model_name
    return validate_path(path)

def get_valid_dataset_path(dataset_name: str) -> Path:
    """获取已验证的数据集路径"""
    path = DATASET_DIR / dataset_name
    return validate_path(path)

# 示例路径生成函数
def get_model_path(model_name: str) -> Path:
    """获取模型完整路径"""
    return MODEL_DIR / model_name

def get_dataset_path(dataset_name: str) -> Path:
    """获取数据集完整路径"""
    return DATASET_DIR / dataset_name