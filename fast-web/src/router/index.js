import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/layout/index.vue'

/**
 * 动态路由生成
 * 根据后端路由生成前端路由
 */

// 预加载所有视图组件（Vite 要求静态分析）
const modules = import.meta.glob('/src/views/**/*.vue')

// 预加载 ParentView 组件
const ParentView = () => import('@/layout/ParentView.vue')

export function generateRoutes(routes) {
  return routes
    .filter(route => route.path !== '/home')
    .map(route => transformRoute(route, ''))
}

/**
 * 转换单个路由
 * @param route 路由对象
 * @param parentPath 父路由路径（不含开头的 /）
 */
function transformRoute(route, parentPath) {
  // 计算相对路径
  let relativePath = route.path || ''

  // 去掉开头的 /
  if (relativePath.startsWith('/')) {
    relativePath = relativePath.substring(1)
  }

  // 如果是子路由，提取相对部分
  // 例如: parentPath = 'system', route.path = 'system/user' -> relativePath = 'user'
  if (parentPath && relativePath.startsWith(parentPath + '/')) {
    relativePath = relativePath.substring(parentPath.length + 1)
  }

  const item = {
    path: relativePath,
    name: route.name,
    meta: {
      title: route.meta?.title || route.menuName,
      icon: route.meta?.icon || route.icon,
      // visible: '1' 表示隐藏，映射到 hidden: true
      hidden: route.meta?.hidden || route.visible === '1'
    }
  }

  // 如果有组件路径，加载组件
  if (route.component) {
    item.component = loadComponent(route.component)
  } else if (route.children?.length) {
    // 目录类型（没有组件但有子路由），使用 ParentView 作为容器
    item.component = ParentView
  }

  // 递归处理子路由
  if (route.children?.length) {
    // parentPath 传递当前路由的完整路径（不含开头的 /）
    const currentPath = relativePath
    item.children = route.children.map(child => transformRoute(child, currentPath))
  }

  return item
}

/**
 * 动态加载组件
 */
function loadComponent(component) {
  const path = `/src/views/${component}.vue`
  if (modules[path]) {
    return modules[path]
  }
  console.error(`组件不存在: ${path}`)
  return () => import('@/views/404.vue')
}

/**
 * 静态路由（不需要权限验证）
 */
export const constantRoutes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    name: 'Layout',
    component: Layout,
    redirect: '/home',
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('@/views/home.vue'),
        meta: { title: '首页', icon: 'HomeFilled' }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/profile/index.vue'),
        meta: { title: '个人中心', hidden: true }
      },
      {
        path: 'system/dict/data',
        name: 'DictData',
        component: () => import('@/views/system/dict/data.vue'),
        meta: { title: '字典数据', hidden: true }
      }
    ]
  },
  {
    path: '/404',
    name: '404',
    component: () => import('@/views/404.vue'),
    meta: { title: '404' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes: constantRoutes
})

export default router