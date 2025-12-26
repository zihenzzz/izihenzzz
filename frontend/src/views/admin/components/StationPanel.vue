<template>
  <div class="station-management">
    <div class="card">
      <div class="card-header">
        <h3>车站列表</h3>
        <el-button type="primary" @click="showDialog()">
          <el-icon><Plus /></el-icon>添加车站
        </el-button>
      </div>
      
      <el-table :data="stations" style="width: 100%" v-loading="loading">
        <el-table-column type="index" width="50" />
        <el-table-column prop="name" label="车站名称" />
        <el-table-column prop="city" label="所在城市" />
        <el-table-column prop="address" label="地址" />
        <el-table-column prop="phone" label="电话" width="130" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="showDialog(row)">编辑</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑车站' : '添加车站'" width="500px">
      <el-form :model="form" label-width="80px" :rules="rules" ref="formRef">
        <el-form-item label="车站名称" prop="name">
          <el-input v-model="form.name" placeholder="如: 北京六里桥客运站" />
        </el-form-item>
        <el-form-item label="所在城市" prop="city">
          <el-input v-model="form.city" placeholder="如: 北京" />
        </el-form-item>
        <el-form-item label="详细地址">
          <el-input v-model="form.address" placeholder="如: 北京市丰台区六里桥南里甲1号" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="form.phone" placeholder="如: 010-83831716" />
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
import { getStations, addStation, updateStation, deleteStation } from '@/api/admin'

const emit = defineEmits(['refresh'])

const loading = ref(false)
const stations = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const formRef = ref(null)

const defaultForm = { name: '', city: '', address: '', phone: '' }
const form = ref({ ...defaultForm })

const rules = {
  name: [{ required: true, message: '请输入车站名称', trigger: 'blur' }],
  city: [{ required: true, message: '请输入所在城市', trigger: 'blur' }]
}

const loadStations = async () => {
  loading.value = true
  try {
    const res = await getStations()
    if (res.data.code === 200) stations.value = res.data.data || []
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
      ? await updateStation(form.value.id, form.value)
      : await addStation(form.value)
    if (res.data.code === 200) {
      ElMessage.success(isEdit.value ? '更新成功' : '添加成功')
      dialogVisible.value = false
      loadStations()
      emit('refresh')
    } else {
      ElMessage.error(res.data.message || '操作失败')
    }
  } catch (e) { ElMessage.error('操作失败') }
  finally { submitting.value = false }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除该车站？', '提示', { type: 'warning' })
    const res = await deleteStation(row.id)
    if (res.data.code === 200) {
      ElMessage.success('删除成功')
      loadStations()
      emit('refresh')
    }
  } catch (e) { if (e !== 'cancel') ElMessage.error('删除失败') }
}

onMounted(loadStations)
</script>

<style lang="scss" scoped>
.card {
  .card-header {
    display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;
    h3 { font-size: 16px; font-weight: 600; }
  }
}
</style>
