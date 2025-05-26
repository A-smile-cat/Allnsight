<template>
  <h1>用户管理</h1>
  <div>
    <el-input v-model="searchInput" style="width: 200px" placeholder="请输入用户名或账号"></el-input>
    <el-button type="warning" style="margin-left: 10px" @click="searchAll(true,'query')">查询</el-button>
    <el-button type="success" style="margin-left: 10px" @click="handleAdd">新增</el-button>
  </div>
  <el-table :data="tableData" stripe style="width: 100%">
    <el-table-column prop="username" label="用户名" />
    <el-table-column prop="account_id" label="账号" />
    <el-table-column prop="email" label="注册邮箱" width="180px" />
    <el-table-column prop="status" label="账户状态">
      <template #default="scope">
        <el-tag :type="getStatusTagType(scope.row.status)">
          {{ getStatusText(scope.row.status) }}
        </el-tag>
      </template>
    </el-table-column>
    <el-table-column prop="gender" label="性别" width="70px" />
    <el-table-column prop="birthday" label="出生年月" width="120px">
      <template #default="scope">
        
        {{ formatDate(scope.row.birthday) }}
      </template>
    </el-table-column>
    <el-table-column prop="phone" label="联系电话" />
    <el-table-column label="操作">
      <template #default="scope">
        <el-button type="primary" @click="handleEdit(scope.row)">编辑</el-button>
        <el-button type="danger" @click="handleDelete(scope.row)">禁用</el-button>
      </template>
    </el-table-column>
  </el-table>

  <el-dialog v-model="dialogVisible" :title="dialogTitle" width="30%">
  <el-form :model="formData" :rules="formRules" label-width="80px" ref="formRef">
    <el-form-item label="用户名" prop="username">
      <el-input v-model="formData.username"></el-input>
    </el-form-item>
    <el-form-item label="绑定邮箱" prop="email">
      <el-input v-model="formData.email"></el-input>
    </el-form-item>
    <el-form-item label="密码" prop="password" :rules="passwordRules">
      <el-input v-model="formData.password" type="password"></el-input>
    </el-form-item>
    <el-form-item label="联系方式" prop="phone">
      <el-input v-model="formData.phone"></el-input>
    </el-form-item>
    <el-form-item label="用户角色" prop="role_id" required>
      <el-radio-group v-model="formData.role_id">
        <el-radio 
          v-for="role in roles" 
          :key="role.role_id" 
          :label="role.role_id"
        >
          {{ role.role_name }}
        </el-radio>
      </el-radio-group>
    </el-form-item>
  </el-form>
  <template #footer>
    <el-button @click="dialogVisible = false">取消</el-button>
    <el-button type="primary" @click="submitForm">确定</el-button>
  </template>
  </el-dialog>

  <el-pagination 
    style="margin-top: 20px" 
    background 
    layout="prev, pager, next" 
    :total="total"
    :current-page="currentPage"
    @current-change="handlePageChange"
  />
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, computed } from 'vue'
import request from "@/api/request";
import { ElMessage, ElMessageBox } from "element-plus";
import api from '@/api/login.js'

const searchInput = ref('')
const tableData = reactive([])
const total = ref(0)
const currentPage = ref(1)

// 新增用户对话框状态
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formType = ref('add') // 'add' 或 'edit'
const formData = reactive({
  username: '',
  phone: '',
  email: '',
  password: '',
  role_id: null
})
const roles = ref([])

const passwordRules = computed(() => [
  { 
    required: formType.value === 'add',
    message: '请输入密码',
    trigger: 'blur'
  },
  { 
    min: 6, 
    message: '密码长度至少6位', 
    trigger: 'blur' 
  }
])

const formRules = reactive({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 30, message: '长度应在2-30个字符之间', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { 
      type: 'email',
      message: '请输入有效的邮箱地址',
      trigger: ['blur', 'change']
    }
  ],
  // 移除这里的静态赋值
  phone: [
    { pattern: /^1[3456789]\d{9}$/, message: '请输入有效的手机号码', trigger: 'blur' }
  ],
  role_id: [
    { required: true, message: '请选择用户角色', trigger: 'blur' }
  ]
})

