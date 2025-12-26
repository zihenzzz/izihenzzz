<template>
  <div class="search-page">
    <div class="container">
      <div class="search-filter">
        <h3 class="filter-title">🚌 查询条件</h3>
        <div class="filter-form">
          <el-row :gutter="16">
            <el-col :span="6">
              <el-select v-model="searchParams.departure" placeholder="出发站" filterable clearable style="width: 100%">
                <el-option v-for="s in stations" :key="s.id" :label="s.name" :value="s.name" />
              </el-select>
            </el-col>
            <el-col :span="6">
              <el-select v-model="searchParams.arrival" placeholder="到达站" filterable clearable style="width: 100%">
                <el-option v-for="s in stations" :key="s.id" :label="s.name" :value="s.name" />
              </el-select>
            </el-col>
            <el-col :span="6">
              <el-date-picker v-model="searchParams.date" type="date" placeholder="出发日期" :disabled-date="disabledDate" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-col>
            <el-col :span="6">
              <el-button type="primary" @click="handleSearch" :loading="loading"><el-icon><Search /></el-icon>查询</el-button>
            </el-col>
          </el-row>
        </div>
      </div>
      
      <div class="search-results">
        <div class="results-header" v-if="buses.length > 0">
          <span class="results-count">共找到 <strong>{{ buses.length }}</strong> 趟班次</span>
          <el-radio-group v-model="sortBy" size="small">
            <el-radio-button label="departureTime">发车时间</el-radio-button>
            <el-radio-button label="duration">历时</el-radio-button>
            <el-radio-button label="price">票价</el-radio-button>
          </el-radio-group>
        </div>
        
        <div v-if="loading" class="loading-state"><el-skeleton :rows="5" animated /></div>
        <el-empty v-else-if="buses.length === 0 && hasSearched" description="未找到相关班次" />
        
        <div v-else class="bus-list">
          <div v-for="bus in sortedBuses" :key="bus.id" class="bus-card">
            <div class="bus-info">
              <div class="bus-time">
                <span class="time">{{ formatTime(bus.departureTime) }}</span>
                <span class="station">{{ bus.departureStationName }}</span>
              </div>
              <div class="bus-duration">
                <span class="duration-text">{{ formatDuration(bus.duration) }}</span>
                <div class="duration-line"><div class="line"></div><el-icon><Right /></el-icon></div>
                <span class="bus-company">{{ bus.busCompany || '客运公司' }}</span>
              </div>
              <div class="bus-time">
                <span class="time">{{ formatTime(bus.arrivalTime) }}</span>
                <span class="station">{{ bus.arrivalStationName }}</span>
              </div>
            </div>
            <div class="bus-type">
              <el-tag size="small" :type="getBusTypeColor(bus.trainType)">{{ bus.trainCode }}</el-tag>
              <span class="type-name">{{ getBusTypeName(bus.trainType) }}</span>
            </div>
            <div class="bus-seats">
              <div v-if="bus.secondClassPrice > 0" class="seat-item" @click="bookTicket(bus, 'SECOND_CLASS')">
                <span class="seat-type">普通座</span>
                <span class="seat-price">¥{{ bus.secondClassPrice }}</span>
                <span class="seat-count">{{ getTicketCount(bus, 'secondClass') }}张</span>
              </div>
              <div v-if="bus.firstClassPrice > 0" class="seat-item" @click="bookTicket(bus, 'FIRST_CLASS')">
                <span class="seat-type">商务座</span>
                <span class="seat-price">¥{{ bus.firstClassPrice }}</span>
                <span class="seat-count">{{ getTicketCount(bus, 'firstClass') }}张</span>
              </div>
              <div v-if="bus.hardSleeperPrice > 0" class="seat-item" @click="bookTicket(bus, 'HARD_SLEEPER')">
                <span class="seat-type">下铺</span>
                <span class="seat-price">¥{{ bus.hardSleeperPrice }}</span>
                <span class="seat-count">{{ getTicketCount(bus, 'hardSleeper') }}张</span>
              </div>
              <div v-if="bus.softSleeperPrice > 0" class="seat-item" @click="bookTicket(bus, 'SOFT_SLEEPER')">
                <span class="seat-type">上铺</span>
                <span class="seat-price">¥{{ bus.softSleeperPrice }}</span>
                <span class="seat-count">{{ getTicketCount(bus, 'softSleeper') }}张</span>
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
import { searchTrains, getAllStations } from '@/api/train'
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
const buses = ref([])
const stations = ref([])
const searchParams = reactive({ departure: '', arrival: '', date: dayjs().format('YYYY-MM-DD') })

const disabledDate = (time) => time.getTime() < Date.now() - 8.64e7

const fetchStations = async () => {
  try {
    const res = await getAllStations()
    if (res.data.code === 200) stations.value = res.data.data || []
  } catch (e) { console.error(e) }
}

