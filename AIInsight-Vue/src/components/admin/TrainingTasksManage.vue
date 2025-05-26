<template>
    <div class="file-management">
      <!-- 标题和添加按钮 -->
      <div class="header">
        <h1>模型训练</h1>
        <div>
          <el-button type="primary" @click="handleAddTask">新建训练任务</el-button>
          <el-button type="warning" @click="refreshStatus">状态刷新</el-button>
        </div>
      </div>

      <!-- 查询区域 -->
      <div class="query-section">
        <el-form :inline="true">
          <el-form-item label="任务名称">
            <el-input
              v-model="searchTaskName"
              placeholder="请输入任务名称"
              clearable
            />
          </el-form-item>
          <el-form-item label="任务类型">
            <el-select
              v-model="searchType"
              placeholder="任务类型"
              clearable
              class="custom-select-width"
            >
              <el-option label="图像分类训练任务" value="classification" />
              <el-option label="图像分割训练任务" value="segmentation" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select
              v-model="searchStatus"
              placeholder="状态"
              clearable
              class="custom-select-width"
            >
              <el-option label="排队中" value="queued" />
              <el-option label="训练中" value="training" />
              <el-option label="已完成" value="completed" />
              <el-option label="失败" value="error" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="searchAll(true,'query')">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 文件表格 -->
      <div class="table-section">
        <el-table :data="fileList" border style="width: 100%">
          <el-table-column prop="taskName" label="任务名称" width="90px"/>
          <el-table-column prop="modelName" label="保存的模型名称" width="140px"/>
          <!-- <el-table-column prop="datasetName" label="使用的数据集" width="90px"/>
          <el-table-column prop="description" label="任务描述" width="90px"/> -->
          <el-table-column prop="type" label="任务类型" width="150px">
            <template #default="{ row }">
              <el-tag :type="getTagType2(row.type)">
                {{ getTypeText2(row.type) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="epochs" label="训练轮数" width="90px"/>
          <el-table-column prop="batchSize" label="批次大小" width="90px"/>
          <el-table-column prop="learningRate" label="学习率" width="80px"/>
          <el-table-column prop="accuracy" label="准确率" width="80px"/>
          <el-table-column prop="loss" label="损失率" width="80px"/>
          <el-table-column prop="status" label="状态" width="110px" >
            <template #default="{ row }">
              <el-tag :type="getTagType(row.status)">
                {{ getStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="140px"/>
          <el-table-column label="操作" width="400px">
            <template #default="{ row }">
              <el-button  type="warning" @click="handleEdit(row)">查看或编辑</el-button>
              <el-button  type="danger" @click="handleDelete(row)">删除</el-button>
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

        <!-- 添加/编辑 任务对话框 -->
        <el-dialog
        v-model="editdialogVisible"
        :title="dialogTitle"
        width="500px"
        :before-close="handleEditDialogClose"
        >
        <el-form :model="taskForm" :rules="rules" ref="taskFormRef">
            <el-form-item label="任务名称" prop="taskName">
            <el-input
                v-model="taskForm.taskName"
                placeholder="请输入训练任务名称"
                autofocus
                :disabled="isFormDisabled"
            />
            </el-form-item>
            <el-form-item label="模型名称" prop="modelName" :disabled="isEdit">
            <el-input
                v-model="taskForm.modelName"
                placeholder="请输入训练完成后保存的模型名称"
                :disabled="isEdit"
            />
            </el-form-item>
            <el-form-item label="任务描述" prop="description" :disabled="isFormDisabled">
            <el-input
                v-model="taskForm.description"
                type="textarea"
                :rows="2"
                placeholder="请输入任务描述（选填）"
                maxlength="500"
                show-word-limit
                :disabled="isFormDisabled"
            />
            </el-form-item>
            <el-form-item label="数据集选择" prop="datasetId" :disabled="isFormDisabled">
            <el-select
                v-model="taskForm.datasetId"
                placeholder="请选择使用的数据集"
                :disabled="isFormDisabled"
            >
            <el-option v-for="dataset in datasetList" :key="dataset.dataset_id" :label="dataset.name" :value="dataset.dataset_id" />
            </el-select>
            </el-form-item>
            <el-form-item label="任务类型" prop="type" :disabled="isEdit">
            <el-select
                v-model="taskForm.type"
                placeholder="请选择任务类型"
                :disabled="isEdit"
            >
            <el-option label="图像分类训练任务" value="classification" />
            <el-option label="图像分割训练任务" value="segmentation" />
            </el-select>
            </el-form-item>
            <el-form-item label="训练轮数" prop="epochs" :disabled="isFormDisabled">
            <el-input
                v-model="taskForm.epochs"
                type="number"
                placeholder="请输入训练轮数"
                :disabled="isFormDisabled"
            />
            </el-form-item>
            <el-form-item label="批次大小" prop="batchSize" :disabled="isFormDisabled">
            <el-input
                v-model="taskForm.batchSize"
                type="number"
                placeholder="请输入批次大小"
                :disabled="isFormDisabled"
            />
            </el-form-item>
            <el-form-item label="学习率" prop="learningRate" :disabled="isFormDisabled">
            <el-input
                v-model="taskForm.learningRate"
                type="number"
                step="any"
                placeholder="请输入学习率"
                :disabled="isFormDisabled"
            />
            </el-form-item>
        </el-form>
        <template #footer>
            <span class="dialog-footer">
            <el-button @click="editdialogVisible = false">取消</el-button>
            <el-button type="primary" @click="handleSubmit"> 确定 </el-button>
            </span>
        </template>
        </el-dialog>

    </div>
  </template>
  
  <script setup>
  import { ref, onMounted,reactive, nextTick } from 'vue'
  import { ElMessage, ElMessageBox } from "element-plus";
  import api from '@/api/login.js'

  const storedUser = localStorage.getItem('user');
  const userInfo = storedUser ? JSON.parse(storedUser) : {};
  const userId = userInfo.id; // 假设 user_id 的键名为 id
  const dialogTitle = ref('')
  const formType = ref('')
  const searchTaskName = ref('')
  const searchModelName = ref('')
  const searchType = ref('')
  const searchStatus = ref('')
  const editdialogVisible = ref(false) // 对话框可见性
  const taskFormRef = ref(null) // 表单引用
  const datasetList = reactive([])
  const isFormDisabled = ref(false)
  const isEdit = ref(false)
  const taskForm = reactive({ // 任务表单数据
    taskName: '',
    modelName: '',
    description: '',
    type: '',
    epochs: '10',
    batchSize: '16',
    learningRate: '0.005',
    datasetId: '',
    user_id: userId
  })
  // 表单验证规则
  const rules = {
    taskName: [
      { required: true, message: '请输入任务名称', trigger: 'blur' },
      { min: 2, max: 30, message: '名称长度在2到30个字符之间', trigger: 'blur' }
    ],
    modelName: [
      { required: true, message: '请输入要保存的模型名称', trigger: 'blur' },
      { min: 2, max: 30, message: '名称长度在2到30个字符之间', trigger: 'blur' }
    ],
    description: [
      { max: 500, message: '描述最多500个字符', trigger: 'blur' }
    ],
    datasetId: [
      { required: true, message: '请选择数据集', trigger: 'change' }
    ],
    type: [
      { required: true, message: '请选择类型', trigger: 'change' }
    ],
    epochs: [
      { required: true, message: '请输入训练轮数', trigger: 'blur' },
      { validator: (rule, value, callback) => {
      if (!isNaN(value) && value !== '') {
        callback();
      } else {
        callback(new Error('请输入有效的数字'));
      }
    },
    trigger: 'blur' }
    ],
    batchSize: [
      { required: true, message: '请输入批次大小', trigger: 'blur' },
      { validator: (rule, value, callback) => {
      if (!isNaN(value) && value !== '') {
        callback();
      } else {
        callback(new Error('请输入有效的数字'));
      }
    },
    trigger: 'blur' }
    ],
    learningRate: [
      { required: true, message: '请输入学习率', trigger: 'blur' },
      { validator: (rule, value, callback) => {
      if (!isNaN(value) && value !== '') {
        callback();
      } else {
        callback(new Error('请输入有效的数字'));
      }
    },
    trigger: 'blur' }
    ]
  }
  
  // 文件列表
  const fileList = reactive([])
  
  // 分页相关
  const currentPage = ref(1)
  const pageSize = ref(10)
  const total = ref(1)

  const statusMap = {
    queued: { text: '排队中', type: 'info' },
    training: { text: '训练中', type: 'primary' },
    completed: { text: '已完成', type: 'success' },
    error: { text: '失败', type: 'danger' }
  };

  // 获取标签类型的方法
  const getTagType = (status) => {
    return statusMap[status]?.type || 'info';
  };

  // 获取状态文本的方法
  const getStatusText = (status) => {
    return statusMap[status]?.text || '未知状态';
  };

  const getTagType2 = (type) => {
    return type === 'classification' ? 'primary' : 'success';
  };

  const getTypeText2 = (type) => {
    return type === 'classification'? '图像分类训练任务' :'图像分割训练任务';
  }

  // 初始化加载数据
  onMounted(() => {
    searchAll()
    fetchDatasets()
  })
  const refreshStatus = () => {
    searchAll(true,'refresh')
    fetchDatasets()
  }

  // 获取数据集的方法
const fetchDatasets = async () => {
  try {
    const res = await api.searchDataset({ user_id: userId }); // 假设存在 getDatasets 接口
    const resultArray = typeof res.result === 'string' ? JSON.parse(res.result) : res.result 
    if (res?.code === 200 && Array.isArray(resultArray)) {
      datasetList.splice(0, datasetList.length, ...resultArray);
    }
  } catch (error) {
    // console.error('获取数据集失败:', error);
    ElMessage.error('获取数据集失败: ' + error.message);
  }
};
  
  // 查询与获取数据
  const searchAll = async(showMessage=false,type='') => {
    try {
      const params = {
        taskName: searchTaskName.value,
        type: searchType.value,
        status: searchStatus.value,
        page: currentPage.value,
        pageSize: pageSize.value,
        user_id: userId
      }
      
      const res = await api.searchTask(params)
      const resultArray = typeof res.result === 'string' ? JSON.parse(res.result) : res.result 
      const transformedArray = resultArray.map(item => {
        return{
          ...item,
          taskName: item.task_name,
          type: item.task_type,
          batchSize: item.batch_size,
          learningRate: item.learning_rate,
          status: item.status,
          createTime: item.time
        }
      })
      
      
      if (res?.code === 200 && Array.isArray(transformedArray)) {
        fileList.splice(0, fileList.length, ...transformedArray)
        total.value = transformedArray.length
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
  
  // 重置
  const handleReset = () => {
    searchTaskName.value = ''
    searchModelName.value = ''
    searchType.value = ''
    searchStatus.value = ''
    currentPage.value = 1
    searchAll()
  }
  
  // 新建训练任务
  const handleAddTask = () => {
    Object.keys(taskForm).forEach(key => {
      if (key !== 'user_id') {
        taskForm[key] = ''
      }
    })
    taskForm.epochs = '10';
    taskForm.batchSize = '16';
    taskForm.learningRate = '0.005';
    dialogTitle.value = '新建训练任务'
    editdialogVisible.value = true
    formType.value = 'add'
  }

  // 提交表单
  const handleSubmit = () => {
    taskFormRef.value.validate(async (valid) => {
      if (valid) {
        let res
        const params ={
          task_name: taskForm.taskName,
          modelName: taskForm.modelName,
          task_des: taskForm.description,
          task_type: taskForm.type,
          epochs: taskForm.epochs,
          batch_size: taskForm.batchSize,
          learning_rate: taskForm.learningRate,
          dataset_id: taskForm.datasetId,
          user_id: userId
        }
        if (formType.value === 'add') {
          res = await api.addTask(params)
        } else {
          res = await  api.editTask(`/task/${taskForm.task_id}`, params)
        }
        
        if (res.code === 200) {
          ElMessage.success(formType.value === 'add' ? '新增成功' : '更新成功')
          editdialogVisible.value = false
          searchAll()
        } else {
          ElMessage.error(res.message || '操作失败')
        }
        editdialogVisible.value = false
      } else {
        ElMessage.error('请填写必填字段')
        return false
      }
    })
  }
  
  
  const handleEdit = async(row) => {
    dialogTitle.value = '查看或编辑任务'
    Object.assign(taskForm, {
      ...row,
      taskName: row.task_name,
      description: row.task_des,
      type: row.task_type,
      epochs: row.epochs,
      batchSize: row.batch_size,
      learningRate: row.learning_rate,
      datasetId: row.dataset_id,
      user_id: userId
    })
    isFormDisabled.value = row.status === 'training' || row.status === 'completed'
    isEdit.value = true
    editdialogVisible.value = true
    formType.value = 'edit'
    await nextTick()
    taskFormRef.value?.clearValidate()
  }
  
  const handleDelete = (row) => {
    ElMessageBox.confirm('确定删除该任务吗？删除该任务会同时删除该任务对应的训练好的模型', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    .then(async() => {
      const res = await api.deleteTask(userId, row.task_id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        searchAll()
      } else {
        ElMessage.error(res.message || '删除失败')
      }
    })
  }



  const handleEditDialogClose = (done) => {
    done();
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
  .file-management {
    padding: 0px;
    background-color: white;
  }
  
  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
  }
  
  .query-section {
    margin-bottom: 20px;
    padding: 15px;
    background-color: #f5f7fa;
    border-radius: 4px;
  }
  
  .table-section {
    margin-bottom: 20px;
  }
  
  .pagination-section {
    display: flex;
    justify-content: flex-end;
  }
  .custom-select-width {
    width: 200px; /* 可以根据实际需求调整宽度 */
  }
  </style>