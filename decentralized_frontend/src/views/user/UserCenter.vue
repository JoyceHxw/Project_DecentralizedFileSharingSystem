<script setup>
import PageContainer from '@/components/PageContainer.vue'
import { ref, watch } from 'vue'
import { useUserStore } from '@/stores/user'
import { userUpdateService } from '@/api/user'
import { ElMessage } from 'element-plus'
// import avatarDefault from '@/assets/default.png'

// 是在使用仓库中数据的初始值 (无需响应式) 解构无问题
const {
  user: { userId, username, nickname, avatar },
  getUser
} = useUserStore()

const form = ref({
  userId,
  avatar,
  username,
  nickname
})

// 标记表单是否发生变化
const isChanged = ref(false)

// 监视表单内容变化
watch(
  form,
  () => {
    // 检查表单内容是否发生变化
    isChanged.value = true
  },
  { deep: true }
)

const submitForm = async () => {
  // 提交修改
  console.log(form.value)
  await userUpdateService(form.value)
  // 通知 user 模块，进行数据的更新
  getUser()
  // 提示用户
  ElMessage.success('修改成功')
  // 按钮禁用
  isChanged.value = false
}
</script>
<template>
  <page-container title="个人中心">
    <!-- 表单部分 -->
    <el-form ref="formRef" :model="form" label-width="100px">
      <el-form-item label="用户名">
        <el-input
          style="width: 400px"
          v-model="form.username"
          disabled
        ></el-input>
      </el-form-item>
      <el-form-item label="头像">
        <!-- <el-avatar :src="form.avatar || avatarDefault" /> -->
        <el-input style="width: 400px" v-model="form.avatar"></el-input>
      </el-form-item>
      <el-form-item label="昵称">
        <el-input style="width: 400px" v-model="form.nickname"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button
          type="primary"
          @click="submitForm"
          color="#009A96"
          :disabled="!isChanged"
          >修改</el-button
        >
      </el-form-item>
    </el-form>
  </page-container>
</template>
