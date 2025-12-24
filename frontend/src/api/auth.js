import request from '@/utils/request'

// 认证相关API
export const login = (data) => {
  return request.post('/auth/login', data)
}

export const register = (data) => {
  return request.post('/auth/register', data)
}

export const getUserInfo = () => {
  return request.get('/auth/me')
}

export const logout = () => {
  return request.post('/auth/logout')
}
