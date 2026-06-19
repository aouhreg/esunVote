<template>
  <div>
    <el-card shadow="always">
      
      <!-- 標題列 + 新增按鈕 -->
      <template #header>
        <div class="card-header">
          <span>
            <el-icon><Setting /></el-icon> 投票項目管理
          </span>
          <!-- 新增按鈕，點選會跳出對話框 -->
          <el-button type="primary" size="small" v-on:click="openAddDialog">
            <el-icon><Plus /></el-icon> 新增項目
          </el-button>
        </div>
      </template>

      <!-- 投票項目表格 -->
      <el-table 
        :data="filteredItems" 
        stripe 
        style="width:100%" 
        v-loading="loading" 
        :key="tableKey"
      >
        <!-- 自動編號 -->
        <el-table-column type="index" label="編號" width="80" :index="function(i) { return i + 1 }" />
        
        <!-- 名稱 -->
        <el-table-column prop="name" label="名稱" min-width="200" />
        
        <!-- 目前票數 -->
        <el-table-column prop="totalVotes" label="目前票數" width="120" />
        
        <!-- 操作按鈕區 -->
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" type="primary" v-on:click="openEditDialog(row)">
              編輯
            </el-button>
            <el-button size="small" type="danger" v-on:click="handleDelete(row.id)">
              刪除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 底部：顯示總共有幾個項目 -->
      <div class="table-footer">
        共 {{ filteredItems.length }} 項
      </div>

    </el-card>

    <!-- 
      新增 / 編輯的對話框
      v-model="dialogVisible"：控制對話框開關
      :title：對話框的標題，會根據是新增還是編輯顯示不同文字
    -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="dialogTitle" 
      width="400px"
    >
      <!-- 表單 -->
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        
        <!-- 名稱輸入欄位 -->
        <el-form-item label="名稱" prop="name">
          <el-input 
            v-model="form.name" 
            maxlength="100" 
            placeholder="請輸入投票項目名稱"
          />
        </el-form-item>

      </el-form>

      <!-- 對話框底部按鈕 -->
      <template #footer>
        <!-- 取消按鈕 -->
        <el-button v-on:click="dialogVisible = false">取消</el-button>
        <!-- 儲存按鈕 -->
        <el-button 
          type="primary" 
          v-bind:loading="saving" 
          v-on:click="handleSave"
        >
          {{ saving ? '儲存中...' : '儲存' }}
        </el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script>
// Options API 寫法：所有功能都寫在 export default 裡面

import { ElMessage, ElMessageBox } from 'element-plus'
import { voteApi } from '../api/index.js'

export default {
  name: 'AdminView',

  data: function() {
    return {
      items: [],// 從後端抓回來的投票項目列表
      loading: false, // 表格載入中
      dialogVisible: false, // 對話框開關
      isEdit: false, // 是否在編輯模式（true 是編輯，false 是新增）
      saving: false,// 是否正在儲存中
      editId: null,// 正在編輯的項目 ID（只有編輯模式才有值）
      tableKey: 0,// 用來強制重新建立表格
      form: {
        name: ''
      }// 表單驗證規則
    }
  },

  computed: {
    //排序後的項目列表（依照票數從高到低）
    filteredItems: function() {
      // 複製一份
      var result = []
      for (var i = 0; i < this.items.length; i++) {
        result.push(this.items[i])
      }
      // 排序
      result.sort(function(a, b) {
        return b.totalVotes - a.totalVotes
      })
      
      return result
    },
    dialogTitle: function() {
      if (this.isEdit) {
        return '編輯投票項目'
      } else {
        return '新增投票項目'
      }
    }
  },

  methods: {
    
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

    // 開啟新增對話框
    openAddDialog: function() {
      this.isEdit = false     // 設定為新增模式
      this.editId = null      // 沒有在編輯任何項目
      this.form = {           // 清空表單
        name: ''
      }
      this.dialogVisible = true  // 顯示對話框
    },

    // 開啟編輯對話框
    openEditDialog: function(row) {
      this.isEdit = true       // 設定為編輯模式
      this.editId = row.id     // 記錄要編輯的那一筆資料的 id
      this.form = {            // 把該筆資料的名稱帶入表單
        name: row.name
      }
      this.dialogVisible = true  // 顯示對話框
    },

    // 儲存（新增或編輯）
    handleSave: function() {
      var self = this
      
      // 檢查表單有沒有通過驗證
      // this.$refs.formRef 是 el-form 的參考
      this.$refs.formRef.validate(function(valid) {
        
        // 如果驗證沒過，直接結束
        if (!valid) {
          return
        }
        
        // 設定為儲存中
        self.saving = true
        
        if (self.isEdit) {
          // 編輯模式：呼叫修改 API
          voteApi.updateItem(self.editId, self.form.name).then(function() {
            ElMessage.success('更新成功')
            self.dialogVisible = false  // 關閉對話框
            self.loadItems()             // 重新載入列表
            
          }).catch(function(error) {
            ElMessage.error(error.message)
            
          }).finally(function() {
            self.saving = false
          })
          
        } else {
          // 新增模式：呼叫新增 API
          voteApi.createItem(self.form.name).then(function() {
            ElMessage.success('新增成功')
            self.dialogVisible = false  // 關閉對話框
            self.loadItems()             // 重新載入列表
            
          }).catch(function(error) {
            ElMessage.error(error.message)
            
          }).finally(function() {
            self.saving = false
          })
        }
        
      })
    },

    // 刪除項目
    handleDelete: function(id) {
      var self = this
      
      // 跳出確認對話框，問使用者是否真的要刪除
      ElMessageBox.confirm(
        '確定要刪除此投票項目？',
        '確認刪除',
        {
          confirmButtonText: '確定刪除',
          cancelButtonText: '取消',
          type: 'warning',
        }
      ).then(function() {
        
        voteApi.deleteItem(id).then(function() {
          self.tableKey = self.tableKey + 1
          ElMessage.success('刪除成功')
          self.loadItems()
          
        }).catch(function(error) {
          ElMessage.error(error.message)
        })
        
      }).catch(function() {
        ElMessage.info('已取消刪除')
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

.table-footer {
  margin-top: 12px;
  font-size: 13px;
  color: #999;
}
</style>
