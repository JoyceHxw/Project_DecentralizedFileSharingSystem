<script setup>
import { ref } from 'vue'
import { Delete, Edit, Money } from '@element-plus/icons-vue'
import AccountEditor from '@/components/AccountEditor.vue'
import { accountGetListService, accountDeleteService } from '@/api/account'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'

const accountList = ref([]) // 账号列表
const total = ref(0) // 总账号数
const loading = ref(false) // loading状态

const {
  user: { userId }
} = useUserStore()

// 基于params参数，获取账户列表
const getAccountList = async () => {
  loading.value = true
  const res = await accountGetListService(userId)
  loading.value = false
  accountList.value = res.data
  total.value = res.data.total
}
getAccountList()

const accountEditRef = ref()
// 添加逻辑
const onAddAccount = () => {
  accountEditRef.value.open({})
}

const operation = ref()
// 编辑逻辑
const onEditAccount = (row) => {
  operation.value = '编辑'
  accountEditRef.value.open(row)
}

// 钱包充值逻辑
const onStakeAccount = (row) => {
  operation.value = '充值'
  accountEditRef.value.open(row)
}

// 删除逻辑
const onDeleteAccount = async (row) => {
  // 提示用户是否要删除
  await ElMessageBox.confirm('此操作将永久删除该账户, 是否继续?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
  await accountDeleteService(userId, row.accountId)
  ElMessage.success('删除成功')
  // 重新渲染列表
  getAccountList()
}

// 添加或者编辑 成功的回调
const onSuccess = () => {
  getAccountList()
}
</script>

<template>
  <page-container title="账户管理">
    <template #extra>
      <el-button type="primary" @click="onAddAccount" color="#009A96"
        >添加账户</el-button
      >
    </template>

    <!-- 表格区域 -->
    <el-table :data="accountList" v-loading="loading">
      <el-table-column label="账户地址" prop="accountAddress" width="450">
      </el-table-column>
      <el-table-column
        label="swarm主机"
        prop="hostname"
        width="250"
      ></el-table-column>
      <el-table-column label="钱包余额" prop="balance" width="250">
      </el-table-column>
      <!-- 利用作用域插槽 row 可以获取当前行的数据 => v-for 遍历 item -->
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button
            circle
            plain
            type="primary"
            :icon="Edit"
            @click="onEditAccount(row)"
          ></el-button>
          <el-button
            circle
            plain
            type="primary"
            :icon="Money"
            @click="onStakeAccount(row)"
          ></el-button>
          <el-button
            circle
            plain
            type="danger"
            :icon="Delete"
            @click="onDeleteAccount(row)"
          ></el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加编辑的抽屉 -->
    <account-editor
      ref="accountEditRef"
      @success="onSuccess"
      :operation="operation"
    ></account-editor>
  </page-container>
</template>

<style lang="scss" scoped></style>
