<template>
  <top-bar />
  <div class="news-container">
    <h1 class="page-title">最新资讯</h1>

    <!-- 新闻列表 -->
    <div class="news-list">
      <div
          v-for="(news, index) in newsList"
          :key="index"
          class="news-item"
          @click="goToDetail(news.id)"
      >
        <div class="news-image">
          <img :src="news.image" :alt="news.title">
        </div>
        <div class="news-content">
          <h2 class="news-title">{{ news.title }}</h2>
          <p class="news-summary">{{ news.summary }}</p>
          <div class="news-meta">
            <span class="news-date">{{ news.date }}</span>
            <span class="news-category">{{ news.category }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 视频部分 -->
    <div class="video-section">
      <h2 class="section-title">科普视频</h2>
      <div class="video-container" >
        <iframe
            loading="lazy"
            :src="videoData.url"
            frameborder="0"
            allow="clipboard-write; encrypted-media; gyroscope; picture-in-picture"
            allowfullscreen
        ></iframe>
<!--        <iframe src="//player.bilibili.com/player.html?isOutside=true&aid=468793961&bvid=BV1w5411d7i1&cid=713218838&p=1&autoplay=false" scrolling="no" border="0" frameborder="no" framespacing="0" allowfullscreen="true"></iframe>-->
        <div class="video-info">
          <h3 class="video-title">{{ videoData.title }}</h3>
          <p class="video-desc">{{ videoData.description }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref,watch } from 'vue'
import { useRouter } from 'vue-router'
import TopBar from "@/components/topBar"
import news1 from '@/assets/news/news1.jpeg'
import news2 from '@/assets/news/news2.jpg'
import news3 from '@/assets/news/news3.jpg'
import video from '@/assets/videos/video.mp4'

const router = useRouter()

// 新闻数据
const newsList = ref([
  {
    id: 1,
    title: '平台使用说明',
    summary: '近年来，人工智能技术在生态环境保护领域的应用日益广泛，从生态监测到图像分析，AI正在改变传统湿地保护模式。',
    image: news1,
    date: '2025-03-01',
    category: '置顶'
  },
  {
    id: 2,
    title: '外来物种入侵的危害与防治',
    summary: '外来物种入侵是指生物物种由原产地通过自然或人为的途径迁移到新的生态环境的过程。外来物种入侵会排挤本地物种，改变当地生态系统结构，降低生物多样性，影响生态平衡。',
    image: news2,
    date: '2025-04-10',
    category: '环境'
  },
  {
    id: 3,
    title: '全卷积神经网络在遥感图像智能解译领域的应用',
    summary: '全卷积神经网络在遥感图像智能解译中优势显著，它能精准识别地物，高效处理海量数据，端到端学习简化流程，大幅提升解译的精度与效率。',
    image: news3,
    date: '2025-05-01',
    category: '科技'
  }
])


const videoData = ref({
  title: '央视科教频道：地理中国',
  description: '当地为何要引种互花米草？（节选）',
  url: video
})

// 跳转到详情页
const goToDetail = (id) => {
  router.push(`/news/${id}`)
}

// 监听videoData.url的变化
watch(() => videoData.value.url, () => {
  const videoIframe = document.querySelector('iframe')
  if (videoIframe) {
    videoIframe.contentWindow.postMessage('{"event":"command","func":"pauseVideo","args":[]}', '*')
  }
})
</script>

<style scoped>
.news-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-title {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
  font-size: 2.2rem;
}

.news-list {
  display: flex;
  flex-direction: column;
  gap: 25px;
  margin-bottom: 40px;
}

.news-item {
  display: flex;
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  cursor: pointer;
}

.news-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.15);
}

.news-image {
  flex: 0 0 300px;
}

.news-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.news-content {
  flex: 1;
  padding: 20px;
  display: flex;
  flex-direction: column;
}

.news-title {
  margin: 0 0 10px;
  color: #222;
  font-size: 1.4rem;
}

.news-summary {
  margin: 0 0 15px;
  color: #555;
  line-height: 1.6;
  flex-grow: 1;
}

.news-meta {
  display: flex;
  justify-content: space-between;
  color: #888;
  font-size: 0.9rem;
}

.video-section {
  margin-top: 50px;
}

.section-title {
  text-align: center;
  margin-bottom: 25px;
  color: #333;
  font-size: 1.8rem;
}

.video-container {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.video-container iframe {
  width: 100%;
  height: 500px;
  display: block;
}

.video-info {
  padding: 20px;
}

.video-title {
  margin: 0 0 10px;
  color: #222;
  font-size: 1.3rem;
}

.video-desc {
  margin: 0;
  color: #555;
  line-height: 1.6;
}

@media (max-width: 768px) {
  .news-item {
    flex-direction: column;
  }

  .news-image {
    flex: 0 0 200px;
  }

  .video-container iframe {
    height: 300px;
  }
}
</style>