<template>
  <top-bar />
  <div class="wetland-container">
    <div class="title-group">
      <h1>“云瞰泽影”—滨海湿地外来入侵物种互花米草智能监测与评估系统</h1>
      <h2 style="font-size: 35px">基于卷积神经网络的智能化遥感图像解译平台</h2>
    </div>

    <el-carousel
        :interval="3000"
        type="card"
        height="400px"
        indicator-position="outside"
    >
      <el-carousel-item v-for="(img, index) in wetlandImages" :key="index">
        <div class="carousel-content">
          <img :src="img.url" :alt="img.desc"/>
          <div class="image-overlay">
            <h3>{{ img.title }}</h3>
            <p>{{ img.desc }}</p>
          </div>
        </div>
      </el-carousel-item>
    </el-carousel>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import TopBar from "@/components/topBar"

// 图片数据改用ref响应式变量
const wetlandImages = ref([
  {
    url: new URL('../assets/1.jpg', import.meta.url).href,
    title: "基于卷积神经网络的智能化遥感图像解译算法",
    desc: "基于FCN开发智能化遥感图像解译算法，充分挖掘多源遥感大数据，\n" +
        "融合相关领域地理学数据，建立标准化的湿地遥感制图数据集"
  },
  {
    url: new URL('../assets/2.jpeg', import.meta.url).href,
    title: "基于线性光谱混合模型的遥感图像信息分析",
    desc: "线性光谱混合模型（Linear Spectral Mixture Model，LSMM）是一种用于分析\n" +
        "和解释遥感影像中光谱信息的数学模型，其关键在于确定端元和它们在每个像素\n" +
        "中的丰度。"
  },
  {
    url: new URL('../assets/5.jpg', import.meta.url).href,
    title: "基于ANN-CA耦合模型建立互花米草时空动态监测与扩散趋势预测模型",
    desc: "采用 3 层 BP 神经网络，在不断训练与校正与验证模型精度的基\n" +
        "础上，对特定区域内互花米草进行模拟，"
  },
  {
    url: new URL('../assets/6.jpg', import.meta.url).href,
    title: "基于多模态数据融合技术的滨海湿地环境精细化监测",
    desc: "LiDAR 数据则能够提供高精度的三维地形\n" +
        "和植被结构信息，这对于理解互花米草的空间分布和生长环境至关重要。"
  },
  {
    url: new URL('../assets/7.jpg', import.meta.url).href,
    title: "时间序列分析揭示互花米草的周期性特征与扩散趋势",
    desc: "通过连续收集和分析时间序列的高光谱数据，研究人员能够详细观察互花米\n" +
        "草在不同季节的生长模式和行为，以及其在长期内的扩散趋势。"
  },
  {
    url: new URL('../assets/8.jpg', import.meta.url).href,
    title: "滨海湿地外来入侵物种智能监测与评估",
    desc: "助力生态系统保护和生态监测，同时基于深度学习开发智能化遥感图像解译平台"
  }
])
</script>

<style scoped>
.wetland-container {
  background: linear-gradient(135deg, #42b983 0%, #3498db 100%);
  padding: 30px 0;
}

.title-group {
  text-align: center;
  margin-bottom: 30px;
}

h1 {
  font-size: 2.8rem;
  color: white;
  text-shadow: 0 2px 4px rgba(0,0,0,0.3);
  margin-bottom: 10px;
  transition: transform 0.3s; /* 悬停动画 */
}
h1:hover {
  transform: scale(1.1); /* 悬停时轻微放大 */
}
h2 {
  font-size: 1.5rem;
  color: rgba(255,255,255,0.9);
  font-weight: 300;
  transition: transform 0.3s;
}
h2:hover {
  transform: scale(1.1); /* 悬停时轻微放大 */
}
.el-carousel {
  width: 85%;
  margin: 0 auto;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.2);
}

.carousel-content {
  position: relative;
  width: 100%;
  height: 100%;
}

.carousel-content img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  filter: brightness(0.9);
  transition: all 0.5s;
}

.image-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(0,0,0,0.6);
  color: white;
  padding: 20px;
  transition: all 0.5s;
}

.image-overlay h3 {
  font-size: 1.8rem;
  margin-bottom: 8px;
}

.el-carousel--card .el-carousel__item {
  transition: transform 0.4s;
  border-radius: 8px;
  overflow: hidden;
}

.el-carousel--card .el-carousel__item.is-active {
  transform: scale(1.05);
  box-shadow: 0 8px 25px rgba(0,0,0,0.3);
}
</style>