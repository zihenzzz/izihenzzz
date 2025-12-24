<template>
  <div class="booking-page">
    <div class="container">
      <!-- 步骤指示 -->
      <div class="steps">
        <el-steps :active="step" align-center finish-status="success">
          <el-step title="确认订单" />
          <el-step title="支付订单" />
          <el-step title="完成预订" />
        </el-steps>
      </div>
      
      <!-- 步骤1: 确认订单 -->
      <div v-if="step === 1" class="step-content">
        <div class="card">
          <h3 class="card-title">订单信息</h3>
          
          <div class="train-summary">
            <div class="train-header">
              <el-tag type="success">{{ trainData.trainCode }}</el-tag>
              <span class="train-type">{{ getTrainTypeName(trainData.trainType) }}</span>
            </div>
            <div class="train-route">
              <div class="route-point">
                <span class="time">{{ trainData.departureTime }}</span>
                <span class="station">{{ trainData.departureStationName }}</span>
              </div>
              <div class="route-arrow">
                <el-icon><Right /></el-icon>
              </div>
              <div class="route-point">
                <span class="time">{{ trainData.arrivalTime }}</span>
                <span class="station">{{ trainData.arrivalStationName }}</span>
              </div>
            </div>
          </div>
          
          <el-divider />
          
          <div class="passenger-form">
            <h4>乘客信息</h4>
            <el-form :model="passengerForm" label-width="80px">
              <el-form-item label="乘客姓名" required>
                <el-input v-model="passengerForm.name" placeholder="请输入乘客姓名" />
              </el-form-item>
              <el-form-item label="身份证" required>
                <el-input v-model="passengerForm.idCard" placeholder="请输入身份证号码" />
              </el-form-item>
            </el-form>
          </div>
        </div>
        
        <div class="card">
          <h3 class="card-title">席位信息</h3>
          <div class="seat-info">
            <div class="seat-type-row">
              <span class="seat-label">席位类型：</span>
              <el-tag>{{ getSeatTypeName(query.seatType) }}</el-tag>
            </div>
            <div class="seat-price-row">
              <span class="price-label">票价：</span>
              <span class="price-value">¥{{ selectedPrice }}</span>
            </div>
          </div>
        </div>
        
        <div class="card order-summary">
          <h3 class="card-title">订单金额</h3>
          <div class="amount-row">
            <span>票面价</span>
            <span>¥{{ selectedPrice }}</span>
          </div>
          <div class="amount-row total">
            <span>合计</span>
            <span class="total-price">¥{{ selectedPrice }}</span>
          </div>
        </div>
        
        <div class="action-bar">
          <el-button @click="goBack">返回修改</el-button>
          <el-button type="primary" @click="submitOrder" :loading="submitting">
            提交订单
          </el-button>
        </div>
      </div>
      
      <!-- 步骤2: 支付订单 -->
      <div v-if="step === 2" class="step-content">
        <div class="card">
          <h3 class="card-title">订单号：{{ orderData.orderNo }}</h3>
          
          <div class="payment-amount">
            <span class="label">应付金额</span>
            <span class="amount">¥{{ orderData.amount }}</span>
          </div>
          
          <el-divider />
          
          <div class="payment-methods">
            <h4>选择支付方式</h4>
            <el-radio-group v-model="paymentMethod" class="payment-options">
              <el-radio-button label="wechat">
                <el-icon><ChatDotRound /></el-icon>
                微信支付
              </el-radio-button>
              <el-radio-button label="alipay">
                <el-icon><CreditCard /></el-icon>
                支付宝
              </el-radio-button>
              <el-radio-button label="bank">
                <el-icon><Wallet /></el-icon>
                银行卡
              </el-radio-button>
            </el-radio-group>
          </div>
          
          <div class="pay-info">
            <p><el-icon><Clock /></el-icon> 请在 <strong>{{ payDeadline }}</strong> 前完成支付</p>
          </div>
        </div>
        
        <div class="action-bar">
          <el-button @click="cancelPayment">取消支付</el-button>
          <el-button type="primary" @click="confirmPayment" :loading="paying">
            立即支付
          </el-button>
        </div>
      </div>
      
      <!-- 步骤3: 完成预订 -->
      <div v-if="step === 3" class="step-content">
        <div class="success-card">
          <div class="success-icon">
            <el-icon><CircleCheck /></el-icon>
          </div>
          <h2>预订成功</h2>
          <p>订单号：{{ orderData.orderNo }}</p>
          <p>请在30分钟内完成支付，超时订单将自动取消</p>
          
          <div class="success-actions">
            <el-button type="primary" @click="viewOrder">查看订单</el-button>
            <el-button @click="goHome">返回首页</el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getTrainById, getTrainTicketInfo } from '@/api/train'
import { createOrder, payOrder } from '@/api/order'
import { Right, Clock, ChatDotRound, CreditCard, Wallet, CircleCheck } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'

const router = useRouter()
const route = useRoute()

const step = ref(1)
const submitting = ref(false)
const paying = ref(false)
const paymentMethod = ref('wechat')

const trainData = ref({})
const orderData = ref({})
const query = reactive({
  trainId: route.params.trainId,
  seatType: route.query.seatType,
  date: route.query.date,
  from: route.query.from,
  to: route.query.to
})

const passengerForm = reactive({
  name: '',
  idCard: ''
})

// 获取车次信息
const fetchTrainData = async () => {
  try {
    const response = await getTrainById(query.trainId)
    if (response.data.code === 200) {
      trainData.value = response.data.data
    }
  } catch (error) {
    ElMessage.error('获取车次信息失败')
  }
}

