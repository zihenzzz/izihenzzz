import request from '@/utils/request'

// 获取统计数据
export function getStats() {
  return request.get('/admin/stats')
}

// 获取用户列表
export function getUsers(params) {
  return request.get('/admin/users', { params })
}

// 获取订单列表
export function getAdminOrders(params) {
  return request.get('/admin/orders', { params })
}

// 强制取消订单
export function forceCancelOrder(id) {
  return request.put(`/admin/orders/${id}/force-cancel`)
}

// 获取车次列表
export function getAdminTrains(params) {
  return request.get('/admin/trains', { params })
}

// 添加车次
export function addTrain(data) {
  return request.post('/trains', data)
}

// 更新车次
export function updateTrain(id, data) {
  return request.put(`/trains/${id}`, data)
}

// 更新车次状态
export function updateTrainStatus(id, status) {
  return request.put(`/admin/trains/${id}/status`, null, { params: { status } })
}

// 删除车次
export function deleteTrain(id) {
  return request.delete(`/admin/trains/${id}`)
}

// 获取车站列表
export function getStations() {
  return request.get('/stations')
}

// 添加车站
export function addStation(data) {
  return request.post('/stations', data)
}

// 更新车站
export function updateStation(id, data) {
  return request.put(`/stations/${id}`, data)
}

// 删除车站
export function deleteStation(id) {
  return request.delete(`/stations/${id}`)
}

// 清理过期订单
export function cleanupExpiredOrders() {
  return request.post('/admin/orders/cleanup-expired')
}

// 获取系统健康状态
export function getHealthStatus() {
  return request.get('/admin/health')
}
