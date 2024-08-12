<script setup>
import {
  Management,
  User,
  SwitchButton,
  Stamp,
  Wallet,
  CaretBottom,
  Upload,
  List,
  Folder,
  Connection,
  Plus,
  Link
} from '@element-plus/icons-vue'
import avatarDefault from '@/assets/default.png'
import { useUserStore } from '@/stores/user'
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { userLogoutService } from '@/api/user'

const userStore = useUserStore()
const router = useRouter()

onMounted(() => {
  userStore.getUser()
  // console.log(userStore.user)
})

const handleCommand = async (key) => {
  if (key === 'logout') {
    // 退出操作
    await ElMessageBox.confirm('你确认要进行退出么', '温馨提示', {
      type: 'warning',
      confirmButtonText: '确认',
      cancelButtonText: '取消'
    })

    await userLogoutService()
    // 清除本地的user信息)
    userStore.setUser({})
    router.push('/login')
  } else {
    // 跳转操作
    router.push(`/user/${key}`)
  }
}
</script>

<template>
  <!-- 
    el-menu 整个菜单组件
      :default-active="$route.path"  配置默认高亮的菜单项
      router  router选项开启，el-menu-item 的 index 就是点击跳转的路径

    el-menu-item 菜单项
      index="/article/channel" 配置的是访问的跳转路径，配合default-active的值，实现高亮
  -->
  <el-container class="layout-container">
    <el-aside width="200px">
      <a href="/home">
        <div class="el-aside__logo"></div>
      </a>
      <el-menu
        active-text-color="#ffd04b"
        background-color="#232323"
        :default-active="$route.path"
        text-color="#fff"
        router
      >
        <el-sub-menu index="/manage/file/folder">
          <!-- 多级菜单的标题 - 具名插槽 title -->
          <template #title>
            <el-icon><Management /></el-icon>
            <span>文件管理</span>
          </template>

          <!-- 展开的内容 - 默认插槽 -->
          <el-menu-item index="/manage/file/folder">
            <el-icon><Folder /></el-icon>
            <span>目录</span>
          </el-menu-item>
          <el-menu-item index="/manage/file">
            <el-icon><List /></el-icon>
            <span>文件列表</span>
          </el-menu-item>
          <el-menu-item index="/manage/file/myUpload">
            <el-icon><Upload /></el-icon>
            <span>我的上传记录</span>
          </el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="/manage/team">
          <!-- 多级菜单的标题 - 具名插槽 title -->
          <template #title>
            <el-icon><Connection /></el-icon>
            <span>群组管理</span>
          </template>
          <el-menu-item index="/manage/team">
            <el-icon><Plus /></el-icon>
            <span>加入群组</span>
          </el-menu-item>
          <el-menu-item index="/manage/myTeam">
            <el-icon><Link /></el-icon>
            <span>我的群组</span>
          </el-menu-item>
        </el-sub-menu>

        <el-menu-item index="/manage/stamp">
          <el-icon><Stamp /></el-icon>
          <span>邮票管理</span>
        </el-menu-item>
        <el-menu-item index="/manage/account">
          <el-icon><Wallet /></el-icon>
          <span>账户管理</span>
        </el-menu-item>
        <el-menu-item index="/user/center">
          <el-icon><User /></el-icon>
          <span>个人中心</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header>
        <div>
          Hi,
          <strong>{{
            userStore.user.nickname || userStore.user.username
          }}</strong>
        </div>
        <el-dropdown placement="bottom-end" @command="handleCommand">
          <!-- 展示给用户，默认看到的 -->
          <span class="el-dropdown__box">
            <el-avatar :src="userStore.user.avatar || avatarDefault" />
            <el-icon><CaretBottom /></el-icon>
          </span>

          <!-- 折叠的下拉部分 -->e
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="center" :icon="User"
                >个人中心</el-dropdown-item
              >
              <el-dropdown-item command="logout" :icon="SwitchButton"
                >退出登录</el-dropdown-item
              >
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>
      <el-main>
        <router-view></router-view>
      </el-main>
      <el-footer>去中心化文件共享平台 ©2024 Created by 黄渲雯</el-footer>
    </el-container>
  </el-container>
</template>

<style scoped>
.layout-container {
  height: 100vh;
}
.layout-container .el-aside {
  background-color: #232323;
}
.layout-container .el-aside__logo {
  height: 120px;
  background: url('@/assets/SWARM.png') no-repeat center / 180px auto;
}
.layout-container .el-aside .el-menu {
  border-right: none;
}
.layout-container .el-header {
  background-color: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.layout-container .el-header .el-dropdown__box {
  display: flex;
  align-items: center;
}
.layout-container .el-header .el-dropdown__box .el-icon {
  color: #999;
  margin-left: 10px;
}
.layout-container .el-header .el-dropdown__box:active,
.layout-container .el-header .el-dropdown__box:focus {
  outline: none;
}
.layout-container .el-footer {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  color: #666;
}
</style>
