<template>
  <div class="search-page">
    <div class="container">
      <!-- 搜索条件 -->
      <div class="search-filter glass-card ripple-effect">
        <h3 class="filter-title">查询条件</h3>
        <div class="filter-form">
          <el-row :gutter="16">
            <el-col :span="6">
              <el-input
                v-model="searchParams.departure"
                placeholder="出发城市"
                prefix-icon="Location"
                clearable
              />
            </el-col>
            <el-col :span="6">
              <el-input
                v-model="searchParams.arrival"
                placeholder="到达城市"
                prefix-icon="Location"
                clearable
              />
            </el-col>
            <el-col :span="6">
              <el-date-picker
                v-model="searchParams.date"
                type="date"
                placeholder="出发日期"
                :disabled-date="disabledDate"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-col>
            <el-col :span="6">
              <el-button type="primary" @click="handleSearch" :loading="loading">
                <el-icon><Search /></el-icon>
                查询
              </el-button>
            </el-col>
          </el-row>
        </div>
      </div>
      
      <!-- 搜索结果 -->
      <div class="search-results">
        <div class="results-header" v-if="trains.length > 0">
          <span class="results-count">共找到 <strong>{{ trains.length }}</strong> 趟车次</span>
          <div class="sort-options">
            <span>排序：</span>
            <el-radio-group v-model="sortBy" size="small">
              <el-radio-button label="departureTime">出发时间</el-radio-button>
              <el-radio-button label="duration">历时</el-radio-button>
              <el-radio-button label="price">票价</el-radio-button>
            </el-radio-group>
          </div>
        </div>
        
        <!-- 加载状态 -->
        <div v-if="loading" class="loading-state">
          <el-skeleton :rows="5" animated />
        </div>
        
        <!-- 空状态 -->
        <div v-else-if="trains.length === 0 && hasSearched" class="empty-state">
          <el-empty description="未找到相关车次">
            <template #description>
              <p>请尝试修改查询条件</p>
            </template>
            <el-button type="primary" @click="resetSearch">重新查询</el-button>
          </el-empty>
        </div>
        
        <!-- 车次列表 -->
        <div v-else class="train-list">
          <div 
            v-for="train in sortedTrains" 
            :key="train.id" 
            class="train-card"
          >
            <div class="train-info">
              <div class="train-time">
                <span class="time">{{ formatTime(train.departureTime) }}</span>
                <span class="station">{{ train.departureStationName }}</span>
              </div>
              
              <div class="train-duration">
                <span class="duration-text">{{ formatDuration(train.duration) }}</span>
                <div class="duration-line">
                  <div class="line"></div>
                  <el-icon><Right /></el-icon>
                </div>
                <span class="arrival-time">{{ formatTime(train.arrivalTime) }}</span>
              </div>
              
              <div class="train-time">
                <span class="time">{{ formatTime(train.arrivalTime) }}</span>
                <span class="station">{{ train.arrivalStationName }}</span>
              </div>
            </div>
            
            <div class="train-type">
              <el-tag size="small" :type="getTrainTypeColor(train.trainType)">
                {{ train.trainCode }}
              </el-tag>
              <span class="type-name">{{ getTrainTypeName(train.trainType) }}</span>
            </div>
            
            <div class="train-seats">
              <div 
                v-if="train.secondClassPrice > 0" 
                class="seat-item"
                :class="{ 'sold-out': getTicketCount(train, 'secondClass') === 0 }"
                @click="bookTicket(train, 'SECOND_CLASS')"
              >
                <span class="seat-type">二等座</span>
                <span class="seat-price">¥{{ train.secondClassPrice }}</span>
                <span class="seat-count">
                  {{ getTicketCount(train, 'secondClass') > 0 ? getTicketCount(train, 'secondClass') + '张' : '无票' }}
                </span>
              </div>
              
              <div 
                v-if="train.firstClassPrice > 0" 
                class="seat-item"
                :class="{ 'sold-out': getTicketCount(train, 'firstClass') === 0 }"
                @click="bookTicket(train, 'FIRST_CLASS')"
              >
                <span class="seat-type">一等座</span>
                <span class="seat-price">¥{{ train.firstClassPrice }}</span>
                <span class="seat-count">
                  {{ getTicketCount(train, 'firstClass') > 0 ? getTicketCount(train, 'firstClass') + '张' : '无票' }}
                </span>
              </div>
              
              <div 
                v-if="train.hardSleeperPrice > 0" 
                class="seat-item"
                :class="{ 'sold-out': getTicketCount(train, 'hardSleeper') === 0 }"
                @click="bookTicket(train, 'HARD_SLEEPER')"
              >
                <span class="seat-type">硬卧</span>
                <span class="seat-price">¥{{ train.hardSleeperPrice }}</span>
                <span class="seat-count">
                  {{ getTicketCount(train, 'hardSleeper') > 0 ? getTicketCount(train, 'hardSleeper') + '张' : '无票' }}
                </span>
              </div>
              
              <div 
                v-if="train.softSleeperPrice > 0" 
                class="seat-item"
                :class="{ 'sold-out': getTicketCount(train, 'softSleeper') === 0 }"
                @click="bookTicket(train, 'SOFT_SLEEPER')"
              >
                <span class="seat-type">软卧</span>
                <span class="seat-price">¥{{ train.softSleeperPrice }}</span>
                <span class="seat-count">
                  {{ getTicketCount(train, 'softSleeper') > 0 ? getTicketCount(train, 'softSleeper') + '张' : '无票' }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { searchTrains } from '@/api/train'
import { useUserStore } from '@/stores/user'
import { Search, Right } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loading = ref(false)
const hasSearched = ref(false)
const sortBy = ref('departureTime')
const trains = ref([])

const searchParams = reactive({
  departure: '',
  arrival: '',
  date: dayjs().format('YYYY-MM-DD')
})

// 禁用过去的日期
const disabledDate = (time) => {
  return time.getTime() < Date.now() - 8.64e7
}

// 获取车次余票数量
const getTicketCount = (train, seatType) => {
  if (train.extra && train.extra[seatType] !== undefined) {
    return train.extra[seatType]
  }
  return Math.floor(Math.random() * 50) + 1 // 模拟数据
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return '--:--'
  if (typeof time === 'string') {
    return time.length <= 5 ? time : time.substring(0, 5)
  }
  return dayjs(time).format('HH:mm')
}

// 格式化历时
const formatDuration = (minutes) => {
  if (!minutes) return '--'
  const hours = Math.floor(minutes / 60)
  const mins = minutes % 60
  return `${hours}小时${mins}分`
}

// 获取车次类型颜色
const getTrainTypeColor = (type) => {
  const colors = {
    'G': 'success',
    'D': 'primary',
    'K': 'warning',
    'T': 'info'
  }
  return colors[type] || 'info'
}

// 获取车次类型名称
const getTrainTypeName = (type) => {
  const names = {
    'G': '高铁',
    'D': '动车',
    'K': '普快',
    'T': '特快'
  }
  return names[type] || '列车'
}

// 排序后的车次列表
const sortedTrains = computed(() => {
  const sorted = [...trains.value]
  switch (sortBy.value) {
    case 'departureTime':
      return sorted.sort((a, b) => a.departureTime.localeCompare(b.departureTime))
    case 'duration':
      return sorted.sort((a, b) => (a.duration || 0) - (b.duration || 0))
    case 'price':
      return sorted.sort((a, b) => (a.secondClassPrice || 0) - (b.secondClassPrice || 0))
    default:
      return sorted
  }
})

// 搜索车次
const handleSearch = async () => {
  if (!searchParams.departure || !searchParams.arrival || !searchParams.date) {
    ElMessage.warning('请填写完整的查询信息')
    return
  }
  
  loading.value = true
  hasSearched.value = true
  
  try {
    const response = await searchTrains({
      departureStation: searchParams.departure,
      arrivalStation: searchParams.arrival,
      travelDate: searchParams.date
    })
    
    if (response.data.code === 200) {
      trains.value = response.data.data || []
      if (trains.value.length === 0) {
        ElMessage.info('未找到相关车次')
      }
    } else {
      ElMessage.error(response.data.message || '查询失败')
    }
  } catch (error) {
    ElMessage.error('查询失败，请检查网络连接')
  } finally {
    loading.value = false
  }
}

// 重置搜索
const resetSearch = () => {
  searchParams.departure = ''
  searchParams.arrival = ''
  searchParams.date = dayjs().format('YYYY-MM-DD')
  hasSearched.value = false
  trains.value = []
}

// 预订车票
const bookTicket = (train, seatType) => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录后再预订')
    router.push({ name: 'Login', query: { redirect: `/booking/${train.id}?seatType=${seatType}&date=${searchParams.date}` } })
    return
  }
  
  router.push({
    name: 'Booking',
    params: { trainId: train.id },
    query: { 
      seatType,
      date: searchParams.date,
      from: searchParams.departure,
      to: searchParams.arrival
    }
  })
}

