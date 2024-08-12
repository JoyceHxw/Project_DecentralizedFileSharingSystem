<script setup>
import { ref } from 'vue'
import { teamUpdateService } from '@/api/team'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElLoading } from 'element-plus'

// 控制抽屉显示隐藏
const visibleDrawer = ref(false)

const {
  user: { userId }
} = useUserStore()

const defaultForm = {
  userId: userId,
  teamId: '',
  teamName: null,
  teamPassword: '',
  description: null
}

// 准备数据
const formRef = ref()
const formModel = ref({ ...defaultForm })

const rulePassword = {
  teamName: [{ required: true, message: '请输入群组名称', trigger: 'blur' }],
  teamPassword: [
    { required: true, message: '请输入进群密码', trigger: 'blur' },
    { pattern: /^\d{6}$/, message: '密码需为6位数字' }
  ]
}

const emit = defineEmits(['success'])
const submitUpdate = async () => {
  // console.log(formModel.value)
  // 注册成功之前，先进行校验，校验成功 → 请求，校验失败 → 自动提示
  await formRef.value.validate()
  const loading = ElLoading.service({
    lock: true,
    text: 'Loading',
    background: 'rgba(0, 0, 0, 0.7)'
  })
  const res = await teamUpdateService(formModel.value)
  loading.close()
  if (res.code === 200) {
    visibleDrawer.value = false
    ElMessage.success('编辑成功')
    emit('success', 'edit')
  }
}

const onCancel = () => {
  visibleDrawer.value = false
  defaultForm.value = {}
}

const open = async (row) => {
  visibleDrawer.value = true // 显示抽屉
  formModel.value = { ...defaultForm } // 基于默认的数据，重置form数据
  formModel.value.teamId = row.team.teamId
  formModel.value.teamName = row.team.teamName
  formModel.value.description = row.team.description
  formModel.value.teamPassword = row.team.teamPassword
}
defineExpose({
  open
})
</script>
<template>
  <el-drawer
    v-model="visibleDrawer"
    title="编辑群组信息"
    direction="rtl"
    size="30%"
  >
    <el-form
      ref="formRef"
      :model="formModel"
      :rules="rulePassword"
      label-width="100px"
    >
      <el-form-item label="群组名称">
        <el-input style="width: 400px" v-model="formModel.teamName"></el-input
      ></el-form-item>
      <el-form-item label="群组描述">
        <el-input
          v-model="formModel.description"
          style="width: 300px"
          autosize
          type="textarea"
        />
      </el-form-item>
      <el-form-item label="进群密码" prop="teamPassword">
        <el-input
          style="width: 400px"
          v-model="formModel.teamPassword"
          type="password"
        ></el-input
      ></el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitUpdate" color="#009A96"
          >提交</el-button
        >
        <el-button @click="onCancel" type="info">取消</el-button>
      </el-form-item>
    </el-form>
  </el-drawer>
</template>
