<template>
  <div style="display:flex;justify-content:center;align-items:center;min-height:calc(100vh - 120px)">
    <div class="card" style="padding:40px;width:400px">
      <h2 style="font-size:22px;font-weight:800;margin:0 0 8px;color:#004D3E;text-align:center">
        管理員登入
      </h2>
      <p style="text-align:center;color:#6b7280;font-size:14px;margin:0 0 28px">請輸入帳號密碼以管理投票項目</p>

      <div style="margin-bottom:18px">
        <label style="display:block;font-size:13px;font-weight:500;color:#374151;margin-bottom:6px">帳號</label>
        <input v-model="form.username" class="input-field" style="width:100%" placeholder="請輸入帳號" @keyup.enter="handleLogin" />
      </div>
      <div style="margin-bottom:24px">
        <label style="display:block;font-size:13px;font-weight:500;color:#374151;margin-bottom:6px">密碼</label>
        <input v-model="form.password" class="input-field" style="width:100%" type="password" placeholder="請輸入密碼" @keyup.enter="handleLogin" />
      </div>

      <div v-if="error" style="font-size:13px;color:#dc2626;margin-bottom:16px;text-align:center">{{ error }}</div>

      <button class="btn-primary" style="width:100%;padding:12px;font-size:16px" :disabled="loading" @click="handleLogin">
        <div v-if="loading" style="display:flex;align-items:center;justify-content:center;gap:8px">
          <div class="loading-spinner" style="width:18px;height:18px;border-width:2px"></div>
          登入中...
        </div>
        <span v-else>登入</span>
      </button>

      <div style="margin-top:16px;text-align:center;font-size:13px;color:#9ca3af">
        預設帳號：admin / admin123
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { login as loginApi, setAuthToken } from '../api/index.js'
import { login as authLogin } from '../store/auth.js'

const router = useRouter()
const route = useRoute()
const form = reactive({ username: '', password: '' })
const loading = ref(false)
const error = ref('')

async function handleLogin() {
  if (!form.username.trim() || !form.password.trim()) {
    error.value = '請輸入帳號和密碼'
    return
  }
  loading.value = true
  error.value = ''
  try {
    const res = await loginApi(form.username.trim(), form.password.trim())
    authLogin(res.token, res.username, res.role)
    setAuthToken(res.token)
    const redirect = route.query.redirect || '/admin'
    router.push(redirect)
  } catch (e) {
    error.value = e.message
  } finally {
    loading.value = false
  }
}
</script>
