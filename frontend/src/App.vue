
<template>
  <div>
    <!-- el-container 是 Element Plus 的容器 -->
    <el-container>
      
      <!-- 頂部藍色導覽列 -->
      <el-header class="app-header">
        <div class="header-inner">
    
          <el-menu mode="horizontal" :ellipsis="false" router class="nav-menu">
            
            <el-menu-item>
              <span class="app-title">線上投票系統</span>
            </el-menu-item>

            <el-menu-item index="/vote">
              <el-icon><Histogram /></el-icon>
              投票統計
            </el-menu-item>

            <el-menu-item index="/admin">
              <el-icon><Setting /></el-icon>
              後台管理
            </el-menu-item>

          </el-menu>
        </div>
      </el-header>

      <el-main class="app-main">
        <router-view />
      </el-main>
      <el-footer class="app-footer" style="text-align: center; font-size: 14px; color: #888;">
        <span>Copyright © 2026 蘭殷杰</span>
      </el-footer>
    </el-container>
  </div>
</template>

<script>
import { el } from 'element-plus/es/locale/index.mjs';

export default {
  name: 'App'
}
</script>

<style>

.app-title { 
  font-size: 20px; 
  font-weight: 700; 
  color: #409eff; 
  text-decoration: none;  
}
</style>
