<script setup>
import PageContainer from '@/components/PageContainer.vue'
import { ref } from 'vue'
import { useUserStore } from '@/stores/user'
import { fileGetListService, fileDeleteService } from '@/api/file'
import { formatTime } from '../../utils/format'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Delete } from '@element-plus/icons-vue'

const {
  user: { userId }
} = useUserStore()

const fileNameKey = ref('')

const fileList = ref([]) // 账号列表
const loading = ref(false) // loading状态
const getFileList = async () => {
  loading.value = true
  const res = await fileGetListService(userId, fileNameKey.value, true)
  fileList.value = res.data
  loading.value = false
}
getFileList()

// 删除逻辑
const onDeleteFile = async (row) => {
  // 提示用户是否要删除
  await ElMessageBox.confirm('此操作将永久删除该文件, 是否继续?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
  await fileDeleteService(userId, row.fileId)
  ElMessage.success('删除成功')
  // 重新渲染列表
  getFileList()
}
</script>
<template>
  <page-container title="文件上传记录">
    <template #extra>
      <div class="alignment-container">
        <el-input
          v-model="fileNameKey"
          style="width: 200px"
          placeholder="搜索文件名"
          :prefix-icon="Search"
          @keyup.enter="getFileList"
        />
      </div>
    </template>
    <el-table :data="fileList" v-loading="loading" height="490">
      <el-table-column label="文件名" prop="filename" width="175">
      </el-table-column>
      <el-table-column
        label="上传时的文件路径"
        prop="filepath"
        width="250"
      ></el-table-column>
      <el-table-column label="reference" prop="reference" width="200">
        <template v-slot="{ row }">
          <el-popover
            placement="bottom"
            :width="200"
            trigger="click"
            :content="row.reference"
          >
            <template #reference>
              <el-text class="w-150px mb-2" truncated>
                {{ row.reference }}
              </el-text>
            </template>
          </el-popover>
        </template>
      </el-table-column>
      <el-table-column label="batchId" prop="batchId" width="150">
        <template v-slot="{ row }">
          <el-popover
            placement="bottom"
            :width="200"
            trigger="click"
            :content="row.batchId"
          >
            <template #reference>
              <el-text class="w-150px mb-2" truncated>
                {{ row.batchId }}
              </el-text>
            </template>
          </el-popover>
        </template>
      </el-table-column>
      <el-table-column label="是否加密" prop="isEncrypted" width="100">
        <template v-slot="{ row }">
          {{ row.isEncrypted === 0 ? '否' : '是' }}
        </template>
      </el-table-column>
      <el-table-column label="上传时间" prop="createTime" width="150">
        <template #default="{ row }">
          {{ formatTime(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-button
            v-if="row.userId === userId"
            circle
            plain
            type="danger"
            :icon="Delete"
            @click="onDeleteFile(row)"
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
