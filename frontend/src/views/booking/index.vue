<template>
  <div class="booking-page">
    <div class="container">
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
          <h3 class="card-title">🚌 班次信息</h3>
          <div class="bus-summary">
            <div class="bus-header">
              <el-tag type="success">{{ busData.trainCode }}</el-tag>
              <span class="bus-type">{{ getBusTypeName(busData.trainType) }}</span>
              <span class="bus-company">{{ busData.busCompany }}</span>
            </div>
            <div class="bus-route">
              <div class="route-point">
                <span class="time">{{ busData.departureTime }}</span>
                <span class="station">{{ busData.departureStationName }}</span>
              </div>
              <div class="route-arrow"><el-icon><Right /></el-icon></div>
              <div class="route-point">
                <span class="time">{{ busData.arrivalTime }}</span>
                <span class="station">{{ busData.arrivalStationName }}</span>
              </div>
            </div>
            <div class="bus-meta">
              <span>乘车日期：{{ query.date }}</span>
              <span>车型：{{ busData.busModel }}</span>
            </div>
          </div>
          <el-divider />
          <div class="passenger-form">
            <h4>乘客信息</h4>
            <el-form :model="passengerForm" label-width="100px">
              <el-form-item label="乘客姓名" required>
                <el-input v-model="passengerForm.name" placeholder="请输入乘客真实姓名" />
              </el-form-item>
              <el-form-item label="身份证号" required>
                <el-input v-model="passengerForm.idCard" placeholder="请输入身份证号码" />
              </el-form-item>
              <el-form-item label="手机号码" required>
                <el-input v-model="passengerForm.phone" placeholder="请输入手机号码" />
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
          <div class="amount-row"><span>票面价</span><span>¥{{ selectedPrice }}</span></div>
          <div class="amount-row total"><span>合计</span><span class="total-price">¥{{ selectedPrice }}</span></div>
        </div>
        
        <div class="action-bar">
          <el-button @click="goBack">返回修改</el-button>
          <el-button type="primary" @click="submitOrder" :loading="submitting">提交订单</el-button>
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
              <el-radio-button label="wechat"><el-icon><ChatDotRound /></el-icon>微信支付</el-radio-button>
              <el-radio-button label="alipay"><el-icon><CreditCard /></el-icon>支付宝</el-radio-button>
            </el-radio-group>
          </div>
          <div class="pay-info">
            <p><el-icon><Clock /></el-icon> 请在 <strong>30分钟</strong> 内完成支付，超时订单将自动取消</p>
          </div>
        </div>
        <div class="action-bar">
          <el-button @click="cancelPayment">取消支付</el-button>
          <el-button type="primary" @click="confirmPayment" :loading="paying">立即支付</el-button>
        </div>
      </div>
      
      <!-- 步骤3: 完成预订 -->
      <div v-if="step === 3" class="step-content">
        <div class="success-card">
          <div class="success-icon"><el-icon><CircleCheck /></el-icon></div>
          <h2>🎉 预订成功</h2>
          <p class="order-no">订单号：{{ orderData.orderNo }}</p>
          <div class="ticket-info">
            <p><strong>{{ busData.departureStationName }}</strong> → <strong>{{ busData.arrivalStationName }}</strong></p>
            <p>{{ query.date }} {{ busData.departureTime }} 发车</p>
            <p>乘客：{{ passengerForm.name }} | {{ getSeatTypeName(query.seatType) }}</p>
          </div>
          <div class="tips">
            <p>✅ 请凭身份证原件到车站取票或直接刷身份证上车</p>
            <p>✅ 请提前30分钟到达车站</p>
          </div>
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
import { getTrainById } from '@/api/train'
import { createOrder, payOrder } from '@/api/order'
import { Right, Clock, ChatDotRound, CreditCard, CircleCheck } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const step = ref(1)
const submitting = ref(false)
const paying = ref(false)
const paymentMethod = ref('wechat')
const busData = ref({})
const orderData = ref({})
const query = reactive({ trainId: route.params.trainId, seatType: route.query.seatType, date: route.query.date })
const passengerForm = reactive({ name: '', idCard: '', phone: '' })

const fetchBusData = async () => {
  try {
    const res = await getTrainById(query.trainId)
    if (res.data.code === 200) busData.value = res.data.data
  } catch (e) { ElMessage.error('获取班次信息失败') }
}

const selectedPrice = computed(() => {
  const t = query.seatType
  if (t === 'FIRST_CLASS') return busData.value.firstClassPrice || 0
  if (t === 'SECOND_CLASS') return busData.value.secondClassPrice || 0
  if (t === 'HARD_SLEEPER') return busData.value.hardSleeperPrice || 0
  if (t === 'SOFT_SLEEPER') return busData.value.softSleeperPrice || 0
  return 0
})

const getBusTypeName = (t) => ({ EXPRESS: '快客', NORMAL: '普通班车', SLEEPER: '卧铺车' }[t] || '班车')
const getSeatTypeName = (t) => ({ FIRST_CLASS: '商务座', SECOND_CLASS: '普通座', HARD_SLEEPER: '下铺', SOFT_SLEEPER: '上铺' }[t] || '座位')

