<template>
  <div class="home">
    <!-- 搜索区域 -->
    <section class="hero-section">
      <div class="hero-background">
        <div class="hero-overlay"></div>
      </div>
      <div class="hero-content">
        <h1 class="hero-title">便捷出行，从这里开始</h1>
        <p class="hero-subtitle">快速查询、轻松预订、安全出行</p>
        
        <!-- 搜索框 -->
        <div class="search-box">
          <div class="search-tabs">
            <span class="search-tab active">火车票</span>
          </div>
          
          <div class="search-form">
            <div class="search-row">
              <div class="search-item">
                <label>出发地</label>
                <el-input
                  v-model="searchForm.departure"
                  placeholder="请输入出发城市"
                  size="large"
                  :prefix-icon="Location"
                  class="ripple-effect"
                />
              </div>
              
              <div class="swap-btn ripple-effect" @click="swapStations">
                <el-icon><Switch /></el-icon>
              </div>
              
              <div class="search-item">
                <label>目的地</label>
                <el-input
                  v-model="searchForm.arrival"
                  placeholder="请输入到达城市"
                  size="large"
                  :prefix-icon="Location"
                  class="ripple-effect"
                />
              </div>
              
              <div class="search-item date-picker">
                <label>出发日期</label>
                <el-date-picker
                  v-model="searchForm.date"
                  type="date"
                  placeholder="请选择日期"
                  size="large"
                  :disabled-date="disabledDate"
                  value-format="YYYY-MM-DD"
                />
              </div>
              
              <div class="search-item search-btn">
                <el-button type="primary" size="large" @click="handleSearch" :loading="loading">
                  <el-icon><Search /></el-icon>
                  查询车次
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
    
    <!-- 热门线路 -->
    <section class="hot-lines-section">
      <div class="container">
        <h2 class="section-title">热门线路</h2>
        <div class="hot-lines-grid">
          <div 
            v-for="line in hotLines" 
            :key="line.id" 
            class="hot-line-card ripple-effect"
            @click="quickSearch(line)"
          >
            <div class="line-route">
              <span class="station">{{ line.from }}</span>
              <span class="arrow">
                <el-icon><Right /></el-icon>
              </span>
              <span class="station">{{ line.to }}</span>
            </div>
            <div class="line-info">
              <span class="trains-count">{{ line.count }}趟车次</span>
              <el-button type="primary" size="small" text>立即查询</el-button>
            </div>
          </div>
        </div>
      </div>
    </section>
    
    <!-- 特色服务 -->
    <section class="features-section">
      <div class="container">
        <h2 class="section-title">我们的服务</h2>
        <div class="features-grid">
          <div class="feature-card ripple-effect">
            <div class="feature-icon">
              <el-icon><Timer /></el-icon>
            </div>
            <h3>实时查询</h3>
            <p>车次信息实时更新，票价余票一目了然</p>
          </div>
          <div class="feature-card ripple-effect">
            <div class="feature-icon success">
              <el-icon><CircleCheck /></el-icon>
            </div>
            <h3>快速预订</h3>
            <p>简化购票流程，三步即可完成预订</p>
          </div>
          <div class="feature-card ripple-effect">
            <div class="feature-icon warning">
              <el-icon><Lock /></el-icon>
            </div>
            <h3>安全支付</h3>
            <p>多重安全保护，支付更放心</p>
          </div>
          <div class="feature-card ripple-effect">
            <div class="feature-icon danger">
              <el-icon><Service /></el-icon>
            </div>
            <h3>贴心服务</h3>
            <p>24小时客服支持，退改签更便捷</p>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { Location, Switch, Search, Right, Timer, CircleCheck, Lock, Service } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'

const router = useRouter()
const loading = ref(false)

const searchForm = reactive({
  departure: '',
  arrival: '',
  date: dayjs().format('YYYY-MM-DD')
})

