<template>
    <div class="article-management">

      <!-- 标题和写文章按钮 -->
      <div class="header">
        <h1>文章管理</h1>
        <div>
          <el-link :href="router.resolve({ name: 'EditArticle',query:{article:JSON.stringify({author_id:userId})}}).href" target="_blank">
            <el-button type="primary">写文章</el-button>
          </el-link>
          <el-button type="warning" @click="searchAll(true,'refresh')" style="margin-left: 20px;">状态刷新</el-button>
        </div>
      </div>

      <!-- 查询区域 -->
      <div class="query-section">
        <el-form :inline="true">
          <el-form-item label="文章标题">
            <el-input
              v-model="searchTitle"
              placeholder="请输入文章标题"
              clearable
            />
          </el-form-item>
          <el-form-item label="作者">
            <el-input
              v-model="searchAuthor"
              placeholder="请输入作者"
              clearable
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="searchAll(true,'query')">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 文章表格 -->
      <div class="table-section">
        <el-table :data="articleList" border style="width: 100%">
          <el-table-column prop="title" label="标题" >
            <template #default="{ row }">
              <el-tooltip placement="top" :content="row.abstractt">
                    <template #content>
                         摘要： {{ row.abstractt }}
                    </template>
                    <el-link  @click="() => viewArticle(row)" type="primary">{{ row.title }}</el-link>
              </el-tooltip>
            </template>
          </el-table-column>
          <el-table-column prop="username" label="作者" />
          <el-table-column prop="time" label="创建日期" width="200px"/>
          <el-table-column prop="is_public" label="是否公开" width="90px">
            <template #default="{ row }">
              <el-switch
                v-model="row.is_public"
                @change="togglePublic(row)"
              />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="400px">
            <template #default="{ row }">
                <el-button type="success" @click="() => viewArticle(row)">查看文章</el-button>
                <el-button type="warning" @click="() => editArticle(row)">编辑文章</el-button>
                <el-button style="margin: 10px;" type="danger" @click="handleDelete(row)">删除</el-button>
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

      <!-- 摘要对话框 -->
      <el-dialog 
            v-model="dialogVisible" 
            width="30%"> 
            <el-divider content-position="left">摘要</el-divider> 
            <el-input 
                type="textarea" 
                :value="currentAbstract" 
                rows="6" 
                maxlength="255" 
                disabled> 
            </el-input> 
            
            <template #footer> 
                <span class="dialog-footer"> 
                    <el-button @click="dialogVisible = false">取 消</el-button> 
                    <el-button type="primary" @click="dialogVisible = false">确 定</el-button> 
                </span> 
            </template> 
        </el-dialog>

    </div>
  </template>

  <script setup>
  import { ref, onMounted, reactive, nextTick } from 'vue'
  import { ElMessage, ElMessageBox } from "element-plus";
  import api from '@/api/login.js'
  import { useRoute, useRouter } from 'vue-router'

  const router = useRouter()

  const storedUser = localStorage.getItem('user');
  const userInfo = storedUser ? JSON.parse(storedUser) : {};
  const userId = userInfo.id; // 假设 user_id 的键名为 id

  const searchTitle = ref('')
  const searchAuthor = ref('')

  // 新增对话框相关变量
  const dialogVisible = ref(false)
  const currentAbstract = ref('')

  // 新增点击标题显示摘要的方法
  const showAbstract = (row) => {
      currentAbstract.value = row.abstractt
      dialogVisible.value = true
  }

  // 查看文章
  const viewArticle = (row) => {
  const articleUrl = router.resolve({
    path: '../../admin/article',
    query: {
      id: row.article_id // 假设文章 ID 字段为 article_id
    }
  });
  window.open(articleUrl.href, '_blank');
};



  // 文章列表
  const articleList = reactive([])

  // 分页相关
  const currentPage = ref(1)
  const pageSize = ref(10)
  const total = ref(1)

  // 初始化加载数据
  onMounted(() => {
    searchAll()
  })

  // 查询与获取数据
  const searchAll = async(showMessage=false,type='') => {
    try {
      const params = {
        title: searchTitle.value,
        author: searchAuthor.value,
        page: currentPage.value,
        pageSize: pageSize.value,
        user_id: userId
      }

      const res = await api.searchArticle(params)
      const resultArray = typeof res.result === 'string' ? JSON.parse(res.result) : res.result
      if (res?.code === 200 && Array.isArray(resultArray)) {
        articleList.splice(0, articleList.length, ...resultArray)
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
    searchTitle.value = ''
    searchAuthor.value = ''
    currentPage.value = 1
    searchAll()
  }

  // 写文章
  const editArticle = (row) => {
    if (row.author_id !== userId) {
        ElMessage.error('您无权编辑该文章');
        return;
    }

    const routeData = router.resolve({
      name: 'EditArticle',
      query: { 
          article: JSON.stringify({
          articleTitle: row.title,
          articleAbstract: row.abstractt,
          articleContentMd: row.content_md,
          author_id: row.author_id,
          article_id: row.article_id,
          flag: "1"
        })
      }
    });
    window.open(routeData.href, '_blank');
  };

  // 删除文章
  const handleDelete = (row) => {
    ElMessageBox.confirm('确定删除该文章吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    .then(async() => {
      const res = await api.deleteArticle(userId, row.article_id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        searchAll()
      }else{
        // ElMessage.error(res.message || '删除失败')
      }
    })
  }

  // 切换文章公开状态
  const togglePublic = async (row) => {
    try {
      const params = {
        article_id: row.article_id,
        author_id: row.author_id,
        is_public: row.is_public,
        user_id: userId
      }
      const res = await api.toggleArticlePublic('/updateStatus', params)
      if (res.code === 200) {
        ElMessage.success('公开状态更新成功')
      } else {
        // ElMessage.error('公开状态更新失败')
        row.is_public = !row.is_public // 回滚操作
      }
    } catch (error) {
      // ElMessage.error('请求出错: ' + error.message)
      row.is_public = !row.is_public // 回滚操作
    }
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
  .article-management {
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
  </style>