const submitOrder = async () => {
  if (!passengerForm.name || !passengerForm.idCard || !passengerForm.phone) {
    ElMessage.warning('请填写完整的乘客信息'); return
  }
  submitting.value = true
  try {
    const res = await createOrder({ trainId: parseInt(query.trainId), seatType: query.seatType, passengerName: passengerForm.name, passengerIdCard: passengerForm.idCard, travelDate: query.date })
    if (res.data.code === 200) { orderData.value = res.data.data; step.value = 2 }
    else ElMessage.error(res.data.message || '订单提交失败')
  } catch (e) { ElMessage.error('订单提交失败') }
  finally { submitting.value = false }
}

const confirmPayment = async () => {
  paying.value = true
  try {
    const res = await payOrder(orderData.value.orderId)
    if (res.data.code === 200) { step.value = 3; ElMessage.success('支付成功！') }
    else ElMessage.error(res.data.message || '支付失败')
  } catch (e) { ElMessage.error('支付失败') }
  finally { paying.value = false }
}

const cancelPayment = () => { step.value = 1 }
const goBack = () => { router.back() }
const viewOrder = () => { router.push({ name: 'OrderDetail', params: { id: orderData.value.orderId } }) }
const goHome = () => { router.push('/') }

onMounted(() => { fetchBusData() })
</script>

<style lang="scss" scoped>
.booking-page { min-height: calc(100vh - 136px); padding: 20px 0; background: #f5f7fa; }
.steps { margin-bottom: 32px; background: #fff; padding: 24px; border-radius: 12px; }
.step-content { max-width: 800px; margin: 0 auto; }
.card { margin-bottom: 20px; background: #fff; padding: 24px; border-radius: 12px; box-shadow: 0 2px 12px rgba(0,0,0,0.08); }
.card-title { font-size: 18px; font-weight: 600; margin-bottom: 20px; color: #333; }
.bus-summary .bus-header { display: flex; align-items: center; gap: 12px; margin-bottom: 16px; }
.bus-type, .bus-company { font-size: 14px; color: #666; }
.bus-route { display: flex; align-items: center; justify-content: center; gap: 40px; padding: 20px; background: #f5f7fa; border-radius: 8px; }
.route-point { text-align: center; }
.route-point .time { display: block; font-size: 28px; font-weight: 600; color: #333; }
.route-point .station { font-size: 14px; color: #666; }
.route-arrow { font-size: 24px; color: #1e88e5; }
.bus-meta { margin-top: 16px; display: flex; gap: 24px; color: #666; font-size: 14px; }
.passenger-form h4 { font-size: 16px; font-weight: 600; margin-bottom: 16px; }
.seat-info { display: flex; justify-content: space-between; align-items: center; }
.seat-label, .price-label { color: #666; margin-right: 8px; }
.price-value { font-size: 24px; font-weight: 600; color: #ff6600; }
.order-summary .amount-row { display: flex; justify-content: space-between; padding: 8px 0; font-size: 15px; }
.amount-row.total { border-top: 1px solid #f0f0f0; margin-top: 8px; padding-top: 16px; font-weight: 600; }
.total-price { font-size: 28px; color: #ff6600; }
.action-bar { display: flex; justify-content: flex-end; gap: 16px; margin-top: 24px; }
.payment-amount { text-align: center; padding: 24px 0; }
.payment-amount .label { display: block; font-size: 14px; color: #666; margin-bottom: 8px; }
.payment-amount .amount { font-size: 48px; font-weight: 600; color: #ff6600; }
.payment-methods { text-align: center; }
.payment-methods h4 { font-size: 16px; font-weight: 600; margin-bottom: 16px; }
.payment-options { display: flex; justify-content: center; gap: 16px; }
.pay-info { text-align: center; margin-top: 24px; padding: 16px; background: #fff7e6; border-radius: 6px; color: #ff6600; }
.success-card { text-align: center; padding: 60px 40px; background: #fff; border-radius: 12px; }
.success-icon { width: 80px; height: 80px; border-radius: 50%; background: linear-gradient(135deg, #52c41a, #73d13d); display: flex; align-items: center; justify-content: center; margin: 0 auto 24px; font-size: 40px; color: #fff; }
.success-card h2 { font-size: 28px; font-weight: 600; color: #52c41a; margin-bottom: 16px; }
.order-no { font-size: 16px; color: #666; margin-bottom: 24px; }
.ticket-info { background: #f5f7fa; padding: 20px; border-radius: 8px; margin-bottom: 20px; }
.ticket-info p { margin: 8px 0; color: #333; }
.tips { text-align: left; background: #e6f7ff; padding: 16px; border-radius: 8px; margin-bottom: 24px; }
.tips p { margin: 8px 0; color: #1890ff; font-size: 14px; }
.success-actions { display: flex; justify-content: center; gap: 16px; }
</style>