// 获取票价
const selectedPrice = computed(() => {
  const type = query.seatType
  switch (type) {
    case 'FIRST_CLASS':
      return trainData.value.firstClassPrice || 0
    case 'SECOND_CLASS':
      return trainData.value.secondClassPrice || 0
    case 'HARD_SLEEPER':
      return trainData.value.hardSleeperPrice || 0
    case 'SOFT_SLEEPER':
      return trainData.value.softSleeperPrice || 0
    default:
      return 0
  }
})

// 支付截止时间
const payDeadline = computed(() => {
  if (orderData.value.payDeadline) {
    return dayjs(orderData.value.payDeadline).format('HH:mm')
  }
  return '30:00'
})

// 获取车次类型名称
const getTrainTypeName = (type) => {
  const names = { 'G': '高铁', 'D': '动车', 'K': '普快', 'T': '特快' }
  return names[type] || '列车'
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

// 提交订单
const submitOrder = async () => {
  if (!passengerForm.name || !passengerForm.idCard) {
    ElMessage.warning('请填写乘客信息')
    return
  }
  
  submitting.value = true
  
  try {
    const response = await createOrder({
      trainId: parseInt(query.trainId),
      seatType: query.seatType,
      passengerName: passengerForm.name,
      passengerIdCard: passengerForm.idCard,
      travelDate: query.date
    })
    
    if (response.data.code === 200) {
      orderData.value = response.data.data
      step.value = 2
    } else {
      ElMessage.error(response.data.message || '订单提交失败')
    }
  } catch (error) {
    ElMessage.error('订单提交失败')
  } finally {
    submitting.value = false
  }
}

// 确认支付
const confirmPayment = async () => {
  paying.value = true
  
  try {
    const response = await payOrder(orderData.value.orderId)
    if (response.data.code === 200) {
      step.value = 3
      ElMessage.success('支付成功')
    } else {
      ElMessage.error(response.data.message || '支付失败')
    }
  } catch (error) {
    ElMessage.error('支付失败')
  } finally {
    paying.value = false
  }
}

// 取消支付
const cancelPayment = () => {
  step.value = 1
}

// 返回
const goBack = () => {
  router.back()
}

// 查看订单
const viewOrder = () => {
  router.push({ name: 'OrderDetail', params: { id: orderData.value.orderId } })
}

// 返回首页
const goHome = () => {
  router.push('/')
}

onMounted(() => {
  fetchTrainData()
})
</script>

<style lang="scss" scoped>
.booking-page {
  min-height: calc(100vh - 64px - 72px);
  padding: 20px 0;
}

.steps {
  margin-bottom: 32px;
}

.step-content {
  max-width: 800px;
  margin: 0 auto;
}

.card {
  margin-bottom: 20px;
  
  .card-title {
    font-size: 18px;
    font-weight: 600;
    margin-bottom: 20px;
    color: #333;
  }
}

.train-summary {
  .train-header {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 16px;
    
    .train-type {
      font-size: 14px;
      color: #666;
    }
  }
  
  .train-route {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 40px;
    
    .route-point {
      text-align: center;
      
      .time {
        display: block;
        font-size: 28px;
        font-weight: 600;
        color: #333;
      }
      
      .station {
        font-size: 14px;
        color: #666;
      }
    }
    
    .route-arrow {
      font-size: 24px;
      color: #1890ff;
    }
  }
}

.passenger-form {
  h4 {
    font-size: 16px;
    font-weight: 600;
    margin-bottom: 16px;
    color: #333;
  }
}

.seat-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  
  .seat-label,
  .price-label {
    color: #666;
    margin-right: 8px;
  }
  
  .price-value {
    font-size: 24px;
    font-weight: 600;
    color: #ff6600;
  }
}

.order-summary {
  .amount-row {
    display: flex;
    justify-content: space-between;
    padding: 8px 0;
    font-size: 15px;
    
    &.total {
      border-top: 1px solid #f0f0f0;
      margin-top: 8px;
      padding-top: 16px;
      font-size: 16px;
      font-weight: 600;
      
      .total-price {
        font-size: 28px;
        color: #ff6600;
      }
    }
  }
}

.action-bar {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  margin-top: 24px;
}

.payment-amount {
  text-align: center;
  padding: 24px 0;
  
  .label {
    display: block;
    font-size: 14px;
    color: #666;
    margin-bottom: 8px;
  }
  
  .amount {
    font-size: 48px;
    font-weight: 600;
    color: #ff6600;
  }
}

.payment-methods {
  h4 {
    font-size: 16px;
    font-weight: 600;
    margin-bottom: 16px;
    text-align: center;
  }
  
  .payment-options {
    display: flex;
    justify-content: center;
    gap: 16px;
  }
}

.pay-info {
  text-align: center;
  margin-top: 24px;
  padding: 16px;
  background: #fff7e6;
  border-radius: 6px;
  color: #ff6600;
  
  .el-icon {
    margin-right: 4px;
  }
  
  strong {
    font-size: 18px;
  }
}

.success-card {
  text-align: center;
  padding: 60px 40px;
  background: #fff;
  border-radius: 12px;
  
  .success-icon {
    width: 80px;
    height: 80px;
    border-radius: 50%;
    background: linear-gradient(135deg, #52c41a 0%, #73d13d 100%);
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 0 auto 24px;
    font-size: 40px;
    color: #fff;
  }
  
  h2 {
    font-size: 28px;
    font-weight: 600;
    color: #52c41a;
    margin-bottom: 16px;
  }
  
  p {
    font-size: 15px;
    color: #666;
    margin-bottom: 8px;
  }
  
  .success-actions {
    margin-top: 32px;
    display: flex;
    justify-content: center;
    gap: 16px;
  }
}
</style>
