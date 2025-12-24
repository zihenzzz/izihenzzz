<template>
  <div class="orders-page">
    <div class="container">
      <div class="page-header">
        <h2>我的订单</h2>
      </div>
      
      <!-- 订单筛选 -->
      <div class="filter-bar card">
        <el-radio-group v-model="statusFilter" @change="fetchOrders">
          <el-radio-button label="">全部</el-radio-button>
          <el-radio-button label="0">待支付</el-radio-button>
          <el-radio-button label="1">已支付</el-radio-button>
          <el-radio-button label="2">已取消</el-radio-button>
          <el-radio-button label="3">已退票</el-radio-button>
        </el-radio-group>
      </div>
      
      <!-- 订单列表 -->
      <div class="orders-list">
        <!-- 加载状态 -->
        <div v-if="loading" class="loading-state">
          <el-skeleton v-for="i in 3" :key="i" :rows="4" animated />
        </div>
        
        <!-- 空状态 -->
        <el-empty v-else-if="orders.length === 0 && !loading" description="暂无订单记录" />
        
        <!-- 订单卡片 -->
        <div v-else v-for="order in orders" :key="order.id" class="order-card card">
          <div class="order-header">
            <div class="order-info">
              <span class="order-no">订单号：{{ order.orderNo }}</span>
              <span class="order-time">{{ formatTime(order.createTime) }}</span>
            </div>
            <div class="order-status">
              <el-tag :type="getStatusType(order.status)">{{ getStatusText(order.status) }}</el-tag>
            </div>
          </div>
          
          <div class="order-content">
            <div class="train-info">
              <div class="train-route">
                <span class="station">{{ order.departureStation }}</span>
                <span class="arrow">
                  <el-icon><Right /></el-icon>
                </span>
                <span class="station">{{ order.arrivalStation }}</span>
              </div>
              <div class="train-details">
                <el-tag size="small" type="success">{{ order.trainCode }}</el-tag>
                <span class="date">{{ order.travelDate }}</span>
                <span class="time">{{ order.departureTime }} - {{ order.arrivalTime }}</span>
              </div>
            </div>
            
            <div class="passenger-info">
              <span class="label">乘客：</span>
              <span class="value">{{ order.passengerName }}</span>
            </div>
            
            <div class="seat-info">
              <span class="seat-type">{{ getSeatTypeName(order.seatType) }}</span>
              <span class="seat-no">{{ order.carriageNo }}车厢 {{ order.seatNo }}座</span>
            </div>
            
            <div class="order-amount">
              <span class="amount-label">订单金额</span>
              <span class="amount-value">¥{{ order.amount }}</span>
            </div>
          </div>
          
          <div class="order-footer">
            <div class="deadline" v-if="order.status === 0">
              <el-icon><Clock /></el-icon>
              剩余支付时间：{{ getPayRemainingTime(order.payDeadline) }}
            </div>
            <div class="actions">
              <el-button v-if="order.status === 1" type="danger" size="small" @click="handleRefund(order)">
                退票
              </el-button>
              <el-button v-if="order.status === 0" type="primary" size="small" @click="handlePay(order)">
                立即支付
              </el-button>
              <el-button v-if="order.status === 0" size="small" @click="handleCancel(order)">
                取消订单
              </el-button>
              <el-button size="small" @click="viewDetail(order)">
                查看详情
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { getMyOrders, payOrder, cancelOrder, refundOrder } from '@/api/order'
import { Right, Clock } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'
import duration from 'dayjs/plugin/duration'

dayjs.extend(duration)

const router = useRouter()
const loading = ref(false)
const orders = ref([])
const statusFilter = ref('')
let timer = null

// 获取订单列表
const fetchOrders = async () => {
  loading.value = true
  try {
    const response = await getMyOrders()
    if (response.data.code === 200) {
      let data = response.data.data || []
      
      // 按状态筛选
      if (statusFilter.value !== '') {
        data = data.filter(o => o.status === parseInt(statusFilter.value))
      }
      
      orders.value = data
    }
  } catch (error) {
    ElMessage.error('获取订单列表失败')
  } finally {
    loading.value = false
  }
}

// 格式化时间
const formatTime = (time) => {
  return dayjs(time).format('YYYY-MM-DD HH:mm')
}

