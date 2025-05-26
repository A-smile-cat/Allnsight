<template>
    <div class="image-classification-container">
      <h1 class="title">图像分类</h1>
      <p class="subtitle">上传待分类图片，选择模型进行分类</p>
      
      <div class="control-panel">
        <el-select v-model="selectedModel" placeholder="请选择模型" class="model-select">
          <el-option
            v-for="model in modelList"
            :key="model.value"
            :label="model.label"
            :value="model.value"
          />
        </el-select>
        <el-button 
          type="primary" 
          @click="startClassification" 
          :disabled="!selectedModel || !uploadedImage"
          :loading="processing"
        >
          开始分类
        </el-button>
      </div>
      
      <div class="content-panels">
        <!-- 待分类图片区 -->
        <div class="image-panel">
          <h3>待分类图片</h3>
          <div class="image-upload-area" @click="triggerFileInput">
            <div v-if="!uploadedImage" class="upload-placeholder">
              <el-button type="primary">选择图片</el-button>
              <p>请上传JPG/PNG格式图片</p>
              <p class="hint">尚未上传图片</p>
            </div>
            <img v-else :src="uploadedImage" alt="待分类图片" class="preview-image">
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
        
        <!-- 分类结果区 -->
        <div class="result-panel">
          <h3>分类结果</h3>
          <div class="result-table-area">
            <el-table 
              :data="classificationResults" 
              border 
              style="width: 100%"
              v-loading="processing"
            >
              <el-table-column prop="imageName" label="图片名称" width="180" v-if="batchMode" />
              <el-table-column prop="label" label="标签" />
              <el-table-column prop="confidence" label="置信度">
                <template #default="{row}">
                  {{ (row.confidence * 1).toFixed(2) }}%
                </template>
              </el-table-column>
            </el-table>
            
            <div v-if="classificationResults.length === 0" class="empty-placeholder">
              <p class="hint">尚未分类</p>
            </div>
          </div>
          <div class="export-action">
            <el-button 
              type="primary" 
              plain 
              @click="exportResults('json')"
              :disabled="classificationResults.length === 0"
            >
              导出JSON
            </el-button>
            <el-button 
              type="primary" 
              plain 
              @click="exportResults('csv')"
              :disabled="classificationResults.length === 0"
            >
              导出CSV
            </el-button>
          </div>
        </div>
      </div>
      
      <!-- 批量导入对话框 -->
      <el-dialog v-model="batchImportDialogVisible" title="批量导入图片" width="50%">
        <el-upload
          class="batch-upload"
          ref="batchUploadRef"
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
  import { ref, onMounted } from 'vue'
  import { ElMessage } from 'element-plus'
  import { UploadFilled } from '@element-plus/icons-vue'
  import api from '@/api/login.js'
  
  // 数据状态
  const selectedModel = ref('')
  const uploadedImage = ref(null)
  const fileInput = ref(null)
  const batchImportDialogVisible = ref(false)
  const batchImages = ref([])
  const batchMode = ref(false)
  const currentBatchIndex = ref(0)
  const modelList = ref([])
  const classificationResults = ref([])
  const processing = ref(false)
  const batchUploadRef = ref(null)
  
  // 模拟用户信息
  const storedUser = localStorage.getItem('user')
  const userInfo = storedUser ? JSON.parse(storedUser) : {}
  const userId = userInfo.id
  
  onMounted(() => {
    searchModel()
  })
  
  // 查找可用模型
  const searchModel = async () => {
    try {
      const params = {
        type: "classification",
        user_id: userId
      }
      
      const res = await api.searchModel(params)
      const resultArray = typeof res.result === 'string' ? JSON.parse(res.result) : res.result 
      if (res?.code === 200 && Array.isArray(resultArray)) {
        modelList.value = resultArray.map(item => ({
          value: item.model_id,
          label: item.model_name
        }))
      }
    } catch (error) {
      console.error('模型加载失败:', error)
    }
  }
  
  // 触发文件选择
  const triggerFileInput = () => {
    fileInput.value.click()
  }
  
  // 处理单个文件上传
  const handleFileUpload = (event) => {
    const file = event.target.files[0]
    if (!file) return
    
    if (!['image/jpeg', 'image/png'].includes(file.type)) {
      ElMessage.error('请上传JPG或PNG格式的图片')
      return
    }
    
    const reader = new FileReader()
    reader.onload = (e) => {
      uploadedImage.value = e.target.result
      classificationResults.value = []
      batchMode.value = false
    }
    reader.readAsDataURL(file)
  }
  
  // 开始分类
  const startClassification = async () => {
    if (!selectedModel.value || !uploadedImage.value) {
      ElMessage.warning('请先选择模型并上传图片')
      return
    }
  
    processing.value = true
    
    try {
      // 模拟API调用
      const res = await api.classifyImage(uploadedImage.value,selectedModel.value)
      
      // 处理返回结果
      if (batchMode.value) {
        // 批量模式，添加到结果数组
        classificationResults.value.push({
          imageName: batchImages.value[currentBatchIndex.value].name,
          label: res.labels,
          confidence: res.confidence
        })
        
        // 处理下一张图片
        if (currentBatchIndex.value < batchImages.value.length - 1) {
          currentBatchIndex.value++
          uploadedImage.value = batchImages.value[currentBatchIndex.value].data
          // 自动处理下一张
          setTimeout(startClassification, 500)
        } else {
          ElMessage.success(`批量处理完成，共处理了 ${batchImages.value.length} 张图片`)
        }
      } else {
        // 单张图片模式
        classificationResults.value = [{
          imageName: '当前图片',
          label: res.labels,
          confidence: res.confidence
        }]
        ElMessage.success('分类完成')
      }
    } catch (error) {
      ElMessage.error('分类失败: ' + (error.message || '未知错误'))    
    } finally {
      processing.value = false
      // batchImages.value = []
    }
  }
  
  // 导出结果
  const exportResults = (format) => {
    if (classificationResults.value.length === 0) return
    
    let content, mimeType, fileName
    
    if (format === 'json') {
      content = JSON.stringify(classificationResults.value, null, 2)
      mimeType = 'application/json'
      fileName = `classification_results_${new Date().getTime()}.json`
    } else {
      // CSV格式
      const headers = Object.keys(classificationResults.value[0]).join(',')
      const rows = classificationResults.value.map(item => 
        Object.values(item).map(val => 
          typeof val === 'string' ? `"${val.replace(/"/g, '""')}"` : val
        ).join(',')
      ).join('\n')
      
      content = `${headers}\n${rows}`
      mimeType = 'text/csv'
      fileName = `classification_results_${new Date().getTime()}.csv`
    }
    
    const blob = new Blob([content], { type: mimeType })
    const link = document.createElement('a')
    link.href = URL.createObjectURL(blob)
    link.download = fileName
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
  }
  
  
  // 打开批量导入对话框
  const openBatchImportDialog = () => {
    batchImportDialogVisible.value = true
    batchImages.value = []
    if (batchUploadRef.value) {
    batchUploadRef.value.clearFiles()
  }
  }
  
  // 处理批量上传
  const handleBatchUpload = (file) => {
    if (!['image/jpeg', 'image/png'].includes(file.raw.type)) {
      ElMessage.error(`文件 ${file.name} 不是JPG/PNG格式，已跳过`)
      return
    }
    
    const reader = new FileReader()
    reader.onload = (e) => {
      batchImages.value.push({
        name: file.name,
        data: e.target.result
      })
    }
    reader.readAsDataURL(file.raw)
  }
  
  // 确认批量导入
  const confirmBatchImport = () => {
    if (batchImages.value.length === 0) {
      ElMessage.warning('请先上传图片')
      return
    }
    
    batchImportDialogVisible.value = false
    batchMode.value = true
    currentBatchIndex.value = 0
    classificationResults.value = []
    
    // 加载第一张图片
    uploadedImage.value = batchImages.value[0].data
    
    ElMessage.success(`已导入 ${batchImages.value.length} 张图片，开始处理第一张`)
    startClassification()
  }
  </script>
  
  <style scoped>
  .image-classification-container {
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
  
  .content-panels {
    display: flex;
    justify-content: space-between;
    gap: 20px;
    margin-top: 20px;
  }
  
  .image-panel, .result-panel {
    flex: 1;
    background-color: white;
    border-radius: 4px;
    padding: 15px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  }
  
  .image-panel h3, .result-panel h3 {
    text-align: center;
    margin-bottom: 15px;
    color: #333;
  }
  
  .image-upload-area {
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
  
  .image-upload-area:hover {
    border-color: #409eff;
  }
  
  .result-table-area {
    min-height: 300px;
    border: 2px dashed #ddd;
    border-radius: 4px;
    margin-bottom: 15px;
    padding: 10px;
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
  
  .batch-actions, .export-action {
    display: flex;
    justify-content: center;
    margin-top: 10px;
    gap: 10px;
  }
  
  .batch-upload {
    margin-top: 20px;
  }
  
  .el-table {
    margin-top: 10px;
  }
  </style>