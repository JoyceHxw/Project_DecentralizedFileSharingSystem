import request from '@/utils/request'

export const fileUploadService = ({
  userId,
  accountId,
  filename,
  filepath,
  batchId,
  isEncrypted,
  folderId
}) =>
  request.post('/file/uploadFile', {
    userId,
    accountId,
    filename,
    filepath,
    batchId,
    isEncrypted,
    folderId
  })

export const fileDownloadService = ({ userId, accountId, reference, path }) =>
  request.post('file/download', { userId, accountId, reference, path })

export const fileGetListService = (userId, filename, isMy) =>
  request.get('/file/find', {
    params: {
      userId,
      filename,
      isMy
    }
  })

export const fileGetListFolderService = (userId, folderId) =>
  request.get('/file/findByFolder', {
    params: {
      userId,
      folderId
    }
  })

export const fileDeleteService = (userId, fileId) =>
  request.post('/file/delete', null, {
    params: {
      userId,
      fileId
    }
  })

export const fileCopyService = ({ userId, copiedFileId, targetFolderId }) =>
  request.post('/file/copy', {
    userId,
    copiedFileId,
    targetFolderId
  })
