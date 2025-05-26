<!-- 个人中心页面 -->
<template>
  <div class="profile">
    <el-row :gutter="20">
      <!-- 个人信息卡片 -->
      <el-col :span="8">
        <el-card class="profile-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="header-title">个人信息</span>
            </div>
          </template>
          <div class="profile-info">
            <div class="info-list">
              <el-form ref="infoFormRef" :model="userInfo" :rules="rules" label-width="100px">
                <el-form-item label="账号：">
                  <span>{{ userInfo?.account_id }}</span>
                </el-form-item>
                <el-form-item label="用户名：" prop="username">
                  <el-input v-model="userInfo.username" placeholder="暂无"></el-input>
                </el-form-item>
                <el-form-item label="手机号：" prop="phone">
                  <span>{{ formatSensitiveInfo(userInfo?.phone) }}</span>
                </el-form-item>
                <el-form-item label="性别：" prop="gender">
                  <el-select v-model="userInfo.gender" placeholder="请选择性别">
                    <el-option label="男" value="男"></el-option>
                    <el-option label="女" value="女"></el-option>
                    <el-option label="保密" value="保密"></el-option>
                  </el-select>
                </el-form-item>
                <el-form-item label="出生年月：" prop="birthday">
                  <el-date-picker
                    v-model="userInfo.birthday"
                    type="month"
                    placeholder="选择年月"
                    value-format="YYYY-MM-DD"
                    format="YYYY年MM月"
                    @change="handleBirthdayChange"
                  />
                </el-form-item>
                <el-form-item label="联系地址：" prop="address">
                  <div style="display: flex; flex-direction: column; width: 100%">
                    <el-cascader
                      v-model="userInfo.region"
                      :options="regionOptions"
                      placeholder="请选择省市区"
                      style="margin-bottom: 10px"
                    />
                    <el-input
                      v-model="userInfo.address"
                      placeholder="详细地址"
                    />
                  </div>
                </el-form-item>
                <el-form-item label="绑定邮箱：" prop="email">
                  <span>{{ formatSensitiveInfo(userInfo?.email) }}</span>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="handleSaveInfo">保存修改</el-button>
                </el-form-item>
              </el-form>
            </div>
          </div>
        </el-card>
      </el-col>

      
    </el-row>
  </div>
</template>

<script setup>
import { reactive,ref,onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Edit, Camera, Lock, Check } from '@element-plus/icons-vue'
import axios from 'axios'
import api from '@/api/login.js'
import router from '@/router'

const regionOptions = ref([])
const infoFormRef = ref(null)

const isAuthPage = ref(true)
// 从localStorage获取用户信息
const storedUser = localStorage.getItem('user')
if(!storedUser) {
  ElMessage.error('请先登录')
  // window.location.href = '/login'
  router.push('/login')
}
const userInfo = ref(storedUser ? JSON.parse(storedUser) : {})
// 如果region是字符串，转换为数组
if (userInfo.value.region && typeof userInfo.value.region === 'string') {
  userInfo.value.region = userInfo.value.region.split(' ')
}
// 从外部文件加载地区数据
onMounted(async () => {
  try {
    const response = await axios.get('./pca-code.json')
    regionOptions.value = response.data
  } catch (error) {
    console.error('读取地区数据失败:', error)
  }
})

const rules = reactive({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 30, message: '用户名长度应在2-30个字符之间', trigger: 'blur' }
  ],
  phone: [
    { message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'blur' }
  ],
  birthday: [
    { message: '请选择出生年月', trigger: 'blur' }
  ],
  region: [
    { required: true, message: '请选择地区', trigger: 'blur' }
  ],
  address: [
    { message: '请输入详细地址', trigger: 'blur' }  
  ]
})
const handleSaveInfo = async () => {
  try {
    await infoFormRef.value.validate()

    // 更新本地存储
    localStorage.setItem('user', JSON.stringify(userInfo.value))

    // 将region数组转换为字符串
    if(userInfo.value.region) {
      userInfo.value.region = userInfo.value.region.join(' ')
    }

    // 这里调用API更新用户信息
    await updateUserInfo(userInfo.value)
  
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败')
  }
}

const updateUserInfo = async (userData) => {
  try {
    const response = await api.updateUserInfo(userData)
    const { code, message } = response
    if (code === 200) {
      ElMessage.success('修改个人信息成功')
      // 刷新页面
      window.location.reload()
    } else if (code === 401){
      ElMessage.error(message)
      // 清除用户信息并跳转到登录页面
      localStorage.removeItem('user')
      // window.location.href = '/login'
      router.push('/login')
    }else{
      ElMessage.error(message)
    }
  }catch{
    ElMessage.error('修改个人信息失败')
  }
}

const formatSensitiveInfo = (value) => {
  if (!value) return '暂无'
  if (value.includes('@')) { // 邮箱处理
    const [name, domain] = value.split('@')
    return `${name.substring(0, 5)}***@${domain}`
  } else { // 手机号处理
    return `${value.substring(0, 3)}****${value.substring(value.length - 4)}`
  }
}
</script>

<style scoped>
.profile {
  padding: 10px;
  min-height: calc(100vh - 140px);
  background-color: #f5f7fa;
}

.profile-card,
.password-card {
  width:500px;
  height: 100%;
  transition: all 0.3s;
}

.profile-card:hover,
.password-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-title {
  font-size: 16px;
  font-weight: bold;
  display: flex;
  align-items: center;
  gap: 8px;
}

.profile-info {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.avatar-wrapper {
  position: relative;
  margin-bottom: 30px;
  text-align: center;
}

.change-avatar {
  margin-top: 16px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.info-list {
  width: 100%;
}

.info-item {
  display: flex;
  margin-bottom: 20px;
  padding: 0 20px;
  line-height: 24px;
}

.info-item .label {
  width: 80px;
  color: #606266;
  font-weight: 500;
}

.info-item .value {
  color: #303133;
  flex: 1;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

:deep(.el-form-item__content) {
  flex-wrap: nowrap;
}

:deep(.el-input__wrapper) {
  box-shadow: 0 0 0 1px #dcdfe6 inset;
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #409eff inset;
}

:deep(.el-form--label-top .el-form-item__label) {
  margin-bottom: 8px;
}

:deep(.el-button--primary) {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
}
</style>

