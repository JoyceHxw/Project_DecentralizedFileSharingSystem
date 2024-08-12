<script setup>
import { ref, watch } from 'vue'
import {
  accountEditService,
  accountAddService,
  accountStakeService
} from '@/api/account'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElLoading } from 'element-plus'

// 控制抽屉显示隐藏
const visibleDrawer = ref(false)

const props = defineProps({
  operation: String
})

const {
  user: { userId }
} = useUserStore()

// 默认数据
const defaultForm = {
  accountId: 0,
  accountAddress: '',
  userId: userId,
  hostname: '',
  amount: ''
}

// 准备数据
const formModel = ref({ ...defaultForm })

const isStake = ref()
watch(
  () => props.operation,
  (newOperation) => {
    isStake.value = newOperation === '充值'
  }
)

// 提交
const emit = defineEmits(['success'])
const onPublish = async () => {
  // 发请求
  if (formModel.value.accountId) {
    if (isStake.value) {
      const loading = ElLoading.service({
        lock: true,
        text: 'Loading',
        background: 'rgba(0, 0, 0, 0.7)'
      })
      const res = await accountStakeService(formModel.value)
      loading.close()
      if (res.code === 200) {
        ElMessage.success('充值成功')
        visibleDrawer.value = false
        emit('success', 'edit')
      }
    } else {
      // 编辑操作
      const res = await accountEditService(formModel.value)
      if (res.code === 200) {
        ElMessage.success('修改成功')
        visibleDrawer.value = false
        emit('success', 'edit')
      }
    }
  } else {
    // 添加操作
    const res = await accountAddService(formModel.value)
    if (res.code === 200) {
      ElMessage.success('添加成功')
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
const open = async (row) => {
  visibleDrawer.value = true // 显示抽屉

  if (row.accountId) {
    // 需要基于 row.id 发送请求，获取编辑对应的详情数据，进行回显
    formModel.value = row
  } else {
    formModel.value = { ...defaultForm } // 基于默认的数据，重置form数据
  }
}

defineExpose({
  open
})
</script>

<template>
  <el-drawer
    v-model="visibleDrawer"
    :title="
      formModel.accountId ? (isStake ? '钱包充值' : '编辑账户') : '添加账户'
    "
    direction="rtl"
    size="40%"
  >
    <el-form :model="formModel" ref="formRef" label-width="100px">
      <div v-if="isStake">
        <el-form-item label="充值金额">
          <el-input
            v-model="formModel.amount"
            placeholder="请填写充值金额"
          ></el-input>
        </el-form-item>
      </div>
      <div v-else>
        <el-form-item label="账户地址" prop="accountAddress">
          <el-input
            v-model="formModel.accountAddress"
            placeholder="请填写账户地址"
          ></el-input>
        </el-form-item>
        <el-form-item label="swarm主机" prop="hostname">
          <el-input
            v-model="formModel.hostname"
            placeholder="请填写主机地址"
          ></el-input
        ></el-form-item>
      </div>
      <el-form-item>
        <el-button @click="onPublish" color="#009A96">确认</el-button>
        <el-button @click="onCancel" type="info">取消</el-button>
      </el-form-item>
    </el-form>
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
