<template>
  <div id="app">
    <!-- 路由视图占位符，不同路由组件会在这里渲染 -->
    <router-view></router-view>
    <!-- 页脚 -->
    <footer v-if="$route.name !== 'error'">
      版权所有 &copy; 2025
    <br>
      联系方式：123456789
    <br>
      地址：山东科技大学
    <br>
      友情链接：
    <br>
      <a href="https://www.bijianet.cn/index">海洋海岸带遥感影像智能云解译平台&nbsp;|&nbsp;</a>
      <a href="https://engine-aiearth.aliyun.com/#/">数知地球 AI Earth&nbsp;|&nbsp;</a>
      <a href="https://earthengine.google.com/">Google Earth Engine</a>
    </footer>
  </div>
</template>

<script setup>
import { onMounted, onBeforeUnmount } from 'vue'
import { useStore } from 'vuex'

const store = useStore()

const handleWindowClose = (event) => {
  // 仅在实际关闭/离开页面时执行退出
  if (event.type === 'pagehide' || 
     (event.type === 'beforeunload' && !event.persisted)) {
     store.dispatch('logout')
  }
}

onMounted(() => {
  window.addEventListener('beforeunload', handleWindowClose)
  window.addEventListener('pagehide', handleWindowClose)
})

onBeforeUnmount(() => {
  window.removeEventListener('beforeunload', handleWindowClose)
  window.removeEventListener('pagehide', handleWindowClose)
})
</script>

<style scoped>
/* 全局样式 */
nav {
  background-color: #333;
  color: white;
  padding: 10px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

nav a {
  color: white;
  margin-right: 20px;
  text-decoration: none;
}

footer {
  position: relative;
  z-index: 10;
  background-color: #636363;
  color: white;
  text-align: center;
  padding: 10px;
  /* position: fixed; */
  bottom: 0;
  width: 100%;
}
/* 设置页脚超链接的颜色 */
footer a {
  color: wheat; /* 正常状态颜色 */
  text-decoration: none;
}

footer a:visited {
  color: blue; /* 访问后颜色 */
}

footer a:hover {
  color: blue; /* 鼠标悬停颜色 */
}

footer a:active {
  color: blue; /* 点击时颜色 */
}
/* 按钮容器样式 */
.button-group {
  display: flex;
  align-items: center;
  gap: 10px;
}

.logout-button {
  margin-left: auto; /* 将退出按钮推到最右边 */
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 8px 15px;
}

.user-button {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 15px;
  background-color: #409eff;
  border-color: #409eff;
}

.user-button:hover {
  background-color: #79bbff;
  border-color: #79bbff;
}

.user-button span {
  color: white;
}

</style>