// 初始化搜索参数
onMounted(() => {
  if (route.query.from) searchParams.departure = route.query.from
  if (route.query.to) searchParams.arrival = route.query.to
  if (route.query.date) searchParams.date = route.query.date
  
  if (searchParams.departure && searchParams.arrival && searchParams.date) {
    handleSearch()
  }
})
</script>

<style lang="scss" scoped>
.search-page {
  min-height: calc(100vh - 64px - 72px);
  padding: 24px 0;
  position: relative;
}

.search-filter {
  margin-bottom: 24px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 32px;
  box-shadow: 
    0 20px 40px rgba(0, 0, 0, 0.1),
    0 0 0 1px rgba(255, 255, 255, 0.2),
    inset 0 1px 0 rgba(255, 255, 255, 0.4);
  position: relative;
  overflow: hidden;
  animation: slide-in-up 0.6s ease-out;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 1px;
    background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.6), transparent);
  }
  
  .filter-title {
    font-size: 18px;
    font-weight: 700;
    margin-bottom: 20px;
    color: #333;
    position: relative;
    
    &::after {
      content: '';
      position: absolute;
      bottom: -8px;
      left: 0;
      width: 40px;
      height: 3px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border-radius: 2px;
    }
  }
  
  .filter-form {
    .el-button {
      width: 100%;
      height: 40px;
      border-radius: 20px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border: none;
      font-weight: 600;
      transition: all 0.3s ease;
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
      }
    }
    
    .el-input__wrapper,
    .el-date-editor__inner {
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
  }
}