const getStatusTagType = (status) => {
  const statusMap = {
    '0': 'info',    // 未登录
    '1': 'success', // 登录
    '2': 'danger'   // 禁用
  }
  return statusMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    '0': '未登录',
    '1': '登录',
    '2': '禁用'
  }
  return textMap[status] || '未知状态'
}

// 查询所有用户数据
const searchAll = async (showMessage=false,type='') => {
  try {
    const params = {
      keyword: searchInput.value,
      page: currentPage.value,
      pageSize: 10
    }
    
    const res = await api.searchAll(params)
    const resultArray = typeof res.result === 'string' ? JSON.parse(res.result) : res.result 
    if (res?.code === 200 && Array.isArray(resultArray)) {
      tableData.splice(0, tableData.length, ...resultArray)
      total.value = resultArray.length
      if(showMessage){
          if(type === 'refresh'){
            ElMessage.success('刷新成功')
          }else if(type ==='query'){
            if(total.value === 0){
              ElMessage.error('查询成功，无查询结果')
            }else{
              ElMessage.success(`查询成功,共${total.value}条数据`)
            }
          }
        }
    } else {
      // ElMessage.error('查询失败: 返回数据格式不正确')
    }
  } catch (error) {
    // console.error(error)
    // ElMessage.error('请求出错: ' + error.message)
  }
}



// 分页变化
const handlePageChange = (page) => {
  currentPage.value = page
  searchAll()
}

// 新增用户
const handleAdd = async () => {
  dialogTitle.value = '新增用户'
  formType.value = 'add'
  Object.assign(formData, {
    username: '',
    account: '',
    email: '',
    password: '',
    role_id: null
  })
  dialogVisible.value = true
  await nextTick()
  formRef.value?.clearValidate() // 重置验证状态
}

// 编辑用户
const handleEdit = async (row) => {
  dialogTitle.value = '编辑用户'
  formType.value = 'edit'
  Object.assign(formData, {
    ...row,
    password: ''
  })
  dialogVisible.value = true
  await nextTick()
  formRef.value?.clearValidate() // 重置验证状态
}

// 提交表单
const formRef = ref(null)

const submitForm = async () => {
  try {
    // 移除新增用户时手动检查密码是否为空的逻辑，由表单验证处理
    // 验证表单
    await formRef.value.validate()
    
    let res
    if (formType.value === 'add') {
      res = await api.addUser(formData)
    } else {
      res = await api.editUser(`/user/${formData.id}`, formData)
    }
    
    if (res.code === 200) {
      ElMessage.success(formType.value === 'add' ? '新增成功' : '更新成功')
      dialogVisible.value = false
      searchAll(true,'refresh')
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    if (error.name !== 'Error') { // 过滤掉验证错误
      console.error(error)
      ElMessage.error('请检查您的输入是否完整')
    }
  }
}

// 禁用用户
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要禁用该用户 ${row.username} 吗?`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await api.stopUser({id:`${row.id}`})
    if (res.code === 200) {
      ElMessage.success('禁用成功')
      searchAll(true,"refresh") // 刷新列表
    } else {
      // ElMessage.error(res.message || '禁用失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      // console.error(error)
      // ElMessage.error('禁用用户出错')
    }
  }
}

// 日期格式化函数
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  const year = date.getFullYear()
  const month = date.getMonth() + 1
  return `${year}年${month.toString().padStart(2, '0')}月`
}
const fetchRoles = async () => {
  try {
    const res = await api.getRoles()
    if (res?.code === 200) {
      roles.value = typeof res.result === 'string' ? JSON.parse(res.result) : res.result
    }
  } catch (error) {
    console.error('获取角色列表失败:', error)
  }
}

// 初始化加载数据
onMounted(() => {
  searchAll()
  fetchRoles() 
})

</script>
