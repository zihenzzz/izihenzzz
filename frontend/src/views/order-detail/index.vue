<template>
  <div class="order-detail-page">
    <div class="container" v-if="order">
      <el-page-header @back="goBack" class="page-header">
        <template #content><span class="page-title">订单详情</span></template>
      </el-page-header>
      
      <div class="order-status-bar" :class="getStatusClass(order.status)">
        <div class="status-info">
          <el-icon v-if="order.status === 0"><Clock /></el-icon>
          <el-icon v-else-if="order.status === 1"><CircleCheck /></el-icon>
          <el-icon v-else><CircleClose /></el-icon>
          <span class="status-text">{{ getStatusText(order.status) }}</span>
        </div>
        <div class="order-number">订单号：{{ order.orderNo }}</div>
      </div>
      
      <div class="detail-content">
        <div class="card">
          <h3 class="card-title">🚌 班次信息</h3>
          <div class="bus-info">
            <div class="route">
              <div class="station">
                <span class="time">{{ order.departureTime }}</span>
                <span class="name">{{ order.departureStation }}</span>
              </div>
              <div class="arrow">
                <span class="bus-code">{{ order.trainCode }}</span>
                <el-icon><Right /></el-icon>
              </div>
              <div class="station">
                <span class="time">{{ order.arrivalTime }}</span>
                <span class="name">{{ order.arrivalStation }}</span>
              </div>
            </div>
            <div class="meta">
              <span class="date">乘车日期：{{ order.travelDate }}</span>
            </div>
          </div>
        </div>
        
        <div class="card">
          <h3 class="card-title">乘客信息</h3>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="乘客姓名">{{ order.passengerName }}</el-descriptions-item>
            <el-descriptions-item label="身份证号">{{ formatIdCard(order.passengerIdCard) }}</el-descriptions-item>
            <el-descriptions-item label="席位类型">{{ getSeatTypeName(order.seatType) }}</el-descriptions-item>
            <el-descriptions-item label="座位号">{{ order.seatNo || '上车后分配' }}</el-descriptions-item>
          </el-descriptions>
        </div>
        
        <div class="card">
          <h3 class="card-title">支付信息</h3>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="订单金额"><span class="price">¥{{ order.amount }}</span></el-descriptions-item>
            <el-descriptions-item label="支付状态"><el-tag :type="getStatusType(order.status)">{{ getStatusText(order.status) }}</el-tag></el-descriptions-item>
            <el-descriptions-item label="下单时间">{{ formatTime(order.createTime) }}</el-descriptions-item>
            <el-descriptions-item label="支付时间">{{ order.payTime ? formatTime(order.payTime) : '--' }}</el-descriptions-item>
          </el-descriptions>
        </div>
        
        <div class="card tips-card" v-if="order.status === 1">
          <h3 class="card-title">🎫 乘车提示</h3>
          <ul class="tips-list">
            <li>请凭身份证原件到车站取票或直接刷身份证上车</li>
            <li>请提前30分钟到达车站</li>
            <li>如需退票，请在发车前2小时办理</li>
          </ul>
        </div>
        
        <div class="action-bar" v-if="order.status !== 2 && order.status !== 3">
          <el-button v-if="order.status === 1" type="danger" @click="handleRefund">申请退票</el-button>
          <el-button v-if="order.status === 0" type="primary" @click="handlePay">立即支付</el-button>
          <el-button v-if="order.status === 0" @click="handleCancel">取消订单</el-button>
        </div>
      </div>
    </div>
    <div v-else class="loading-state"><el-skeleton :rows="6" animated /></div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getOrderDetail, payOrder, cancelOrder, refundOrder } from '@/api/order'
import { Right, Clock, CircleCheck, CircleClose } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'

const router = useRouter()
const route = useRoute()
const order = ref(null)

const fetchOrder = async () => {
  try {
    const res = await getOrderDetail(route.params.id)
    if (res.data.code === 200) order.value = res.data.data
    else ElMessage.error('获取订单详情失败')
  } catch (e) { ElMessage.error('获取订单详情失败') }
}

