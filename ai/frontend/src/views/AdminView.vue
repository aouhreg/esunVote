<template>
  <div>
    <div class="card" style="padding:24px">
      <div style="display:flex;justify-content:space-between;align-items:center;flex-wrap:wrap;gap:12px;margin-bottom:20px">
        <h2 style="font-size:18px;font-weight:700;margin:0;color:#004D3E">投票項目管理</h2>
        <div style="display:flex;gap:8px">
          <input v-model="searchKeyword" class="input-field" placeholder="搜尋項目..." style="width:180px" />
          <button class="btn-primary" @click="openAddDialog" style="white-space:nowrap">+ 新增</button>
        </div>
      </div>

      <table class="table-custom" v-if="!loading">
        <thead>
          <tr>
            <th>#</th>
            <th>名稱</th>
            <th>票數</th>
            <th style="text-align:right">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(item, i) in filteredItems" :key="item.id">
            <td style="color:#9ca3af">{{ i + 1 }}</td>
            <td style="font-weight:500">{{ item.name }}</td>
            <td><span class="tag tag-primary">{{ item.voteCount }}</span></td>
            <td style="text-align:right">
              <button class="btn-secondary" style="margin-right:6px;padding:6px 14px;font-size:13px" @click="openEditDialog(item)">編輯</button>
              <button class="btn-danger" style="padding:6px 14px;font-size:13px" @click="handleDelete(item.id)">刪除</button>
            </td>
          </tr>
          <tr v-if="filteredItems.length === 0">
            <td colspan="4" style="text-align:center;color:#9ca3af;padding:40px 16px">暫無項目</td>
          </tr>
        </tbody>
      </table>
      <div v-else style="display:flex;justify-content:center;padding:40px">
        <div class="loading-spinner"></div>
      </div>
      <div v-if="searchKeyword.trim()" style="margin-top:12px;font-size:13px;color:#9ca3af">
        顯示 {{ filteredItems.length }} / {{ items.length }} 項
      </div>
    </div>

    <div v-if="dialogVisible" class="modal-overlay" @click.self="dialogVisible = false">
      <div class="modal-content">
        <h3 style="font-size:18px;font-weight:700;margin:0 0 24px;color:#1c1c1c">{{ isEdit ? '編輯項目' : '新增項目' }}</h3>
        <div style="margin-bottom:20px">
          <label style="display:block;font-size:13px;font-weight:500;color:#374151;margin-bottom:6px">名稱</label>
          <input v-model="form.name" class="input-field" style="width:100%" maxlength="100" placeholder="請輸入投票項目名稱" />
          <div v-if="formError" style="font-size:13px;color:#dc2626;margin-top:4px">{{ formError }}</div>
        </div>
        <div style="display:flex;justify-content:flex-end;gap:10px">
          <button class="btn-secondary" @click="dialogVisible = false">取消</button>
          <button class="btn-primary" :disabled="saving" @click="handleSave" style="min-width:80px">
            {{ saving ? '儲存中...' : '儲存' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { voteApi } from '../api/index.js'

const items = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const saving = ref(false)
const editId = ref(null)
const searchKeyword = ref('')
const form = ref({ name: '' })
const formError = ref('')

const filteredItems = computed(() => {
  const kw = searchKeyword.value.trim().toLowerCase()
  let list = kw ? items.value.filter(i => i.name.toLowerCase().includes(kw)) : items.value
  return [...list].sort((a, b) => b.voteCount - a.voteCount)
})

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

function openAddDialog() {
  isEdit.value = false
  editId.value = null
  form.value = { name: '' }
  formError.value = ''
  dialogVisible.value = true
}

function openEditDialog(row) {
  isEdit.value = true
  editId.value = row.id
  form.value = { name: row.name }
  formError.value = ''
  dialogVisible.value = true
}

function validate() {
  const name = form.value.name.trim()
  if (!name) { formError.value = '請輸入名稱'; return false }
  const dup = items.value.find(i => i.name.toLowerCase() === name.toLowerCase() && i.id !== editId.value)
  if (dup) { formError.value = '此名稱已存在'; return false }
  formError.value = ''
  return true
}

async function handleSave() {
  if (!validate()) return
  saving.value = true
  try {
    if (isEdit.value) {
      await voteApi.updateItem(editId.value, form.value.name.trim())
    } else {
      await voteApi.createItem(form.value.name.trim())
    }
    dialogVisible.value = false
    await loadItems()
  } catch (e) {
    formError.value = e.message
  } finally {
    saving.value = false
  }
}

async function handleDelete(id) {
  if (!confirm('確定刪除此項目？相關投票紀錄也將一併刪除。')) return
  try {
    await voteApi.deleteItem(id)
    items.value = items.value.filter(i => i.id !== id)
    await loadItems()
  } catch (e) {
    // ignore
  }
}

onMounted(loadItems)
</script>