const hotLines = ref([
  { id: 1, from: '北京', to: '上海', count: 42 },
  { id: 2, from: '广州', to: '深圳', count: 38 },
  { id: 3, from: '杭州', to: '南京', count: 25 },
  { id: 4, from: '武汉', to: '成都', count: 18 },
  { id: 5, from: '西安', to: '北京', count: 22 },
  { id: 6, from: '重庆', to: '上海', count: 15 }
])

// 禁用过去的日期
const disabledDate = (time) => {
  return time.getTime() < Date.now() - 8.64e7
}

// 交换出发地和目的地
const swapStations = () => {
  const temp = searchForm.departure
  searchForm.departure = searchForm.arrival
  searchForm.arrival = temp
}

// 搜索车次
const handleSearch = () => {
  if (!searchForm.departure || !searchForm.arrival || !searchForm.date) {
    ElMessage.warning('请填写完整的查询信息')
    return
  }
  
  loading.value = true
  router.push({
    name: 'Search',
    query: {
      from: searchForm.departure,
      to: searchForm.arrival,
      date: searchForm.date
    }
  })
}

// 快捷搜索
const quickSearch = (line) => {
  searchForm.departure = line.from
  searchForm.arrival = line.to
  handleSearch()
}
</script>

<style lang="scss" scoped>
.home {
  min-height: 100%;
  position: relative;
}

.hero-section {
  position: relative;
  height: 520px;
  overflow: hidden;
  
  .hero-background {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: 
      linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
    
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: 
        radial-gradient(circle at 25% 25%, rgba(255, 255, 255, 0.1) 0%, transparent 50%),
        radial-gradient(circle at 75% 75%, rgba(255, 255, 255, 0.05) 0%, transparent 50%),
        url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><circle cx="50" cy="50" r="40" fill="none" stroke="rgba(255,255,255,0.1)" stroke-width="0.5"/></svg>') repeat;
      background-size: 100px 100px, 200px 200px, 100px 100px;
      animation: background-shift 20s ease-in-out infinite;
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
    }
  }
  
  .hero-overlay {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.2);
  }
  
  .hero-content {
    position: relative;
    z-index: 2;
    max-width: 1200px;
    margin: 0 auto;
    padding: 80px 20px 0;
    text-align: center;
  }
  
  .hero-title {
    font-size: 48px;
    font-weight: 800;
    color: #fff;
    margin-bottom: 16px;
    text-shadow: 
      0 2px 8px rgba(0, 0, 0, 0.3),
      0 0 20px rgba(255, 255, 255, 0.2);
    animation: title-glow 3s ease-in-out infinite alternate;
    background: linear-gradient(45deg, #fff, #f0f8ff, #fff);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }
  
  .hero-subtitle {
    font-size: 20px;
    color: rgba(255, 255, 255, 0.95);
    margin-bottom: 48px;
    font-weight: 300;
    text-shadow: 0 1px 3px rgba(0, 0, 0, 0.2);
    animation: fade-in-up 1s ease-out 0.5s both;
  }
}

.search-box {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: 20px;
  box-shadow: 
    0 20px 40px rgba(0, 0, 0, 0.1),
    0 0 0 1px rgba(255, 255, 255, 0.2),
    inset 0 1px 0 rgba(255, 255, 255, 0.4);
  overflow: hidden;
  animation: slide-in-up 0.8s ease-out 0.3s both;
  position: relative;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 1px;
    background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.6), transparent);
  }
  
  .search-tabs {
    padding: 20px 28px;
    border-bottom: 1px solid rgba(0, 0, 0, 0.05);
    background: rgba(255, 255, 255, 0.5);
    
    .search-tab {
      font-size: 18px;
      font-weight: 700;
      color: #667eea;
      padding-bottom: 12px;
      border-bottom: 3px solid #667eea;
      position: relative;
      
      &::after {
        content: '';
        position: absolute;
        bottom: -3px;
        left: 0;
        right: 0;
        height: 3px;
        background: linear-gradient(90deg, #667eea, #764ba2, #f093fb);
        border-radius: 2px;
        animation: shimmer 2s infinite;
      }
    }
  }
  
  .search-form {
    padding: 32px;
  }
  
  .search-row {
    display: flex;
    align-items: flex-end;
    gap: 20px;
  }
  
  .search-item {
    flex: 1;
    
    label {
      display: block;
      font-size: 15px;
      color: #555;
      margin-bottom: 10px;
      font-weight: 500;
    }
    
    &.date-picker {
      flex: 1.2;
    }
    
    &.search-btn {
      flex: 0 0 auto;
      
      .el-button {
        width: 160px;
        height: 48px;
        font-size: 16px;
        font-weight: 600;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border: none;
        border-radius: 24px;
        box-shadow: 0 8px 20px rgba(102, 126, 234, 0.3);
        transition: all 0.3s ease;
        
        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 12px 25px rgba(102, 126, 234, 0.4);
        }
        
        &:active {
          transform: translateY(0);
        }
      }
    }
  }
  
  .swap-btn {
    width: 48px;
    height: 48px;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.9);
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    color: #667eea;
    transition: all 0.3s ease;
    margin-bottom: 4px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    border: 2px solid rgba(102, 126, 234, 0.2);
    
    &:hover {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      transform: rotate(180deg) scale(1.1);
      box-shadow: 0 8px 20px rgba(102, 126, 234, 0.3);
    }
    
    .el-icon {
      font-size: 20px;
    }
  }
}

