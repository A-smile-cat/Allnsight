<template> 
  <div class="articles-area"> 
    <el-card style="text-align: left;width: 990px;margin: 35px auto 0 auto"> 
      <div> 
        <span style="font-size: 20px"><strong>{{article.title}}</strong></span> 
        <el-divider content-position="left">{{article.time}}</el-divider> 
        <div class="markdown-body"> 
          <div v-html="article.content_html"></div> 
        </div> 
      </div> 
    </el-card> 
  </div> 
</template> 

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage} from "element-plus"
import api from '@/api/login.js'

const route = useRoute()
const article = reactive({})

const loadArticle = async () => {
  try {
    const res = await api.getArticle('/article/' + route.query.id)
    const resultArray = typeof res.result === 'string' ? JSON.parse(res.result) : res.result
    if (res?.code === 200 && typeof resultArray === 'object' && resultArray !== null) {
        Object.assign(article, resultArray)
        
      }
  } catch (error) {
    // console.error('加载文章失败:', error)
    ElMessage.error('加载文章失败，请稍后重试')
  }
}

onMounted(() => {
  loadArticle()
})
</script> 

<style scoped> 
  @import "../../styles/markdown.css"; 
</style>