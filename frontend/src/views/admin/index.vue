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
              <el-icon><Van /></el-icon>
              <span>班次管理</span>
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
          <DashboardPanel v-if="activeMenu === 'dashboard'" :stats="stats" :recentOrders="recentOrders" :hotRoutes="hotRoutes" />
          
          <!-- 班次管理 -->
          <TrainPanel v-if="activeMenu === 'trains'" :stations="stations" @refresh="loadTrains" />
          
          <!-- 车站管理 -->
          <StationPanel v-if="activeMenu === 'stations'" @refresh="loadStations" />
          
          <!-- 订单管理 -->
          <OrderPanel v-if="activeMenu === 'orders'" />
          
          <!-- 用户管理 -->
          <UserPanel v-if="activeMenu === 'users'" />
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { DataAnalysis, Van, Location, List, User } from '@element-plus/icons-vue'
import { getStats, getStations } from '@/api/admin'
import DashboardPanel from './components/DashboardPanel.vue'
import TrainPanel from './components/TrainPanel.vue'
import StationPanel from './components/StationPanel.vue'
import OrderPanel from './components/OrderPanel.vue'
import UserPanel from './components/UserPanel.vue'

const activeMenu = ref('dashboard')
const stats = ref({ userCount: 0, trainCount: 0, orderCount: 0, totalRevenue: 0 })
const recentOrders = ref([])
const hotRoutes = ref([])
const stations = ref([])

const handleMenuSelect = (index) => {
  activeMenu.value = index
}

const loadStats = async () => {
  try {
    const res = await getStats()
    if (res.data.code === 200) {
      const data = res.data.data
      stats.value = {
        userCount: data.userCount || 0,
        trainCount: data.trainCount || 0,
        orderCount: data.orderCount || 0,
        totalRevenue: data.totalRevenue || 0
      }
      recentOrders.value = data.recentOrders || []
      // 转换热门线路格式
      if (data.hotRoutes) {
        hotRoutes.value = Object.entries(data.hotRoutes)
          .map(([route, count]) => ({ route, count }))
          .sort((a, b) => b.count - a.count)
          .slice(0, 5)
      }
    }
  } catch (error) {
    console.error('加载统计数据失败', error)
  }
}

const loadStations = async () => {
  try {
    const res = await getStations()
    if (res.data.code === 200) {
      stations.value = res.data.data || []
    }
  } catch (error) {
    console.error('加载车站数据失败', error)
  }
}

const loadTrains = () => {
  // 触发刷新
}

onMounted(() => {
  loadStats()
  loadStations()
})
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
</style>