.section-title {
  font-size: 28px;
  font-weight: 600;
  text-align: center;
  margin-bottom: 32px;
  color: #333;
}

.hot-lines-section {
  padding: 80px 0;
  background: rgba(255, 255, 255, 0.02);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  
  .hot-lines-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 24px;
  }
  
  .hot-line-card {
    background: rgba(255, 255, 255, 0.9);
    backdrop-filter: blur(20px);
    -webkit-backdrop-filter: blur(20px);
    border-radius: 20px;
    padding: 28px;
    cursor: pointer;
    transition: all 0.4s ease;
    box-shadow: 
      0 8px 32px rgba(0, 0, 0, 0.08),
      0 0 0 1px rgba(255, 255, 255, 0.3),
      inset 0 1px 0 rgba(255, 255, 255, 0.6);
    position: relative;
    overflow: hidden;
    
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: -100%;
      width: 100%;
      height: 100%;
      background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.4), transparent);
      transition: left 0.6s;
    }
    
    &:hover {
      transform: translateY(-8px) scale(1.02);
      box-shadow: 
        0 20px 40px rgba(0, 0, 0, 0.15),
        0 0 0 1px rgba(102, 126, 234, 0.3),
        inset 0 1px 0 rgba(255, 255, 255, 0.8);
      
      &::before {
        left: 100%;
      }
    }
    
    .line-route {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 16px;
      margin-bottom: 20px;
      
      .station {
        font-size: 22px;
        font-weight: 700;
        color: #333;
        text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
      }
      
      .arrow {
        color: #667eea;
        font-size: 28px;
        animation: arrow-bounce 2s infinite;
      }
    }
    
    .line-info {
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      .trains-count {
        color: #666;
        font-size: 15px;
        font-weight: 500;
      }
      
      .el-button {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border: none;
        border-radius: 20px;
        padding: 8px 16px;
        font-weight: 600;
        transition: all 0.3s ease;
        
        &:hover {
          transform: scale(1.05);
          box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
        }
      }
    }
  }
}

