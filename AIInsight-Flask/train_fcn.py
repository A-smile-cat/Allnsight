# train_fcn.py
import torch
import torch.nn as nn
import torch.optim as optim
from torch.utils.data import DataLoader
from tqdm import tqdm
from torchvision.models.segmentation import fcn_resnet50
import matplotlib.pyplot as plt
import numpy as np


def build_fcn(num_classes=2, pretrained=True):
    """构建 FCN-ResNet50 模型"""
    weights = "DEFAULT" if pretrained else None
    model = fcn_resnet50(weights=weights)

    # 修改输出层并初始化
    model.classifier[4] = nn.Conv2d(512, num_classes, kernel_size=1)
    model.aux_classifier[4] = nn.Conv2d(256, num_classes, kernel_size=1)

    # 初始化新层
    nn.init.kaiming_normal_(model.classifier[4].weight, mode='fan_out', nonlinearity='relu')
    nn.init.kaiming_normal_(model.aux_classifier[4].weight, mode='fan_out', nonlinearity='relu')

    return model


def visualize_sample(image, mask, pred=None):
    """可视化样本和预测结果"""
    plt.figure(figsize=(15, 5))

    plt.subplot(1, 3 if pred is not None else 2, 1)
    plt.imshow(image.permute(1, 2, 0))
    plt.title("Input Image")

    plt.subplot(1, 3 if pred is not None else 2, 2)
    plt.imshow(mask.squeeze(), cmap="gray", vmin=0, vmax=1)
    plt.title("Ground Truth")

    if pred is not None:
        plt.subplot(1, 3, 3)
        plt.imshow(pred.squeeze(), cmap="gray", vmin=0, vmax=1)
        plt.title("Prediction")

    plt.show()


def train_fcn(
        train_loader: DataLoader,
        test_loader: DataLoader,
        num_classes=2,
        epochs=10,
        lr=0.001,
        device="cuda" if torch.cuda.is_available() else "cpu",
        save_path="best_fcn_model.pth"
):
    """训练 FCN 模型并返回最佳模型"""
    print(f"Using device: {device}")

    # 初始化模型
    model = build_fcn(num_classes=num_classes).to(device)

    # 处理类别不平衡（根据你的数据调整权重）
    class_weights = torch.tensor([1.0, 3.0], device=device)  # 假设类别1（河流）较少
    criterion = nn.CrossEntropyLoss(weight=class_weights)

    optimizer = optim.Adam(model.parameters(), lr=lr)
    scheduler = optim.lr_scheduler.ReduceLROnPlateau(optimizer, 'min', patience=2)

    best_loss = float("inf")
    train_losses = []
    test_losses = []

    # 先验证数据是否正确
    sample_images, sample_masks = next(iter(train_loader))
    print("\nData verification:")
    print(f"Image shape: {sample_images.shape}, Mask shape: {sample_masks.shape}")
    print(f"Unique mask values: {torch.unique(sample_masks)}")
    visualize_sample(sample_images[0], sample_masks[0])

    for epoch in range(epochs):
        model.train()
        train_loss = 0.0
        correct_pixels = 0
        total_pixels = 0

        for images, masks in tqdm(train_loader, desc=f"Epoch {epoch + 1}/{epochs}"):
            images = images.to(device)
            masks = masks.squeeze(1).long().to(device)

            optimizer.zero_grad()
            outputs = model(images)["out"]
            loss = criterion(outputs, masks)
            loss.backward()
            optimizer.step()

            train_loss += loss.item()

            # 计算准确率
            preds = outputs.argmax(dim=1)
            correct_pixels += (preds == masks).sum().item()
            total_pixels += masks.numel()

        avg_train_loss = train_loss / len(train_loader)
        train_acc = correct_pixels / total_pixels
        train_losses.append(avg_train_loss)

        # 验证阶段
        model.eval()
        test_loss = 0.0
        test_correct = 0
        test_total = 0

        with torch.no_grad():
            for images, masks in test_loader:
                images = images.to(device)
                masks = masks.squeeze(1).long().to(device)

                outputs = model(images)["out"]
                loss = criterion(outputs, masks)
                test_loss += loss.item()

                preds = outputs.argmax(dim=1)
                test_correct += (preds == masks).sum().item()
                test_total += masks.numel()

                # 可视化第一个测试样本
                if epoch == 0 and test_loss == loss.item():
                    visualize_sample(
                        images[0].cpu(),
                        masks[0].cpu(),
                        preds[0].cpu()
                    )

        avg_test_loss = test_loss / len(test_loader)
        test_acc = test_correct / test_total
        test_losses.append(avg_test_loss)

        scheduler.step(avg_test_loss)

        print(f"\nEpoch {epoch + 1}:")
        print(f"Train Loss: {avg_train_loss:.4f} | Train Acc: {train_acc:.2%}")
        print(f"Test Loss: {avg_test_loss:.4f} | Test Acc: {test_acc:.2%}")
        print(f"Class Distribution: {torch.unique(outputs.argmax(dim=1), return_counts=True)}")

        # 保存最佳模型
        if avg_test_loss < best_loss:
            best_loss = avg_test_loss
            torch.save(model.state_dict(), save_path)
            print(f"Saved new best model with test loss: {best_loss:.4f}")
        # 每个epoch结束后清理
        torch.cuda.empty_cache()
    # 绘制损失曲线
    plt.figure(figsize=(10, 5))
    plt.plot(train_losses, label="Train Loss")
    plt.plot(test_losses, label="Test Loss")
    plt.xlabel("Epoch")
    plt.ylabel("Loss")
    plt.legend()
    plt.title("Training and Validation Loss")
    plt.show()

    # 加载最佳模型
    best_model = build_fcn(num_classes=num_classes).to(device)
    best_model.load_state_dict(torch.load(save_path))

    return best_model