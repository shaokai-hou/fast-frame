import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/layout/index.vue'

/**
 * 动态路由生成
 * 根据后端路由生成前端路由
 */

// 预加载所有视图组件（Vite 要求静态分析）
const modules = import.meta.glob('/src/views/**/*.vue')

// 预加载 ParentView 组件
const ParentView = () => import('@/components/ParentView/index.vue')

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
    // 使用路由名称作为 keep-alive 的缓存标识
    name: route.name || route.menuName || route.component?.replace(/\//g, '-') || undefined,
    meta: {
      title: route.meta?.title || route.menuName,
      icon: route.meta?.icon || route.icon,
      // visible: '1' 表示隐藏，映射到 hidden: true
      hidden: route.meta?.hidden || route.visible === '1',
      // noCache: true 表示不缓存页面
      noCache: route.meta?.noCache === true || route.meta?.noCache === 'true',
      // affix: true 表示固定在 TagsView
      affix: route.meta?.affix === true || route.meta?.affix === 'true',
      // link: iframe 页面外链地址
      link: route.meta?.link || route.link
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
  // 组件不存在，返回 404 页面
  return () => import('@/views/404.vue')
}

/**
 * 静态路由（不需要权限验证）
 */
export const constantRoutes = [
  {
    path: '/redirect',
    component: Layout,
    hidden: true,
    children: [
      {
        path: '/redirect/:path(.*)',
        component: () => import('@/views/redirect.vue'),
        meta: { title: 'Redirect' }
      }
    ]
  },
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

/**
 * 创建路由实例
 */
const createRouterInstance = () => createRouter({
  history: createWebHistory(),
  routes: constantRoutes
})

const router = createRouterInstance()

/**
 * 重置路由（清除动态路由）
 * 通过替换 matcher 来清除动态添加的路由
 */
export function resetRouter() {
  const newRouter = createRouterInstance()
  router.matcher = newRouter.matcher
}

export default router