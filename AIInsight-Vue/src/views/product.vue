<template>
    <top-bar />
    <div class="app-container">
      <h1 class="page-title">地理专题图展示系统</h1>
      <div class="grid-container">
        <!-- 第一行卡片 -->
        <div class="grid-row">
          <el-card v-for="(item, index) in firstRowItems" :key="'first-'+index" class="grid-card">
            <div class="card-content">
              <div class="thumbnail-container">
                <img :src="item.image" :alt="item.title" class="thumbnail">
              </div>
              <h3 class="card-title">{{ item.title }}</h3>
              <div class="info-bar">
                <span class="original-image">原始影像</span>
                <span class="processing-time">{{ item.time }}</span>
              </div>
            </div>
            <div class="card-actions">
              <el-button type="text" @click="viewLargeImage(item.image)">
                <el-icon><View /></el-icon>
              </el-button>
              <el-button type="text" @click="downloadImage(item.image)">
                <el-icon><Download /></el-icon>
              </el-button>
            </div>
          </el-card>
        </div>
        
        <!-- 第二行卡片 -->
        <div class="grid-row">
          <el-card v-for="(item, index) in secondRowItems" :key="'second-'+index" class="grid-card">
            <div class="card-content">
              <div class="thumbnail-container">
                <img :src="item.image" :alt="item.title" class="thumbnail">
              </div>
              <h3 class="card-title">{{ item.title }}</h3>
              <div class="info-bar">
                <span class="original-image">原始影像</span>
                <span class="processing-time">{{ item.time }}</span>
              </div>
            </div>
            <div class="card-actions">
              <el-button type="text" @click="viewLargeImage(item.image)">
                <el-icon><View /></el-icon>
              </el-button>
              <el-button type="text" @click="downloadImage(item.image)">
                <el-icon><Download /></el-icon>
              </el-button>
            </div>
          </el-card>
        </div>
      </div>
  
      <!-- 查看大图对话框 -->
      <el-dialog v-model="dialogVisible" title="查看大图" width="80%">
        <img :src="currentImage" class="large-image" alt="大图预览">
      </el-dialog>
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue'
  import { ElCard, ElButton, ElIcon, ElDialog } from 'element-plus'
  import { View, Download, User } from '@element-plus/icons-vue'
  import TopBar from "@/components/topBar"
  import image1 from '@/assets/product-image/1.png'
  import image2 from '@/assets/product-image/2.png'
  import image3 from '@/assets/product-image/3.png'
  import image4 from '@/assets/product-image/4.png'
  import image5 from '@/assets/product-image/5.png'
  import image6 from '@/assets/product-image/6.png'
  import image7 from '@/assets/product-image/7.png'
  import image8 from '@/assets/product-image/8.png'
  
  // 第一行卡片数据
  const firstRowItems = ref([
    {
      title: '海州湾养殖区专题图',
      image: image1,
      time: '处理时间 2025-03-30 09:20'
    },
    {
      title: '黄河口湿地分类专题图',
      image: image2,
      time: '处理时间 2025-03-30 09:21'
    },
    {
      title: '连云港湿地覆盖分类专题图',
      image: image3,
      time: '处理时间 2025-03-30 09:22'
    },
    {
      title: '连云港连云区湿地覆盖分类专题图',
      image: image4,
      time: '处理时间 2025-03-30 10:35'
    }
  ])
  
  // 第二行卡片数据
  const secondRowItems = ref([
    {
      title: '黄河口湿地分类专题图',
      image: image5,
      time: '处理时间 2025-03-30 10:36'
    },
    {
      title: '黄海绿潮分布专题图',
      image: image6,
      time: '处理时间 2025-03-30 10:37'
    },
    {
      title: '莱州湾岸线专题图',
      image: image7,
      time: '处理时间 2025-03-30 10:38'
    },
    {
      title: '青岛市黄岛区土地覆盖分类专题图',
      image: image8,
      time: '处理时间 2025-03-30 10:39'
    }
  ])
  
  // 对话框控制
  const dialogVisible = ref(false)
  const currentImage = ref('')
  
  // 查看大图
  const viewLargeImage = (imageUrl) => {
    currentImage.value = imageUrl
    dialogVisible.value = true
  }
  
  // 下载图片
  const downloadImage = (imageUrl) => {
    const link = document.createElement('a')
    link.href = imageUrl
    link.download = imageUrl.split('/').pop()
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
  }
  
  // 显示用户信息
  const showUserInfo = () => {
    ElMessage.info('用户信息功能待实现')
  }
  </script>
  
  <style scoped>
  .app-container {
    background-color: #1a1a1a;
    color: white;
    min-height: 100vh;
    padding: 20px;
  }
  
  .page-title {
    text-align: center;
    margin-bottom: 30px;
    color: #ffffff;
  }
  
  .grid-container {
    display: flex;
    flex-direction: column;
    gap: 20px;
  }
  
  .grid-row {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;
  }
  
  .grid-card {
    background-color: #2d2d2d;
    border: none;
    border-radius: 8px;
    transition: transform 0.3s;
  }
  
  .grid-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 10px 20px rgba(0, 0, 0, 0.3);
  }
  
  .card-content {
    padding: 15px;
  }
  
  .thumbnail-container {
    height: 180px;
    overflow: hidden;
    border-radius: 4px;
    margin-bottom: 15px;
  }
  
  .thumbnail {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.3s;
  }
  
  .thumbnail:hover {
    transform: scale(1.05);
  }
  
  .card-title {
    margin: 0 0 10px 0;
    font-size: 16px;
    font-weight: 500;
    color: #ffffff;
    text-align: center;
  }
  
  .info-bar {
    display: flex;
    justify-content: space-between;
    font-size: 12px;
    color: #cccccc;
    padding-top: 10px;
    border-top: 1px solid #444;
  }
  
  .card-actions {
    display: flex;
    justify-content: space-around;
    padding: 10px 0;
    border-top: 1px solid #444;
  }
  
  .card-actions .el-button {
    color: #ffffff;
  }
  
  .card-actions .el-button:hover {
    color: #409eff;
  }
  
  .large-image {
    width: 100%;
    height: auto;
    display: block;
    margin: 0 auto;
  }
  
  /* 响应式设计 */
  @media (max-width: 1200px) {
    .grid-row {
      grid-template-columns: repeat(2, 1fr);
    }
  }
  
  @media (max-width: 768px) {
    .grid-row {
      grid-template-columns: 1fr;
    }
  }
  </style>