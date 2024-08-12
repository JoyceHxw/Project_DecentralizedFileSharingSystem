<script setup>
import PageContainer from '@/components/PageContainer.vue'
import { ref } from 'vue'
import { useUserStore } from '@/stores/user'
import {
  fileGetListFolderService,
  fileDeleteService,
  fileCopyService
} from '@/api/file'
import {
  folderGetListService,
  folderCreateService,
  folderCopyService,
  folderDeleteService
} from '@/api/folder'
import { teamGetListService } from '@/api/team'
import { formatTime } from '../../utils/format'
import { Delete, Download, CopyDocument } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import FileDownload from '@/components/FileDownload.vue'
import FileUpload from '@/components/FileUpload.vue'

const {
  user: { userId }
} = useUserStore()

const currentFolderId = ref('')
const currentFolderName = ref('/')

const tempFolderId = ref('')

const folderList = ref([])
const fileList = ref([]) // 账号列表

//前进退出修改路径
//文件夹栈
const folderRecord = ref([])
const length = ref(0)

const loading = ref(false) // loading状态

const teamList = ref([]) // 账号列表
const getTeamList = async () => {
  const res = await teamGetListService(userId, '', true)
  teamList.value = res.data
}
getTeamList()

const fileFolderList = ref([])

const getFileFolderList = async (folderId) => {
  //加载动画开启
  loading.value = true
  //发送请求，获取指定文件夹下的子文件夹列表和文件列表
  const folderRes = await folderGetListService(userId, folderId)
  folderList.value = folderRes.data
  const fileRes = await fileGetListFolderService(userId, folderId)
  fileList.value = fileRes.data

  //提取folderList中的属性，添加type属性为文件夹
  const folders = folderRes.data.map((item) => ({
    name: item.folderName,
    itemId: item.folderId,
    creatorId: item.userId,
    creatorName: item.userName,
    createTime: item.createTime,
    type: 'folder'
  }))

  //提取fileList中的属性，添加type属性为文件
  const files = fileRes.data.map((item) => ({
    name: item.filename,
    itemId: item.fileId,
    creatorId: item.userId,
    creatorName: item.userName,
    createTime: item.createTime,
    type: 'file'
  }))

  // 合并文件夹和文件列表。并复制给全局变量
  const combinedList = folders.concat(files)
  fileFolderList.value = combinedList
  //加载结束，展示数据
  loading.value = false
}

//选完群组后需要添加根目录，还需要初始化新路径记录，和标题
const goFirstList = async (folderId) => {
  folderRecord.value = []
  length.value = 0
  currentFolderName.value = '/'
  currentFolderId.value = folderId
  getFileFolderList(folderId)
  folderRecord.value.push({ folderId: folderId, folderName: '/' })
  length.value++
}

const onCreateFolder = async () => {
  ElMessageBox.prompt('请输入文件名', '创建新文件夹', {
    confirmButtonText: '确认',
    cancelButtonText: '取消'
  })
    .then(async ({ value }) => {
      const res = await folderCreateService({
        folderName: value,
        parentFolderId: currentFolderId.value,
        userId
      })
      if (res.code === 200) {
        ElMessage({
          type: 'success',
          message: '文件夹创建成功'
        })
        getFileFolderList(currentFolderId.value)
      }
    })
    .catch(() => {
      ElMessage({
        type: 'info',
        message: '取消文件夹创建'
      })
    })
}

const fileDownloadRef = ref()
const onDownloadFile = (row) => {
  fileDownloadRef.value.open(row)
}

