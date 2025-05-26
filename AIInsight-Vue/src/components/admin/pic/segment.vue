<template>
    <div class="image-segmentation-container">
      <h1 class="title">图像分割</h1>
      <p class="subtitle">上传待分割图片，选择模型进行分割</p>
      
      <div class="control-panel">
        <el-select v-model="selectedModel" placeholder="请选择模型" class="model-select">
          <el-option
            v-for="model in modelList"
            :key="model.value"
            :label="model.label"
            :value="model.value"
          />
        </el-select>
        <el-button type="primary" @click="startSegmentation" :disabled="!selectedModel || !uploadedImage">
          开始分割
        </el-button>
      </div>
      
      <div class="image-panels">
        <!-- 待分割图片区 -->
        <div class="image-panel">
          <h3>待分割图片</h3>
          <div class="image-upload-area" @click="triggerFileInput">
            <div v-if="!uploadedImage" class="upload-placeholder">
              <el-button type="primary">选择图片</el-button>
              <p>请上传JPG/PNG格式图片</p>
              <p class="hint">尚未上传图片</p>
            </div>
            <img v-else :src="uploadedImage" alt="待分割图片" class="preview-image">
          </div>
          <input 
            type="file" 
            ref="fileInput" 
            @change="handleFileUpload" 
            accept="image/jpeg,image/png" 
            style="display: none"
          >
          <div class="batch-actions">
            <el-button type="primary" plain @click="openBatchImportDialog">
              批量导入
            </el-button>
          </div>
        </div>
        
        <!-- 分割掩码图区 -->
        <div class="image-panel">
          <h3>分割掩码图</h3>
          <div class="image-display-area">
            <div v-if="!segmentationMask" class="empty-placeholder">
              <p class="hint">尚未分割</p>
            </div>
            <img v-else :src="segmentationMask" alt="分割掩码图" class="preview-image">
          </div>
          <div class="download-action">
            <el-button 
              type="primary" 
              plain 
              @click="downloadImage(segmentationMask, 'mask')"
              :disabled="!segmentationMask"
            >
              下载掩码图
            </el-button>
          </div>
        </div>
        
        <!-- 叠加结果图区 -->
        <div class="image-panel">
          <h3>叠加结果图</h3>
          <div class="image-display-area">
            <div v-if="!overlayResult" class="empty-placeholder">
              <p class="hint">尚未分割</p>
            </div>
            <img v-else :src="overlayResult" alt="叠加结果图" class="preview-image">
          </div>
          <div class="download-action">
            <el-button 
              type="primary" 
              plain 
              @click="downloadImage(overlayResult, 'overlay')"
              :disabled="!overlayResult"
            >
              下载结果图
            </el-button>
            <el-button 
              type="success" 
              plain 
              @click="exportBatchResults"
              :disabled="!overlayResult"
              v-if="batchMode"
            >
              批量导出
            </el-button>
          </div>
        </div>
      </div>
      
      <!-- 批量导入对话框 -->
      <el-dialog v-model="batchImportDialogVisible" title="批量导入图片" width="50%">
        <el-upload
          class="batch-upload"
          drag
          multiple
          action="#"
          :auto-upload="false"
          :on-change="handleBatchUpload"
          accept="image/jpeg,image/png"
        >
          <el-icon class="el-icon--upload"><upload-filled /></el-icon>
          <div class="el-upload__text">
            拖拽文件到此处或<em>点击上传</em>
          </div>
          <template #tip>
            <div class="el-upload__tip">
              请上传JPG/PNG格式图片
            </div>
          </template>
        </el-upload>
        <template #footer>
          <el-button @click="batchImportDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmBatchImport">确认导入</el-button>
        </template>
      </el-dialog>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted,reactive, nextTick } from 'vue'
  import { ElMessage } from 'element-plus';
  import { UploadFilled } from '@element-plus/icons-vue';
  import api from '@/api/login.js'
  
  // 数据状态
  const selectedModel = ref('');
  const uploadedImage = ref(null);
  const segmentationMask = ref(null);
  const overlayResult = ref(null);
  const fileInput = ref(null);
  const batchImportDialogVisible = ref(false);
  const batchImages = ref([]);
  const batchMode = ref(false);
  const currentBatchIndex = ref(0);
  // 新增模型列表和总数
  const modelList = ref([]);
  const storedUser = localStorage.getItem('user');
  const userInfo = storedUser ? JSON.parse(storedUser) : {};
  const userId = userInfo.id; // 假设 user_id 的键名为 id
  const processing = ref(false);
  const downloadRequired = ref(false);


  onMounted(() => {
    searchModel();
  });

  // 触发文件选择
  const triggerFileInput = () => {
    fileInput.value.click();
  };
  
  // 处理单个文件上传
  const handleFileUpload = (event) => {
    const file = event.target.files[0];
    if (!file) return;
    
    if (!['image/jpeg', 'image/png'].includes(file.type)) {
      ElMessage.error('请上传JPG或PNG格式的图片');
      return;
    }
    
    const reader = new FileReader();
    reader.onload = (e) => {
      uploadedImage.value = e.target.result;
      // 重置分割结果
      segmentationMask.value = null;
      overlayResult.value = null;
      batchMode.value = false;
    };
    reader.readAsDataURL(file);
  };
  
  // 查找可用模型
  const searchModel = async() => {
    try {
      const params = {
        type: "segmentation",
        user_id: userId
      }
      
      const res = await api.searchModel(params)
      const resultArray = typeof res.result === 'string' ? JSON.parse(res.result) : res.result 
      if (res?.code === 200 && Array.isArray(resultArray)) {
        modelList.value = resultArray.map(item => ({
          value: item.model_id,
          label: item.model_name
        }))
      } else {
        // ElMessage.error('查询失败: 返回数据格式不正确')
      }
    } catch (error) {
      // console.error(error)
      // ElMessage.error('请求出错: ' + error.message)
    }
  }


  // 开始分割
  const startSegmentation = async() => {
    if (!selectedModel.value || !uploadedImage.value) {
      ElMessage.warning('请先选择模型并上传图片');
      return;
    }

    processing.value = true;
    downloadRequired.value = true;
    
    try {
        ElMessage.warning('正在进行分割，请耐心等待...');
        // 调用API时不存储结果
        const res = await api.segmentImage(uploadedImage.value,selectedModel.value);
        
        segmentationMask.value = `data:image/png;base64,${res.mask}`;
        overlayResult.value = `data:image/png;base64,${res.overlay}`;
        
        ElMessage.success('分割完成，请立即下载结果，图片不在服务器端保存！');
    } catch (error) {
        // ElMessage.error('分割失败: ' + error.message);
    } finally {
        processing.value = false;
    }
  };
  

  
  // 下载图片
  const downloadImage = (imageData, type) => {
    if (!imageData) return;
    
    const link = document.createElement('a');
    link.href = imageData;
    link.download = `segmentation_${type}_${new Date().getTime()}.png`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };
  
  // 打开批量导入对话框
  const openBatchImportDialog = () => {
    ElMessage.warning('批量导入功能正在开发中，敬请期待！');
    return;
    batchImportDialogVisible.value = true;
    batchImages.value = [];
  };
  
  // 处理批量上传
  const handleBatchUpload = (file) => {
    if (!['image/jpeg', 'image/png'].includes(file.raw.type)) {
      ElMessage.error(`文件 ${file.name} 不是JPG/PNG格式，已跳过`);
      return;
    }
    
    const reader = new FileReader();
    reader.onload = (e) => {
      batchImages.value.push({
        name: file.name,
        data: e.target.result
      });
    };
    reader.readAsDataURL(file.raw);
  };
  
  // 确认批量导入
  const confirmBatchImport = () => {
    if (batchImages.value.length === 0) {
      ElMessage.warning('请先上传图片');
      return;
    }
    
    batchImportDialogVisible.value = false;
    batchMode.value = true;
    currentBatchIndex.value = 0;
    
    // 加载第一张图片
    uploadedImage.value = batchImages.value[0].data;
    segmentationMask.value = null;
    overlayResult.value = null;
    
    ElMessage.success(`已导入 ${batchImages.value.length} 张图片，开始处理第一张`);
  };
  
  // 处理下一张批量图片
  const processNextBatchImage = () => {
    uploadedImage.value = batchImages.value[currentBatchIndex.value].data;
    segmentationMask.value = null;
    overlayResult.value = null;
    
    // 自动开始分割
    startSegmentation();
  };
  
  // 批量导出结果
  const exportBatchResults = () => {
    if (!batchMode.value) {
      ElMessage.warning('当前不是批量处理模式');
      return;
    }
    
    // 这里应该是将批量处理的结果打包下载
    // 模拟创建一个ZIP文件下载
    ElMessage.success('批量导出功能已触发（模拟）');
    
    // 实际应用中，这里可以调用后端API获取打包好的结果
    // 或者使用JSZip等库在前端打包
  };
  </script>
  
  <style scoped>
  .image-segmentation-container {
    background-color: #f5f5f5;
    min-height: 100vh;
    padding: 20px;
    font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', Arial, sans-serif;
  }
  
  .title {
    color: #409eff;
    text-align: center;
    margin-bottom: 10px;
  }
  
  .subtitle {
    text-align: center;
    color: #666;
    margin-bottom: 20px;
  }
  
  .control-panel {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 20px;
    margin-bottom: 30px;
  }
  
  .model-select {
    width: 400px;
  }
  
  .image-panels {
    display: flex;
    justify-content: space-between;
    gap: 20px;
    margin-top: 20px;
  }
  
  .image-panel {
    flex: 1;
    background-color: white;
    border-radius: 4px;
    padding: 15px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  }
  
  .image-panel h3 {
    text-align: center;
    margin-bottom: 15px;
    color: #333;
  }
  
  .image-upload-area, .image-display-area {
    height: 300px;
    border: 2px dashed #ddd;
    border-radius: 4px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    cursor: pointer;
    margin-bottom: 15px;
    overflow: hidden;
    position: relative;
  }
  
  .image-upload-area:hover, .image-display-area:hover {
    border-color: #409eff;
  }
  
  .upload-placeholder, .empty-placeholder {
    text-align: center;
    color: #999;
  }
  
  .hint {
    font-size: 14px;
    color: #ccc;
    margin-top: 10px;
  }
  
  .preview-image {
    max-width: 100%;
    max-height: 100%;
    object-fit: contain;
  }
  
  .batch-actions, .download-action {
    display: flex;
    justify-content: center;
    margin-top: 10px;
    gap: 10px;
  }
  
  .batch-upload {
    margin-top: 20px;
  }
  </style>