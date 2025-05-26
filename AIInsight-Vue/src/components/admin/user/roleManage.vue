<template>
    <div class="role-management">
      <!-- 标题和添加按钮 -->
      <div class="header">
        <h1>角色管理</h1>
        <div>
          <el-button type="primary" @click="handleAddRole">新增角色</el-button>
          <el-button type="warning" @click="refresh">状态刷新</el-button>
        </div>
      </div>
  
      <!-- 角色表格 -->
      <div class="table-section">
        <el-table :data="roleList" border style="width: 100%">
          <el-table-column prop="role_name" label="角色名" />
          <el-table-column prop="description" label="角色描述" />
          <el-table-column label="操作" width="300px">
            <template #default="{ row }">
              <el-button type="primary" @click="handleView(row)">查看</el-button>
              <el-button type="warning" @click="handleEdit(row)">编辑</el-button>
              <el-button type="danger" @click="handleDelete(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
  
      <!-- 分页 -->
      <div class="pagination-section">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 30, 50]"
          :small="false"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
  
      <!-- 新增/编辑角色对话框 -->
      <el-dialog
        v-model="roleDialogVisible"
        :title="dialogTitle"
        width="600px"
        :before-close="handleDialogClose"
      >
        <el-form :model="roleForm" :rules="rules" ref="roleFormRef">
          <el-form-item label="角色名称" prop="role_name">
            <el-input
              v-model="roleForm.role_name"
              placeholder="请输入角色名称"
              :disabled="isViewMode"
            />
          </el-form-item>
          <el-form-item label="角色描述" prop="description">
            <el-input
              v-model="roleForm.description"
              type="textarea"
              :rows="3"
              placeholder="请输入角色描述"
              :disabled="isViewMode"
            />
          </el-form-item>
          <el-form-item label="权限设置" prop="menus" :disabled="isViewMode" >
            <el-tree
              ref="menuTreeRef"
              :data="menuTreeData"
              show-checkbox
              node-key="id"
              :props="defaultProps"
              :default-expand-all="true"
              :check-strictly="isCheckStictly"
              :disabled="isViewMode"
              style="width: 800px;"
            />
          </el-form-item>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="roleDialogVisible = false">取消</el-button>
            <el-button
              type="primary"
              @click="handleSubmit"
              v-if="!isViewMode"
            >
              确定
            </el-button>
          </span>
        </template>
      </el-dialog>
    </div>
  </template>
  
  <script setup>
  import { ref, reactive, onMounted, nextTick } from 'vue'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import api from '@/api/login.js'
  
  // 角色列表数据
  const roleList = reactive([])
  const menuTreeData = ref([])
  const allMenus = reactive([])
  const storedUser = localStorage.getItem('user');
  const userInfo = storedUser ? JSON.parse(storedUser) : {};
  const userId = userInfo.id; // 假设 user_id 的键名为 id
  
  // 表单相关
  const roleForm = reactive({
    role_id: '',
    role_name: '',
    description: '',
    menus: []
  })
  const roleFormRef = ref(null)
  const menuTreeRef = ref(null)
  
  // 对话框控制
  const roleDialogVisible = ref(false)
  const dialogTitle = ref('')
  const isViewMode = ref(false)
  const isCheckStictly = ref(true)
  
  // 分页相关
  const currentPage = ref(1)
  const pageSize = ref(10)
  const total = ref(0)
  
  // 树形控件配置
  const defaultProps = {
    children: 'children',
    label: 'name_des'
  }
  
  // 表单验证规则
  const rules = {
    role_name: [
      { required: true, message: '请输入角色名称', trigger: 'blur' },
      { min: 2, max: 30, message: '长度在2到30个字符之间', trigger: 'blur' }
    ],
    description: [
      { max: 500, message: '描述最多500个字符', trigger: 'blur' }
    ]
  }
  
  // 初始化加载数据
  onMounted(() => {
    searchAll()
    // loadAllMenus()
  })

  const refresh = () => {
    searchAll()
    loadAllMenus()
    ElMessage.success("状态刷新成功")
  }
  
  // 加载所有菜单权限
  const loadAllMenus = async () => {
    try {
      const res = await api.getAllMenus()
      const resultArray = typeof res.result === 'string' ? JSON.parse(res.result) : res.result 
      if (res?.code === 200 && Array.isArray(resultArray)) {
        allMenus.splice(0, allMenus.length,...resultArray)
        buildMenuTree()
      }
    } catch (error) {
    //   console.error('加载菜单失败:', error)
    }
  }
  
  // 构建菜单树形结构
  const buildMenuTree = () => {
    // 获取顶级菜单 (parent_id为0或1)
    const topMenus = allMenus.filter(
      menu => (menu.parent_id === 0 || menu.parent_id === 1) && menu.id !== 1
    )
    
    // 构建树形结构
    menuTreeData.value = topMenus.map(menu => {
      const children = allMenus.filter(
        child => child.parent_id === menu.id && child.parent_id !== 1 && child.parent_id!== 0
      )
      return {
        ...menu,
        children: children.length > 0 ? children : undefined
      }
    })
  }
  
  // 查询角色列表
  const searchAll = async () => {
    try {
      const params = {
        page: currentPage.value,
        pageSize: pageSize.value
      }

      const res = await api.getRoles(params)
      const resultArray = typeof res.result === 'string' ? JSON.parse(res.result) : res.result 
        if (res?.code === 200 && Array.isArray(resultArray)) {
            roleList.splice(0, roleList.length, ...resultArray)
            total.value = resultArray.length
            // ElMessage.success(res.message || '查询成功')
        }else{

        }
    } catch (error) {
    //   console.error('查询失败:', error)
    }
  }
  
  // const shouldCallCheckChange = computed(() => {
  //     return dialogTitle.value === '新增角色';
  // });
  // 新增角色
  const handleAddRole = () => {
    dialogTitle.value = '新增角色'
    isViewMode.value = false
    isCheckStictly.value = false
    resetForm()
    loadAllMenus()
    roleDialogVisible.value = true
  }
  
  // 查看角色
  const handleView = async (row) => {
    dialogTitle.value = '查看角色权限'
    isViewMode.value = true
    isCheckStictly.value = true
    Object.assign(roleForm,{
        ...row
    })
    await loadAllMenus()
    roleDialogVisible.value = true
    await nextTick()
    await loadRoleDetail(row.role_id)
  }
  
  // 编辑角色
  const handleEdit = async (row) => {
    dialogTitle.value = '编辑角色'
    isViewMode.value = false
    isCheckStictly.value = true
    Object.assign(roleForm,{
        ...row
    })
    await loadAllMenus()
    roleDialogVisible.value = true
    await nextTick()
    await loadRoleDetail(row.role_id)
  }
  
  // 加载角色详情
  const loadRoleDetail = async (roleId) => {
    try {
      const params = {
        role_id: roleId
      }
      const res = await api.getRoleMenu(params)
      const resultArray = typeof res.result === 'string' ? JSON.parse(res.result) : res.result 
      if (res?.code === 200 && Array.isArray(resultArray)) {
        await nextTick()
        if (menuTreeRef.value && resultArray) {
          menuTreeRef.value.setCheckedKeys(resultArray)
        }else{
          // console.log('menuTreeRef 不存在或结果数组为空');
        }
        // nextTick(() => {
        //   if (menuTreeRef.value && resultArray) {
        //     menuTreeRef.value.setCheckedKeys(resultArray)
        //   }
        // })
      }
    //   if (res.code === 200) {
    //     // 设置选中的菜单
    //     nextTick(() => {
    //       if (menuTreeRef.value && res.result.menus) {
    //         menuTreeRef.value.setCheckedKeys(res.result.menus)
    //       }
    //     })
    //   }
    } catch (error) {
      console.error('加载角色详情失败:', error)
    }
  }
  
  // 删除角色
  const handleDelete = (row) => {
    ElMessageBox.confirm('确定删除该角色吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(async () => {
      try {
        const res = await api.deleteRole(row.role_id)
        if (res.code === 200) {
          ElMessage.success('删除成功')
          searchAll()
        }
      } catch (error) {
        // console.error('删除失败:', error)
      }
    })
  }
  
  // 提交表单
  const handleSubmit = async () => {
    try {
      await roleFormRef.value.validate()
      
      // 获取选中的菜单ID
      const checkedKeys = menuTreeRef.value.getCheckedKeys()
      const halfCheckedKeys = menuTreeRef.value.getHalfCheckedKeys()
      const selectedMenus = [...checkedKeys, ...halfCheckedKeys]
      
      let f = 1
      // 验证至少选择了一个权限或完全不选
      if (selectedMenus.length === 0) {
        f = 0
        await ElMessageBox.confirm(
          '您未选择任何权限，该角色将无法访问系统，确定继续吗？',
          '提示',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
      }
      
      for (const key of selectedMenus) {
        const menu = allMenus.find(m => m.id === key);
        if (menu && menu.children && menu.children.length > 0) {
          const hasChildSelected = menu.children.some(child => selectedMenus.includes(child.id));
          if (!hasChildSelected) {
            ElMessage.error(`选择了父级节点 ${menu.name_des}，必须选择至少一个子级节点`);
            return;
          }
        }
      }

      // 验证个人中心权限
      const personalCenterId = 13 // 个人中心的id
      if (selectedMenus.length > 0 && !selectedMenus.includes(personalCenterId)) {
        ElMessage.error('必须勾选个人中心权限')
        return
      }

      if(f===1){
        const requiredMenus = 1
        selectedMenus.push(requiredMenus)
      }

      // 准备提交数据
      const submitData = {
        ...roleForm,
        menus: selectedMenus
      }
      
      // 调用API
      let res
      if (submitData.role_id) {
        res = await api.updateRole(submitData)
      } else {
        res = await api.addRole(submitData)
      }
      
      if (res.code === 200) {
        ElMessage.success(submitData.role_id ? '更新成功' : '新增成功')
        roleDialogVisible.value = false
        searchAll()
      }
    } catch (error) {
      console.error('提交失败:', error)
    }
  }
  
  // 重置表单
  const resetForm = () => {
    roleForm.role_id = ''
    roleForm.role_name = ''
    roleForm.description = ''
    nextTick(() => {
      if (menuTreeRef.value) {
        menuTreeRef.value.setCheckedKeys([])
      }
    })
  }
  
  // 关闭对话框
  const handleDialogClose = (done) => {
    done()
  }
  
  // 分页变化
  const handleSizeChange = (val) => {
    pageSize.value = val
    searchAll()
  }
  
  const handleCurrentChange = (val) => {
    currentPage.value = val
    searchAll()
  }
  </script>
  
  <style scoped>
  .role-management {
    padding: 20px;
    background-color: white;
  }
  
  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
  }
  
  .table-section {
    margin-bottom: 20px;
  }
  
  .pagination-section {
    display: flex;
    justify-content: flex-end;
  }
  
  .el-tree {
    max-height: 400px;
    overflow-y: auto;
  }
  </style>