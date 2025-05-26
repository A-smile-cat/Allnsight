<!-- 个人中心页面 -->
<template>
  <div class="profile">
    <el-row :gutter="20">

      <!-- 修改密码卡片 -->
      <el-col :span="8">
        <el-card class="password-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="header-title">修改密码</span>
              <el-icon><Lock /></el-icon>
            </div>
          </template>
          <el-form
              ref="passwordFormRef"
              :model="passwordForm"
              :rules="passwordRules"
              label-width="100px"
              status-icon
          >
            <el-form-item label="原密码" prop="oldPassword">
              <el-input
                  v-model="passwordForm.oldPassword"
                  type="password"
                  show-password
                  placeholder="请输入原密码"
              />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input
                  v-model="passwordForm.newPassword"
                  type="password"
                  show-password
                  placeholder="请输入新密码"
              />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input
                  v-model="passwordForm.confirmPassword"
                  type="password"
                  show-password
                  placeholder="请确认新密码"
              />
            </el-form-item>
            <el-form-item>
              <el-button
                  type="primary"
                  :loading="passwordLoading"
                  @click="handleChangePassword"
              >
                <el-icon><Check /></el-icon>
                修改密码
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Edit, Camera, Lock, Check } from '@element-plus/icons-vue'
import api from '@/api/login.js'

const storedUser = localStorage.getItem('user');
  const userInfo = storedUser ? JSON.parse(storedUser) : {};
  const userId = userInfo.id; // 假设 user_id 的键名为 id
// 修改密码表单
const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: '',
  id: userId
})

// 修改密码表单校验规则
const validatePass2 = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== passwordForm.value.newPassword) {
    callback(new Error('两次输入密码不一致!'))
  } else {
    callback()
  }
}

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度应在6-20个字符之间', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度应在6-20个字符之间', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validatePass2, trigger: 'blur' }
  ]
}

const passwordLoading = ref(false)
const passwordFormRef = ref(null)

// 处理修改密码
const handleChangePassword = async () => {
  if (!passwordFormRef.value) return

  try {
    await passwordFormRef.value.validate()
    passwordLoading.value = true
    
    const params = {
        oldPassword: passwordForm.value.oldPassword,
        newPassword: passwordForm.value.newPassword,
        confirmPassword:passwordForm.value.confirmPassword,
        id: userId
      }
    let res
    res = await api.updatePassword(params)
    if(res?.code === 200){
      ElMessage.success(res.message)
    }else{
      // ElMessage.error(res.message)
    }
    // 这里应该调用API修改密码
    // 示例: await updatePassword(passwordForm.value)
    passwordForm.value = {
      oldPassword: '',
      newPassword: '',
      confirmPassword: ''
    }
  } catch (error) {
    // console.error('修改密码失败:', error)
    // ElMessage.error('修改密码失败')
  } finally {
    passwordLoading.value = false
  }
}

</script>

<style scoped>
.profile {
  padding: 20px;
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