// 删除逻辑
const onDelete = async (row) => {
  // 提示用户是否要删除
  await ElMessageBox.confirm('此操作将永久删除该文件, 是否继续?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
  if (row.type === 'file') {
    const res = await fileDeleteService(userId, row.itemId)
    if (res.code === 200) {
      ElMessage.success('删除文件成功')
    }
  } else {
    const res = await folderDeleteService(userId, row.itemId)
    if (res.code === 200) {
      ElMessage.success('删除文件夹成功')
    }
  }
  // 重新渲染列表
  getFileFolderList(currentFolderId.value)
}

//复制文件或文件夹
const copiedType = ref()
const copiedFileId = ref()
const copiedFolderId = ref()

const onCopy = (row) => {
  if (row.type === 'file') {
    copiedType.value = 'file'
    copiedFileId.value = row.itemId
  } else {
    copiedType.value = 'folder'
    copiedFolderId.value = row.itemId
  }
  ElMessage.success('拷贝成功')
}

//粘贴文件或文件夹
const onPaste = async () => {
  if (copiedType.value === 'file') {
    const res = await fileCopyService({
      userId,
      copiedFileId: copiedFileId.value,
      targetFolderId: currentFolderId.value
    })
    if (res.code === 200) {
      ElMessage.success('粘贴成功')
    }
    getFileFolderList(currentFolderId.value)
  } else if (copiedType.value === 'folder') {
    const res = await folderCopyService({
      userId,
      copiedFolderId: copiedFolderId.value,
      targetFolderId: currentFolderId.value
    })
    if (res.code === 200) {
      ElMessage.success('粘贴成功')
    }
    getFileFolderList(currentFolderId.value)
  } else {
    ElMessage.error('请先拷贝')
  }
}

//点击进入子文件夹，压栈并更新目录
const goIntoFolder = async (row) => {
  currentFolderId.value = row.itemId
  currentFolderName.value += ' > ' + row.name
  folderRecord.value.push({ folderId: row.itemId, folderName: row.name })
  length.value++

  getFileFolderList(currentFolderId.value)
}

//返回上一层目录
const goBackFolder = async () => {
  //注意顺序，先更新上一级目录名称，因为需要当前目录，再弹出最后一个元素
  //截取路径字符串
  let lastFolderName =
    folderRecord.value[folderRecord.value.length - 1].folderName
  currentFolderName.value = currentFolderName.value.slice(
    0,
    currentFolderName.value.length - lastFolderName.length - 3
  )
  folderRecord.value.pop()
  currentFolderId.value =
    folderRecord.value[folderRecord.value.length - 1].folderId
  length.value--
  getFileFolderList(currentFolderId.value)
}

const fileUploadRef = ref()
const onUploadFile = () => {
  fileUploadRef.value.open({})
}

// 添加或者编辑 成功的回调
const onSuccess = () => {
  getFileFolderList(currentFolderId.value)
}
</script>
<template>
  <page-container :title="'目录 > ' + currentFolderName">
    <template #extra>
      <div class="alignment-container">
        <el-select
          v-model="tempFolderId"
          placeholder="选择群组"
          style="width: 240px"
          @change="goFirstList(tempFolderId)"
        >
          <el-option
            v-for="item in teamList"
            :key="item.team.rootFolderId"
            :label="item.team.teamName"
            :value="item.team.rootFolderId"
          />
        </el-select>
        <el-button
          type="primary"
          @click="goBackFolder"
          color="#009A96"
          :disabled="length <= 1"
          >返回</el-button
        >
        <el-button type="primary" @click="onPaste" color="#009A96"
          >粘贴</el-button
        >
        <el-button type="primary" @click="onCreateFolder" color="#009A96"
          >创建新文件夹</el-button
        >
        <el-button type="primary" @click="onUploadFile" color="#009A96"
          >上传</el-button
        >
      </div>
    </template>
    <el-table :data="fileFolderList" v-loading="loading" height="490">
      <el-table-column label="" width="50">
        <template #default="{ row }">
          <div v-if="row.type === 'folder'">
            <img
              src="@/assets/folder.png"
              alt="Type 1 Image"
              width="25"
              height="25"
            />
          </div>
          <div v-else>
            <img
              src="@/assets/docs.png"
              alt="Default Image"
              width="25"
              height="25"
            />
          </div>
        </template>
      </el-table-column>
      <el-table-column label="名称" prop="name" width="350">
        <template #default="{ row }">
          <el-link
            :underline="false"
            v-if="row.type === 'folder'"
            @click="goIntoFolder(row)"
            >{{ row.name }}</el-link
          >
        </template>
      </el-table-column>
      <el-table-column label="创建者" prop="creatorName" width="150">
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="250">
        <template #default="{ row }">
          {{ formatTime(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="类型" prop="type" width="150"> </el-table-column>
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button
            circle
            plain
            type="primary"
            :icon="CopyDocument"
            @click="onCopy(row)"
          ></el-button>
          <el-button
            v-if="row.type === 'file'"
            circle
            plain
            type="primary"
            :icon="Download"
            @click="onDownloadFile(row)"
          ></el-button>
          <el-button
            v-if="row.creatorId === userId"
            circle
            plain
            type="danger"
            :icon="Delete"
            @click="onDelete(row)"
          ></el-button>
        </template>
      </el-table-column>
    </el-table>
    <file-upload
      ref="fileUploadRef"
      @success="onSuccess"
      :folderId="currentFolderId"
    ></file-upload>
    <file-download ref="fileDownloadRef" @success="onSuccess"></file-download>
  </page-container>
</template>
<style scoped>
.alignment-container {
  width: 550px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
