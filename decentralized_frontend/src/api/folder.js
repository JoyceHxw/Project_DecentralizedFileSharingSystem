import request from '@/utils/request'

export const folderGetListService = (userId, parentFolderId) =>
  request.get('/folder/find', {
    params: {
      userId,
      parentFolderId
    }
  })

export const folderCreateService = ({ folderName, parentFolderId, userId }) =>
  request.post('/folder/create', {
    folderName,
    parentFolderId,
    userId
  })

export const folderDeleteService = (userId, folderId) =>
  request.post('/folder/delete', null, {
    params: {
      userId,
      folderId
    }
  })

export const folderCopyService = ({ userId, copiedFolderId, targetFolderId }) =>
  request.post('/folder/copy', {
    userId,
    copiedFolderId,
    targetFolderId
  })
