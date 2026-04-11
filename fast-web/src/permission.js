import router, {generateRoutes} from '@/router'
import {getToken} from '@/utils/auth'
import {useUserStore} from '@/store/user'
import {usePermissionStore} from '@/store/permission'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import getPageTitle from '@/utils/get-page-title'

NProgress.configure({showSpinner: false})

// 白名单路由（不需要登录）
const whiteList = ['/login', '/404']

router.beforeEach(async (to, from, next) => {
    NProgress.start()

    // 设置页面标题
    document.title = getPageTitle(to.meta.title)

    const token = getToken()

    if (token) {
        // 已登录
        if (to.path === '/login') {
            // 已登录访问登录页，跳转到首页
            next({path: '/home'})
            NProgress.done()
        } else {
            const permissionStore = usePermissionStore()
            if (permissionStore.routesLoaded) {
                next()
            } else {
                // 加载用户信息和动态路由
                const userStore = useUserStore()
                try {
                    await userStore.getUserInfoAction()
                    const routes = await userStore.getRoutesAction()
                    const dynamicRoutes = generateRoutes(routes)

                    // 动态路由添加到 Layout
                    dynamicRoutes.forEach(route => router.addRoute('Layout', route))

                    // 通配符路由（必须在动态路由之后）
                    router.addRoute({path: '/:pathMatch(.*)*', redirect: '/404'})

                    permissionStore.setRoutes(dynamicRoutes)
                    next({...to, replace: true})
                } catch (error) {
                    // 获取用户信息失败，清除token并跳转到登录页
                    await userStore.logoutAction()
                    next(`/login?redirect=${to.path}`)
                    NProgress.done()
                }
            }
        }
    } else {
        // 未登录
        if (whiteList.includes(to.path)) {
            next()
        } else {
            // 跳转到登录页，并记录重定向地址
            next(`/login?redirect=${to.path}`)
            NProgress.done()
        }
    }
})

router.afterEach(() => {
    NProgress.done()
})
