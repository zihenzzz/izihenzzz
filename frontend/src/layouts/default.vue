<template>
  <div class="layout">
    <!-- Particle Background Container -->
    <div class="particles-container" ref="particlesContainer"></div>
    
    <!-- Emergency Navigation Button -->
    <button class="emergency-nav ripple-effect" @click="emergencyNavigate" title="紧急导航">
    </button>
    
    <!-- 头部导航 -->
    <header class="navbar">
      <div class="nav-container">
        <div class="logo ripple-effect" @click="goHome">
          <span class="logo-text">🚌 汽车票预订系统</span>
        </div>
        
        <nav class="nav-menu">
          <router-link to="/" class="nav-item ripple-effect" data-page="home">首页</router-link>
          <router-link to="/search" class="nav-item ripple-effect" data-page="search">班次查询</router-link>
          <router-link to="/orders" class="nav-item ripple-effect" data-page="orders">我的订单</router-link>
          <router-link to="/profile" class="nav-item ripple-effect" data-page="profile">个人中心</router-link>
          <router-link v-if="userStore.isAdmin" to="/admin" class="nav-item ripple-effect" data-page="admin">管理后台</router-link>
        </nav>
        
        <div class="user-info ripple-effect">
          <span id="userName">{{ userStore.isLoggedIn ? userStore.username : '未登录' }}</span>
          <div class="user-avatar" id="userAvatar">
            {{ userStore.isLoggedIn ? '👤' : '🔑' }}
          </div>
        </div>
      </div>
    </header>
    
    <!-- 主内容区域 -->
    <main class="main-content">
      <div class="page active">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </main>
    
    <!-- 底部 -->
    <footer class="footer">
      <div class="footer-content">
        <p>&copy; 2025 汽车票预订系统 | 祝您旅途愉快 🚌</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessageBox } from 'element-plus'
import { onMounted, ref } from 'vue'
import { useEnhanced } from '@/composables/useEnhanced'

const router = useRouter()
const userStore = useUserStore()
const particlesContainer = ref(null)
const { addButtonEffects, createRipple } = useEnhanced()

// 初始化涟漪效果
const initRippleEffect = () => {
  const elements = document.querySelectorAll('.ripple-effect')
  elements.forEach(el => {
    el.addEventListener('click', createRipple)
  })
}

// 初始化粒子效果
const initParticles = (container) => {
  if (!container) return
  const particleCount = 30
  for (let i = 0; i < particleCount; i++) {
    const particle = document.createElement('div')
    particle.className = 'particle'
    particle.style.left = Math.random() * 100 + '%'
    particle.style.top = Math.random() * 100 + '%'
    particle.style.animationDelay = Math.random() * 8 + 's'
    particle.style.animationDuration = (Math.random() * 4 + 4) + 's'
    const size = Math.random() * 4 + 2
    particle.style.width = size + 'px'
    particle.style.height = size + 'px'
    particle.style.opacity = Math.random() * 0.5 + 0.3
    container.appendChild(particle)
  }
}

const goHome = () => {
  router.push('/')
}

const handleCommand = async (command) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'orders':
      router.push('/orders')
      break
    case 'logout':
      try {
        await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        userStore.doLogout()
      } catch {
        // 用户取消
      }
      break
  }
}

const emergencyNavigate = () => {
  router.push('/')
}

// Initialize enhanced UI features on mount
onMounted(() => {
  // Initialize ripple effects
  initRippleEffect()
  
  // Initialize particles if container exists
  if (particlesContainer.value) {
    initParticles(particlesContainer.value)
  }
  
  // Add button effects
  setTimeout(() => {
    addButtonEffects()
  }, 200)
})
</script>

