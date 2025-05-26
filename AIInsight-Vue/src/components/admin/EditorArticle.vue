<template> 
  <div> 
    <h1 style="margin: 18px 0px 0px 18px ">编辑文章</h1>
    <el-row> 
      <el-input 
        v-model="article.articleTitle" 
        style="margin: 10px 0px;font-size: 18px;" 
        :disabled="!canEdit"
        placeholder="请输入标题"
        maxlength="50"
      >
      </el-input> 
    </el-row> 
    <el-row style="height: calc(100vh - 140px);"> 
      <mavon-editor 
        v-model="article.articleContentMd" 
        style="height: 100%; width: 100%;" 
        ref="md" 
        @save="saveArticles" 
        fontSize="16px"
        :disabled="!canEdit"> 
        <template #left-toolbar-after>
            <el-button 
                class="op-icon" 
                :icon="Document"  
                :title="'编辑摘要'" 
                circle
                @click="dialogVisible = true"
                :disabled="!canEdit">
            </el-button> 
        </template>
        
      </mavon-editor> 
      
      <el-dialog 
        v-model="dialogVisible" 
        width="30%"> 
        <el-divider content-position="left">摘要</el-divider> 
        <el-input 
          type="textarea" 
          v-model="article.articleAbstract" 
          rows="6" 
          maxlength="500" 
          show-word-limit
          :disabled="!canEdit">
        </el-input> 
        
        <template #footer> 
          <span class="dialog-footer"> 
            <el-button @click="dialogVisible = false">取 消</el-button> 
            <el-button type="primary" @click="dialogVisible = false">确 定</el-button> 
          </span> 
        </template> 
      </el-dialog> 
    </el-row> 
  </div> 
</template> 

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { arrowMiddleware, ElMessage, ElMessageBox } from 'element-plus';
import api from '@/api/login.js'
import { mavonEditor } from 'mavon-editor'
import { Document } from '@element-plus/icons-vue'
import 'mavon-editor/dist/css/index.css'

const route = useRoute();
const md = ref(null);
const imgUpload = ref(null);
const dialogVisible = ref(false);
const storedUser = localStorage.getItem('user');
const userInfo = storedUser ? JSON.parse(storedUser) : {};
const userId = userInfo.id; // 假设 user_id 的键名为 id
const canEdit = ref(true); // 新增响应式变量，控制编辑权限

const rules = {
  articleTitle: [
    { required: true, message: '请输入文章标题', trigger: 'blur' },
    { min: 1, max: 50, message: '文章标题长度在 1 到 50 个字符之间', trigger: 'blur' }
  ]
};

const article = ref({
  articleTitle: '',
  articleAbstract: '',
  author_id: '',
  article_id:'',
  flag:''
});

onMounted(() => {
  
  if (route.query.article) {
    try {
      article.value = JSON.parse(route.query.article);
      // 设置flag默认为0（新建）
      if (!article.value.flag) {
        article.value.flag = "0";
      }
      // 检查 author_id 是否为空或者不等于 userId
      if (!article.value.author_id || article.value.author_id !== userId) {
        canEdit.value = false;
        ElMessage.error('您无权编辑该页面');
      }
    } catch (e) {
      // console.error('解析文章数据失败:', e);
      ElMessage.error('解析文章数据失败');
    }
  }
});

const saveArticles = (value, render) => {
  ElMessageBox.confirm('是否保存文章?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 使用验证规则进行验证
    if (article.value.articleTitle.length > 50) {
      ElMessage.error('文章标题长度不能超过 50 个字符');
      return;
    }
    // 检查文章标题是否为空
    if (!article.value.articleTitle || !article.value.articleTitle.trim()) {
      ElMessage.error('文章标题不能为空');
      return;
    }
    ElMessageBox.confirm('是否公开该文章?', '提示', {
      confirmButtonText: '公开',
      cancelButtonText: '不公开',
      type: 'warning'
    }).then(() => {
      // 用户选择公开
      saveArticleRequest(value, render, true);
    }).catch(() => {
      // 用户选择不公开
      saveArticleRequest(value, render, false);
    });
  }).catch(() => {
    ElMessage({
      type: 'info',
      message: '已取消保存'
    });
  });
};

const saveArticleRequest = async(value, render, isPublic) => {
  const params = {
    title: article.value.articleTitle,
    content_md: value,
    content_html: render,
    abstractt: article.value.articleAbstract,
    is_public: isPublic,
    author_id: article.value.author_id,
    user_id:userId,
    article_id:''
  }

  let res
  if (article.value.flag === "1") {
    // 更新文章逻辑
    params.article_id = article.value.article_id // 假设有id字段
    res = await api.updateArticle(params)
  } else {
    // 新建文章逻辑
    res = await api.saveArticle('/saveArticle', params)
  }
  
  if (res.code === 200) {
    ElMessage.success(article.value.flag === "1" ? '更新成功' : '保存成功')
  } else {
    ElMessage.error(res.message || '操作失败')
  }
};


</script>

<style scoped>
.editor-container {
  padding: 0 10%; /* 控制两侧留白 */
}
.editor {
  width: 100%;
  max-width: 1200px; /* 最大宽度限制 */
  min-width: 600px;  /* 最小宽度保证可用性 */
}
</style>