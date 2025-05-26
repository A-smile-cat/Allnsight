<template>
  <el-menu
      :default-active="activeIndex"
      class="custom-menu-with-bg"
      mode="horizontal"
      @select="handleSelect"
      router
  >
    <el-menu-item index="/home">系统首页</el-menu-item>
    <el-sub-menu index="2" class="wetland-menu">
      <template #title>智慧湿地</template>
      <el-menu-item index="/dashboard">数据中心</el-menu-item>
      <el-menu-item index="/product">成果展示</el-menu-item>
      <el-sub-menu index="/ai-tool">
        <template #title>AI工具</template>
        <el-menu-item index="/ai-tool/classify">智能识别</el-menu-item>
        <el-menu-item index="/ai-tool/prediction">分布预测</el-menu-item>
        <el-menu-item index="/ai-tool/segmention">图像解译</el-menu-item>
      </el-sub-menu>
    </el-sub-menu>
    <el-menu-item index="/protect">数据上传</el-menu-item>
    <el-menu-item index="/news">通知公告</el-menu-item>
    <el-menu-item index="/about">关于我们</el-menu-item>
    <el-menu-item index="/login">登录/注册</el-menu-item>
  </el-menu>
</template>

<script setup>
import { ref, watch } from 'vue'  // 导入 ref 和 watch
import { useRoute } from 'vue-router'

const route = useRoute()
const activeIndex = ref(route.path)

// 监听路由变化，处理嵌套路由
watch(() => route.path, (newPath) => {
  // 处理AI工具下的子路由
  if (newPath.startsWith('/ai-tool')) {
    activeIndex.value = '/ai-tool'
  } else {
    activeIndex.value = newPath
  }
})
</script>

<style scoped>

.custom-menu-with-bg {
  background: url('../assets/topBg.jpg') no-repeat center center;
  background-size: cover;
  border-bottom: none !important;
}

/* 调整菜单项文字颜色确保可读性 */
.custom-menu-with-bg .el-menu-item{
  color: white;
  font-weight: 600;
}

.wetland-menu{
  color: white !important;
  --el-menu-text-color: white;
  font-weight: 600;
}

.custom-menu-with-bg .el-menu-item:hover {
  background-color: rgba(255, 255, 255, 1);
}
.custom-menu-with-bg .el-sub-menu:hover {
  background-color: rgba(255, 255, 255, 1);
}

.custom-menu-with-bg .el-menu-item.is-active {
  color: #69b3f0 !important;
  font-weight: 700; /* 选中文字加粗 */
  border-bottom: 2px solid #ffd04b;
}
</style>