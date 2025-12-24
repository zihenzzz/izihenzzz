<template>
  <div class="admin-page">
    <div class="container">
      <div class="page-header">
        <h2>管理后台</h2>
      </div>
      
      <el-row :gutter="20">
        <el-col :span="4">
          <el-menu
            :default-active="activeMenu"
            class="admin-menu"
            @select="handleMenuSelect"
          >
            <el-menu-item index="dashboard">
              <el-icon><DataAnalysis /></el-icon>
              <span>数据概览</span>
            </el-menu-item>
            <el-menu-item index="trains">
              <el-icon><Train /></el-icon>
              <span>车次管理</span>
            </el-menu-item>
            <el-menu-item index="stations">
              <el-icon><Location /></el-icon>
              <span>车站管理</span>
            </el-menu-item>
            <el-menu-item index="orders">
              <el-icon><List /></el-icon>
              <span>订单管理</span>
            </el-menu-item>
            <el-menu-item index="users">
              <el-icon><User /></el-icon>
              <span>用户管理</span>
            </el-menu-item>
          </el-menu>
        </el-col>
        
        <el-col :span="20">
          <!-- 数据概览 -->
          <div v-if="activeMenu === 'dashboard'" class="dashboard-content">
            <el-row :gutter="20" class="stat-cards">
              <el-col :span="6">
                <div class="stat-card">
                  <div class="stat-icon blue">
                    <el-icon><Train /></el-icon>
                  </div>
                  <div class="stat-info">
                    <span class="stat-value">{{ stats.trainCount }}</span>
                    <span class="stat-label">车次总数</span>
                  </div>
                </div>
              </el-col>
              <el-col :span="6">
                <div class="stat-card">
                  <div class="stat-icon green">
                    <el-icon><Ticket /></el-icon>
                  </div>
                  <div class="stat-info">
                    <span class="stat-value">{{ stats.orderCount }}</span>
                    <span class="stat-label">订单总数</span>
                  </div>
                </div>
              </el-col>
              <el-col :span="6">
                <div class="stat-card">
                  <div class="stat-icon orange">
                    <el-icon><User /></el-icon>
                  </div>
                  <div class="stat-info">
                    <span class="stat-value">{{ stats.userCount }}</span>
                    <span class="stat-label">用户总数</span>
                  </div>
                </div>
              </el-col>
              <el-col :span="6">
                <div class="stat-card">
                  <div class="stat-icon purple">
                    <el-icon><Money /></el-icon>
                  </div>
                  <div class="stat-info">
                    <span class="stat-value">¥{{ stats.revenue }}</span>
                    <span class="stat-label">总收入</span>
                  </div>
                </div>
              </el-col>
            </el-row>
            
            <el-row :gutter="20" class="chart-section">
              <el-col :span="12">
                <div class="card">
                  <h3>最近订单</h3>
                  <el-table :data="recentOrders" style="width: 100%">
                    <el-table-column prop="orderNo" label="订单号" width="180" />
                    <el-table-column prop="trainCode" label="车次" width="100" />
                    <el-table-column prop="amount" label="金额">
                      <template #default="{ row }">
                        ¥{{ row.amount }}
                      </template>
                    </el-table-column>
                    <el-table-column prop="status" label="状态">
                      <template #default="{ row }">
                        <el-tag :type="getStatusType(row.status)" size="small">
                          {{ getStatusText(row.status) }}
                        </el-tag>
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
                      <span class="route-name">{{ route.from }} → {{ route.to }}</span>
                      <span class="route-count">{{ route.count }} 订单</span>
                    </div>
                  </div>
                </div>
              </el-col>
            </el-row>
          </div>
          
          <!-- 车次管理 -->
          <div v-if="activeMenu === 'trains'" class="train-management">
            <div class="card">
              <div class="card-header">
                <h3>车次列表</h3>
                <el-button type="primary" @click="showTrainDialog">
                  <el-icon><Plus /></el-icon>
                  添加车次
                </el-button>
              </div>
              
              <el-table :data="trains" style="width: 100%">
                <el-table-column prop="trainCode" label="车次" width="100" />
                <el-table-column prop="trainType" label="类型" width="80">
                  <template #default="{ row }">
                    <el-tag size="small">{{ row.trainType }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="出发-到达">
                  <template #default="{ row }">
                    {{ row.departureStationName }} → {{ row.arrivalStationName }}
                  </template>
                </el-table-column>
                <el-table-column label="时间" width="150">
                  <template #default="{ row }">
                    {{ row.departureTime }} - {{ row.arrivalTime }}
                  </template>
                </el-table-column>
                <el-table-column prop="secondClassPrice" label="二等座" width="100">
                  <template #default="{ row }">
                    ¥{{ row.secondClassPrice || '-' }}
                  </template>
                </el-table-column>
                <el-table-column prop="status" label="状态" width="80">
                  <template #default="{ row }">
                    <el-switch
                      v-model="row.status"
                      :active-value="1"
                      :inactive-value="0"
                      @change="updateTrainStatus(row)"
                    />
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="150">
                  <template #default="{ row }">
                    <el-button type="primary" link size="small" @click="editTrain(row)">编辑</el-button>
                    <el-button type="danger" link size="small" @click="deleteTrain(row)">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
          
          <!-- 车站管理 -->
          <div v-if="activeMenu === 'stations'" class="station-management">
            <div class="card">
              <div class="card-header">
                <h3>车站列表</h3>
                <el-button type="primary" @click="showStationDialog">
                  <el-icon><Plus /></el-icon>
                  添加车站
                </el-button>
              </div>
              
              <el-table :data="stations" style="width: 100%">
                <el-table-column type="index" width="50" />
                <el-table-column prop="name" label="车站名称" />
                <el-table-column prop="city" label="所在城市" />
                <el-table-column prop="abbreviation" label="简称" width="100" />
                <el-table-column label="操作" width="150">
                  <template #default="{ row }">
                    <el-button type="primary" link size="small" @click="editStation(row)">编辑</el-button>
                    <el-button type="danger" link size="small" @click="deleteStation(row)">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
          
          <!-- 订单管理 -->
          <div v-if="activeMenu === 'orders'" class="order-management">
            <div class="card">
              <div class="card-header">
                <h3>订单列表</h3>
                <el-input
                  placeholder="搜索订单号"
                  prefix-icon="Search"
                  v-model="orderSearch"
                  style="width: 240px"
                  clearable
                />
              </div>
              
              <el-table :data="filteredOrders" style="width: 100%">
                <el-table-column prop="orderNo" label="订单号" width="180" />
                <el-table-column prop="trainCode" label="车次" width="100" />
                <el-table-column label="出发-到达">
                  <template #default="{ row }">
                    {{ row.departureStation }} → {{ row.arrivalStation }}
                  </template>
                </el-table-column>
                <el-table-column prop="passengerName" label="乘客" width="100" />
                <el-table-column prop="amount" label="金额">
                  <template #default="{ row }">
                    ¥{{ row.amount }}
                  </template>
                </el-table-column>
                <el-table-column prop="status" label="状态" width="100">
                  <template #default="{ row }">
                    <el-tag :type="getStatusType(row.status)" size="small">
                      {{ getStatusText(row.status) }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="createTime" label="下单时间" width="160">
                  <template #default="{ row }">
                    {{ formatTime(row.createTime) }}
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
          
          <!-- 用户管理 -->
          <div v-if="activeMenu === 'users'" class="user-management">
            <div class="card">
              <div class="card-header">
                <h3>用户列表</h3>
              </div>
              
              <el-table :data="users" style="width: 100%">
                <el-table-column type="index" width="50" />
                <el-table-column prop="username" label="用户名" width="120" />
                <el-table-column prop="realName" label="真实姓名" width="100" />
                <el-table-column prop="phone" label="手机号" width="120" />
                <el-table-column prop="email" label="邮箱" width="180" />
                <el-table-column prop="role" label="角色" width="80">
                  <template #default="{ row }">
                    <el-tag :type="row.role === 'ADMIN' ? 'danger' : ''" size="small">
                      {{ row.role === 'ADMIN' ? '管理员' : '用户' }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="createTime" label="注册时间" width="160">
                  <template #default="{ row }">
                    {{ formatTime(row.createTime) }}
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { 
  DataAnalysis, Train, Location, List, User, Ticket, Money, Plus, Search 
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'

const activeMenu = ref('dashboard')
const orderSearch = ref('')

const stats = ref({
  trainCount: 8,
  orderCount: 156,
  userCount: 89,
  revenue: 45230
})

const trains = ref([
  { id: 1, trainCode: 'G101', trainType: 'G', departureStationName: '北京南', arrivalStationName: '上海虹桥', departureTime: '08:00', arrivalTime: '12:36', secondClassPrice: 553, status: 1 },
  { id: 2, trainCode: 'G103', trainType: 'G', departureStationName: '北京南', arrivalStationName: '上海虹桥', departureTime: '09:00', arrivalTime: '13:30', secondClassPrice: 553, status: 1 },
  { id: 3, trainCode: 'D201', trainType: 'D', departureStationName: '武汉站', arrivalStationName: '成都东', departureTime: '07:30', arrivalTime: '14:00', secondClassPrice: 0, status: 1 }
])

const stations = ref([
  { id: 1, name: '北京南', city: '北京', abbreviation: 'BJN' },
  { id: 2, name: '上海虹桥', city: '上海', abbreviation: 'SHH' },
  { id: 3, name: '广州南', city: '广州', abbreviation: 'GZN' },
  { id: 4, name: '深圳北', city: '深圳', abbreviation: 'SZB' }
])

const orders = ref([
  { id: 1, orderNo: 'RB123456789', trainCode: 'G101', departureStation: '北京南', arrivalStation: '上海虹桥', passengerName: '张三', amount: 553, status: 1, createTime: new Date() },
  { id: 2, orderNo: 'RB987654321', trainCode: 'G103', departureStation: '北京南', arrivalStation: '上海虹桥', passengerName: '李四', amount: 553, status: 0, createTime: new Date() }
])

const users = ref([
  { id: 1, username: 'admin', realName: '管理员', phone: '13800000000', email: 'admin@rail.com', role: 'ADMIN', createTime: new Date() },
  { id: 2, username: 'test', realName: '测试用户', phone: '13900000000', email: 'test@rail.com', role: 'USER', createTime: new Date() }
])

const recentOrders = ref([
  { orderNo: 'RB123456789', trainCode: 'G101', amount: 553, status: 1 },
  { orderNo: 'RB987654321', trainCode: 'G103', amount: 553, status: 0 },
  { orderNo: 'RB456789123', trainCode: 'D201', amount: 180, status: 1 }
])

const hotRoutes = ref([
  { from: '北京', to: '上海', count: 45 },
  { from: '广州', to: '深圳', count: 38 },
  { from: '杭州', to: '南京', count: 25 },
  { from: '武汉', to: '成都', count: 18 }
])

const handleMenuSelect = (index) => {
  activeMenu.value = index
}

const formatTime = (time) => {
  return dayjs(time).format('YYYY-MM-DD HH:mm')
}

const getStatusType = (status) => {
  const types = { 0: 'warning', 1: 'success', 2: 'info', 3: 'danger' }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = { 0: '待支付', 1: '已支付', 2: '已取消', 3: '已退票' }
  return texts[status] || '未知'
}

const filteredOrders = computed(() => {
  if (!orderSearch.value) return orders.value
  return orders.value.filter(o => o.orderNo.includes(orderSearch.value))
})

const showTrainDialog = () => {
  ElMessage.info('添加车次功能开发中')
}

const editTrain = (train) => {
  ElMessage.info('编辑车次功能开发中')
}

const deleteTrain = async (train) => {
  try {
    await ElMessageBox.confirm('确认删除该车次？', '提示', { type: 'warning' })
    trains.value = trains.value.filter(t => t.id !== train.id)
    ElMessage.success('删除成功')
  } catch {
    // 取消
  }
}

const updateTrainStatus = (train) => {
  ElMessage.success(train.status === 1 ? '车次已启用' : '车次已停运')
}

const showStationDialog = () => {
  ElMessage.info('添加车站功能开发中')
}

const editStation = (station) => {
  ElMessage.info('编辑车站功能开发中')
}

const deleteStation = async (station) => {
  try {
    await ElMessageBox.confirm('确认删除该车站？', '提示', { type: 'warning' })
    stations.value = stations.value.filter(s => s.id !== station.id)
    ElMessage.success('删除成功')
  } catch {
    // 取消
  }
}
</script>

<style lang="scss" scoped>
.admin-page {
  min-height: calc(100vh - 64px - 72px);
  padding: 20px 0;
}

.page-header {
  margin-bottom: 20px;
  
  h2 {
    font-size: 24px;
    font-weight: 600;
  }
}

.admin-menu {
  min-height: calc(100vh - 180px);
  border-radius: 8px;
}

.stat-cards {
  margin-bottom: 20px;
}

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
    .stat-value {
      display: block;
      font-size: 28px;
      font-weight: 600;
      color: #333;
    }
    
    .stat-label {
      font-size: 14px;
      color: #999;
    }
  }
}

.chart-section {
  .card {
    h3 {
      font-size: 16px;
      font-weight: 600;
      margin-bottom: 16px;
    }
  }
}

.hot-routes {
  .route-item {
    display: flex;
    align-items: center;
    padding: 12px 0;
    border-bottom: 1px solid #f0f0f0;
    
    &:last-child {
      border-bottom: none;
    }
    
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
    
    .route-name {
      flex: 1;
      color: #333;
    }
    
    .route-count {
      color: #999;
      font-size: 13px;
    }
  }
}

.card {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    
    h3 {
      font-size: 16px;
      font-weight: 600;
    }
  }
}
</style>
