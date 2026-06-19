<template>
  <div>
    <el-card shadow="always">
      <template #header>
        <div class="card-header">
          <span>
            <el-icon><Histogram /></el-icon> 投票區
          </span>
        </div>
      </template>

      <!-- 投票人姓名輸入 -->
      <div class="voter-input">
        <el-input
          v-model="voterName"
          maxlength="50"
          placeholder="請輸入您的姓名"
          style="width:300px"
        />
      </div>

      <!-- 
         投票項目表格
         data="filteredItems"：要顯示的資料（已經排序過）
         stripe：交替行顏色
         v-loading="loading"：載入中顯示轉圈
         :key="tableKey"：改變 key 可以強制重新建立表格
       -->
      <el-table 
         :data="filteredItems" 
         stripe 
         style="width:100%" 
         v-loading="loading" 
         :key="tableKey"
       >
        
        <el-table-column label="選擇" width="60">
          <template #default="{ row }">
            <!-- 
              :checked：是否勾選（看 row.id 有沒有在 selectedIds 裡面）
              @change：點選時切換勾選狀態
            -->
            <el-checkbox
              :checked="selectedIds.has(row.id)"
              @change="toggleItem(row.id)"
            />
          </template>
        </el-table-column>

        <el-table-column type="index" label="編號" width="80" :index="function(i) { return i + 1 }" />
        <el-table-column prop="name" label="投票項目" min-width="200" />
        <el-table-column prop="totalVotes" label="目前票數" width="120"  >
        </el-table-column>

      </el-table>

      <div class="vote-action">
        <el-button
          type="primary"
          size="large"
          v-bind:disabled="selectedIds.size === 0"
          v-bind:loading="submitting"
          v-on:click="submitVote"
        >
          提交投票（已選 {{ selectedIds.size }} 項）
        </el-button>
      </div>

    </el-card>
  </div>
</template>

<script>

import { ElMessage } from 'element-plus'
import { voteApi } from '../api/index.js'

export default {
  name: 'VoteView',

  data: function() {
    return {
      items: [],// 從後端抓回來的投票項目列表
      loading: false,// 表格是否在載入中
      submitting: false,// 是否正在提交投票中
      selectedIds: new Set(),// 存放使用者勾選的項目 ID
      voterName: '',// 投票人姓名
      tableKey: 0
    }
  },

  computed: {
    filteredItems: function() {
      var result = []
      for (var i = 0; i < this.items.length; i++) {
        result.push(this.items[i])
      }
      
      result.sort(function(a, b) {
        return b.totalVotes - a.totalVotes
      })
    
      return result
    }
  },

  methods: {
    
    // 從後端載入投票項目
    loadItems: function() {
      var self = this  
      self.loading = true  
      
      voteApi.getItems().then(function(data) {
        
        self.items = data
        self.tableKey = self.tableKey + 1
        
      }).catch(function(error) {
        ElMessage.error('載入失敗：' + error.message)
        
      }).finally(function() {
        self.loading = false
      })
    },

    // 切換某個項目的勾選狀態
    // 如果已經勾選就取消，沒勾選就選上
    toggleItem: function(id) {
      var newSet = new Set(this.selectedIds)
      
      if (newSet.has(id)) {
        newSet.delete(id)
      } else {
        newSet.add(id)
      }
      
      this.selectedIds = newSet
    },

    // 提交投票
    submitVote: function() {
      var self = this

      if (self.voterName.trim() === '') {
        ElMessage.warning('請輸入您的姓名')
        return
      }

      if (self.selectedIds.size === 0) {
        ElMessage.warning('請至少選擇一個投票項目')
        return
      }

      self.submitting = true

      var idArray = Array.from(self.selectedIds)

      voteApi.castVotes(idArray, self.voterName.trim()).then(function() {
        self.selectedIds = new Set()  // 清空勾選
        ElMessage.success('投票成功！')
        self.loadItems()              // 重新載入票數

      }).catch(function(error) {
        ElMessage.error('投票失敗：' + error.message)

      }).finally(function() {
        self.submitting = false
      })
    }

  },

  mounted: function() {
    this.loadItems()
  }
}
</script>

<style scoped>

.card-header { 
  display: flex; 
  justify-content: space-between; 
  align-items: center; 
  flex-wrap: wrap; 
  gap: 12px; 
}

.card-header span { 
  font-size: 16px; 
  font-weight: 600; 
}

.voter-input {
  margin-bottom: 16px;
}

.vote-action { 
  margin-top: 16px; 
  text-align: center; 
}
</style>
