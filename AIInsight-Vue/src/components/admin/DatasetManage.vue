<template>
    <div class="dataset-management">
      <!-- 标题和添加按钮 -->
      <div class="header">
        <h1>数据集管理</h1>
        <div>
          <el-button type="primary" @click="handleAddDataset">添加数据集</el-button>
          <el-button type="warning" @click="searchAll(true,'refresh')">状态刷新</el-button>
        </div>
      </div>

  
      <!-- 查询区域 -->
      <div class="query-section">
        <el-form :inline="true">
          <el-form-item label="数据集名称">
            <el-input
              v-model="searchInput"
              placeholder="请输入数据集名称"
              clearable
            />
          </el-form-item>
          <el-form-item label="验证状态">
            <el-select
              v-model="searchStatus"
              placeholder="验证状态"
              clearable
              class="custom-select-width"
            >
              <el-option label="未上传文件" value="new" />
              <el-option label="文件上传中" value="uploading" />
              <el-option label="文件上传完成" value="uploaded" />
              <el-option label="文件上传失败" value="upload_failed" />
              <el-option label="训练中" value="training" />
              <el-option label="训练完成" value="trained" />
              <el-option label="解析错误" value="error" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="searchAll(true,'query')">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
  
      <!-- 数据集表格 -->
      <div class="table-section">
        <el-table :data="datasetList" border style="width: 100%">
          <el-table-column prop="name" label="数据集名称" />
          <el-table-column prop="description" label="描述" />
          <el-table-column prop="status" label="当前状态" >
            <template #default="{ row }">
              <el-tag :type="getTagType(row.status)">
                {{ getStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="username" label="创建人" />
          <el-table-column prop="time" label="创建时间" />
          <el-table-column label="操作" width="400px">
            <template #default="{ row }">
              <el-button  type="primary" @click="handleUpload(row)">上传</el-button>
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

        <!-- 添加/编辑 数据集对话框 -->
        <el-dialog
        v-model="editdialogVisible"
        :title="dialogTitle"
        width="500px"
        :before-close="handleEditDialogClose"
        >
        <el-form :model="datasetForm" :rules="rules" ref="datasetFormRef">
            <el-form-item label="数据集名称" prop="name">
            <el-input
                v-model="datasetForm.name"
                placeholder="请输入数据集名称"
                autofocus
            />
            </el-form-item>
            <el-form-item label="数据集描述" prop="description">
            <el-input
                v-model="datasetForm.description"
                type="textarea"
                :rows="4"
                placeholder="请输入数据集描述（选填）"
                maxlength="500"
                show-word-limit
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

        <!-- 上传文件对话框 -->
        <el-dialog
        v-model="uploaddialogVisible"
        title="上传数据集"
        width="500px"
        :before-close="handleClose"
        >
        <el-alert
          title="上传要求"
          type="warning"
          :closable="false"
          show-icon
        >
          <p>请上传ZIP格式的数据集文件</p>
          <p>2D数据集需包含train/images、train/labels、val/images、val/labels目录</p>
        </el-alert>

      <el-upload
        class="upload-area"
        ref="uploadRef"
        drag
        action="#"
        :auto-upload="false"
        :on-change="handleFileChange"
        :accept="'.zip'"
        :limit="1"
        :on-exceed="handleUploadExceed"
        :on-progress="handleUploadProgress"
      >
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">
          拖拽文件到此处或 <em>点击上传</em>
        </div>
        <template #tip>
          <div class="el-upload__tip">请上传ZIP格式的数据集文件</div>
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
  const searchInput = ref([])
  const searchStatus = ref([])
  const editdialogVisible = ref(false) // 对话框可见性
  const uploaddialogVisible = ref(false) // 对话框可见性
  const isUploading = ref(false);
  const selectedFile = ref(null);
  const datasetFormRef = ref(null) // 表单引用
  const uploadRef = ref(null) // 上传组件引用
  const datasetForm = reactive({ // 数据集表单数据
    name: '',
    description: '',
    user_id: userId
  })
  // 表单验证规则
  const rules = {
    name: [
      { required: true, message: '请输入数据集名称', trigger: 'blur' },
      { min: 2, max: 30, message: '名称长度在2到30个字符之间', trigger: 'blur' }
    ],
    description: [
      { max: 500, message: '描述最多500个字符', trigger: 'blur' }
    ]
  }
  
  // 数据集列表
  const datasetList = reactive([])
  
  // 分页相关
  const currentPage = ref(1)
  const pageSize = ref(10)
  const total = ref(1)



  const statusMap = {
    new: { text: '未上传文件', type: 'info' },
    uploading: { text: '文件上传中', type: 'primary' },
    uploaded: { text: '文件上传完成', type: 'success' },
    upload_failed: { text: '文件上传失败', type: 'danger' },
    training: { text: '训练中', type: 'warning' },
    trained: { text: '训练完成', type: 'success' },
    error: { text: '解析错误', type: 'danger' }
};

// 获取标签类型的方法
const getTagType = (status) => {
    return statusMap[status]?.type || 'info';
};

// 获取状态文本的方法
const getStatusText = (status) => {
    return statusMap[status]?.text || '未知状态';
};


  // 初始化加载数据
  onMounted(() => {
    searchAll()
  })
  
  // 查询与获取数据
const searchAll = async(showMessage=false,type='') => {
  try {
    const params = {
      keyword: searchInput.value,
      status: searchStatus.value,
      page: currentPage.value,
      pageSize: 10,
      user_id: userId
    }
    
    const res = await api.searchDataset(params)
    const resultArray = typeof res.result === 'string' ? JSON.parse(res.result) : res.result 
    if (res?.code === 200 && Array.isArray(resultArray)) {
      datasetList.splice(0, datasetList.length, ...resultArray)
      total.value = resultArray.length
     // ElMessage.success(res.message || '查询成功')
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
    searchInput.value = ''
    searchStatus.value = ''
    currentPage.value = 1
    searchAll()
  }
  
  // 添加数据集
  const handleAddDataset = () => {
    datasetForm.name = ''
    datasetForm.description = ''
    dialogTitle.value = '添加数据集'
    editdialogVisible.value = true
    formType.value = 'add'
  }

  // 提交表单
const handleSubmit = () => {
  datasetFormRef.value.validate(async (valid) => {
    if (valid) {
    let res
    if (formType.value === 'add') {
      res = await api.addDataset(datasetForm)
    } else {
      res = await  api.editDataset(`/dataset/${datasetForm.dataset_id}`, datasetForm)
    }
    
    if (res.code === 200) {
      ElMessage.success(formType.value === 'add' ? '新增成功' : '更新成功')
      editdialogVisible.value = false
      searchAll()
      if(formType.value === 'add'){
        //弹窗提示：是否上传文件
        //点击是，调用handleUpload方法
        ElMessageBox.confirm('新增成功，是否上传文件？', '提示', {
          confirmButtonText: '是',
          cancelButtonText: '否',
          type: 'warning'
        }).then(() => {
          ElMessage.warning('请点击对应数据集的上传按钮上传文件')
        }).catch(() => {})
      }
    } else {
    //   ElMessage.error(res.message || '操作失败')
    }
      editdialogVisible.value = false
    } else {
      ElMessage.error('请填写数据集名称')
      return false
    }
  })
}
  
  // 操作按钮
  const handleUpload = (row) => {
    uploaddialogVisible.value = true;
    currentRow.value = row;
  }
  
  const handleDownload = async(row) => {
    try {
    // 调用后端下载接口
    const response = await api.downloadDataset(userId,row.dataset_id, {
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
      fileName = `dataset_${row.dataset_id}.zip`;
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
    dialogTitle.value = '编辑数据集'
    datasetForm.name = row.name
    datasetForm.description = row.description || ''
    editdialogVisible.value = true
    formType.value = 'edit'

    Object.assign(datasetForm,{
        ...row
    })
    await nextTick()
    datasetFormRef.value?.clearValidate()
  }
  
  const handleDelete = (row) => {
    ElMessageBox.confirm('确定删除该数据集吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    .then(async() => {
      const res = await api.deleteDataset(userId,row.dataset_id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        searchAll()
      }else{
        // ElMessage.error(res.message || '删除失败')
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
  if (!file.name.endsWith('.zip')) {
    ElMessage.error('请上传ZIP格式的文件');
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
// 确认上传
const confirmUpload = async () => {
  const data = currentRow.value;
  if (!selectedFile.value) {
    ElMessage.warning('请先选择文件');
    return;
  }
  
  if(data.status !== "new" && data.status!== "upload_failed"){
    ElMessage.error('当前数据集状态不允许上传文件')
    return;
  }
  isUploading.value = true;
  const extraData = {
    dataset_id: data.dataset_id,
    user_id: userId
  }
  try {
    let res;
    res = await api.uploadDataset('/uploadDataset',selectedFile.value.raw,extraData);
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
};

// 取消上传
const cancelUpload = () => {
  selectedFile.value = null;
  uploadRef.value.clearFiles();
  uploaddialogVisible.value = false;
};

// // 暴露方法给父组件调用
// defineExpose({
//   openModal
// });
  
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
  .dataset-management {
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


<!-- <script setup>
const isAuthPage = ref(true)
</script> -->