.results-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding: 0 4px;
  
  .results-count {
    font-size: 14px;
    color: #666;
    
    strong {
      color: #1890ff;
      font-size: 16px;
    }
  }
}

.loading-state,
.empty-state {
  background: #fff;
  border-radius: 8px;
  padding: 60px 20px;
}

.train-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.train-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 24px;
  display: flex;
  align-items: center;
  box-shadow: 
    0 8px 32px rgba(0, 0, 0, 0.08),
    0 0 0 1px rgba(255, 255, 255, 0.3),
    inset 0 1px 0 rgba(255, 255, 255, 0.6);
  transition: all 0.4s ease;
  position: relative;
  overflow: hidden;
  animation: slide-in-up 0.6s ease-out;
  
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
    transform: translateY(-4px);
    box-shadow: 
      0 16px 40px rgba(0, 0, 0, 0.12),
      0 0 0 1px rgba(102, 126, 234, 0.2),
      inset 0 1px 0 rgba(255, 255, 255, 0.8);
    
    &::before {
      left: 100%;
    }
  }
  
  .train-info {
    flex: 1;
    display: flex;
    align-items: center;
    gap: 40px;
  }
  
  .train-time {
    text-align: center;
    
    .time {
      display: block;
      font-size: 24px;
      font-weight: 600;
      color: #333;
    }
    
    .station {
      font-size: 13px;
      color: #999;
    }
  }
  
  .train-duration {
    text-align: center;
    min-width: 160px;
    
    .duration-text {
      font-size: 13px;
      color: #666;
      display: block;
      margin-bottom: 4px;
    }
    
    .duration-line {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 4px;
      
      .line {
        width: 100px;
        height: 2px;
        background: linear-gradient(90deg, #1890ff 0%, #52c41a 100%);
      }
      
      .el-icon {
        color: #52c41a;
      }
    }
    
    .arrival-time {
      display: block;
      font-size: 12px;
      color: #999;
      margin-top: 4px;
    }
  }
  
  .train-type {
    text-align: center;
    padding: 0 24px;
    border-right: 1px solid #f0f0f0;
    margin-right: 24px;
    
    .type-name {
      display: block;
      font-size: 12px;
      color: #666;
      margin-top: 4px;
    }
  }
  
  .train-seats {
    display: flex;
    gap: 16px;
  }
  
  .seat-item {
    text-align: center;
    padding: 12px 16px;
    border-radius: 6px;
    cursor: pointer;
    transition: all 0.3s;
    min-width: 100px;
    
    &:hover:not(.sold-out) {
      background: #e6f7ff;
    }
    
    &.sold-out {
      opacity: 0.5;
      cursor: not-allowed;
      
      .seat-count {
        color: #999;
      }
    }
    
    .seat-type {
      display: block;
      font-size: 13px;
      color: #666;
    }
    
    .seat-price {
      display: block;
      font-size: 18px;
      font-weight: 600;
      color: #ff6600;
      margin: 4px 0;
    }
    
    .seat-count {
      display: block;
      font-size: 12px;
      color: #52c41a;
    }
  }
}

// Enhanced Animations
@keyframes slide-in-up {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
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
    background: rgba(102, 126, 234, 0.3);
    transform: translate(-50%, -50%);
    transition: width 0.6s, height 0.6s;
  }
  
  &:active::after {
    width: 300px;
    height: 300px;
  }
}

// Enhanced Button Styles
:deep(.el-button--primary) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 20px;
  font-weight: 600;
  transition: all 0.3s ease;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
  }
}

// 响应式
@media (max-width: 768px) {
  .train-card {
    flex-direction: column;
    gap: 16px;
    
    .train-info {
      flex-direction: column;
      gap: 16px;
    }
    
    .train-type {
      border-right: none;
      border-bottom: 1px solid rgba(0, 0, 0, 0.1);
      padding-right: 0;
      margin-right: 0;
      padding-bottom: 16px;
    }
    
    .train-seats {
      flex-wrap: wrap;
      justify-content: center;
    }
  }
  
  .search-filter {
    padding: 20px;
    margin-bottom: 16px;
  }
}
</style>
