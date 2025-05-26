<template>
    <div class="about-page">
      <el-card class="about-card">
        <div class="about-header">
          <h1 class="about-title">关于我们</h1>
          <el-divider />
        </div>
  
        <div class="about-content">
          <div class="about-section">
            <h2 class="section-title">团队简介</h2>
            <p class="section-text">
                “云瞰泽影”—滨海湿地外来入侵物种互花米草智能监测与评估系统，同时是基于卷积神经网络的智能化遥感图像解译平台，致力于为政府和企业和科研机构提供专业的遥感解译、空间分析和地理可视化服务。
            </p>
          </div>
  
          <div class="about-section">
            <h2 class="section-title">我们的团队</h2>
            <p class="section-text">
              团队成员来自多个学科多个专业，拥有丰富的专业知识和扎实的专业基础，极大地促进了多学科交叉融合领域的研究。
            </p>
          </div>
  
          <div class="about-section">
            <h2 class="section-title">技术优势</h2>
            <p class="section-text">
              拥有自主知识产权的遥感解译算法，结合计算机视觉技术、人工智能、大数据技术，实现90%以上的自动化解译精度，处理效率比传统方法提升5-10倍。
            </p>
          </div>
  
          <div class="feedback-section">
            <h2 class="section-title">意见反馈</h2>
            <el-form 
              ref="feedbackFormRef" 
              :model="feedbackForm" 
              :rules="feedbackRules" 
              @submit.prevent="submitFeedback"
            >
              <el-form-item prop="content" label="您的宝贵意见">
                <el-input
                  type="textarea"
                  v-model="feedbackForm.content"
                  :rows="5"
                  placeholder="请输入您的意见或建议（500字以内）"
                  maxlength="500"
                  show-word-limit
                />
              </el-form-item>
  
              <el-form-item>
                <el-button 
                  type="primary" 
                  native-type="submit"
                  :loading="submitting"
                >
                  提交反馈
                </el-button>
                <el-button @click="resetForm">
                  重置
                </el-button>
              </el-form-item>
            </el-form>
          </div>
          <!-- 添加显示反馈内容的区域 -->
          <div v-if="submittedContent" class="submitted-content">
            <h2 class="section-title">您提交的反馈内容</h2>
            <p>{{ submittedContent }}</p>
          </div>
        </div>
      </el-card>
    </div>
    
  </template>
  
  <script setup>
  import { ref } from 'vue'
  import { ElMessage } from 'element-plus'
  
  // 反馈表单数据
  const feedbackForm = ref({
    content: ''
  })
  
  // 存储提交的反馈内容
  const submittedContent = ref('')
  
  // 表单验证规则
  const feedbackRules = {
    content: [
      { required: true, message: '请输入反馈内容', trigger: 'blur' },
    ]
  }
  
  // 提交状态
  const submitting = ref(false)
  const feedbackFormRef = ref(null)
  
  // 提交反馈
  const submitFeedback = () => {
    feedbackFormRef.value.validate((valid) => {
      if (valid) {
        submitting.value = true
        
        // 存储提交的反馈内容
        submittedContent.value = feedbackForm.value.content
        
        // 模拟API调用
        setTimeout(() => {
          ElMessage.success('反馈提交成功！')
          feedbackFormRef.value.resetFields()
          submitting.value = false
        }, 1000)
      }
    })
  }
  
  // 重置表单
  const resetForm = () => {
    feedbackFormRef.value.resetFields()
    // 重置时清空显示的反馈内容
    submittedContent.value = ''
  }
  </script>
  
  <style scoped>
  .about-page {
    max-width: 1200px;
    margin: 0 auto;
    padding: 20px;
  }
  
  .about-card {
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  }
  
  .about-header {
    text-align: center;
    margin-bottom: 30px;
  }
  
  .about-title {
    font-size: 32px;
    color: #333;
    margin-bottom: 20px;
  }
  
  .about-content {
    padding: 0 20px;
  }
  
  .about-section {
    margin-bottom: 40px;
  }
  
  .section-title {
    font-size: 24px;
    color: #409EFF;
    margin-bottom: 15px;
    border-left: 4px solid #409EFF;
    padding-left: 10px;
  }
  
  .section-text {
    font-size: 16px;
    line-height: 1.8;
    color: #606266;
    text-align: justify;
  }
  
  .feedback-section {
    margin-top: 50px;
    padding-top: 30px;
    border-top: 1px dashed #dcdfe6;
  }
  
  /* 响应式设计 */
  @media (max-width: 768px) {
    .about-page {
      padding: 10px;
    }
    
    .about-title {
      font-size: 28px;
    }
    
    .section-title {
      font-size: 20px;
    }
  }
  .submitted-content {
    margin-top: 30px;
    padding: 20px;
    background-color: #f5f7fa;
    border-radius: 4px;
  }
  </style>