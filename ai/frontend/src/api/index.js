import axios from 'axios'
import { getToken } from '../utils/auth.js'
import { logout } from '../store/auth.js'
import router from '../router/index.js'

const http = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' },
})

http.interceptors.request.use((config) => {
  const token = getToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

http.interceptors.response.use(
  (response) => {
    const body = response.data
    if (body.success === false) {
      return Promise.reject(new Error(body.message || '請求失敗'))
    }
    return body.data !== undefined ? body.data : body
  },
  (error) => {
    if (error.response?.status === 401) {
      logout()
      router.push('/login')
      return Promise.reject(new Error('未授權'))
    }
    const msg = error.response?.data?.message || error.message || '網路錯誤'
    return Promise.reject(new Error(msg))
  }
)

export default http

export const voteApi = {
  getItems() {
    return http.get('/items')
  },
  getItem(id) {
    return http.get(`/items/${id}`)
  },
  createItem(name) {
    return http.post('/items', { name })
  },
  updateItem(id, name) {
    return http.put(`/items/${id}`, { name })
  },
  deleteItem(id) {
    return http.delete(`/items/${id}`)
  },
  castVotes(voter, itemIds) {
    return http.post('/votes', { voter, itemIds })
  },
  checkVoted(username) {
    return http.get('/votes/check', { params: { username } })
  },
}

export const authApi = {
  login(username, password) {
    return http.post('/auth/login', { username, password })
  },
  register(username, password) {
    return http.post('/auth/register', { username, password })
  },
}

export async function login(username, password) {
  const res = await authApi.login(username, password)
  return res
}

export function setAuthToken(token) {
  http.defaults.headers.common['Authorization'] = `Bearer ${token}`
}
