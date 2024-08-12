import axios from 'axios'
// import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import router from '@/router'

const baseURL = 'http://localhost:8080/'

const instance = axios.create({
  baseURL,
  timeout: 120 * 1000
})

instance.defaults.withCredentials = true //请求携带cookie

//响应拦截器
instance.interceptors.response.use(
  (res) => {
    //成功登录情况
    if (res.data.code === 200) {
      return res.data
    }
    //未登录情况，响应状态码为500
    if (res.data.code === 500) {
      router.push('/login')
      return
    }
    //消息提示框，错误情况，展示错误描述
    ElMessage.error(res.data.description || '服务异常')
    return res.data
  },
  (err) => {
    //无法获取响应则直接显示“服务异常”
    ElMessage.error('服务异常')
    return err
  }
)

export default instance
export { baseURL }
