<script setup>
import PageContainer from '@/components/PageContainer.vue'
import { ref } from 'vue'
import { accountGetListService } from '@/api/account'
import { useUserStore } from '@/stores/user'
import { stampGetListService } from '@/api/stamp'
import StampBuyer from '@/components/StampBuyer.vue'
import StampEditor from '@/components/StampEditor.vue'
import { CirclePlus, Clock } from '@element-plus/icons-vue'

const {
  user: { userId }
} = useUserStore()

const accountId = ref()

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
  const res = await stampGetListService(userId, accountId.value)
  stampList.value = res.data
  loading.value = false
}

const stampBuyRef = ref()
const onAddStamp = () => {
  stampBuyRef.value.open({})
}

const stampEditRef = ref()
const operation = ref('')
const onTopUpStamp = (row) => {
  operation.value = '延长邮票有效期'
  stampEditRef.value.open(row)
}

const onDiluteStamp = (row) => {
  operation.value = '增加邮票深度'
  stampEditRef.value.open(row)
}

// 添加或者编辑 成功的回调
const onSuccess = () => {
  getStampList()
}
</script>
<template>
  <page-container title="邮票管理">
    <template #extra>
      <el-space wrap>
        <el-select
          v-model="accountId"
          placeholder="选择账户"
          style="width: 240px"
          @change="getStampList"
        >
          <el-option
            v-for="item in accountList"
            :key="item.accountId"
            :label="item.accountAddress"
            :value="item.accountId"
          />
        </el-select>
        <el-button type="primary" @click="onAddStamp" color="#009A96"
          >购买邮票</el-button
        >
      </el-space>
    </template>
    <el-table :data="stampList" v-loading="loading" height="490">
      <el-table-column label="batchId" prop="batchId" width="300">
      </el-table-column>
      <el-table-column label="金额" prop="amount" width="200"></el-table-column>
      <el-table-column label="深度" prop="depth" width="150"> </el-table-column>
      <el-table-column label="类型" prop="type" width="150">
        <template v-slot="{ row }">
          {{ row.type === 0 ? '不可变' : '可变' }}
        </template></el-table-column
      >
      <el-table-column label="剩余有效时间(秒)" prop="ttl" width="150">
      </el-table-column>
      <!-- 利用作用域插槽 row 可以获取当前行的数据 => v-for 遍历 item -->
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button
            circle
            plain
            type="primary"
            :icon="Clock"
            @click="onTopUpStamp(row)"
          ></el-button>
          <el-button
            circle
            plain
            type="danger"
            :icon="CirclePlus"
            @click="onDiluteStamp(row)"
          ></el-button>
        </template>
      </el-table-column>
    </el-table>
    <stamp-buyer
      ref="stampBuyRef"
      :accountId="accountId"
      @success="onSuccess"
    ></stamp-buyer>
    <stamp-editor
      ref="stampEditRef"
      :operation="operation"
      :accountId="accountId"
      @success="onSuccess"
    ></stamp-editor>
  </page-container>
</template>
