<script setup>
import PageContainer from '@/components/PageContainer.vue'
import { useUserStore } from '@/stores/user'
import { ref } from 'vue'
import {
  teamGetListService,
  teamJoinService,
  teamQuitService
} from '@/api/team'
import { Search, Plus, Minus } from '@element-plus/icons-vue'
import { formatTime } from '../../utils/format'
import { ElMessage, ElMessageBox } from 'element-plus'

const {
  user: { userId }
} = useUserStore()

const teamNameKey = ref('')

const teamList = ref([]) // 账号列表
const loading = ref(false) // loading状态

const getTeamList = async () => {
  loading.value = true
  const res = await teamGetListService(userId, teamNameKey.value, false)
  teamList.value = res.data
  loading.value = false
}
getTeamList()

// 加入队伍
const onJoinTeam = async (row) => {
  // 提示用户是否要删除
  await ElMessageBox.prompt('输入6位数字群组密码', '密码', {
    confirmButtonText: '加入',
    cancelButtonText: '取消',
    inputType: 'password',
    inputPattern: /^\d{6}$/,
    inputErrorMessage: '密码需为6位数字'
  })
    .then(async ({ value }) => {
      const res = await teamJoinService({
        userId,
        teamId: row.team.teamId,
        password: value
      })
      if (res.code === 200) {
        ElMessage({
          type: 'success',
          message: '加入成功'
        })
        // 重新渲染列表
        getTeamList()
      }
    })
    .catch(() => {
      ElMessage({
        type: 'info',
        message: '取消加入'
      })
    })
}

// 退出队伍
const onQuitTeam = async (row) => {
  // 提示用户是否要删除
  await ElMessageBox.confirm('是否确定退出该群组?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
  await teamQuitService(userId, row.team.teamId)
  ElMessage.success('退出成功')
  // 重新渲染列表
  getTeamList()
}
</script>
<template>
  <page-container title="加入群组">
    <template #extra>
      <div class="alignment-container">
        <el-input
          v-model="teamNameKey"
          style="width: 200px"
          placeholder="搜索群组名"
          :prefix-icon="Search"
          @keyup.enter="getTeamList"
        />
      </div>
    </template>
    <el-table :data="teamList" v-loading="loading" height="490">
      <el-table-column label="群组名" prop="team.teamName" width="200">
      </el-table-column>
      <el-table-column label="群组描述" prop="team.description" width="450">
        <template #default="{ row }">
          <el-text class="w-150px mb-2" truncated>
            {{ row.team.description }}
          </el-text>
        </template>
      </el-table-column>
      <el-table-column label="创建者" prop="team.userName" width="150">
      </el-table-column>
      <el-table-column label="创建时间" prop="team.createTime" width="200">
        <template #default="{ row }">
          {{ formatTime(row.team.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-button
            v-if="!row.isJoined"
            circle
            plain
            type="primary"
            :icon="Plus"
            @click="onJoinTeam(row)"
          ></el-button>
          <el-button
            v-else
            circle
            plain
            type="danger"
            :icon="Minus"
            @click="onQuitTeam(row)"
          ></el-button>
        </template>
      </el-table-column>
    </el-table>
  </page-container>
</template>
<style scoped>
.alignment-container {
  width: 1000px;
  display: flex;
  justify-content: space-between;
}
</style>
