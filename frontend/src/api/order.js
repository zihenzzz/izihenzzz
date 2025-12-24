import request from '@/utils/request'

// 订单相关API
export const createOrder = (data) => {
  return request.post('/orders', data)
}

export const getMyOrders = () => {
  return request.get('/orders')
}

export const getOrderDetail = (id) => {
  return request.get(`/orders/${id}`)
}

export const payOrder = (id) => {
  return request.post(`/orders/${id}/pay`)
}

export const cancelOrder = (id) => {
  return request.post(`/orders/${id}/cancel`)
}

export const refundOrder = (id) => {
  return request.post(`/orders/${id}/refund`)
}

export const getAvailableSeats = (params) => {
  return request.get('/orders/seats', { params })
}