<style lang="scss" scoped>
.layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
  
  &-content {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
    height: 64px;
    display: flex;
    align-items: center;
    justify-content: space-between;
  }
  
  .logo {
    display: flex;
    align-items: center;
    cursor: pointer;
    transition: transform 0.3s;
    
    &:hover {
      transform: scale(1.02);
    }
    
    &-icon {
      font-size: 28px;
      color: #fff;
      margin-right: 8px;
    }
    
    &-text {
      font-size: 20px;
      font-weight: 600;
      color: #fff;
      letter-spacing: 1px;
    }
  }
  
  .nav {
    display: flex;
    gap: 32px;
    
    &-link {
      color: rgba(255, 255, 255, 0.9);
      font-size: 15px;
      font-weight: 500;
      padding: 8px 0;
      position: relative;
      transition: color 0.3s;
      
      &:hover,
      &.router-link-active {
        color: #fff;
        
        &::after {
          content: '';
          position: absolute;
          bottom: 0;
          left: 0;
          right: 0;
          height: 2px;
          background: #fff;
          border-radius: 1px;
        }
      }
    }
  }
  
  &-right {
    display: flex;
    align-items: center;
    gap: 12px;
  }
  
  .header-btn {
    padding: 8px 20px;
    border-radius: 20px;
    font-size: 14px;
    font-weight: 500;
    color: #fff;
    border: 1px solid rgba(255, 255, 255, 0.5);
    transition: all 0.3s;
    
    &:hover {
      background: rgba(255, 255, 255, 0.1);
      border-color: #fff;
    }
    
    &.primary {
      background: #fff;
      color: #1890ff;
      border-color: #fff;
      
      &:hover {
        background: rgba(255, 255, 255, 0.9);
      }
    }
  }
  
  .user-info {
    display: flex;
    align-items: center;
    gap: 6px;
    color: #fff;
    cursor: pointer;
    padding: 6px 12px;
    border-radius: 20px;
    transition: background 0.3s;
    
    &:hover {
      background: rgba(255, 255, 255, 0.1);
    }
    
    .username {
      font-weight: 500;
    }
  }
}

.main-content {
  flex: 1;
  padding: 24px 0;
}

.footer {
  background: #001529;
  color: rgba(255, 255, 255, 0.65);
  padding: 32px 0;
  
  &-content {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
    text-align: center;
    font-size: 14px;
  }
}

// Enhanced Global Layout Styles
.layout {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
  
  &::before {
    content: '';
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: 
      radial-gradient(circle at 20% 80%, rgba(120, 119, 198, 0.3) 0%, transparent 50%),
      radial-gradient(circle at 80% 20%, rgba(255, 119, 198, 0.3) 0%, transparent 50%),
      radial-gradient(circle at 40% 40%, rgba(120, 219, 255, 0.3) 0%, transparent 50%);
    pointer-events: none;
    z-index: -1;
  }
}

// Particle Background Container
.particles-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: -1;
}

// Enhanced Navbar
.navbar {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 8px 32px rgba(31, 38, 135, 0.37);
  position: sticky;
  top: 0;
  z-index: 1000;
  
  .nav-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
    height: 64px;
    display: flex;
    align-items: center;
    justify-content: space-between;
  }
  
  .logo {
    cursor: pointer;
    transition: all 0.3s ease;
    
    &:hover {
      transform: scale(1.05);
      filter: brightness(1.1);
    }
    
    .logo-text {
      font-size: 20px;
      font-weight: 600;
      color: #fff;
      letter-spacing: 1px;
      text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
    }
  }
  
  .nav-menu {
    display: flex;
    gap: 32px;
    
    .nav-item {
      color: rgba(255, 255, 255, 0.9);
      font-size: 15px;
      font-weight: 500;
      padding: 8px 16px;
      border-radius: 20px;
      position: relative;
      transition: all 0.3s ease;
      text-decoration: none;
      overflow: hidden;
      
      &::before {
        content: '';
        position: absolute;
        top: 0;
        left: -100%;
        width: 100%;
        height: 100%;
        background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
        transition: left 0.5s;
      }
      
      &:hover {
        color: #fff;
        background: rgba(255, 255, 255, 0.1);
        transform: translateY(-2px);
        
        &::before {
          left: 100%;
        }
      }
      
      &.router-link-active {
        color: #fff;
        background: rgba(255, 255, 255, 0.2);
        box-shadow: 0 4px 15px rgba(255, 255, 255, 0.2);
        
        &::after {
          content: '';
          position: absolute;
          bottom: -2px;
          left: 50%;
          transform: translateX(-50%);
          width: 20px;
          height: 2px;
          background: #fff;
          border-radius: 1px;
          animation: pulse 2s infinite;
        }
      }
    }
  }
  
  .user-info {
    display: flex;
    align-items: center;
    gap: 8px;
    color: #fff;
    cursor: pointer;
    padding: 6px 12px;
    border-radius: 20px;
    transition: all 0.3s ease;
    background: rgba(255, 255, 255, 0.1);
    backdrop-filter: blur(10px);
    
    &:hover {
      background: rgba(255, 255, 255, 0.2);
      transform: scale(1.02);
    }
    
    .user-avatar {
      width: 32px;
      height: 32px;
      border-radius: 50%;
      background: rgba(255, 255, 255, 0.2);
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 16px;
    }
  }
}

