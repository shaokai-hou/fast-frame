import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus,{ ElDialog } from 'element-plus'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import 'element-plus/dist/index.css'

// 本地 Inter 字体（替代 Google Fonts）
import '@fontsource/inter/300.css'
import '@fontsource/inter/400.css'
import '@fontsource/inter/500.css'
import '@fontsource/inter/600.css'
import '@fontsource/inter/700.css'

import App from './App.vue'
import router from './router'
import '@/styles/index.scss'
import './permission'
import { setupDirectives } from './directives'
import Pagination from './components/Pagination/index.vue'
import TreeSelect from './components/TreeSelect/index.vue'
import PageContainer from './components/PageContainer/index.vue'
import SearchBar from './components/SearchBar/index.vue'

// 全局配置 Dialog - 点击遮罩层不关闭
ElDialog.props.closeOnClickModal.default = false

const app = createApp(App)

// 注册自定义指令
setupDirectives(app)

// 注册全局组件
app.component('Pagination', Pagination)
app.component('TreeSelect', TreeSelect)
app.component('PageContainer', PageContainer)
app.component('SearchBar', SearchBar)

// 注册Element Plus图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(createPinia())
app.use(router)
app.use(ElementPlus, { locale: zhCn })

app.mount('#app')
