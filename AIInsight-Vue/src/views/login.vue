<template>
  <!-- 最外层的大盒子 -->
  <div class="bigBox">
<!--    <div class="tab">欢迎使用xxx系统</div>-->
    <div class="box" ref="box">
      <!-- 滑动盒子 -->
      <div class="pre-box">
        <h1>WELCOME</h1>
        <p>JOIN US!</p>
        <div class="img-box">
          <img src="../assets/img/login1.jpg" alt="" id="avatar" />
        </div>
      </div>
      <!-- 注册盒子 -->
      <div class="register-form">
        <!-- 标题盒子 -->
        <div class="title-box">
          <h1>注册</h1>
        </div>
        <!-- 输入框盒子 -->
        <el-form ref="registerFormRef" :model="registerForm" :rules="rules" label-with="5px">
          <el-form-item prop="username" label=" ">
            <el-input type="text" placeholder="用户名" :suffix-icon="User" v-model="registerForm.username"/>
          </el-form-item>
          <el-form-item prop="password" label=" ">
            <el-input type="password" placeholder="密码" :suffix-icon="Lock" show-password v-model="registerForm.password"/>
          </el-form-item>
          <el-form-item prop="confirmPassword" label=" ">
            <el-input type="password" placeholder="确认密码" :suffix-icon="Lock" show-password v-model="registerForm.confirmPassword"/>
          </el-form-item>
          <el-form-item prop="email" label=" ">
            <el-input type="text" placeholder="邮箱" :suffix-icon="Message" v-model="registerForm.email"/>
          </el-form-item>
          <el-form-item prop="emailCode" label=" " class="email-code-item">
            <div class="code-input-container">
              <el-input type="text" placeholder="邮箱验证码" v-model="registerForm.emailCode"/>
              <el-button
                  type="primary"
                  size="small"
                  @click="sendEmailCode1"
                  class="code-button"
                  :disabled="emailCodeDisabled">
                {{ emailCodeBtnText }}
              </el-button>
            </div>
          </el-form-item>
        </el-form>
        <!-- 按钮盒子 -->
        <div class="btn-box">
          <button @click="register">注册</button>
          <!-- 绑定点击事件 -->
          <p @click="mySwitch">已有账号?去登录</p>
        </div>
      </div>
      <!-- 登录盒子 -->
      <div class="login-form">
        <!-- 标题盒子 -->
        <div class="title-box">
          <h1>登录</h1>
        </div>
        <!-- 输入框盒子 -->
        <el-form ref="loginFormRef" :model="loginForm" :rules="rules" label-with="5px">
          <el-form-item prop="account_id" label=" ">
            <el-input
              type="text"
              placeholder="账号/邮箱"
              :suffix-icon="User"
              v-model="loginForm.account_id"
            />
          </el-form-item>
          <el-form-item prop="password" label=" ">
            <el-input
              type="password"
              placeholder="密码"
              :suffix-icon="Lock"
              v-model="loginForm.password"
              show-password
            />
          </el-form-item>
          <el-form-item prop="captcha" label=" " class="captcha-form-item">
            <div class="captcha-container">
              <el-input
                  type="text"
                  placeholder="验证码"
                  v-model="loginForm.captcha"
                  class="captcha-input"
              />
              <div
                  class="captcha-img"
              >
                <img :src="captchaUrl" alt="验证码" @click="refreshCaptcha">
              </div>
            </div>
          </el-form-item>

        </el-form>
        <!-- 按钮盒子 -->
        <div class="btn-box">
          <button @click="login">登录</button>
        </div>
        <div class="btn-box">
          <!-- 绑定点击事件 -->
          <p @click="mySwitch">没有账号?去注册&nbsp;&nbsp;&nbsp;</p>
          <p @click="showForgotForm">&nbsp;&nbsp;&nbsp;忘记密码？</p>
        </div>
      </div>
      <!-- 密码找回盒子 -->
      <div class="forgot-form" :class="{ show: isForgotFormVisible}">
        <!-- 标题盒子 -->
        <div class="title-box">
          <h1>重置密码</h1>
        </div>
        <!-- 输入框盒子 -->
        <el-form ref="forgotFormRef" :model="forgotForm" :rules="rules" label-with="5px">
          <el-form-item prop="password" label=" ">
            <el-input type="password" placeholder="新密码"  :suffix-icon="Lock" show-password v-model="forgotForm.password"/>
          </el-form-item>
          <el-form-item prop="confirmPassword" label=" ">
            <el-input type="password" placeholder="确认新密码" :suffix-icon="Lock" show-password v-model="forgotForm.confirmPassword"/>
          </el-form-item>
          <el-form-item prop="email" label=" ">
            <el-input type="text" placeholder="邮箱" :suffix-icon="Message" v-model="forgotForm.email"/>
          </el-form-item>
          <el-form-item prop="emailCode" label=" " class="email-code-item">
            <div class="code-input-container">
              <el-input type="text" placeholder="邮箱验证码" v-model="forgotForm.emailCode"/>
              <el-button
                  type="primary"
                  size="small"
                  @click="sendEmailCode2"
                  class="code-button"
                  :disabled="emailCodeDisabled">
                {{ emailCodeBtnText }}
              </el-button>
            </div>
          </el-form-item>
        </el-form>
        <!-- 按钮盒子 -->
        <div class="btn-box">
          <button @click="resetPassword">重置密码</button>
          <!-- 绑定点击事件 -->
          <button @click="hideForgotForm">返回</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { Lock, User , Message, Phone } from '@element-plus/icons-vue'