// 获取状态类型
const getStatusType = (status) => {
  const types = {
    0: 'warning',
    1: 'success',
    2: 'info',
    3: 'danger'
  }
  return types[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const texts = {
    0: '待支付',
    1: '已支付',
    2: '已取消',
    3: '已退票'
  }
  return texts[status] || '未知'
}

// 获取席位类型名称
const getSeatTypeName = (type) => {
  const names = {
    'FIRST_CLASS': '一等座',
    'SECOND_CLASS': '二等座',
    'HARD_SLEEPER': '硬卧',
    'SOFT_SLEEPER': '软卧'
  }
  return names[type] || '座位'
}

// 获取支付剩余时间
const getPayRemainingTime = (deadline) => {
  if (!deadline) return '--:--:--'
  const diff = dayjs(deadline).diff(dayjs(), 'millisecond')
  if (diff <= 0) return '已过期'
  
  const d = dayjs.duration(diff)
  const minutes = Math.floor(d.asMinutes())
  const seconds = d.seconds()
  return `${minutes}分${seconds}秒`
}

// 查看详情
const viewDetail = (order) => {
  router.push({ name: 'OrderDetail', params: { id: order.id } })
}

// 支付订单
const handlePay = async (order) => {
  try {
    await ElMessageBox.confirm('确认支付订单？', '提示', { type: 'warning' })
    
    const response = await payOrder(order.id)
    if (response.data.code === 200) {
      ElMessage.success('支付成功')
      fetchOrders()
    } else {
      ElMessage.error(response.data.message || '支付失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('支付失败')
    }
  }
}

// 取消订单
const handleCancel = async (order) => {
  try {
    await ElMessageBox.confirm('确认取消订单？取消后座位将释放。', '提示', { type: 'warning' })
    
    const response = await cancelOrder(order.id)
    if (response.data.code === 200) {
      ElMessage.success('订单已取消')
      fetchOrders()
    } else {
      ElMessage.error(response.data.message || '取消失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消失败')
    }
  }
}

// 退票
const handleRefund = async (order) => {
  try {
    await ElMessageBox.confirm('确认退票？退票后将无法恢复。', '提示', { type: 'warning' })
    
    const response = await refundOrder(order.id)
    if (response.data.code === 200) {
      ElMessage.success('退票成功')
      fetchOrders()
    } else {
      ElMessage.error(response.data.message || '退票失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('退票失败')
    }
  }
}

// 定时更新剩余时间
const updateRemainingTime = () => {
  orders.value = [...orders.value]
}

onMounted(() => {
  fetchOrders()
  timer = setInterval(updateRemainingTime, 1000)
})

onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
  }
})
</script>

<style lang="scss" scoped>
.orders-page {
  min-height: calc(100vh - 64px - 72px);
  padding: 20px 0;
}

.page-header {
  margin-bottom: 20px;
  
  h2 {
    font-size: 24px;
    font-weight: 600;
    color: #333;
  }
}

.filter-bar {
  margin-bottom: 20px;
  padding: 16px 20px;
}

.orders-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.loading-state {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.order-card {
  .order-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-bottom: 16px;
    border-bottom: 1px solid #f0f0f0;
    margin-bottom: 16px;
    
    .order-info {
      display: flex;
      align-items: center;
      gap: 16px;
      
      .order-no {
        font-weight: 600;
        color: #333;
      }
      
      .order-time {
        color: #999;
        font-size: 13px;
      }
    }
  }
  
  .order-content {
    display: grid;
    grid-template-columns: 2fr 1fr 1fr 1fr;
    gap: 16px;
    align-items: center;
  }
  
  .train-info {
    .train-route {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-bottom: 8px;
      
      .station {
        font-size: 18px;
        font-weight: 600;
        color: #333;
      }
      
      .arrow {
        color: #1890ff;
      }
    }
    
    .train-details {
      display: flex;
      align-items: center;
      gap: 12px;
      font-size: 13px;
      color: #666;
    }
  }
  
  .passenger-info,
  .seat-info {
    .label {
      color: #999;
      font-size: 13px;
      display: block;
      margin-bottom: 4px;
    }
    
    .value,
    .seat-type {
      font-weight: 600;
      color: #333;
    }
    
    .seat-no {
      font-size: 13px;
      color: #666;
    }
  }
  
  .order-amount {
    text-align: right;
    
    .amount-label {
      display: block;
      font-size: 13px;
      color: #999;
      margin-bottom: 4px;
    }
    
    .amount-value {
      font-size: 24px;
      font-weight: 600;
      color: #ff6600;
    }
  }
  
  .order-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: 16px;
    padding-top: 16px;
    border-top: 1px solid #f0f0f0;
    
    .deadline {
      color: #ff6600;
      font-size: 14px;
      display: flex;
      align-items: center;
      gap: 4px;
    }
    
    .actions {
      display: flex;
      gap: 8px;
    }
  }
}

// 响应式
@media (max-width: 768px) {
  .order-card .order-content {
    grid-template-columns: 1fr;
    gap: 12px;
  }
  
  .order-card .order-amount {
    text-align: left;
  }
}
</style>
