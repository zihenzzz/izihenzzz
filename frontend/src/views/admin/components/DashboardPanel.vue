<template>
  <div class="dashboard-content">
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon blue"><el-icon><Van /></el-icon></div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.trainCount }}</span>
            <span class="stat-label">班次总数</span>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon green"><el-icon><Ticket /></el-icon></div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.orderCount }}</span>
            <span class="stat-label">订单总数</span>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon orange"><el-icon><User /></el-icon></div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.userCount }}</span>
            <span class="stat-label">用户总数</span>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon purple"><el-icon><Money /></el-icon></div>
          <div class="stat-info">
            <span class="stat-value">¥{{ stats.totalRevenue }}</span>
            <span class="stat-label">总收入</span>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-section">
      <el-col :span="12">
        <div class="card">
          <h3>最近订单</h3>
          <el-table :data="recentOrders" style="width: 100%" size="small">
            <el-table-column prop="orderNo" label="订单号" width="160" />
            <el-table-column prop="trainCode" label="班次" width="80" />
            <el-table-column prop="amount" label="金额">
              <template #default="{ row }">¥{{ row.amount }}</template>
            </el-table-column>
            <el-table-column prop="status" label="状态">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)" size="small">{{ getStatusText(row.status) }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-col>
      <el-col :span="12">
        <div class="card">
          <h3>热门线路</h3>
          <div class="hot-routes">
            <div v-for="(route, index) in hotRoutes" :key="index" class="route-item">
              <span class="route-rank">{{ index + 1 }}</span>
              <span class="route-name">{{ route.route }}</span>
              <span class="route-count">{{ route.count }} 订单</span>
            </div>
            <el-empty v-if="!hotRoutes.length" description="暂无数据" />
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { Van, Ticket, User, Money } from '@element-plus/icons-vue'

defineProps({
  stats: { type: Object, default: () => ({}) },
  recentOrders: { type: Array, default: () => [] },
  hotRoutes: { type: Array, default: () => [] }
})

const getStatusType = (status) => {
  const types = { 0: 'warning', 1: 'success', 2: 'info', 3: 'danger' }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = { 0: '待支付', 1: '已支付', 2: '已取消', 3: '已退票' }
  return texts[status] || '未知'
}
</script>

<style lang="scss" scoped>
.stat-cards { margin-bottom: 20px; }

.stat-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  
  .stat-icon {
    width: 56px;
    height: 56px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 16px;
    font-size: 28px;
    color: #fff;
    
    &.blue { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
    &.green { background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%); }
    &.orange { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); }
    &.purple { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); }
  }
  
  .stat-info {
    .stat-value { display: block; font-size: 28px; font-weight: 600; color: #333; }
    .stat-label { font-size: 14px; color: #999; }
  }
}

.chart-section .card h3 { font-size: 16px; font-weight: 600; margin-bottom: 16px; }

.hot-routes {
  .route-item {
    display: flex;
    align-items: center;
    padding: 12px 0;
    border-bottom: 1px solid #f0f0f0;
    
    &:last-child { border-bottom: none; }
    
    .route-rank {
      width: 24px;
      height: 24px;
      border-radius: 50%;
      background: #1890ff;
      color: #fff;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 12px;
      margin-right: 12px;
    }
    
    .route-name { flex: 1; color: #333; }
    .route-count { color: #999; font-size: 13px; }
  }
}
</style>
