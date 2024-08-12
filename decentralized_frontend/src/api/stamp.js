import request from '@/utils/request'

//获取账户列表
export const stampGetListService = (userId, accountId) =>
  request.get('/stamp/find', {
    params: {
      userId,
      accountId
    }
  })

//购买邮票
export const stampAddService = ({ userId, accountId, amount, depth }) =>
  request.post('/stamp/buy', {
    userId,
    accountId,
    amount,
    depth
  })

//延长有效时间
export const stampTopUpService = ({ userId, accountId, batchId, amount }) =>
  request.post('/stamp/topUp', { userId, accountId, batchId, amount })

//增加深度
export const stampDiluteService = ({ userId, accountId, batchId, depth }) =>
  request.post('/stamp/dilute', { userId, accountId, batchId, depth })
