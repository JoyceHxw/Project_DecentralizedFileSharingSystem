<script setup>
import PageContainer from '@/components/PageContainer.vue'
import TeamUpdate from '@/components/TeamUpdate.vue'
import TeamCreate from '@/components/TeamCreate.vue'
import { useUserStore } from '@/stores/user'
import { ref } from 'vue'
import { teamGetListService, teamQuitService } from '@/api/team'
import { Search, Edit, Minus } from '@element-plus/icons-vue'
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
  const res = await teamGetListService(userId, teamNameKey.value, true)
  teamList.value = res.data
  loading.value = false
}
getTeamList()

const teamCreateRef = ref()
const onCreateTeam = () => {
  teamCreateRef.value.open({})
}

const teamUpdateRef = ref()
const onTeamUpdate = (row) => {
  teamUpdateRef.value.open(row)
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

// 添加或者编辑 成功的回调
const onSuccess = () => {
  getTeamList()
}
</script>
<template>
  <page-container title="我的群组">
    <template #extra>
      <div class="alignment-container">
        <el-input
          v-model="teamNameKey"
          style="width: 200px"
          placeholder="搜索群组名"
          :prefix-icon="Search"
          @keyup.enter="getTeamList"
        />
        <el-button type="primary" @click="onCreateTeam" color="#009A96"
          >创建群组</el-button
        >
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
      <el-table-column
        label="创建者"
        prop="team.userName"
        width="150"
      ></el-table-column>
      <el-table-column label="创建时间" prop="team.createTime" width="200">
        <template #default="{ row }">
          {{ formatTime(row.team.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-button
            circle
            plain
            type="danger"
            :icon="Minus"
            @click="onQuitTeam(row)"
          ></el-button>
          <el-button
            v-if="row.team.userId === userId"
            circle
            plain
            type="primary"
            :icon="Edit"
            @click="onTeamUpdate(row)"
          ></el-button>
        </template>
      </el-table-column>
    </el-table>
    <team-create ref="teamCreateRef" @success="onSuccess"></team-create>
    <team-update
      ref="teamUpdateRef"
      @success="onSuccess"
      teamId="1"
    ></team-update>
  </page-container>
</template>
<style scoped>
.alignment-container {
  width: 1000px;
  display: flex;
  justify-content: space-between;
}
</style>
