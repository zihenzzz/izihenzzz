<template>
  <div class="train-management">
    <div class="card">
      <div class="card-header">
        <h3>班次列表</h3>
        <el-button type="primary" @click="showDialog()">
          <el-icon><Plus /></el-icon>添加班次
        </el-button>
      </div>
      
      <el-table :data="trains" style="width: 100%" v-loading="loading">
        <el-table-column prop="trainCode" label="班次号" width="100" />
        <el-table-column prop="trainType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag size="small">{{ getTrainTypeName(row.trainType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="出发-到达">
          <template #default="{ row }">{{ row.departureStationName }} → {{ row.arrivalStationName }}</template>
        </el-table-column>
        <el-table-column label="时间" width="140">
          <template #default="{ row }">{{ row.departureTime }} - {{ row.arrivalTime }}</template>
        </el-table-column>
        <el-table-column prop="secondClassPrice" label="普通座" width="90">
          <template #default="{ row }">¥{{ row.secondClassPrice || '-' }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-switch v-model="row.status" :active-value="1" :inactive-value="0" @change="handleStatusChange(row)" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="showDialog(row)">编辑</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 添加/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑班次' : '添加班次'" width="600px">
      <el-form :model="form" label-width="100px" :rules="rules" ref="formRef">
        <el-form-item label="班次号" prop="trainCode">
          <el-input v-model="form.trainCode" placeholder="如: K101" />
        </el-form-item>
        <el-form-item label="班次类型" prop="trainType">
          <el-select v-model="form.trainType" placeholder="选择类型" style="width: 100%">
            <el-option label="普通班次" value="NORMAL" />
            <el-option label="快客班次" value="EXPRESS" />
            <el-option label="卧铺班次" value="SLEEPER" />
          </el-select>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="出发站" prop="departureStationName">
              <el-select v-model="form.departureStationName" filterable placeholder="选择出发站" style="width: 100%">
                <el-option v-for="s in stations" :key="s.id" :label="s.name" :value="s.name" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="到达站" prop="arrivalStationName">
              <el-select v-model="form.arrivalStationName" filterable placeholder="选择到达站" style="width: 100%">
                <el-option v-for="s in stations" :key="s.id" :label="s.name" :value="s.name" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="出发时间" prop="departureTime">
              <el-time-picker v-model="form.departureTime" format="HH:mm" value-format="HH:mm" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="到达时间" prop="arrivalTime">
              <el-time-picker v-model="form.arrivalTime" format="HH:mm" value-format="HH:mm" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="普通座价格">
              <el-input-number v-model="form.secondClassPrice" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="商务座价格">
              <el-input-number v-model="form.firstClassPrice" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20" v-if="form.trainType === 'SLEEPER'">
          <el-col :span="12">
            <el-form-item label="下铺价格">
              <el-input-number v-model="form.hardSleeperPrice" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="上铺价格">
              <el-input-number v-model="form.softSleeperPrice" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="总座位数">
          <el-input-number v-model="form.totalSeats" :min="1" :max="100" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAdminTrains, addTrain, updateTrain, updateTrainStatus, deleteTrain } from '@/api/admin'

const props = defineProps({
  stations: { type: Array, default: () => [] }
})
const emit = defineEmits(['refresh'])

const loading = ref(false)
const trains = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const formRef = ref(null)

const defaultForm = {
  trainCode: '', trainType: 'NORMAL', departureStationName: '', arrivalStationName: '',
  departureTime: '', arrivalTime: '', secondClassPrice: 50, firstClassPrice: 80,
  hardSleeperPrice: 100, softSleeperPrice: 80, totalSeats: 45, status: 1
}
const form = ref({ ...defaultForm })

const rules = {
  trainCode: [{ required: true, message: '请输入班次号', trigger: 'blur' }],
  trainType: [{ required: true, message: '请选择班次类型', trigger: 'change' }],
  departureStationName: [{ required: true, message: '请选择出发站', trigger: 'change' }],
  arrivalStationName: [{ required: true, message: '请选择到达站', trigger: 'change' }],
  departureTime: [{ required: true, message: '请选择出发时间', trigger: 'change' }],
  arrivalTime: [{ required: true, message: '请选择到达时间', trigger: 'change' }]
}

const getTrainTypeName = (type) => {
  const map = { NORMAL: '普通', EXPRESS: '快客', SLEEPER: '卧铺' }
  return map[type] || type
}

const loadTrains = async () => {
  loading.value = true
  try {
    const res = await getAdminTrains({ page: 1, size: 100 })
    if (res.data.code === 200) trains.value = res.data.data || []
  } catch (e) { console.error(e) }
  finally { loading.value = false }
}

const showDialog = (row = null) => {
  isEdit.value = !!row
  form.value = row ? { ...row } : { ...defaultForm }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate()
  submitting.value = true
  try {
    const res = isEdit.value 
      ? await updateTrain(form.value.id, form.value)
      : await addTrain(form.value)
    if (res.data.code === 200) {
      ElMessage.success(isEdit.value ? '更新成功' : '添加成功')
      dialogVisible.value = false
      loadTrains()
      emit('refresh')
    } else {
      ElMessage.error(res.data.message || '操作失败')
    }
  } catch (e) { ElMessage.error('操作失败') }
  finally { submitting.value = false }
}

const handleStatusChange = async (row) => {
  try {
    const res = await updateTrainStatus(row.id, row.status)
    if (res.data.code === 200) {
      ElMessage.success(row.status === 1 ? '班次已启用' : '班次已停运')
    }
  } catch (e) { ElMessage.error('状态更新失败') }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除该班次？', '提示', { type: 'warning' })
    const res = await deleteTrain(row.id)
    if (res.data.code === 200) {
      ElMessage.success('删除成功')
      loadTrains()
    }
  } catch (e) { if (e !== 'cancel') ElMessage.error('删除失败') }
}

onMounted(loadTrains)
</script>

<style lang="scss" scoped>
.card {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    h3 { font-size: 16px; font-weight: 600; }
  }
}
</style>
