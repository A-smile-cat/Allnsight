import { createStore } from 'vuex'
import api from '@/api/login.js'

export default createStore({
  state: {
    user: window.localStorage.getItem('user') == null ? '' : JSON.parse(window.localStorage.getItem('user') || '[]'),
    adminMenus: []
  },
  mutations: {
    initAdminMenu(state, menus) {
      state.adminMenus = menus
    },
    login(state, data) {
      if (data == null) {
      }
      state.user = data
      window.localStorage.setItem('user', JSON.stringify(data))
    },
    logout(state) {   
      //向后端发送退出登录请求
      api.logout(state.user).then(resp => {
        if (resp.code === 200 || resp.code === 400) {
          state.user = null
          window.localStorage.removeItem('user')
          state.adminMenus = []
          // ElMessage.success('退出登录成功')
        }else{
          // ElMessage.error('退出登录失败')
        }
      })
    },
    addSubMenusWithParentIdOne(state) {
      let newMenus = []
      state.adminMenus.forEach(item => {
        if (item.children && item.children.length > 0) {
          item.children.forEach(child => {
            if (child.parent_id === 1) {
              newMenus.push(child)
            }
          })
        }
      })
      state.adminMenus = [...state.adminMenus, ...newMenus]
    }

  },
  getters: {
    // 计算有子菜单的项
    filteredMenus: (state) => {
      return state.adminMenus.filter(item => item.children && item.children.length > 0 && item.children[0].parent_id !== 1 )
    },
    // 计算无子菜单的项
    singleMenus: (state) => {
      // return state.adminMenus.filter(item => !item.children || item.children.length === 0)
      // let singleItems = state.adminMenus.filter(item => !item.children || item.children.length === 0);
      let subMenusWithParentIdOne = [];
      state.adminMenus.forEach(item => {
        if (item.children && item.children.length > 0) {
          item.children.forEach(child => {
            if (child.parent_id === 1) {
              subMenusWithParentIdOne.push(child);
            }
          });
        }
      });
      // return [...singleItems, ...subMenusWithParentIdOne];
      return [...subMenusWithParentIdOne];
    }
  },
  actions: {
    logout({commit}){
      return new Promise((resolve) => {
        commit('logout')
        resolve()
      })  
    }
  }
})
