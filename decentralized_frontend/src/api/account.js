import request from '@/utils/request'

//获取账户列表
export const accountGetListService = (userId) =>
  request.get('/account/find', {
    params: {
      userId
    }
  })

//查询账户余额
export const accountGetWalletService = (userId, accountId) =>
  request.get('/account/wallet', {
    params: {
      userId,
      accountId
    }
  })

//删除账户
export const accountDeleteService = (userId, accountId) =>
  request.post('/account/delete', null, {
    params: {
      userId,
      accountId
    }
  })

//编辑账户
export const accountEditService = ({
  accountId,
  accountAddress,
  userId,
  hostname
}) =>
  request.post('/account/update', {
    accountId,
    accountAddress,
    userId,
    hostname
  })

export const accountStakeService = ({ userId, accountId, amount }) =>
  request.post('/account/stake', {
    userId,
    accountId,
    amount
  })

//添加账户
export const accountAddService = ({ userId, accountAddress, hostname }) =>
  request.post('/account/add', { userId, accountAddress, hostname })
