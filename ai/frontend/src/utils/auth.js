const TOKEN_KEY = 'vote_admin_token'
const USER_KEY = 'vote_admin_user'
const ROLE_KEY = 'vote_admin_role'

export function getToken() { return localStorage.getItem(TOKEN_KEY) }
export function setToken(token) { localStorage.setItem(TOKEN_KEY, token) }
export function removeToken() { localStorage.removeItem(TOKEN_KEY) }

export function getUser() { return localStorage.getItem(USER_KEY) }
export function setUser(username) { localStorage.setItem(USER_KEY, username) }
export function removeUser() { localStorage.removeItem(USER_KEY) }

export function getRole() { return localStorage.getItem(ROLE_KEY) }
export function setRole(role) { localStorage.setItem(ROLE_KEY, role) }
export function removeRole() { localStorage.removeItem(ROLE_KEY) }

export function isAuthenticated() { return !!getToken() }
export function isAdmin() { return getRole() === 'ADMIN' }

export function logout() {
  removeToken()
  removeUser()
  removeRole()
}
