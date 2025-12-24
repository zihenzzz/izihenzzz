import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login, register, getUserInfo, logout as apiLogout } from '@/api/auth'
import router from '@/router'
import { ElMessage } from 'element-plus'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))
  const loading = ref(false)

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => user.value?.role === 'ADMIN')
  const username = computed(() => user.value?.username || '')

  async function doLogin(username, password) {
    loading.value = true
    try {
      const response = await login({ username, password })
      if (response.data.code === 200) {
        const { token: newToken, tokenType } = response.data.data
        token.value = newToken
        
        // 获取用户信息
        const userResponse = await getUserInfo()
        if (userResponse.data.code === 200) {
          user.value = userResponse.data.data
        }
        
        localStorage.setItem('token', newToken)
        localStorage.setItem('user', JSON.stringify(user.value))
        
        ElMessage.success('登录成功')
        return true
      } else {
        ElMessage.error(response.data.message || '登录失败')
        return false
      }
    } catch (error) {
      ElMessage.error('登录失败，请检查网络连接')
      return false
    } finally {
      loading.value = false
    }
  }

  async function doRegister(userData) {
    loading.value = true
    try {
      const response = await register(userData)
      if (response.data.code === 200) {
        ElMessage.success('注册成功，请登录')
        router.push({ name: 'Login' })
        return true
      } else {
        ElMessage.error(response.data.message || '注册失败')
        return false
      }
    } catch (error) {
      ElMessage.error('注册失败，请检查网络连接')
      return false
    } finally {
      loading.value = false
    }
  }

  async function doLogout() {
    try {
      await apiLogout()
    } catch (error) {
      // 忽略退出错误
    } finally {
      token.value = ''
      user.value = null
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      ElMessage.success('已退出登录')
      router.push({ name: 'Home' })
    }
  }

  function updateUser(userData) {
    user.value = { ...user.value, ...userData }
    localStorage.setItem('user', JSON.stringify(user.value))
  }

  return {
    token,
    user,
    loading,
    isLoggedIn,
    isAdmin,
    username,
    doLogin,
    doRegister,
    doLogout,
    updateUser
  }
})
