<template>
  <div class="home">
    <!-- 搜索区域 -->
    <section class="hero-section">
      <div class="hero-background">
        <div class="hero-overlay"></div>
      </div>
      <div class="hero-content">
        <h1 class="hero-title">便捷出行，从这里开始</h1>
        <p class="hero-subtitle">汽车票在线预订 · 快速查询 · 轻松购票</p>
        
        <!-- 搜索框 -->
        <div class="search-box">
          <div class="search-tabs">
            <span class="search-tab active">🚌 汽车票</span>
          </div>
          
          <div class="search-form">
            <div class="search-row">
              <div class="search-item">
                <label>出发地</label>
                <el-select
                  v-model="searchForm.departure"
                  placeholder="请选择出发城市"
                  size="large"
                  filterable
                  class="ripple-effect"
                >
                  <el-option
                    v-for="station in stations"
                    :key="station.id"
                    :label="station.name"
                    :value="station.name"
                  />
                </el-select>
              </div>
              
              <div class="swap-btn ripple-effect" @click="swapStations">
                <el-icon><Switch /></el-icon>
              </div>
              
              <div class="search-item">
                <label>目的地</label>
                <el-select
                  v-model="searchForm.arrival"
                  placeholder="请选择到达城市"
                  size="large"
                  filterable
                  class="ripple-effect"
                >
                  <el-option
                    v-for="station in stations"
                    :key="station.id"
                    :label="station.name"
                    :value="station.name"
                  />
                </el-select>
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
                  查询班次
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
              <span class="trains-count">{{ line.count }}趟班次</span>
              <span class="price">¥{{ line.price }}起</span>
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
            <p>班次信息实时更新，票价余票一目了然</p>
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
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Location, Switch, Search, Right, Timer, CircleCheck, Lock, Service } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getAllStations } from '@/api/train'
import dayjs from 'dayjs'

const router = useRouter()
const loading = ref(false)
const stations = ref([])

const searchForm = reactive({
  departure: '',
  arrival: '',
  date: dayjs().format('YYYY-MM-DD')
})

const hotLines = ref([
  { id: 1, from: '广州天河客运站', to: '深圳罗湖汽车站', count: 15, price: 65 },
  { id: 2, from: '杭州客运中心站', to: '南京汽车客运站', count: 8, price: 98 },
  { id: 3, from: '成都新南门汽车站', to: '重庆龙头寺汽车站', count: 12, price: 110 },
  { id: 4, from: '上海长途客运总站', to: '杭州客运中心站', count: 10, price: 78 },
  { id: 5, from: '武汉傅家坡客运站', to: '成都新南门汽车站', count: 6, price: 260 },
  { id: 6, from: '北京六里桥客运站', to: '上海长途客运总站', count: 5, price: 280 }
])

// 获取车站列表
const fetchStations = async () => {
  try {
    const response = await getAllStations()
    if (response.data.code === 200) {
      stations.value = response.data.data || []
    }
  } catch (error) {
    console.error('获取车站列表失败', error)
  }
}

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

// 搜索班次
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
  searchForm.date = dayjs().format('YYYY-MM-DD')
  
  router.push({
    name: 'Search',
    query: {
      from: line.from,
      to: line.to,
      date: searchForm.date
    }
  })
}

onMounted(() => {
  fetchStations()
})
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
    background: linear-gradient(135deg, #1e88e5 0%, #1565c0 50%, #0d47a1 100%);
    
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
      animation: background-shift 20s ease-in-out infinite;
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
    text-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
  }
  
  .hero-subtitle {
    font-size: 20px;
    color: rgba(255, 255, 255, 0.95);
    margin-bottom: 48px;
    font-weight: 300;
  }
}

.search-box {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  
  .search-tabs {
    padding: 20px 28px;
    border-bottom: 1px solid rgba(0, 0, 0, 0.05);
    background: rgba(255, 255, 255, 0.5);
    
    .search-tab {
      font-size: 18px;
      font-weight: 700;
      color: #1e88e5;
      padding-bottom: 12px;
      border-bottom: 3px solid #1e88e5;
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
        background: linear-gradient(135deg, #1e88e5 0%, #1565c0 100%);
        border: none;
        border-radius: 24px;
        box-shadow: 0 8px 20px rgba(30, 136, 229, 0.3);
        transition: all 0.3s ease;
        
        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 12px 25px rgba(30, 136, 229, 0.4);
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
    color: #1e88e5;
    transition: all 0.3s ease;
    margin-bottom: 4px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    border: 2px solid rgba(30, 136, 229, 0.2);
    
    &:hover {
      background: linear-gradient(135deg, #1e88e5 0%, #1565c0 100%);
      color: white;
      transform: rotate(180deg) scale(1.1);
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
  background: #f5f7fa;
  
  .hot-lines-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 24px;
  }
  
  .hot-line-card {
    background: #fff;
    border-radius: 20px;
    padding: 28px;
    cursor: pointer;
    transition: all 0.4s ease;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
    
    &:hover {
      transform: translateY(-8px);
      box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
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
      }
      
      .arrow {
        color: #1e88e5;
        font-size: 28px;
      }
    }
    
    .line-info {
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      .trains-count {
        color: #666;
        font-size: 15px;
      }
      
      .price {
        color: #ff6600;
        font-size: 18px;
        font-weight: 600;
      }
    }
  }
}

.features-section {
  padding: 80px 0;
  
  .features-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 28px;
  }
  
  .feature-card {
    text-align: center;
    padding: 36px 28px;
    background: #fff;
    border-radius: 24px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
    transition: all 0.4s ease;
    
    &:hover {
      transform: translateY(-8px);
      box-shadow: 0 20px 40px rgba(0, 0, 0, 0.12);
    }
    
    .feature-icon {
      width: 72px;
      height: 72px;
      border-radius: 50%;
      background: linear-gradient(135deg, #1e88e5 0%, #1565c0 100%);
      display: flex;
      align-items: center;
      justify-content: center;
      margin: 0 auto 20px;
      font-size: 32px;
      color: #fff;
      box-shadow: 0 8px 20px rgba(30, 136, 229, 0.3);
      
      &.success {
        background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
      }
      
      &.warning {
        background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
      }
      
      &.danger {
        background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
      }
    }
    
    h3 {
      font-size: 20px;
      font-weight: 700;
      margin-bottom: 12px;
      color: #333;
    }
    
    p {
      font-size: 15px;
      color: #666;
      line-height: 1.7;
    }
  }
}

@keyframes background-shift {
  0%, 100% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
}

.ripple-effect {
  position: relative;
  overflow: hidden;
}

:deep(.el-select) {
  width: 100%;
}

:deep(.el-input__wrapper) {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

@media (max-width: 768px) {
  .hero-section {
    height: auto;
    padding: 40px 0;
    
    .hero-title {
      font-size: 32px;
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
}
</style>