import mySwitch from '@/utils/mySwitch'
import { reactive, ref } from 'vue'
import api from '@/api/login.js'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { onBeforeUnmount } from 'vue'
import { nextTick } from 'vue'
import { onMounted } from '@vue/runtime-core'
import { useStore } from 'vuex'

const store = useStore()
const isAuthPage = ref(false)
const box = ref(null)
const isForgotFormVisible = ref(false)
// 切换动画效果
const switchForm = (direction) => {
  const boxElement = box.value
  if (!boxElement) return

  // 添加过渡类
  boxElement.style.transition = 'all 0.5s ease-in-out'

  if (direction === 'toLogin') {
    boxElement.style.left = '-50%'
  } else if (direction === 'toRegister') {
    boxElement.style.left = '0'
  }

  // 过渡结束后移除样式
  setTimeout(() => {
    boxElement.style.transition = ''
  }, 500)
}

// 切换函数
const switchToLogin = () => {
  switchForm('toLogin')
  isForgotFormVisible.value = false
}

const switchToRegister = () => {
  switchForm('toRegister')
  isForgotFormVisible.value = false
}


const loginForm = reactive({
  account_id: '',
  password: '',
  captcha: ''
})
const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  email: '',
  emailCode: ''
})
const forgotForm = reactive({
  password: '',
  confirmPassword: '',
  email: '',
  emailCode: ''
})

const loginFormRef = ref('')
const registerFormRef = ref('')
const forgotFormRef = ref('')
const rules = reactive({
  account_id:[
    {required: true, message: '请输入账号或邮箱', trigger: 'blur'},
  ],
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 30, message: '长度应该在2~30个字符之间', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '长度应该大于6', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: '请输入确认密码', trigger: 'blur' },
    { min: 6, message: '长度应该大于6', trigger: 'blur' },
    { validator: (rule, value, callback) => {
        const form = rule.field.includes('register') ? registerForm : forgotForm
        if (value !== form.password) callback('两次密码不一致')
        else callback()
      }, trigger: 'blur'
    }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur'},
    { }
  ],
  emailCode: [
    { required: true, message: '请输入收到的验证码', trigger: 'blur'},
    { min: 6, max: 6, message: '验证码长度为6位', trigger: 'blur'}
  ],
  captcha: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { min: 4, max: 4, message: '验证码长度为4位', trigger: 'blur' }
  ]

})

const router = useRouter()

const showForgotForm = () => {
  isForgotFormVisible.value = true
}

const hideForgotForm = () => {
  isForgotFormVisible.value = false
}

