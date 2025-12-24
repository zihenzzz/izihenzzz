<template>
  <div class="profile-page">
    <div class="container">
      <div class="profile-header card">
        <div class="user-avatar">
          <el-avatar :size="80" :src="userStore.user?.avatar">
            <el-icon><User /></el-icon>
          </el-avatar>
        </div>
        <div class="user-info">
          <h2>{{ userStore.user?.realName || userStore.user?.username }}</h2>
          <p>{{ userStore.user?.phone || '未设置手机号' }}</p>
          <div class="user-badges">
            <el-tag type="success" v-if="userStore.isAdmin">管理员</el-tag>
            <el-tag v-else>普通用户</el-tag>
          </div>
        </div>
      </div>
      
      <el-row :gutter="20">
        <el-col :span="8">
          <div class="menu-card card">
            <div class="menu-item" @click="activeTab = 'info'">
              <el-icon><User /></el-icon>
              <span>个人信息</span>
              <el-icon class="right"><ArrowRight /></el-icon>
            </div>
            <div class="menu-item" @click="activeTab = 'contacts'">
              <el-icon><Postcard /></el-icon>
              <span>常用联系人</span>
              <el-icon class="right"><ArrowRight /></el-icon>
            </div>
            <div class="menu-item" @click="activeTab = 'security'">
              <el-icon><Lock /></el-icon>
              <span>账户安全</span>
              <el-icon class="right"><ArrowRight /></el-icon>
            </div>
            <div class="menu-item" @click="goOrders">
              <el-icon><List /></el-icon>
              <span>我的订单</span>
              <el-icon class="right"><ArrowRight /></el-icon>
            </div>
          </div>
        </el-col>
        
        <el-col :span="16">
          <!-- 个人信息 -->
          <div v-if="activeTab === 'info'" class="content-card card">
            <h3>个人信息</h3>
            <el-form :model="profileForm" label-width="100px" :rules="profileRules" ref="profileFormRef">
              <el-form-item label="用户名">
                <el-input :value="userStore.user?.username" disabled />
              </el-form-item>
              <el-form-item label="真实姓名">
                <el-input v-model="profileForm.realName" placeholder="请输入真实姓名" />
              </el-form-item>
              <el-form-item label="手机号">
                <el-input v-model="profileForm.phone" placeholder="请输入手机号" />
              </el-form-item>
              <el-form-item label="邮箱">
                <el-input v-model="profileForm.email" placeholder="请输入邮箱" />
              </el-form-item>
              <el-form-item label="身份证号">
                <el-input v-model="profileForm.idCard" placeholder="请输入身份证号" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="saveProfile">保存修改</el-button>
              </el-form-item>
            </el-form>
          </div>
          
          <!-- 常用联系人 -->
          <div v-if="activeTab === 'contacts'" class="content-card card">
            <h3>常用联系人</h3>
            <div class="contacts-list" v-if="contacts.length > 0">
              <div v-for="(contact, index) in contacts" :key="index" class="contact-item">
                <div class="contact-info">
                  <span class="name">{{ contact.name }}</span>
                  <span class="id-card">{{ formatIdCard(contact.idCard) }}</span>
                  <span class="phone">{{ contact.phone }}</span>
                </div>
                <div class="contact-actions">
                  <el-button type="danger" size="small" @click="deleteContact(index)">删除</el-button>
                </div>
              </div>
            </div>
            <el-empty v-else description="暂无常用联系人" />
            
            <el-divider />
            
            <h4>添加联系人</h4>
            <el-form :model="newContact" label-width="80px" class="add-contact-form">
              <el-form-item label="姓名" required>
                <el-input v-model="newContact.name" placeholder="联系人姓名" />
              </el-form-item>
              <el-form-item label="身份证" required>
                <el-input v-model="newContact.idCard" placeholder="身份证号码" />
              </el-form-item>
              <el-form-item label="手机号">
                <el-input v-model="newContact.phone" placeholder="手机号码" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="addContact">添加</el-button>
              </el-form-item>
            </el-form>
          </div>
          
          <!-- 账户安全 -->
          <div v-if="activeTab === 'security'" class="content-card card">
            <h3>账户安全</h3>
            <div class="security-item">
              <div class="security-info">
                <el-icon><Lock /></el-icon>
                <div class="security-text">
                  <span class="title">登录密码</span>
                  <span class="desc">定期更换密码，提高账户安全性</span>
                </div>
              </div>
              <el-button @click="showPasswordDialog">修改密码</el-button>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
    
    <!-- 修改密码对话框 -->
    <el-dialog v-model="passwordDialogVisible" title="修改密码" width="400px">
      <el-form :model="passwordForm" label-width="100px" :rules="passwordRules" ref="passwordFormRef">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="passwordDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="changePassword">确认修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { User, Postcard, Lock, List, ArrowRight } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const activeTab = ref('info')
