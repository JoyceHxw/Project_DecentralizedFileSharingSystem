import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/login', component: () => import('@/views/login/LoginPage.vue') },
    {
      path: '/',
      component: () => import('@/views/layout/LayoutContainer.vue'),
      redirect: '/home',
      children: [
        {
          path: '/home',
          component: () => import('@/views/manage/HomePage.vue')
        },
        {
          path: '/manage/file',
          component: () => import('@/views/manage/FileManage.vue')
        },
        {
          path: '/manage/file/folder',
          component: () => import('@/views/manage/FileFolder.vue')
        },
        {
          path: '/manage/file/myUpload',
          component: () => import('@/views/manage/FileMy.vue')
        },
        {
          path: '/manage/team',
          component: () => import('@/views/manage/TeamManage.vue')
        },
        {
          path: '/manage/myTeam',
          component: () => import('@/views/manage/TeamMy.vue')
        },
        {
          path: '/manage/stamp',
          component: () => import('@/views/manage/StampManage.vue')
        },
        {
          path: '/manage/account',
          component: () => import('@/views/manage/AccountManage.vue')
        },
        {
          path: '/user/center',
          component: () => import('@/views/user/UserCenter.vue')
        }
      ]
    }
  ]
})

export default router
