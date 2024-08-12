<script setup>
import { ref } from 'vue'
import { fileDownloadService } from '@/api/file'
import { useUserStore } from '@/stores/user'
import { accountGetListService } from '@/api/account'
import { ElMessage, ElLoading } from 'element-plus'

// 控制抽屉显示隐藏
const visibleDrawer = ref(false)

const isEncrypted = ref()

const {
  user: { userId }
} = useUserStore()

const defaultForm = {
  userId: userId,
  accountId: '',
  reference: '',
  path: ''
}

// 准备数据
const formModel = ref({ ...defaultForm })

const accountList = ref([]) // 账号列表
const getAccountList = async () => {
  const res = await accountGetListService(userId)
  accountList.value = res.data
}
getAccountList()

const submitDownload = async () => {
  const loading = ElLoading.service({
    lock: true,
    text: 'Loading',
    background: 'rgba(0, 0, 0, 0.7)'
  })
  const res = await fileDownloadService(formModel.value)
  loading.close()
  if (res.code === 200) {
    ElMessage.success('下载成功')
    visibleDrawer.value = false
  }
}

const onCancel = () => {
  visibleDrawer.value = false
  defaultForm.value = {}
}

const open = async (row) => {
  visibleDrawer.value = true // 显示抽屉
  formModel.value = { ...defaultForm } // 基于默认的数据，重置form数据
  isEncrypted.value = row.isEncrypted
  if (isEncrypted.value === 0) {
    formModel.value.reference = row.reference
  }
}
defineExpose({
  open
})
</script>
<template>
  <el-drawer
    v-model="visibleDrawer"
    title="文件下载"
    direction="rtl"
    size="40%"
  >
    <el-form ref="formRef" :model="formModel" label-width="100px">
      <el-form-item label="账户">
        <el-select
          v-model="formModel.accountId"
          placeholder="选择账户"
          style="width: 240px"
        >
          <el-option
            v-for="item in accountList"
            :key="item.accountId"
            :label="item.accountAddress"
            :value="item.accountId"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="reference" v-if="isEncrypted">
        <el-input style="width: 400px" v-model="formModel.reference"></el-input>
      </el-form-item>
      <el-form-item label="文件保存位置">
        <el-text class="mx-1" size="small" type="danger"
          >eg：若想保存到D盘“测试文件-下载”目录下，则应为"D:/测试文件-下载/"</el-text
        >
        <el-input style="width: 400px" v-model="formModel.path"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitDownload" color="#009A96"
          >下载</el-button
        >
        <el-button @click="onCancel" type="info">取消</el-button>
      </el-form-item>
    </el-form>
  </el-drawer>
</template>
