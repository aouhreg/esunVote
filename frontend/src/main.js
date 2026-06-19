
import { createApp } from 'vue'

import ElementPlus from 'element-plus'

import 'element-plus/dist/index.css'

import * as ElementPlusIconsVue from '@element-plus/icons-vue'

import App from './App.vue'

import router from './router'

var app = createApp(App)

for (var key in ElementPlusIconsVue) {
  app.component(key, ElementPlusIconsVue[key])
}

app.use(ElementPlus)

// 安裝路由
app.use(router)

app.mount('#app')