// Enhanced Main Content
.main-content {
  flex: 1;
  padding: 24px 0;
  position: relative;
  z-index: 1;
  
  .page {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
    min-height: calc(100vh - 200px);
    position: relative;
    
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: rgba(255, 255, 255, 0.05);
      border-radius: 20px;
      backdrop-filter: blur(10px);
      -webkit-backdrop-filter: blur(10px);
      border: 1px solid rgba(255, 255, 255, 0.1);
      box-shadow: 
        0 8px 32px rgba(31, 38, 135, 0.37),
        inset 0 1px 0 rgba(255, 255, 255, 0.1);
      margin: 20px 0;
    }
  }
}

// Enhanced Footer
.footer {
  background: rgba(0, 0, 0, 0.3);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.8);
  padding: 32px 0;
  position: relative;
  z-index: 1;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 1px;
    background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  }
  
  .footer-content {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
    text-align: center;
    font-size: 14px;
    
    p {
      margin: 0;
      opacity: 0.9;
    }
  }
}

// Emergency Navigation Button
.emergency-nav {
  position: fixed;
  top: 50%;
  right: 20px;
  transform: translateY(-50%);
  z-index: 1001;
  background: rgba(220, 53, 69, 0.9);
  color: white;
  border: none;
  padding: 12px;
  border-radius: 50%;
  cursor: pointer;
  font-size: 18px;
  box-shadow: 0 4px 20px rgba(220, 53, 69, 0.4);
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  
  &:hover {
    background: rgba(220, 53, 69, 1);
    transform: translateY(-50%) scale(1.1);
    box-shadow: 0 6px 25px rgba(220, 53, 69, 0.6);
  }
  
  &::before {
    content: '🚨';
    display: block;
    animation: emergency-pulse 2s infinite;
  }
}

// Enhanced Ripple Effect for Interactive Elements
.ripple-effect {
  position: relative;
  overflow: hidden;
  
  &::after {
    content: '';
    position: absolute;
    top: 50%;
    left: 50%;
    width: 0;
    height: 0;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.3);
    transform: translate(-50%, -50%);
    transition: width 0.6s, height 0.6s;
  }
  
  &:active::after {
    width: 300px;
    height: 300px;
  }
}

// Animations
@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

@keyframes emergency-pulse {
  0%, 100% { 
    transform: scale(1);
    opacity: 1;
  }
  50% { 
    transform: scale(1.1);
    opacity: 0.8;
  }
}

@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-10px); }
}

@keyframes glow {
  0%, 100% { 
    box-shadow: 0 0 20px rgba(120, 119, 198, 0.3);
  }
  50% { 
    box-shadow: 0 0 30px rgba(120, 119, 198, 0.6);
  }
}

// Enhanced transitions
.fade-enter-active,
.fade-leave-active {
  transition: all 0.4s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(20px);
}

// Glass morphism utility classes
.glass-card {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(31, 38, 135, 0.37);
}

.glass-button {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  color: white;
  border-radius: 20px;
  padding: 8px 20px;
  transition: all 0.3s ease;
  cursor: pointer;
  
  &:hover {
    background: rgba(255, 255, 255, 0.2);
    transform: translateY(-2px);
    box-shadow: 0 4px 15px rgba(255, 255, 255, 0.2);
  }
  
  &:active {
    transform: translateY(0);
  }
}
</style>
