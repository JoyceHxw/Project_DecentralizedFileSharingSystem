<script setup>
import { ref, watch } from 'vue'
import { fileUploadService } from '@/api/file'
import { stampGetListService } from '@/api/stamp'
import { useUserStore } from '@/stores/user'
import { accountGetListService } from '@/api/account'
import { ElMessage, ElLoading } from 'element-plus'

//父传子
const props = defineProps({
  folderId: Number
})

// 控制抽屉显示隐藏
const visibleDrawer = ref(false)

const {
  user: { userId }
} = useUserStore()

const defaultForm = {
  userId: userId,
  accountId: '',
  filename: '',
  filepath: '',
  batchId: '',
  isEncrypted: false,
  folderId: 0
}

// 准备数据
const formModel = ref({ ...defaultForm })

const accountList = ref([]) // 账号列表
const getAccountList = async () => {
  const res = await accountGetListService(userId)
  accountList.value = res.data
}
getAccountList()

const loading = ref(false) // loading状态
const stampList = ref([]) //指定账户邮票列表
const getStampList = async () => {
  loading.value = true
  const res = await stampGetListService(userId, formModel.value.accountId)
  stampList.value = res.data
  loading.value = false
}

watch(
  () => formModel.value.accountId,
  (newValue) => {
    stampList.value = []
    if (newValue !== '') {
      getStampList()
    }
  }
)

const emit = defineEmits(['success'])
const submitUpload = async () => {
  // console.log(formModel.value)
  const loading = ElLoading.service({
    lock: true,
    text: 'Loading',
    background: 'rgba(0, 0, 0, 0.7)'
  })
  const res = await fileUploadService(formModel.value)
  loading.close()
  if (res.code === 200) {
    visibleDrawer.value = false
    ElMessage.success('上传成功，reference为：' + res.data)
    emit('success', 'edit')
  }
}

const onCancel = () => {
  visibleDrawer.value = false
  defaultForm.value = {}
}

const open = async () => {
  visibleDrawer.value = true // 显示抽屉
  formModel.value = { ...defaultForm } // 基于默认的数据，重置form数据
  formModel.value.folderId = props.folderId
  console.log(props.folderId)
}
defineExpose({
  open
})
</script>
<template>
  <el-drawer
    v-model="visibleDrawer"
    title="文件上传"
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
      <el-form-item label="邮票batchId">
        <el-select
          v-model="formModel.batchId"
          placeholder="选择邮票"
          style="width: 240px"
        >
          <el-option
            v-for="item in stampList"
            :key="item.stampId"
            :label="item.batchId"
            :value="item.batchId"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="文件路径">
        <el-text class="mx-1" size="small" type="danger"
          >eg："D:/测试文件-上传/SWARM.png"</el-text
        >
        <el-input style="width: 400px" v-model="formModel.filepath"></el-input>
      </el-form-item>
      <el-form-item label="文件名">
        <el-input style="width: 400px" v-model="formModel.filename"></el-input>
      </el-form-item>
      <el-form-item label="是否加密">
        <el-radio-group v-model="formModel.isEncrypted" class="ml-4">
          <el-radio :value="true" size="large">是</el-radio>
          <el-radio :value="false" size="large">否</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitUpload" color="#009A96"
          >上传</el-button
        >
        <el-button @click="onCancel" type="info">取消</el-button>
      </el-form-item>
    </el-form>
  </el-drawer>
</template>