const getTicketCount = (bus, type) => bus.extra?.[type] ?? Math.floor(Math.random() * 30) + 5
const formatTime = (t) => t ? (t.length <= 5 ? t : t.substring(0, 5)) : '--:--'
const formatDuration = (m) => m ? `${Math.floor(m/60)}小时${m%60}分` : '--'
const getBusTypeColor = (t) => ({ EXPRESS: 'success', NORMAL: 'info', SLEEPER: 'warning' }[t] || 'info')
const getBusTypeName = (t) => ({ EXPRESS: '快客', NORMAL: '普通', SLEEPER: '卧铺' }[t] || '班车')

const sortedBuses = computed(() => {
  const s = [...buses.value]
  if (sortBy.value === 'departureTime') return s.sort((a,b) => a.departureTime.localeCompare(b.departureTime))
  if (sortBy.value === 'duration') return s.sort((a,b) => (a.duration||0) - (b.duration||0))
  if (sortBy.value === 'price') return s.sort((a,b) => (a.secondClassPrice||a.hardSleeperPrice||0) - (b.secondClassPrice||b.hardSleeperPrice||0))
  return s
})

const handleSearch = async () => {
  if (!searchParams.departure || !searchParams.arrival || !searchParams.date) {
    ElMessage.warning('请填写完整的查询信息'); return
  }
  loading.value = true; hasSearched.value = true
  try {
    const res = await searchTrains({ departureStation: searchParams.departure, arrivalStation: searchParams.arrival, travelDate: searchParams.date })
    if (res.data.code === 200) { buses.value = res.data.data || []; if (!buses.value.length) ElMessage.info('未找到相关班次') }
    else ElMessage.error(res.data.message || '查询失败')
  } catch (e) { ElMessage.error('查询失败') }
  finally { loading.value = false }
}

const bookTicket = (bus, seatType) => {
  if (!userStore.isLoggedIn) { ElMessage.warning('请先登录'); router.push({ name: 'Login' }); return }
  router.push({ name: 'Booking', params: { trainId: bus.id }, query: { seatType, date: searchParams.date } })
}

onMounted(() => {
  fetchStations()
  if (route.query.from) searchParams.departure = route.query.from
  if (route.query.to) searchParams.arrival = route.query.to
  if (route.query.date) searchParams.date = route.query.date
  if (searchParams.departure && searchParams.arrival && searchParams.date) handleSearch()
})
</script>

<style lang="scss" scoped>
.search-page { min-height: calc(100vh - 136px); padding: 24px 0; background: #f5f7fa; }
.search-filter { margin-bottom: 24px; background: #fff; border-radius: 16px; padding: 24px; box-shadow: 0 4px 20px rgba(0,0,0,0.08); }
.filter-title { font-size: 18px; font-weight: 700; margin-bottom: 20px; color: #333; }
.filter-form .el-button { width: 100%; height: 40px; border-radius: 20px; background: linear-gradient(135deg, #1e88e5, #1565c0); border: none; }
.results-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.results-count strong { color: #1e88e5; font-size: 16px; }
.loading-state { background: #fff; border-radius: 12px; padding: 60px 20px; }
.bus-list { display: flex; flex-direction: column; gap: 12px; }
.bus-card { background: #fff; border-radius: 16px; padding: 24px; display: flex; align-items: center; box-shadow: 0 4px 20px rgba(0,0,0,0.08); transition: all 0.3s; }
.bus-card:hover { transform: translateY(-4px); box-shadow: 0 12px 30px rgba(0,0,0,0.12); }
.bus-info { flex: 1; display: flex; align-items: center; gap: 40px; }
.bus-time { text-align: center; }
.bus-time .time { display: block; font-size: 24px; font-weight: 600; color: #333; }
.bus-time .station { font-size: 13px; color: #999; }
.bus-duration { text-align: center; min-width: 160px; }
.bus-duration .duration-text { font-size: 13px; color: #666; display: block; margin-bottom: 4px; }
.duration-line { display: flex; align-items: center; justify-content: center; gap: 4px; }
.duration-line .line { width: 100px; height: 2px; background: linear-gradient(90deg, #1e88e5, #4caf50); }
.duration-line .el-icon { color: #4caf50; }
.bus-company { display: block; font-size: 12px; color: #999; margin-top: 4px; }
.bus-type { text-align: center; padding: 0 24px; border-right: 1px solid #f0f0f0; margin-right: 24px; }
.type-name { display: block; font-size: 12px; color: #666; margin-top: 4px; }
.bus-seats { display: flex; gap: 16px; }
.seat-item { text-align: center; padding: 12px 16px; border-radius: 8px; cursor: pointer; transition: all 0.3s; min-width: 90px; border: 1px solid #e0e0e0; }
.seat-item:hover { background: #e3f2fd; border-color: #1e88e5; }
.seat-type { display: block; font-size: 13px; color: #666; }
.seat-price { display: block; font-size: 18px; font-weight: 600; color: #ff6600; margin: 4px 0; }
.seat-count { display: block; font-size: 12px; color: #4caf50; }
@media (max-width: 768px) {
  .bus-card { flex-direction: column; gap: 16px; }
  .bus-info { flex-direction: column; gap: 16px; }
  .bus-type { border-right: none; border-bottom: 1px solid #f0f0f0; padding-bottom: 16px; margin-right: 0; }
  .bus-seats { flex-wrap: wrap; justify-content: center; }
}
</style>
