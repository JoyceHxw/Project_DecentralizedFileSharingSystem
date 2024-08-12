<script setup>
import { ref, watch } from 'vue'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElLoading } from 'element-plus'
import { stampTopUpService, stampDiluteService } from '@/api/stamp'

// 控制抽屉显示隐藏
const visibleDrawer = ref(false)

const props = defineProps({
  operation: String,
  accountId: Number
})

const {
  user: { userId }
} = useUserStore()

// 默认数据
const defaultForm = {
  accountId: 0,
  userId: userId,
  batchId: '',
  amount: '',
  depth: ''
}

// 准备数据
const formModel = ref({ ...defaultForm })

// 提交
const emit = defineEmits(['success'])
const onPublish = async () => {
  const loading = ElLoading.service({
    lock: true,
    text: 'Loading',
    background: 'rgba(0, 0, 0, 0.7)'
  })
  // 发请求
  if (isTopUp.value) {
    // 延时操作
    const res = await stampTopUpService(formModel.value)
    loading.close()
    if (res.code === 200) {
      ElMessage.success('延时成功')
      visibleDrawer.value = false
      emit('success', 'edit')
    }
  } else {
    // 扩容操作
    const res = await stampDiluteService(formModel.value)
    loading.close()
    if (res.code === 200) {
      ElMessage.success('扩容成功')
      visibleDrawer.value = false
      // 通知到父组件，添加成功了
      emit('success', 'add')
    }
  }
}
const onCancel = () => {
  visibleDrawer.value = false
  defaultForm.value = {}
}

// 组件对外暴露一个方法 open，基于open传来的参数，区分添加还是编辑
// open({})  => 表单无需渲染，说明是添加
// open({ id, ..., ... })  => 表单需要渲染，说明是编辑
// open调用后，可以打开抽屉
const isTopUp = ref()
const open = async (row) => {
  visibleDrawer.value = true // 显示抽屉
  formModel.value = { ...defaultForm } // 基于默认的数据，重置form数据
  formModel.value.batchId = row.batchId
  formModel.value.accountId = props.accountId
}

watch(
  () => props.operation,
  (newOperation) => {
    isTopUp.value = newOperation === '延长邮票有效期'
  }
)

defineExpose({
  open
})
</script>

<template>
  <el-drawer
    v-model="visibleDrawer"
    :title="operation"
    direction="rtl"
    size="40%"
  >
    <el-form :model="formModel" ref="formRef" label-width="100px">
      <el-form-item v-if="isTopUp" label="金额" prop="amount">
        <el-input
          v-model="formModel.amount"
          placeholder="请填写金额"
        ></el-input>
      </el-form-item>
      <el-form-item v-else label="深度" prop="depth">
        <el-input v-model="formModel.depth" placeholder="请填写深度"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button @click="onPublish" color="#009A96">确认</el-button>
        <el-button @click="onCancel" type="info">取消</el-button>
      </el-form-item>
    </el-form>
    <el-text class="mx-1" size="small">*操作成功后需等待几分钟方可查到</el-text>
  </el-drawer>
</template>

<style scoped>
.editor {
  width: 100%;
}

.editor .ql-editor {
  min-height: 200px;
}
</style>
