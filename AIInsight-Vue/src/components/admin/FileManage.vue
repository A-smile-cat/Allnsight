<template>
    <div class="file-management">
      <!-- 标题和添加按钮 -->
      <div class="header">
        <h1>模型管理</h1>
        <div>
          <el-button type="primary" @click="handleUploadModel">上传模型</el-button>
          <el-button type="warning" @click="searchAll(true,'refresh')">状态刷新</el-button>
        </div>
      </div>

      <!-- 查询区域 -->
      <div class="query-section">
        <el-form :inline="true">

          <el-form-item label="模型名称">
            <el-input
              v-model="searchModelName"
              placeholder="请输入模型名称"
              clearable
            />
          </el-form-item>
          <el-form-item label="类型">
            <el-select
              v-model="searchType"
              placeholder="类型"
              clearable
              class="custom-select-width"
            >
            <el-option label="图像分类模型" value="classification" />
            <el-option label="图像分割模型" value="segmentation" />
            </el-select>
          </el-form-item>
          <el-form-item label="模型来源">
            <el-select
              v-model="searchStatus"
              placeholder="模型来源"
              clearable
              class="custom-select-width"
            >
              <el-option label="文件上传" value="upload" />
              <el-option label="模型训练" value="train" />
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
          <el-table-column prop="model_name" label="模型名称" />
          <el-table-column prop="model_des" label="模型描述" />
          <el-table-column prop="type" label="模型类型" >
            <template #default="{ row }">
              <el-tag :type="getTagType1(row.type)">
                {{ getTypeText(row.type) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="source" label="模型来源" >
            <template #default="{ row }">
              <el-tag :type="getTagType2(row.source)">
                {{ getStatusText(row.source) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="time" label="创建时间" />
          <el-table-column label="操作" width="400px">
            <template #default="{ row }">
              <el-button  type="success" @click="handleDownload(row)">下载</el-button>
              <el-button  type="warning" @click="handleEdit(row)">编辑</el-button>
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


        <!-- 上传模型对话框 -->
        <el-dialog
        v-model="uploaddialogVisible"
        title="上传模型"
        width="500px"
        :before-close="handleClose"
        >
        <el-alert
          title="上传要求"
          type="warning"
          :closable="false"
          show-icon
        >
          <p>请上传以.pkl,.pt,.pth,.h5,.model,.hdf5后缀结尾的模型文件，文件最大为200MB</p>
        </el-alert>
        <el-form :model="uploadForm" :rules="uploadRules" ref="uploadFormRef">
          <el-form-item label="模型名称" prop="modelName">
            <el-input
              v-model="uploadForm.modelName"
              placeholder="请输入模型名称"
            />
          </el-form-item>
          <el-form-item label="模型描述">
            <el-input
              v-model="uploadForm.modelDescription"
              type="textarea"
              :rows="4"
              placeholder="请输入模型描述（选填）"
              maxlength="500"
              show-word-limit
            />
          </el-form-item>
          <el-form-item label="模型类型" prop="modelType">
            <el-select
              v-model="uploadForm.modelType"
              placeholder="请选择模型类型"
            >
              <el-option label="图像分类模型" value="classification" />
              <el-option label="图像分割模型" value="segmentation" />
            </el-select>
          </el-form-item>
        </el-form>
        <el-upload
          class="upload-area"
          ref="uploadRef"
          drag
          action="#"
          :auto-upload="false"
          :on-change="handleFileChange"
          :accept="'.pkl,.pt,.pth,.h5,.model,.hdf5'"
          :limit="1"
          :on-exceed="handleUploadExceed"
          :on-progress="handleUploadProgress"
        >
          <el-icon class="el-icon--upload"><upload-filled /></el-icon>
          <div class="el-upload__text">
            拖拽文件到此处或 <em>点击上传</em>
          </div>
          <template #tip>
            <div class="el-upload__tip">请上传以.pkl,.pt,.pth,.h5,.model,.hdf5后缀结尾的模型文件</div>
          </template>
        </el-upload>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="cancelUpload">取消</el-button>
          <el-button type="primary" @click="confirmUpload" :loading="isUploading">
            上传
          </el-button>
        </span>
      </template>
        </el-dialog>

        <!-- 编辑模型对话框 -->
        <el-dialog
          v-model="editdialogVisible"
          :title="dialogTitle"
          width="500px"
          :before-close="handleEditDialogClose"
        >
          <el-form :model="taskForm" :rules="rules" ref="taskFormRef">
            <el-form-item label="模型名称" prop="model_name">
              <el-input
                v-model="taskForm.model_name"
                placeholder="请输入模型名称"
              />
            </el-form-item>
            <el-form-item label="模型描述" prop="model_des">
              <el-input
                v-model="taskForm.model_des"
                type="textarea"
                :rows="3"
                placeholder="请输入模型描述（选填）"
                maxlength="500"
                show-word-limit
              />
            </el-form-item>
            <el-form-item label="模型类型" prop="type">
              <el-select
                v-model="taskForm.type"
                placeholder="请选择模型类型"
              >
                <el-option label="图像分类模型" value="classification" />
                <el-option label="图像分割模型" value="segmentation" />
              </el-select>
            </el-form-item>
          </el-form>
          <template #footer>
            <span class="dialog-footer">
              <el-button @click="editdialogVisible = false">取消</el-button>
              <el-button type="primary" @click="handleSubmit">确定</el-button>
            </span>
          </template>
        </el-dialog>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted,reactive, nextTick } from 'vue'
  import { ElMessage, ElMessageBox } from "element-plus";
  import { UploadFilled } from '@element-plus/icons-vue';
  import api from '@/api/login.js'

  const storedUser = localStorage.getItem('user');
  const userInfo = storedUser ? JSON.parse(storedUser) : {};
  const userId = userInfo.id; // 假设 user_id 的键名为 id
  const dialogTitle = ref('')
  const formType = ref('')
  const currentRow = ref(null)
  const searchTaskName = ref('')
  const searchModelName = ref('')
  const searchType = ref('')
  const searchStatus = ref('')
  const editdialogVisible = ref(false) // 对话框可见性
  const uploaddialogVisible = ref(false) // 对话框可见性
  const isUploading = ref(false);
  const selectedFile = ref(null);
  const taskFormRef = ref(null) // 表单引用
  const uploadRef = ref(null) // 上传组件引用
  const taskForm = reactive({})
  // 表单验证规则
  const rules = {
    model_name: [
      { required: true, message: '请输入名称', trigger: 'blur' },
      { min: 2, max: 30, message: '名称长度在2到30个字符之间', trigger: 'blur' }
    ],
    description: [
      { max: 500, message: '描述最多500个字符', trigger: 'blur' }
    ],
    type: [
      { required: true, message: '请选择类型', trigger: 'change' }
    ],
  }
  const uploadFormRef = ref(null)
  const uploadForm = reactive({
    modelName: '',
    modelDescription: '',
    modelType: ''
  })
  
  const uploadRules = {
    modelName: [
      { required: true, message: '请输入模型名称', trigger: 'blur' },
      { min: 2, max: 30, message: '名称长度在2到30个字符之间', trigger: 'blur' }
    ],
    modelType: [
      { required: true, message: '请选择模型类型', trigger: 'change' }
    ]
  }
  
  // 文件列表
  const fileList = reactive([])
  
  // 分页相关
  const currentPage = ref(1)
  const pageSize = ref(10)
  const total = ref(1)

  const statusMap = {
    upload: { text: '文件上传', type: 'primary' },
    train: { text: '模型训练', type: 'success' },
  };

  const typeMap = {
    classification: { text: '图像分类模型', type: 'primary' },
    segmentation: { text: '图像分割模型', type: 'success' }
  };

  // 获取标签类型的方法
  const getTagType1 = (type) => {
    return typeMap[type]?.type || 'info';
  };

  const getTagType2 = (status) => {
    return statusMap[status]?.type || 'info';
  };

  // 获取状态文本的方法
  const getStatusText = (status) => {
    return statusMap[status]?.text || '未知状态';
  };

  const getTypeText = (status) => {
    return typeMap[status]?.text || '未知状态';
  };

  // 初始化加载数据
  onMounted(() => {
    searchAll()
  })
  
  // 查询与获取数据
  const searchAll = async(showMessage=false,type = '') => {
    try {
      const params = {
        modelName: searchModelName.value,
        type: searchType.value,
        status: searchStatus.value,
        page: currentPage.value,
        pageSize: pageSize.value,
        user_id: userId
      }
      
      const res = await api.searchModel(params)
      const resultArray = typeof res.result === 'string' ? JSON.parse(res.result) : res.result 
      if (res?.code === 200 && Array.isArray(resultArray)) {
        fileList.splice(0, fileList.length, ...resultArray)
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
  
  // 重置
  const handleReset = () => {
    searchTaskName.value = ''
    searchModelName.value = ''
    searchType.value = ''
    searchStatus.value = ''
    currentPage.value = 1
    searchAll()
  }
  

  // 提交表单
  const handleSubmit = () => {
    taskFormRef.value.validate(async (valid) => {
      if (valid) {
        let res
        res = await  api.editModel(`/model/${taskForm.model_id}`, taskForm)
        
        if (res.code === 200) {
          ElMessage.success('更新成功')
          editdialogVisible.value = false
          searchAll()
        } else {
          // ElMessage.error(res.message || '操作失败')
        }
        editdialogVisible.value = false
      } else {
        ElMessage.error('请填写必填字段')
        return false
      }
    })
  }
  
  const handleDownload = async(row) => {
    try {
      // 调用后端下载接口
      const response = await api.downloadModel(userId, row.model_id, {
        responseType: 'blob' // 重要：告诉axios我们要接收二进制数据
      });
      
      // 创建下载链接
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      
      // 从响应头获取文件名，或者使用默认名
      let fileName;
      if (response.headers && response.headers['content-disposition']) {
        fileName = response.headers['content-disposition'].split('filename=')[1];
      } else {
        fileName = `file_${row.model_id}`;
      }
      
      link.setAttribute('download', fileName);
      document.body.appendChild(link);
      link.click();
      
      // 清理
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
      
      ElMessage.success('下载开始');
    } catch (error) {
      console.error('下载失败:', error);
      ElMessage.error('下载失败: ' + error.message);
    }
  }
  
  const handleEdit = async(row) => {
    dialogTitle.value = '编辑模型'
    Object.assign(taskForm, {
      ...row,
      user_id: userId
    })
    editdialogVisible.value = true
    formType.value = 'edit'
    await nextTick()
    taskFormRef.value?.clearValidate()
  }
  
  const handleDelete = (row) => {
    ElMessageBox.confirm('确定删除该模型吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    .then(async() => {
      const res = await api.deleteModel(userId, row.model_id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        searchAll()
      } else {
        ElMessage.error(res.message || '删除失败')
      }
    })
  }

  // 打开弹窗
  const openModal = () => {
    uploaddialogVisible.value = true;
  };

  const handleEditDialogClose = (done) => {
    done();
  }
  // 关闭弹窗
  const handleClose = (done) => {
    if (isUploading.value) {
      return;
    }
    selectedFile.value = null;
    uploadRef.value.clearFiles();
    done();
  };

  // 文件选择处理
  const handleFileChange = (file) => {
    const validExtensions = ['.zip', '.pt', '.pth'];
    if (!validExtensions.some(ext => file.name.endsWith(ext))) {
      ElMessage.error('请上传有效的模型文件');
      return false;
    }
    selectedFile.value = file;
  };

  const handleUploadExceed = (files, fileList) => {
    fileList.length = 0;
    fileList.push(files[0]);
    selectedFile.value = files[0];
    ElMessage.warning('新文件已选择，已替换之前的文件');
  }

  const handleUploadProgress = (event, file, fileList) => {
    ElMessage.warning("文件正在上传...，请不要关闭窗口")
  }

  
  // 修改确认上传函数，添加表单验证
  const confirmUpload = async () => {
    uploadFormRef.value.validate(async (valid) => {
      if (valid) {
        const data = currentRow.value;
        if (!selectedFile.value) {
          ElMessage.warning('请先选择文件');
          return;
        }
        
        isUploading.value = true;
        const extraData = {
          user_id: userId,
          ...uploadForm
        }
        try {
          let res;
          res = await api.uploadModel('/uploadModel', selectedFile.value.raw, extraData);
          if(res.code === 200){
            ElMessage.success(`文件 ${selectedFile.value.name} 上传成功!`);
          }else{
            ElMessage.error('文件上传失败');
          }
          uploadRef.value.clearFiles();
          selectedFile.value = null;
          uploaddialogVisible.value = false;
          searchAll();
        } catch (error) {
          ElMessage.error('上传失败: ' + error.message);
        } finally {
          isUploading.value = false;
        }
      } else {
        ElMessage.error('请填写必填字段');
        return false;
      }
    })
  };

  // 取消上传
  const cancelUpload = () => {
    selectedFile.value = null;
    uploadRef.value.clearFiles();
    uploaddialogVisible.value = false;
  };

  // 分页变化
  const handleSizeChange = (val) => {
    pageSize.value = val
    searchAll()
  }
  
  const handleCurrentChange = (val) => {
    currentPage.value = val
    searchAll()
  }
  
  // 上传模型函数
  const handleUploadModel = () => {
    uploaddialogVisible.value = true;
    currentRow.value = null; // 清空当前行数据，确保是新的上传操作
    selectedFile.value = null; // 清空已选文件
    uploadRef.value.clearFiles(); // 清空上传组件的文件列表
  };
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