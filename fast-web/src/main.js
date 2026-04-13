import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus,{ ElDialog } from 'element-plus'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import 'element-plus/dist/index.css'

import App from './App.vue'
import router from './router'
import '@/styles/index.scss'
import './permission'
import { setupDirectives } from './directives'
import Pagination from './components/Pagination/index.vue'
import TreeSelect from './components/TreeSelect/index.vue'

// 全局配置 Dialog - 点击遮罩层不关闭
ElDialog.props.closeOnClickModal.default = false

const app = createApp(App)

// 注册自定义指令
setupDirectives(app)

// 注册全局组件
app.component('Pagination', Pagination)
app.component('TreeSelect', TreeSelect)

// 注册Element Plus图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(createPinia())
app.use(router)
app.use(ElementPlus, { locale: zhCn })

app.mount('#app')