.features-section {
  padding: 80px 0;
  background: rgba(255, 255, 255, 0.02);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  
  .features-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 28px;
  }
  
  .feature-card {
    text-align: center;
    padding: 36px 28px;
    background: rgba(255, 255, 255, 0.9);
    backdrop-filter: blur(20px);
    -webkit-backdrop-filter: blur(20px);
    border-radius: 24px;
    box-shadow: 
      0 8px 32px rgba(0, 0, 0, 0.08),
      0 0 0 1px rgba(255, 255, 255, 0.3),
      inset 0 1px 0 rgba(255, 255, 255, 0.6);
    transition: all 0.4s ease;
    position: relative;
    overflow: hidden;
    
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
      opacity: 0;
      transition: opacity 0.3s ease;
    }
    
    &:hover {
      transform: translateY(-8px) scale(1.03);
      box-shadow: 
        0 20px 40px rgba(0, 0, 0, 0.12),
        0 0 0 1px rgba(102, 126, 234, 0.2),
        inset 0 1px 0 rgba(255, 255, 255, 0.8);
      
      &::before {
        opacity: 1;
      }
    }
    
    .feature-icon {
      width: 72px;
      height: 72px;
      border-radius: 50%;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      display: flex;
      align-items: center;
      justify-content: center;
      margin: 0 auto 20px;
      font-size: 32px;
      color: #fff;
      box-shadow: 0 8px 20px rgba(102, 126, 234, 0.3);
      transition: all 0.3s ease;
      position: relative;
      z-index: 1;
      
      &::before {
        content: '';
        position: absolute;
        top: -2px;
        left: -2px;
        right: -2px;
        bottom: -2px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border-radius: 50%;
        z-index: -1;
        opacity: 0;
        transition: opacity 0.3s ease;
      }
      
      &.success {
        background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
        
        &::before {
          background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
        }
      }
      
      &.warning {
        background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
        
        &::before {
          background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
        }
      }
      
      &.danger {
        background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
        
        &::before {
          background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
        }
      }
      
      &:hover {
        transform: scale(1.1);
        
        &::before {
          opacity: 1;
          animation: icon-pulse 1s infinite;
        }
      }
    }
    
    h3 {
      font-size: 20px;
      font-weight: 700;
      margin-bottom: 12px;
      color: #333;
      position: relative;
      z-index: 1;
    }
    
    p {
      font-size: 15px;
      color: #666;
      line-height: 1.7;
      position: relative;
      z-index: 1;
    }
  }
}

// Enhanced Animations
@keyframes background-shift {
  0%, 100% {
    background-position: 0% 50%, 100% 50%, 0 0;
  }
  50% {
    background-position: 100% 50%, 0% 50%, 0 0;
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

@keyframes title-glow {
  0% {
    text-shadow: 
      0 2px 8px rgba(0, 0, 0, 0.3),
      0 0 20px rgba(255, 255, 255, 0.2);
  }
  100% {
    text-shadow: 
      0 2px 8px rgba(0, 0, 0, 0.3),
      0 0 30px rgba(255, 255, 255, 0.4),
      0 0 40px rgba(102, 126, 234, 0.3);
  }
}

@keyframes fade-in-up {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slide-in-up {
  from {
    opacity: 0;
    transform: translateY(50px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes shimmer {
  0% {
    background-position: -100% 0;
  }
  100% {
    background-position: 100% 0;
  }
}

@keyframes arrow-bounce {
  0%, 20%, 50%, 80%, 100% {
    transform: translateX(0);
  }
  40% {
    transform: translateX(5px);
  }
  60% {
    transform: translateX(3px);
  }
}

@keyframes icon-pulse {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
  100% {
    transform: scale(1);
  }
}

// Ripple Effect for Interactive Elements
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

// Enhanced Input Styles
:deep(.el-input__wrapper) {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  
  &:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  }
  
  &.is-focus {
    box-shadow: 0 4px 20px rgba(102, 126, 234, 0.2);
    border-color: #667eea;
  }
}

:deep(.el-date-editor) {
  .el-input__wrapper {
    border-radius: 12px;
  }
}

// 响应式
@media (max-width: 768px) {
  .hero-section {
    height: auto;
    padding: 40px 0;
    
    .hero-title {
      font-size: 32px;
    }
    
    .hero-subtitle {
      font-size: 16px;
    }
  }
  
  .search-box .search-row {
    flex-wrap: wrap;
    gap: 12px;
  }
  
  .search-box .search-item {
    flex: 1 1 45%;
    
    &.date-picker,
    &.search-btn {
      flex: 1 1 100%;
    }
  }
  
  .swap-btn {
    display: none;
  }
  
  .hot-lines-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .features-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .hot-line-card,
  .feature-card {
    padding: 20px;
  }
}
</style>
