<script setup>
import PageContainer from '@/components/PageContainer.vue'
import FileDownload from '@/components/FileDownload.vue'
import { ref } from 'vue'
import { useUserStore } from '@/stores/user'
import { fileGetListService, fileDeleteService } from '@/api/file'
import { formatTime } from '../../utils/format'
import { Search, Download, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const {
  user: { userId }
} = useUserStore()

const fileNameKey = ref('')

const fileList = ref([]) // 账号列表
const loading = ref(false) // loading状态

const getFileList = async () => {
  loading.value = true
  const res = await fileGetListService(userId, fileNameKey.value, false)
  fileList.value = res.data
  loading.value = false
}
getFileList()

const fileDownloadRef = ref()
const onDownloadFile = (row) => {
  fileDownloadRef.value.open(row)
}

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
  <page-container title="所有文件">
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
      <el-table-column label="文件名" prop="filename" width="350">
      </el-table-column>
      <el-table-column label="是否加密" prop="isEncrypted" width="150">
        <template v-slot="{ row }">
          {{ row.isEncrypted === 0 ? '否' : '是' }}
        </template>
      </el-table-column>
      <el-table-column label="上传用户" prop="userName" width="250">
        <template #default="{ row }">
          {{ row.userName }}
        </template>
      </el-table-column>
      <el-table-column label="上传时间" prop="createTime" width="250">
        <template #default="{ row }">
          {{ formatTime(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-button
            circle
            plain
            type="primary"
            :icon="Download"
            @click="onDownloadFile(row)"
          ></el-button>
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
    <file-download ref="fileDownloadRef" @success="onSuccess"></file-download>
  </page-container>
</template>
<style scoped>
.alignment-container {
  width: 1000px;
  display: flex;
  justify-content: space-between;
}
</style>