const goBack = () => router.push('/orders')
const formatTime = (t) => dayjs(t).format('YYYY-MM-DD HH:mm:ss')
const formatIdCard = (id) => id ? id.replace(/(\d{3})\d{12}(\w{1})/, '$1***********$2') : '--'
const getStatusType = (s) => ({ 0: 'warning', 1: 'success', 2: 'info', 3: 'danger' }[s] || 'info')
const getStatusText = (s) => ({ 0: '待支付', 1: '已支付', 2: '已取消', 3: '已退票' }[s] || '未知')
const getStatusClass = (s) => ({ 0: 'status-warning', 1: 'status-success', 2: 'status-cancelled', 3: 'status-refunded' }[s] || '')
const getSeatTypeName = (t) => ({ FIRST_CLASS: '商务座', SECOND_CLASS: '普通座', HARD_SLEEPER: '下铺', SOFT_SLEEPER: '上铺' }[t] || '座位')

const handlePay = async () => {
  try {
    await ElMessageBox.confirm('确认支付订单？', '提示', { type: 'warning' })
    const res = await payOrder(order.value.id)
    if (res.data.code === 200) { ElMessage.success('支付成功'); fetchOrder() }
    else ElMessage.error(res.data.message || '支付失败')
  } catch (e) { if (e !== 'cancel') ElMessage.error('支付失败') }
}

const handleCancel = async () => {
  try {
    await ElMessageBox.confirm('确认取消订单？', '提示', { type: 'warning' })
    const res = await cancelOrder(order.value.id)
    if (res.data.code === 200) { ElMessage.success('订单已取消'); fetchOrder() }
    else ElMessage.error(res.data.message || '取消失败')
  } catch (e) { if (e !== 'cancel') ElMessage.error('取消失败') }
}

const handleRefund = async () => {
  try {
    await ElMessageBox.confirm('确认退票？退票后将无法恢复。', '提示', { type: 'warning' })
    const res = await refundOrder(order.value.id)
    if (res.data.code === 200) { ElMessage.success('退票成功'); fetchOrder() }
    else ElMessage.error(res.data.message || '退票失败')
  } catch (e) { if (e !== 'cancel') ElMessage.error('退票失败') }
}

onMounted(() => fetchOrder())
</script>

<style lang="scss" scoped>
.order-detail-page { min-height: calc(100vh - 136px); padding: 20px 0; background: #f5f7fa; }
.page-header { margin-bottom: 20px; background: #fff; padding: 16px; border-radius: 12px; }
.page-title { font-size: 18px; font-weight: 600; }
.order-status-bar { padding: 24px; border-radius: 12px; margin-bottom: 20px; color: #fff; }
.order-status-bar.status-warning { background: linear-gradient(135deg, #faad14, #ffc53d); }
.order-status-bar.status-success { background: linear-gradient(135deg, #52c41a, #73d13d); }
.order-status-bar.status-cancelled { background: linear-gradient(135deg, #8c8c8c, #a0a0a0); }
.order-status-bar.status-refunded { background: linear-gradient(135deg, #ff4d4f, #ff7875); }
.status-info { display: flex; align-items: center; gap: 8px; font-size: 20px; font-weight: 600; margin-bottom: 8px; }
.order-number { font-size: 14px; opacity: 0.9; }
.card { margin-bottom: 20px; background: #fff; padding: 24px; border-radius: 12px; box-shadow: 0 2px 12px rgba(0,0,0,0.08); }
.card-title { font-size: 16px; font-weight: 600; margin-bottom: 16px; color: #333; }
.bus-info .route { display: flex; align-items: center; justify-content: space-between; padding: 24px; background: #f5f7fa; border-radius: 8px; }
.bus-info .station { text-align: center; }
.bus-info .station .time { display: block; font-size: 28px; font-weight: 600; color: #333; }
.bus-info .station .name { font-size: 14px; color: #666; }
.bus-info .arrow { display: flex; flex-direction: column; align-items: center; gap: 8px; }
.bus-info .bus-code { background: #52c41a; color: #fff; padding: 4px 12px; border-radius: 4px; font-size: 14px; }
.bus-info .arrow .el-icon { color: #1e88e5; font-size: 24px; }
.bus-info .meta { margin-top: 16px; color: #666; font-size: 14px; }
.price { font-size: 24px; font-weight: 600; color: #ff6600; }
.tips-card { background: #e6f7ff; border: 1px solid #91d5ff; }
.tips-list { margin: 0; padding-left: 20px; }
.tips-list li { margin: 8px 0; color: #1890ff; }
.action-bar { display: flex; justify-content: flex-end; gap: 12px; padding-top: 20px; }
.loading-state { max-width: 800px; margin: 0 auto; padding: 40px; }
</style>
