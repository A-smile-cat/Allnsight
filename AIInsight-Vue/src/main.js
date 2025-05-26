import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import ElementPlus, { ElMessage } from 'element-plus'
import 'element-plus/dist/index.css'
import store from './store'
import api from '@/api/login.js'

const app = createApp(App)
app.use(router)
app.use(ElementPlus)
app.use(store)  // 确保store也被use
app.mount('#app')

router.beforeEach((to, from, next) => {
  if (store.state.user && to.path.startsWith('/admin')) {
    initAdminMenu(router, store) //动态加载路由菜单
  }
  if (store.state.user && to.path.startsWith('/login')) {
     api.authApi({id:store.state.user.id}).then(resp => {
      if (resp.code === 200) {
          // next('/admin/person/personInfo')
          router.push({ path: '/admin/person/personInfo' })
      } else {
          // 认证失败时直接跳转首页避免循环
          next('/')
      }
    })
    // next({
    //   path: '/admin/person/personInfo'
    // })
  }
  
  // 如果前端没有登录信息则直接拦截，如果有则判断后端是否正常登录（防止构造参数绕过）
  if (to.meta.requireAuth) {
    console.log(store.state.user)
    if (store.state.user) {
      api.authApi({id:store.state.user.id}).then(resp => {
        if (resp.code === 200) {
          next()
        }else{
          store.dispatch('logout').then(() => {
            // next({ path: '/login' })
            router.push({ path: '/login' })
          })
          // store.commit('logout')
          // // ElMessage.error('登录信息已过期，请重新登录')
          // next({
          //   path: '/login',
          //   // query: {redirect: to.fullPath}
          // })
        }
      }) 
    } else {
      next({
        path: '/login',
        // query: {redirect: to.fullPath}
      })
    }
  } else {
    next()
  }
}
)

const initAdminMenu = (router, store) => {
  // 防止重复触发加载菜单操作
  if (store.state.adminMenus.length > 0) {
    return
  }
  
  api.getMenu({id : store.state.user.id}).then(resp => {
    if (resp && resp.code === 200) {
      var fmtRoutes = formatRoutes(resp.result)
      fmtRoutes.forEach(route => {
        router.addRoute(route)
      })
      // router.addRoutes(fmtRoutes)
      store.commit('initAdminMenu', fmtRoutes)
    }else{
      // ElMessage.error("菜单加载失败")
    }
  })
}

const formatRoutes = (routes) => {
  let fmtRoutes = []
  routes.forEach(route => {
    if (route.children) {
      route.children = formatRoutes(route.children)
    }

    let fmtRoute = {
      path: route.path,
      // path: route.path.startsWith('/admin') 
      //   ? route.path.replace('/admin', '') // 去掉前缀，确保嵌套
      //   : route.path,
      // path: route.path.replace('/admin', '') || '/',
      component: () => import('./components/admin/' + route.component + '.vue'),
      name: route.name,
      parent_id: route.parent_id,
      name_des: route.name_des,
      iconCls: route.icon_cls,
      meta: {
        requireAuth: true
      },
      children: route.children
    }
    fmtRoutes.push(fmtRoute)
  })
  return fmtRoutes
}