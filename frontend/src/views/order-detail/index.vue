<template>
  <div class="order-detail-page">
    <div class="container" v-if="order">
      <el-page-header @back="goBack" class="page-header">
        <template #content>
          <span class="page-title">订单详情</span>
        </template>
      </el-page-header>
      
      <div class="order-status-bar" :class="getStatusClass(order.status)">
        <div class="status-info">
          <el-icon v-if="order.status === 0"><Clock /></el-icon>
          <el-icon v-else-if="order.status === 1"><CircleCheck /></el-icon>
          <el-icon v-else-if="order.status === 2"><CircleClose /></el-icon>
          <el-icon v-else><Remove /></el-icon>
          <span class="status-text">{{ getStatusText(order.status) }}</span>
        </div>
        <div class="order-number">订单号：{{ order.orderNo }}</div>
      </div>
      
      <div class="detail-content">
        <!-- 订单信息 -->
        <div class="card">
          <h3 class="card-title">车次信息</h3>
          <div class="train-info">
            <div class="route">
              <div class="station">
                <span class="time">{{ order.departureTime }}</span>
                <span class="name">{{ order.departureStation }}</span>
              </div>
              <div class="arrow">
                <span class="train-code">{{ order.trainCode }}</span>
                <el-icon><Right /></el-icon>
              </div>
              <div class="station">
                <span class="time">{{ order.arrivalTime }}</span>
                <span class="name">{{ order.arrivalStation }}</span>
              </div>
            </div>
            <div class="meta">
              <span class="date">乘车日期：{{ order.travelDate }}</span>
              <span class="duration">历时：{{ calculateDuration(order.departureTime, order.arrivalTime) }}</span>
            </div>
          </div>
        </div>
        
        <!-- 乘客信息 -->
        <div class="card">
          <h3 class="card-title">乘客信息</h3>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="乘客姓名">{{ order.passengerName }}</el-descriptions-item>
            <el-descriptions-item label="身份证号">{{ formatIdCard(order.passengerIdCard) }}</el-descriptions-item>
            <el-descriptions-item label="席位类型">{{ getSeatTypeName(order.seatType) }}</el-descriptions-item>
            <el-descriptions-item label="座位号">{{ order.carriageNo }}车厢 {{ order.seatNo }}座</el-descriptions-item>
          </el-descriptions>
        </div>
        
        <!-- 支付信息 -->
        <div class="card">
          <h3 class="card-title">支付信息</h3>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="订单金额">
              <span class="price">¥{{ order.amount }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="支付状态">
              <el-tag :type="getStatusType(order.status)">{{ getStatusText(order.status) }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="下单时间">{{ formatTime(order.createTime) }}</el-descriptions-item>
            <el-descriptions-item label="支付时间">
              {{ order.payTime ? formatTime(order.payTime) : '--' }}
            </el-descriptions-item>
            <el-descriptions-item label="支付截止" v-if="order.status === 0">
              {{ formatTime(order.payDeadline) }}
            </el-descriptions-item>
          </el-descriptions>
        </div>
        
        <!-- 操作按钮 -->
        <div class="action-bar" v-if="order.status !== 2 && order.status !== 3">
          <el-button v-if="order.status === 1" type="danger" @click="handleRefund">
            申请退票
          </el-button>
          <el-button v-if="order.status === 0" type="primary" @click="handlePay">
            立即支付
          </el-button>
          <el-button v-if="order.status === 0" @click="handleCancel">
            取消订单
          </el-button>
        </div>
      </div>
    </div>
    
    <!-- 加载状态 -->
    <div v-else class="loading-state">
      <el-skeleton :rows="6" animated />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getOrderDetail, payOrder, cancelOrder, refundOrder } from '@/api/order'
import { Right, Clock, CircleCheck, CircleClose, Remove } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'

const router = useRouter()
const route = useRoute()

const order = ref(null)

// 获取订单详情
const fetchOrder = async () => {
  try {
    const response = await getOrderDetail(route.params.id)
    if (response.data.code === 200) {
      order.value = response.data.data
    } else {
      ElMessage.error('获取订单详情失败')
    }
  } catch (error) {
    ElMessage.error('获取订单详情失败')
  }
}

// 返回
const goBack = () => {
  router.push('/orders')
}

// 格式化时间
const formatTime = (time) => {
  return dayjs(time).format('YYYY-MM-DD HH:mm:ss')
}

// 格式化身份证号
const formatIdCard = (idCard) => {
  if (!idCard) return '--'
  return idCard.replace(/(\d{3})\d{12}(\w{1})/, '$1***********$2')
}

// 获取状态类型
const getStatusType = (status) => {
  const types = { 0: 'warning', 1: 'success', 2: 'info', 3: 'danger' }
  return types[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const texts = { 0: '待支付', 1: '已支付', 2: '已取消', 3: '已退票' }
  return texts[status] || '未知'
}

// 获取状态样式类
const getStatusClass = (status) => {
  const classes = { 0: 'status-warning', 1: 'status-success', 2: 'status-cancelled', 3: 'status-refunded' }
  return classes[status] || ''
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

// 计算历时
const calculateDuration = (departure, arrival) => {
  if (!departure || !arrival) return '--'
  const d1 = dayjs('2000-01-01 ' + departure)
  const d2 = dayjs('2000-01-01 ' + arrival)
  if (d2.isBefore(d1)) {
    d2.add(1, 'day')
  }
  const diff = d2.diff(d1, 'minute')
  const hours = Math.floor(diff / 60)
  const minutes = diff % 60
  return `${hours}小时${minutes}分`
}

// 支付订单
const handlePay = async () => {
  try {
    await ElMessageBox.confirm('确认支付订单？', '提示', { type: 'warning' })
    
    const response = await payOrder(order.value.id)
    if (response.data.code === 200) {
      ElMessage.success('支付成功')
      fetchOrder()
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
const handleCancel = async () => {
  try {
    await ElMessageBox.confirm('确认取消订单？', '提示', { type: 'warning' })
    
    const response = await cancelOrder(order.value.id)
    if (response.data.code === 200) {
      ElMessage.success('订单已取消')
      fetchOrder()
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
const handleRefund = async () => {
  try {
    await ElMessageBox.confirm('确认退票？退票后将无法恢复。', '提示', { type: 'warning' })
    
    const response = await refundOrder(order.value.id)
    if (response.data.code === 200) {
      ElMessage.success('退票成功')
      fetchOrder()
    } else {
      ElMessage.error(response.data.message || '退票失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('退票失败')
    }
  }
}

onMounted(() => {
  fetchOrder()
})
</script>

<style lang="scss" scoped>
.order-detail-page {
  min-height: calc(100vh - 64px - 72px);
  padding: 20px 0;
}

.page-header {
  margin-bottom: 20px;
  
  .page-title {
    font-size: 18px;
    font-weight: 600;
  }
}

.order-status-bar {
  padding: 24px;
  border-radius: 8px;
  margin-bottom: 20px;
  color: #fff;
  
  &.status-warning {
    background: linear-gradient(135deg, #faad14 0%, #ffc53d 100%);
  }
  
  &.status-success {
    background: linear-gradient(135deg, #52c41a 0%, #73d13d 100%);
  }
  
  &.status-cancelled {
    background: linear-gradient(135deg, #8c8c8c 0%, #a0a0a0 100%);
  }
  
  &.status-refunded {
    background: linear-gradient(135deg, #ff4d4f 0%, #ff7875 100%);
  }
  
  .status-info {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 20px;
    font-weight: 600;
    margin-bottom: 8px;
  }
  
  .order-number {
    font-size: 14px;
    opacity: 0.9;
  }
}

.detail-content {
  .card {
    margin-bottom: 20px;
    
    .card-title {
      font-size: 16px;
      font-weight: 600;
      margin-bottom: 16px;
      color: #333;
    }
  }
  
  .train-info {
    .route {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 16px 0;
      margin-bottom: 16px;
      background: #f5f7fa;
      border-radius: 8px;
      padding: 24px;
      
      .station {
        text-align: center;
        
        .time {
          display: block;
          font-size: 28px;
          font-weight: 600;
          color: #333;
        }
        
        .name {
          font-size: 14px;
          color: #666;
        }
      }
      
      .arrow {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 8px;
        
        .train-code {
          background: #52c41a;
          color: #fff;
          padding: 4px 12px;
          border-radius: 4px;
          font-size: 14px;
        }
        
        .el-icon {
          color: #1890ff;
          font-size: 24px;
        }
      }
    }
    
    .meta {
      display: flex;
      gap: 24px;
      color: #666;
      font-size: 14px;
    }
  }
  
  .price {
    font-size: 24px;
    font-weight: 600;
    color: #ff6600;
  }
  
  .action-bar {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    padding-top: 20px;
  }
}

.loading-state {
  max-width: 800px;
}
</style>
