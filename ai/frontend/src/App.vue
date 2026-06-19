<template>
  <div>
    <nav style="position:sticky;top:0;z-index:40;background:#ffffff;padding:0 24px;height:64px;display:flex;align-items:center;border-bottom:1px solid #e5e7eb">
      <div style="max-width:1100px;width:100%;margin:0 auto;display:flex;align-items:center;gap:32px">
        <router-link to="/vote" style="font-size:20px;font-weight:800;color:#004D3E;text-decoration:none">
          ⚡ Vote
        </router-link>
        <div style="display:flex;gap:4px;flex:1">
          <router-link to="/admin" class="nav-link" active-class="nav-link-active">
            <span style="margin-right:6px">⚙</span> 管理
          </router-link>
          <router-link to="/vote" class="nav-link" active-class="nav-link-active">
            <span style="margin-right:6px">📊</span> 投票
          </router-link>
        </div>
        <div v-if="authState.token" style="display:flex;align-items:center;gap:12px">
          <span style="font-size:13px;color:#6b7280">
            <span v-if="authState.role === 'ADMIN'" style="display:inline-block;padding:2px 8px;border-radius:12px;font-size:11px;font-weight:600;background:#e6f5f2;color:#004D3E;margin-right:6px">管理員</span>
            {{ authState.user }}
          </span>
          <button style="background:none;border:1px solid #d1d5db;border-radius:8px;padding:6px 14px;font-size:13px;color:#6b7280;cursor:pointer;transition:all 0.15s" @click="handleLogout">登出</button>
        </div>
        <router-link v-else to="/login" style="background:#004D3E;color:white;border:none;border-radius:8px;padding:6px 18px;font-size:13px;font-weight:600;cursor:pointer;text-decoration:none">登入</router-link>
      </div>
    </nav>
    <main style="max-width:1100px;margin:28px auto;padding:0 16px">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { authState, logout as authLogout } from './store/auth.js'
import { useRouter } from 'vue-router'

const router = useRouter()

function handleLogout() {
  authLogout()
  router.push('/vote')
}
</script>

<style scoped>
.nav-link {
  padding: 8px 18px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #6b7280;
  text-decoration: none;
  transition: all 0.15s ease;
}
.nav-link:hover {
  color: #004D3E;
  background: #f4f8fa;
}
.nav-link-active {
  color: #004D3E;
  background: #e6f5f2;
}
</style>
