import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8080/api/v1',
  headers: { 'Content-Type': 'application/json' }
})

export const clientsApi = {
  getAll: (params) => api.get('/clients', { params }),
  getById: (id) => api.get(`/clients/${id}`),
  getStats: () => api.get('/clients/stats'),
  create: (data) => api.post('/clients', data),
  update: (id, data) => api.put(`/clients/${id}`, data),
  delete: (id) => api.delete(`/clients/${id}`),
  addAddress: (clientId, data) => api.post(`/clients/${clientId}/addresses`, data),
  updateAddress: (clientId, addressId, data) => api.put(`/clients/${clientId}/addresses/${addressId}`, data),
  deleteAddress: (clientId, addressId) => api.delete(`/clients/${clientId}/addresses/${addressId}`),
}
