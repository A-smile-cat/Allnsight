<template>
  <div class="error-page">
    <div class="error-content">
      <h1 class="error-title">404</h1>
      <p class="error-message">您访问的页面不存在</p>
      <p class="error-submessage">可能是地址输入错误，或者页面正在建设中…</p>
      <router-link to="/" class="home-button">
        <el-button type="primary" size="large">返回首页</el-button>
      </router-link>
    </div>
  </div>
</template>

<script setup>
// 使用 <script setup> 语法
</script>

<style scoped>
.error-page {
  background: url("../assets/4.webp") no-repeat;
  background-position: center;
  background-size: cover;
  margin: 0;
  height: 100vh;
  width: 100%;
  position: fixed;
  display: flex;
  justify-content: center;
  align-items: center;
}

.error-content {
  text-align: center;
  background-color: rgba(255, 255, 255, 0.85);
  padding: 40px;
  border-radius: 10px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  max-width: 500px;
}

.error-title {
  font-size: 80px;
  color: #f56c6c;
  margin: 0 0 20px;
  font-weight: bold;
}

.error-message {
  font-size: 24px;
  color: #333;
  margin-bottom: 10px;
}

.error-submessage {
  font-size: 16px;
  color: #666;
  margin-bottom: 30px;
}

.home-button {
  text-decoration: none;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .error-content {
    width: 80%;
    padding: 30px 20px;
  }

  .error-title {
    font-size: 60px;
  }

  .error-message {
    font-size: 20px;
  }
}
</style>