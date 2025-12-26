<template>
  <div class="order-management">
    <div class="card">
      <div class="card-header">
        <h3>订单列表</h3>
        <div class="header-actions">
          <el-select v-model="statusFilter" placeholder="订单状态" clearable style="width: 120px; margin-right: 12px">
            <el-option label="待支付" :value="0" />
            <el-option label="已支付" :value="1" />
            <el-option label="已取消" :value="2" />
            <el-option label="已退票" :value="3" />
          </el-select>
          <el-input placeholder="搜索订单号" v-model="searchKey" style="width: 200px" clearable @clear="loadOrders" @keyup.enter="loadOrders">
            <template #prefix><el-icon><Search /></el-icon></template>
          </el-input>
        </div>
      </div>
      
      <el-table :data="filteredOrders" style="width: 100%" v-loading="loading">
        <el-table-column prop="orderNo" label="订单号" width="170" />
        <el-table-column prop="trainCode" label="班次" width="80" />
        <el-table-column label="出发-到达">
          <template #default="{ row }">{{ row.departureStation }} → {{ row.arrivalStation }}</template>
        </el-table-column>
        <el-table-column prop="passengerName" label="乘客" width="90" />
        <el-table-column prop="amount" label="金额" width="80">
          <template #default="{ row }">¥{{ row.amount }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="160">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button v-if="row.status === 0 || row.status === 1" type="danger" link size="small" @click="handleCancel(row)">取消</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAdminOrders, forceCancelOrder } from '@/api/admin'
import dayjs from 'dayjs'

const loading = ref(false)
const orders = ref([])
const searchKey = ref('')
const statusFilter = ref(null)

const filteredOrders = computed(() => {
  let result = orders.value
  if (searchKey.value) {
    result = result.filter(o => o.orderNo?.includes(searchKey.value))
  }
  return result
})

const loadOrders = async () => {
  loading.value = true
  try {
    const params = { page: 1, size: 100 }
    if (statusFilter.value !== null) params.status = statusFilter.value
    const res = await getAdminOrders(params)
    if (res.data.code === 200) orders.value = res.data.data || []
  } catch (e) { console.error(e) }
  finally { loading.value = false }
}

watch(statusFilter, loadOrders)

const formatTime = (time) => time ? dayjs(time).format('YYYY-MM-DD HH:mm') : '-'

const getStatusType = (status) => {
  const types = { 0: 'warning', 1: 'success', 2: 'info', 3: 'danger' }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = { 0: '待支付', 1: '已支付', 2: '已取消', 3: '已退票' }
  return texts[status] || '未知'
}

const handleCancel = async (row) => {
  try {
    await ElMessageBox.confirm('确认强制取消该订单？', '提示', { type: 'warning' })
    const res = await forceCancelOrder(row.id)
    if (res.data.code === 200) {
      ElMessage.success('订单已取消')
      loadOrders()
    }
  } catch (e) { if (e !== 'cancel') ElMessage.error('取消失败') }
}

onMounted(loadOrders)
</script>

<style lang="scss" scoped>
.card {
  .card-header {
    display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;
    h3 { font-size: 16px; font-weight: 600; }
    .header-actions { display: flex; align-items: center; }
  }
}
</style>
