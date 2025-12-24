import request from '@/utils/request'

// 车次相关API
export const searchTrains = (params) => {
  return request.get('/trains/search', { params })
}

export const getTrainById = (id) => {
  return request.get(`/trains/${id}`)
}

export const getTrainTicketInfo = (id, params) => {
  return request.get(`/trains/${id}/info`, { params })
}

export const addTrain = (data) => {
  return request.post('/trains', data)
}

export const updateTrain = (id, data) => {
  return request.put(`/trains/${id}`, data)
}

export const getAllStations = () => {
  return request.get('/stations')
}
