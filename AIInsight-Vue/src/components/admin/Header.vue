<template>
  <el-header style="background: linear-gradient(to right, #89cff0, #a8e6cf); display: flex; align-items: center; justify-content: space-between; padding: 0 20px; box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08)">
    <div style="display: flex; align-items: center">
      <div style="font-size: 20px; color: white; font-weight: bold; margin-right: 20px">个人中心</div>
      <el-breadcrumb :separator-icon="ArrowRight" style="color: white">
        <el-breadcrumb-item style="color: white" @click="goHome">
            系统主页
        </el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <div style="display: flex; align-items: center">
      <el-dropdown>
        <span style="color: white; cursor: pointer">
          <el-icon :size="20"><User /></el-icon>
          <span style="margin-left: 5px">{{ username }}</span>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="handleChangeAccount">切换账号</el-dropdown-item>
            <el-dropdown-item @click="handleLogout">安全退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </el-header>
</template>

<script setup>
import { ref } from 'vue'
import { User, ArrowRight } from '@element-plus/icons-vue'
import api from '@/api/login.js'
import { ElMessage } from 'element-plus'
import router from '@/router'
import { useStore } from 'vuex'

const store = useStore()
const user = JSON.parse(localStorage.getItem('user')) // 假设用户信息存储在 localStorage 中
const username = user?.username

const goHome = () => {
  router.push('/')
}


const handleLogout = () => {
  localStorage.removeItem('user') // 清除用户信息
  store.commit('logout')
  api.logout(user).then(res => {
    if (res.code !== 200) {
      ElMessage.error('退出失败')
      return
    }
    ElMessage.success(res.message)
  })
  setTimeout(() => {
    router.push('/')
  },500)
}
const handleChangeAccount= () => {
  localStorage.removeItem('user') // 清除用户信息
  store.commit('logout')
  api.logout(user).then(res => {
    if (res.code !== 200) {
      ElMessage.error('退出失败')
      return
    }
    ElMessage.success(res.message)
  })
  setTimeout(() => {
    router.push('/login')
  },500)
}
</script>

<style scoped>
/* 可以添加样式 */
</style>