const profileFormRef = ref(null)
const passwordFormRef = ref(null)
const passwordDialogVisible = ref(false)

const profileForm = reactive({
  realName: '',
  phone: '',
  email: '',
  idCard: ''
})

const newContact = reactive({
  name: '',
  idCard: '',
  phone: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const contacts = ref([
  { name: '张三', idCard: '110101199001011234', phone: '13800138000' }
])

const profileRules = {
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { 
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      }, 
      trigger: 'blur' 
    }
  ]
}

// 跳转到订单页面
const goOrders = () => {
  router.push('/orders')
}

// 保存个人信息
const saveProfile = async () => {
  try {
    // TODO: 调用API保存
    userStore.updateUser(profileForm)
    ElMessage.success('保存成功')
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

// 格式化身份证号
const formatIdCard = (idCard) => {
  if (!idCard) return '--'
  return idCard.replace(/(\d{3})\d{12}(\w{1})/, '$1***********$2')
}

// 添加联系人
const addContact = () => {
  if (!newContact.name || !newContact.idCard) {
    ElMessage.warning('请填写完整信息')
    return
  }
  contacts.value.push({ ...newContact })
  newContact.name = ''
  newContact.idCard = ''
  newContact.phone = ''
  ElMessage.success('添加成功')
}

// 删除联系人
const deleteContact = (index) => {
  contacts.value.splice(index, 1)
  ElMessage.success('删除成功')
}

// 显示修改密码对话框
const showPasswordDialog = () => {
  passwordDialogVisible.value = true
}

// 修改密码
const changePassword = async () => {
  try {
    await passwordFormRef.value.validate()
    // TODO: 调用API修改密码
    passwordDialogVisible.value = false
    ElMessage.success('密码修改成功')
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
  } catch (error) {
    // 验证失败
  }
}

onMounted(() => {
  if (userStore.user) {
    profileForm.realName = userStore.user.realName || ''
    profileForm.phone = userStore.user.phone || ''
    profileForm.email = userStore.user.email || ''
    profileForm.idCard = userStore.user.idCard || ''
  }
})
</script>

<style lang="scss" scoped>
.profile-page {
  min-height: calc(100vh - 64px - 72px);
  padding: 20px 0;
}

.profile-header {
  display: flex;
  align-items: center;
  padding: 32px;
  margin-bottom: 20px;
  
  .user-avatar {
    margin-right: 24px;
  }
  
  .user-info {
    h2 {
      font-size: 24px;
      font-weight: 600;
      margin-bottom: 8px;
    }
    
    p {
      color: #666;
      margin-bottom: 12px;
    }
  }
}

.menu-card {
  padding: 0;
  
  .menu-item {
    display: flex;
    align-items: center;
    padding: 16px 20px;
    cursor: pointer;
    transition: background 0.3s;
    
    &:hover {
      background: #f5f7fa;
    }
    
    .el-icon {
      margin-right: 12px;
      font-size: 20px;
      color: #1890ff;
    }
    
    .right {
      margin-left: auto;
      color: #999;
    }
  }
}

.content-card {
  h3, h4 {
    font-size: 16px;
    font-weight: 600;
    margin-bottom: 16px;
    color: #333;
  }
  
  h4 {
    margin-top: 16px;
  }
}

.contacts-list {
  .contact-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 12px 16px;
    background: #f5f7fa;
    border-radius: 6px;
    margin-bottom: 8px;
    
    .contact-info {
      .name {
        font-weight: 600;
        margin-right: 16px;
      }
      
      .id-card, .phone {
        color: #666;
        margin-right: 16px;
        font-size: 13px;
      }
    }
  }
}

.security-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
  
  .security-info {
    display: flex;
    align-items: center;
    gap: 12px;
    
    .el-icon {
      font-size: 32px;
      color: #1890ff;
    }
    
    .security-text {
      .title {
        display: block;
        font-weight: 600;
        margin-bottom: 4px;
      }
      
      .desc {
        font-size: 13px;
        color: #666;
      }
    }
  }
}
</style>
