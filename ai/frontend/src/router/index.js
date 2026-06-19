import { createRouter, createWebHistory } from 'vue-router'
import { authState } from '../store/auth.js'

const routes = [
  { path: '/', redirect: '/vote' },
  {
    path: '/vote',
    name: 'Vote',
    component: () => import('../views/VoteView.vue'),
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('../views/AdminView.vue'),
    meta: { requiresAuth: true, requiresAdmin: true },
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/LoginView.vue'),
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to, from, next) => {
  if (to.meta.requiresAuth && !authState.token) {
    next({ path: '/login', query: { redirect: to.fullPath } })
  } else if (to.meta.requiresAdmin && authState.role !== 'ADMIN') {
    next({ path: '/vote' })
  } else {
    next()
  }
})

export default router
