<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-header">
        <h1>欢迎回来</h1>
        <p>登录汽车票预订系统，开启便捷出行之旅</p>
      </div>
      
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
        size="large"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            :prefix-icon="User"
            clearable
          />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            :prefix-icon="Lock"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        
        <el-form-item>
          <div class="form-options">
            <el-checkbox v-model="rememberMe">记住我</el-checkbox>
            <el-link type="primary">忘记密码？</el-link>
          </div>
        </el-form-item>
        
        <el-form-item>
          <el-button 
            type="primary" 
            class="login-btn" 
            :loading="userStore.loading"
            @click="handleLogin"
          >
            登 录
          </el-button>
        </el-form-item>
      </el-form>
      
      <div class="login-footer">
        <span>还没有账号？</span>
        <router-link to="/register">立即注册</router-link>
      </div>
      
      <!-- 演示账号 -->
      <div class="demo-accounts">
        <el-collapse>
          <el-collapse-item title="演示账号（点击展开）" name="1">
            <div class="demo-item">
              <span class="label">管理员：</span>
              <code>admin / admin123</code>
            </div>
            <div class="demo-item">
              <span class="label">普通用户：</span>
              <code>test / test123</code>
            </div>
          </el-collapse-item>
        </el-collapse>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { User, Lock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loginFormRef = ref(null)
const rememberMe = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在3-20个字符之间', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6个字符', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      const success = await userStore.doLogin(loginForm.username, loginForm.password)
      if (success) {
        // 跳转到之前访问的页面或首页
        const redirect = route.query.redirect || '/'
        router.push(redirect)
      }
    }
  })
}
</script>

<style lang="scss" scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: 
    linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
  padding: 20px;
  position: relative;
  overflow: hidden;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: 
      radial-gradient(circle at 25% 25%, rgba(255, 255, 255, 0.1) 0%, transparent 50%),
      radial-gradient(circle at 75% 75%, rgba(255, 255, 255, 0.05) 0%, transparent 50%);
    pointer-events: none;
  }
  
  &::after {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(45deg, transparent 30%, rgba(255, 255, 255, 0.02) 50%, transparent 70%);
    animation: light-sweep 8s linear infinite;
    pointer-events: none;
  }
}

.login-container {
  width: 100%;
  max-width: 440px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: 24px;
  padding: 48px;
  box-shadow: 
    0 25px 50px rgba(0, 0, 0, 0.15),
    0 0 0 1px rgba(255, 255, 255, 0.2),
    inset 0 1px 0 rgba(255, 255, 255, 0.4);
  position: relative;
  overflow: hidden;
  animation: login-appear 0.8s ease-out;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 1px;
    background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.6), transparent);
  }
}

.login-header {
  text-align: center;
  margin-bottom: 36px;
  
  h1 {
    font-size: 32px;
    font-weight: 800;
    color: #333;
    margin-bottom: 12px;
    background: linear-gradient(45deg, #667eea, #764ba2);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    animation: title-glow 3s ease-in-out infinite alternate;
  }
  
  p {
    font-size: 16px;
    color: #666;
    line-height: 1.6;
  }
}

.login-form {
  .el-form-item {
    margin-bottom: 24px;
  }
  
  .el-input__wrapper {
    border-radius: 16px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
    border: 1px solid rgba(0, 0, 0, 0.1);
    background: rgba(255, 255, 255, 0.8);
    backdrop-filter: blur(10px);
    transition: all 0.3s ease;
    
    &:hover {
      box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12);
    }
    
    &.is-focus {
      box-shadow: 0 6px 24px rgba(102, 126, 234, 0.15);
      border-color: #667eea;
    }
  }
  
  .login-btn {
    width: 100%;
    height: 50px;
    font-size: 16px;
    font-weight: 700;
    border-radius: 25px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border: none;
    box-shadow: 0 8px 20px rgba(102, 126, 234, 0.3);
    transition: all 0.3s ease;
    position: relative;
    overflow: hidden;
    
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: -100%;
      width: 100%;
      height: 100%;
      background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
      transition: left 0.6s;
    }
    
    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 12px 30px rgba(102, 126, 234, 0.4);
      
      &::before {
        left: 100%;
      }
    }
    
    &:active {
      transform: translateY(0);
    }
  }
}

.form-options {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.login-footer {
  text-align: center;
  margin-top: 24px;
  font-size: 14px;
  color: #666;
  
  a {
    margin-left: 4px;
    font-weight: 500;
  }
}

.demo-accounts {
  margin-top: 28px;
  background: rgba(102, 126, 234, 0.05);
  border-radius: 16px;
  padding: 20px;
  border: 1px solid rgba(102, 126, 234, 0.1);
  
  .demo-item {
    padding: 10px 0;
    font-size: 14px;
    color: #666;
    display: flex;
    align-items: center;
    gap: 8px;
    
    .label {
      color: #999;
      font-weight: 500;
      min-width: 60px;
    }
    
    code {
      background: rgba(102, 126, 234, 0.1);
      padding: 4px 12px;
      border-radius: 8px;
      font-family: 'Monaco', 'Menlo', monospace;
      color: #667eea;
      font-weight: 600;
      border: 1px solid rgba(102, 126, 234, 0.2);
    }
  }
}

// Enhanced Animations
@keyframes login-appear {
  0% {
    opacity: 0;
    transform: translateY(50px) scale(0.9);
  }
  100% {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

@keyframes title-glow {
  0% {
    filter: drop-shadow(0 0 10px rgba(102, 126, 234, 0.3));
  }
  100% {
    filter: drop-shadow(0 0 20px rgba(118, 75, 162, 0.5));
  }
}

@keyframes light-sweep {
  0% {
    transform: translateX(-100%);
  }
  100% {
    transform: translateX(100%);
  }
}

// Form Options Enhancement
.form-options {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  
  .el-checkbox {
    .el-checkbox__input.is-checked .el-checkbox__inner {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border-color: #667eea;
    }
  }
  
  .el-link {
    color: #667eea;
    font-weight: 500;
    transition: all 0.3s ease;
    
    &:hover {
      color: #764ba2;
      transform: translateY(-1px);
    }
  }
}

// Demo Accounts Enhancement
.demo-accounts {
  .demo-item {
    cursor: pointer;
    border-radius: 8px;
    transition: all 0.3s ease;
    
    &:hover {
      background: rgba(102, 126, 234, 0.08);
      transform: translateX(4px);
    }
    
    code {
      cursor: pointer;
      transition: all 0.3s ease;
      
      &:hover {
        background: rgba(102, 126, 234, 0.15);
        transform: scale(1.05);
      }
    }
  }
}
</style>
