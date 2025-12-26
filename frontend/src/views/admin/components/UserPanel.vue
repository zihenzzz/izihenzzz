<template>
  <div class="user-management">
    <div class="card">
      <div class="card-header">
        <h3>用户列表</h3>
      </div>
      
      <el-table :data="users" style="width: 100%" v-loading="loading">
        <el-table-column type="index" width="50" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="真实姓名" width="100" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="email" label="邮箱" width="180" />
        <el-table-column prop="role" label="角色" width="90">
          <template #default="{ row }">
            <el-tag :type="row.role === 'ADMIN' ? 'danger' : ''" size="small">
              {{ row.role === 'ADMIN' ? '管理员' : '用户' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="160">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getUsers } from '@/api/admin'
import dayjs from 'dayjs'

const loading = ref(false)
const users = ref([])

const loadUsers = async () => {
  loading.value = true
  try {
    const res = await getUsers({ page: 1, size: 100 })
    if (res.data.code === 200) users.value = res.data.data || []
  } catch (e) { console.error(e) }
  finally { loading.value = false }
}

const formatTime = (time) => time ? dayjs(time).format('YYYY-MM-DD HH:mm') : '-'

onMounted(loadUsers)
</script>

<style lang="scss" scoped>
.card {
  .card-header {
    display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;
    h3 { font-size: 16px; font-weight: 600; }
  }
}
</style>
