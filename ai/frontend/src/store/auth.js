import { reactive } from 'vue'
import { getToken, getUser, getRole, setToken, setUser, setRole, removeToken, removeUser, removeRole } from '../utils/auth.js'

const state = reactive({
  token: getToken(),
  user: getUser(),
  role: getRole(),
})

export function login(token, username, role) {
  setToken(token)
  setUser(username)
  setRole(role)
  state.token = token
  state.user = username
  state.role = role
}

export function logout() {
  removeToken()
  removeUser()
  removeRole()
  state.token = null
  state.user = null
  state.role = null
}

export function isLoggedIn() {
  return !!state.token
}

export function isAdmin() {
  return state.role === 'ADMIN'
}

export { state as authState }
