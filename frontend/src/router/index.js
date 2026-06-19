
import { createRouter, createWebHistory } from 'vue-router'


var routes = [
  {
    path: '/',
    redirect: '/vote'
  },

  {
    path: '/admin',
    name: 'Admin',
    component: function() {
      return import('../views/AdminView.vue')
    }
  },

  {
    path: '/vote',
    name: 'Vote',
    component: function() {
      return import('../views/VoteView.vue')
    }
  }
]

// 建立路由實例
// createWebHistory 表示網址不會有 # 號，例如 /vote 而不是 /#/vote
var router = createRouter({
  history: createWebHistory(),
  routes: routes
})

// 把 router 匯出去，讓 main.js 可以使用
export default router
