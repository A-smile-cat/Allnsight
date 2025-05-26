import { createRouter, createWebHashHistory } from 'vue-router'


const routes = [
  {
    path: '/',
    name: 'Default',
    redirect: '/home',
    component: () => import('../views/home.vue')
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('../views/login.vue')
  },
  {
    path: '/home',
    name: 'home',
    component: () => import('../views/home.vue')
  },
  {
    path: '/news',
    name: 'notice',
    component: () => import('../components/news')
  },
  {
    path: '/about',
    name: 'about',
    component: () => import('../views/about')
  },
  {
    path: '/product',
    name: 'product',
    component: () => import('../views/product')
  },
  {
    path: '/protect',
    name: 'protect',
    redirect: '/admin/datasetManage',
  },
  {
    path: '/admin',
    name: 'admin',
    component: () => import('../components/admin/AdminIndex.vue'),
    meta: {
      requireAuth: true
    },
    children: [
      {
        path: '/person',
        redirect: '/admin/person/personInfo'
      },
      {
        path: '/admin/person/personInfo',
        name: 'PersonInfo',
        component: () => import('../components/admin/person/personInfo.vue')
      },
      {
        path: '/admin/person/password',
        name: 'PasswordChange',
        component: () => import('../components/admin/person/password.vue')
      },
      {
        path: '/admin/trainingtaskManage',
        name: 'TrainingTask',
        component: () => import('../components/admin/TrainingTasksManage.vue')
      },
      {
        path: '/admin/modelManage',
        name: 'FileManage',
        component: () => import('../components/admin/FileManage.vue')
      },
      {
        path: '/admin/datasetManage',
        name: 'DatasetManage',
        component: () => import('../components/admin/DatasetManage.vue')
      },
      {
        path: '/admin/articleManage',
        name: 'ArticleManage',
        component: () => import('../components/admin/ArticleManage.vue')
      },
      {
        path: '/admin/user',
        redirect: '/admin/user/userManage'
      },
      {
        path: '/admin/user/userManage',
        name: 'UserManage',
        component: () => import('../components/admin/user/userManage.vue')
      },
      {
        path: '/admin/user/roleManage',
        name: 'RoleManage',
        component: () => import('../components/admin/user/roleManage.vue')
      },
      {
        path: '/admin/picProcess',
        redirect: '/admin/picProcess/classify'
      },
      {
        path: '/admin/picProcess/classify',
        name: 'PicClassify',
        component: () => import('../components/admin/pic/classify.vue')
      },
      {
        path: '/admin/picProcess/segment',
        name: 'PicSegment',
        component: () => import('../components/admin/pic/segment.vue')
      },
      {
        path: '/admin/article',
        name: 'Article',
        component: () => import('../components/admin/ArticleDetails.vue')
      }
    ]
  },
  {
    path: '/admin/editArticle',
    name: 'EditArticle',
    component: () => import('../components/admin/EditorArticle.vue'),
    // meta: {
    //   requireAuth: true
    // },
    props: (route) => ({
      article: route.params.article
    })
  },
  //     {
  //       path: '/admin/password',
  //       name: 'PasswordChange',
  //       component: () => import('../components/admin/person/password.vue')
  //     },
  //     {
  //       path: '/admin/userManage',
  //       name: 'userManage',
  //       component: () => import('../components/admin/user/userManage.vue')
  //     },
  //     {
  //       path: '/admin/articleManage',
  //       name: 'ArticleManage',
  //       component: () => import('../components/admin/articleManage.vue')
  //     },
  //     {
  //       path: '/admin/datasetManage',
  //       name: 'DatasetManage',
  //       component: () => import('../components/admin/datasetManage.vue')
  //     }
  //   ]
  // },
  {
    path: '/dashboard',
    name: 'dataShow',
    beforeEnter: (to, from, next) => {
      window.location.href = 'http://localhost:8081/#/dashboard';
    }
  },
  {
    path: '/ai-tool',
    name: 'AI tools',
    children: [
      {
        path: '/ai-tool/classify',
        name: 'classify',
        redirect: '/admin/picProcess/classify'
      },
      {
        path: '/ai-tool/segmention',
        name: 'segmention',
        redirect: '/admin/picProcess/segment'
      },
      {
        path: '/ai-tool/prediction',
        name: 'prediction',
        component: () => import('../views/prediction.vue')
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'error',
    component: () => import('../components/Error404')
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

// router.beforeEach(to, from, next){
//   next()
// }

export default router