const key = ref(Math.floor(Math.random() * 1000000))
const captchaUrl = ref('http://localhost:9090/api/captcha?key='+key.value);
// 获取图形验证码
const refreshCaptcha = async () => {
  try {
    key.value = Math.floor(Math.random() * 1000000)
    captchaUrl.value = `http://localhost:9090/api/captcha?key=${key.value}`

  } catch (error) {
    console.error('获取验证码失败:', error)
    ElMessage.error('获取验证码失败')
  }
}

const emailCodeDisabled = ref(false)
const emailCodeBtnText = ref('发送验证码')
const emailCodeCountdown = ref(60)
let emailCodeTimer = null

// 发送邮箱验证码
//
const sendEmailCode1 = async () => {
  try {
    // 验证邮箱格式
    if (!registerForm.email) {
      ElMessage.warning('请输入邮箱')
      return
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    if (!emailRegex.test(registerForm.email)) {
      ElMessage.warning('请输入有效的邮箱地址')
      return
    }

    // 发送验证码请求
    const response = await api.sendEmailCode1({
      username: registerForm.username,
      email: registerForm.email
    })
    const { code, message } = response
    if(code == 200){
      ElMessage.success('验证码已发送')

      // 开始倒计时
      emailCodeDisabled.value = true
      emailCodeBtnText.value = `${emailCodeCountdown.value}秒后重新获取`

      emailCodeTimer = setInterval(() => {
        emailCodeCountdown.value--
        emailCodeBtnText.value = `${emailCodeCountdown.value}秒后重新获取`

        if (emailCodeCountdown.value <= 0) {
          clearInterval(emailCodeTimer)
          emailCodeDisabled.value = false
          emailCodeBtnText.value = '发送验证码'
          emailCodeCountdown.value = 60
        }
      }, 1000)
    }

    else if(code == 400){
      //ElMessage.error('验证码发送失败，请稍后重试')
    }
  }catch (error) {
    console.error('发送验证码失败:', error)
    ElMessage.error(error.message || '发送验证码失败')
    clearInterval(emailCodeTimer)
    emailCodeDisabled.value = false
    emailCodeBtnText.value = '发送验证码'
    emailCodeCountdown.value = 60
  }
}

const resetPassword = async () => {
  try {
    // 验证表单
    await forgotFormRef.value.validate()
    
    // 检查两次密码是否一致
    if (forgotForm.password !== forgotForm.confirmPassword) {
      ElMessage.error('两次输入的密码不一致')
      return
    }

    // 调用API重置密码
    const response = await api.resetPassword({
      email: forgotForm.email,
      emailCode: forgotForm.emailCode,
      newPassword: forgotForm.password,
      confirmPassword: forgotForm.confirmPassword
    })

    if (response.code === 200) {
      ElMessage.success(response.message)
      hideForgotForm() // 隐藏密码找回表单
      // 清空表单
      forgotForm.password = ''
      forgotForm.confirmPassword = ''
      forgotForm.email = ''
      forgotForm.emailCode = ''
    } else {
      ElMessage.error(response.message || '密码重置失败')
    }
  } catch (error) {
    console.error('密码重置失败:', error)
    ElMessage.error(error.message || '密码重置失败')
  }
}

const sendEmailCode2 = async () => {
  try {
    // 验证邮箱格式
    if (!forgotForm.email) {
      ElMessage.warning('请输入邮箱')
      return
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    if (!emailRegex.test(forgotForm.email)) {
      ElMessage.warning('请输入有效的邮箱地址')
      return
    }

    // 发送验证码请求
    const response = await api.sendEmailCode2({
      email: forgotForm.email
    })
    const { code, message } = response
    if(code == 200){
      ElMessage.success('验证码已发送')

      // 开始倒计时
      emailCodeDisabled.value = true
      emailCodeBtnText.value = `${emailCodeCountdown.value}秒后重新获取`

      emailCodeTimer = setInterval(() => {
        emailCodeCountdown.value--
        emailCodeBtnText.value = `${emailCodeCountdown.value}秒后重新获取`

        if (emailCodeCountdown.value <= 0) {
          clearInterval(emailCodeTimer)
          emailCodeDisabled.value = false
          emailCodeBtnText.value = '发送验证码'
          emailCodeCountdown.value = 60
        }
      }, 1000)
    }

    else if(code == 400){
      //ElMessage.error('验证码发送失败，请稍后重试')
    }
  }catch (error) {
    console.error('发送验证码失败:', error)
    ElMessage.error(error.message || '发送验证码失败')
    clearInterval(emailCodeTimer)
    emailCodeDisabled.value = false
    emailCodeBtnText.value = '发送验证码'
    emailCodeCountdown.value = 60
  }
}

// onBeforeUnmount(() => {
//   if (emailCodeTimer) {
//     clearInterval(emailCodeTimer)
//   }
// })


const login = () => {
  loginFormRef.value.validate((valid) => {
    if (valid) {
      api.loginApi(loginForm, key.value).then(res => {
        if (res.code === 200) {
          ElMessage.success(res.message)
          // window.sessionStorage.setItem('token', res.result)
          const safeUserData = {
            id: res.result.id,
            account_id: res.result.account_id,
            username: res.result.username,
            email: res.result.email,
            phone: res.result.phone,
            role_id: res.result.role_id,
            birthday: res.result.birthday,
            gender: res.result.gender,
            region: res.result.region,
            address: res.result.address,
          }
          // // localStorage.setItem("user",JSON.stringify(safeUserData))

          store.commit('login',safeUserData)
          console.log(store.state.user)
          router.push('/admin/person/personInfo')
        }else{
          //ElMessage.error(res.message)
          refreshCaptcha()
          loginForm.captcha=''
        }
      }).catch(error => {
        loginForm.captcha=''
        console.log(error)
        ElMessage.error(error)
      })
    } else {
      refreshCaptcha()
      loginForm.captcha=''
      return ElMessage.error("验证未通过") //前端验证未通过
    }
  })
}

const register = () => {
  registerFormRef.value.validate((valid) => {
    if (valid) {
      api.registerApi(registerForm).then(res => {
        if (res.code === 200) {
          ElMessage.success(res.message)
          mySwitch()
        }
      }).catch(error => {
        console.log(error);
      })
    } else {
      return
    }
  })
}

onBeforeUnmount(() => {
  if (emailCodeTimer) {
    clearInterval(emailCodeTimer)
  }
});


</script>

<style scoped>
/* 去除input的轮廓 */
input {
  outline: none;
}

.bigBox {
  /* 溢出隐藏 */
  height: 100vh;
  overflow-x: hidden;
  display: flex;
  /* 渐变方向从左到右 */
  background: linear-gradient(to right, rgb(217, 249,205), rgb(171, 217, 241));
}

/* 最外层的大盒子 */
.box {
  width: 1050px;
  height: 600px;
  display: flex;
  /* 相对定位 */
  position: relative;
  z-index: 2;
  margin: auto;
  /* 设置圆角 */
  border-radius: 8px;
  /* 设置边框 */
  border: 1px solid rgba(255, 255, 255, 0.6);
  /* 设置盒子阴影 */
  box-shadow: 2px 1px 19px rgba(0, 0, 0, 0.1);
}

/* 滑动的盒子 */
.pre-box {
  /* 宽度为大盒子的一半 */
  width: 50%;
  height: 100%;
  /* 绝对定位 */
  position: absolute;
  /* 距离大盒子左侧为0 */
  left: 0;
  /* 距离大盒子顶部为0 */
  top: 0;
  z-index: 99;
  border-radius: 4px;
  background-color: #d9f9cd;
  box-shadow: 2px 1px 19px rgba(0, 0, 0, 0.1);
  /* 动画过渡，先加速再减速 */
  transition: 0.5s ease-in-out;
}

/* 滑动盒子的标题 */
.pre-box h1 {
  margin-top: 150px;
  text-align: center;
  /* 文字间距 */
  letter-spacing: 5px;
  color: white;
  /* 禁止选中 */
  user-select: none;
  /* 文字阴影 */
  text-shadow: 4px 4px 3px rgba(0, 0, 0, 0.1);
}

/* 滑动盒子的文字 */
.pre-box p {
  height: 30px;
  line-height: 30px;
  text-align: center;
  margin: 20px 0;
  /* 禁止选中 */
  user-select: none;
  font-weight: bold;
  color: white;
  text-shadow: 4px 4px 3px rgba(0, 0, 0, 0.1);
}

/* 图片盒子 */
.img-box {
  width: 200px;
  height: 200px;
  margin: 20px auto;
  /* 设置为圆形 */
  border-radius: 50%;
  /* 设置用户禁止选中 */
  user-select: none;
  overflow: hidden;
  box-shadow: 4px 4px 3px rgba(0, 0, 0, 0.1);
}

/* 图片 */
.img-box img {
  width: 100%;
  transition: 0.5s;
}

/* 登录和注册盒子 */
.login-form,
.register-form {
  flex: 1;
  height: 100%;
}

/* 标题盒子 */
.title-box {
  height: 200px;
  line-height: 300px;
}

/* 标题 */
.title-box h1 {
  text-align: center;
  color: white;
  /* 禁止选中 */
  user-select: none;
  letter-spacing: 5px;
  text-shadow: 4px 4px 3px rgba(0, 0, 0, 0.1);
}

/* 输入框盒子 */
.el-form {
  display: flex;
  /* 纵向布局 */
  flex-direction: column;
  /* 水平居中 */
  align-items: center;
}
.el-form-item {
  width: 65%;
}
/* 输入框 */
input {
  /* width: 60%; */
  height: 40px;
  margin-bottom: 20px;
  text-indent: 10px;
  border: 1px solid #fff;
  background-color: rgba(255, 255, 255, 0.3);
  border-radius: 120px;
  /* 增加磨砂质感 */
  backdrop-filter: blur(10px);
}

input:focus {
  /* 光标颜色 */
  color: #b0cfe9;
}

/* 聚焦时隐藏文字 */
input:focus::placeholder {
  opacity: 0;
}

/* 按钮盒子 */
.btn-box {
  display: flex;
  justify-content: center;
}

/* 按钮 */
button {
  width: 100px;
  height: 30px;
  margin: 0 7px;
  line-height: 30px;
  border: none;
  border-radius: 4px;
  background-color: #69b3f0;
  color: white;
}

/* 按钮悬停时 */
button:hover {
  /* 鼠标小手 */
  cursor: pointer;
  /* 透明度 */
  opacity: 0.8;
}

/* 按钮文字 */
.btn-box p {
  height: 30px;
  line-height: 30px;
  /* 禁止选中 */
  user-select: none;
  font-size: 14px;
  color: white;
}

.btn-box p:hover {
  cursor: pointer;
  border-bottom: 1px solid white;
}
/* 邮箱验证码容器样式 */
.code-input-container {
  display: flex;
  width: 100%;
}

/* 输入框样式 */
.code-input {
  flex: 1;
  margin-right: 10px; /* 与按钮间距 */
}

/* 按钮样式 */
.code-button {
  width: 120px; /* 固定按钮宽度 */
  flex-shrink: 0; /* 防止按钮被压缩 */
  font-size: 14px;
}

/* 验证码容器样式 */
.captcha-container {
  display: flex;
  width: 100%;
  gap: 10px; /* 元素间距 */
}

/* 输入框样式 */
.captcha-input {
  flex: 1;
}
/* 验证码图片样式 */
.captcha-img {
  width: 100px;
  height: 40px; /* 与输入框高度匹配 */
  cursor: pointer;
  border-radius: 4px;
  overflow: hidden;
  background-color: #f5f5f5;
  flex-shrink: 0; /* 防止图片被压缩 */
}

.captcha-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

   /* 密码找回盒子 - 初始状态 */
 .forgot-form {
   position: absolute;
   width: 50%;
   height: 100%;
   right: 0;
   top: 0;
   background: linear-gradient(to right, rgb(217, 249,205), rgb(171, 217, 241));
   border-radius: 4px;
   z-index: 98;
   opacity: 0;
   pointer-events: none;
   transform: translateX(20px);
   transition: all 0.3s ease-in-out;
 }

/* 密码找回盒子 - 显示状态 */
.forgot-form.show {
  opacity: 1;
  pointer-events: auto;
  transform: translateX(0);
  z-index: 100;
}

/* 滑动动画 */
.box {
  position: relative;
  transition: all 0.5s ease-in-out;
}

/* 确保登录和注册表单有正确的堆叠顺序 */
.login-form, .register-form {
  position: relative;
  z-index: 1;
}

</style>