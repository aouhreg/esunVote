<template>
  <div>
    <div class="card" style="padding:24px;margin-bottom:20px">
      <h2 style="font-size:18px;font-weight:700;margin:0 0 20px;color:#004D3E">投票</h2>

      <div v-if="!authState.token" class="card" style="padding:32px;text-align:center;background:#f4f8fa;border-color:#b8e6e0">
        <p style="font-size:15px;color:#6b7280;margin:0 0 16px">請先登入後即可參與投票</p>
        <router-link to="/login" class="btn-primary" style="text-decoration:none;display:inline-block">前往登入</router-link>
      </div>

      <template v-else>
        <div style="display:flex;gap:12px;flex-wrap:wrap;margin-bottom:20px">
          <input v-model="voterName" class="input-field" placeholder="請輸入您的名稱..." style="flex:1;min-width:200px" maxlength="100" />
          <input v-model="searchKeyword" class="input-field" placeholder="搜尋項目..." style="width:180px" />
        </div>

        <div v-if="alert" class="alert-success" style="margin-bottom:16px">
          <span>✓</span>
          <span>{{ alert }}</span>
        </div>

        <table class="table-custom" v-if="!loading">
          <thead>
            <tr>
              <th style="width:40px"></th>
              <th>#</th>
              <th>項目</th>
              <th>票數</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(item, i) in filteredItems" :key="item.id" :class="{ 'row-selected': selectedIds.has(item.id) }" style="cursor:pointer" @click="toggleItem(item.id)">
              <td>
                <input type="checkbox" :checked="selectedIds.has(item.id)" class="checkbox-custom" @click.stop="toggleItem(item.id)" />
              </td>
              <td style="color:#9ca3af">{{ i + 1 }}</td>
              <td style="font-weight:500">{{ item.name }}</td>
              <td><span class="tag tag-primary">{{ item.voteCount }}</span></td>
            </tr>
            <tr v-if="filteredItems.length === 0">
              <td colspan="4" style="text-align:center;color:#9ca3af;padding:40px 16px">暫無項目</td>
            </tr>
          </tbody>
        </table>
        <div v-else style="display:flex;justify-content:center;padding:40px">
          <div class="loading-spinner"></div>
        </div>

        <button class="btn-primary" style="width:100%;margin-top:20px;padding:14px;font-size:16px" :disabled="!canVote || voting" @click="handleVote">
          <div v-if="voting" style="display:flex;align-items:center;justify-content:center;gap:8px">
            <div class="loading-spinner" style="width:18px;height:18px;border-width:2px"></div>
            投票中...
          </div>
          <div v-else-if="!voterName.trim()">請輸入名稱後投票</div>
          <div v-else-if="selectedIds.size === 0">請選擇至少一個項目</div>
          <div v-else>為「{{ voterName.trim() }}」送出 {{ selectedIds.size }} 票</div>
        </button>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { voteApi } from '../api/index.js'
import { authState } from '../store/auth.js'

const items = ref([])
const loading = ref(false)
const voterName = ref('')
const searchKeyword = ref('')
const selectedIds = ref(new Set())
const voting = ref(false)
const alert = ref('')

const filteredItems = computed(() => {
  let kw = searchKeyword.value.trim().toLowerCase()
  let list = kw ? items.value.filter(i => i.name.toLowerCase().includes(kw)) : items.value
  return [...list].sort((a, b) => b.voteCount - a.voteCount)
})

const canVote = computed(() => voterName.value.trim() && selectedIds.value.size > 0)

function toggleItem(id) {
  const s = new Set(selectedIds.value)
  if (s.has(id)) s.delete(id)
  else s.add(id)
  selectedIds.value = s
}

async function loadItems() {
  loading.value = true
  try {
    items.value = await voteApi.getItems()
  } catch (e) {
    // ignore
  } finally {
    loading.value = false
  }
}

async function handleVote() {
  voting.value = true
  alert.value = ''
  try {
    const msg = await voteApi.castVotes(voterName.value.trim(), [...selectedIds.value])
    alert.value = msg
    selectedIds.value = new Set()
    voterName.value = ''
    await loadItems()
  } catch (e) {
    // ignore
  } finally {
    voting.value = false
  }
}

onMounted(loadItems)
</script>

<style scoped>
.row-selected td {
  background: #e6f5f2;
}
</style>
