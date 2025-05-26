<template>
    <el-container id="admin-body">
      <el-header style="z-index: 1;height: 50px;margin-left: -20px">
        <Header style="position: absolute;width: 98%;"></Header>
      </el-header>
      <!--<el-row style="height: 100%;">-->
      <el-container style="height: calc(100vh-50px);">
        <el-aside style="margin-top: -50px;width: 200px; background-color: #30333c;">
          <Menu></Menu>
        </el-aside>
        <el-main>
          <router-view/>
        </el-main>
      </el-container>
    </el-container>
  </template>
  
  <script setup>
    import Menu from './Menu.vue'
    import Header from './Header'
    import { ref, onMounted } from 'vue'
    import { useRouter } from 'vue-router'

    const dialogVisible = ref(false)
    const router = useRouter()

    onMounted(() => {
      // router.replace('/admin/dashboard')
    })
  </script>
  
  <style scoped>
  </style>
  