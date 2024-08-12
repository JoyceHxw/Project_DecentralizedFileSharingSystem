import request from '@/utils/request'

//注册接口
export const userRegisterService = ({
  username,
  firstPassword,
  secondPassword
}) =>
  request.post('/user/register', { username, firstPassword, secondPassword })

//登录接口
export const userLoginService = ({ username, password }) =>
  request.post('/user/login', { username, password })

//退出接口
export const userLogoutService = () => request.get('/user/logout')

//获取当前用户
export const getCurrentUser = () => request.get('/user/getCurrentUser')

//修改用户信息
export const userUpdateService = ({ userId, username, nickname, avatar }) =>
  request.post('/user/update', { userId, username, nickname, avatar })
