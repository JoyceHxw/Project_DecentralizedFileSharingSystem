import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getCurrentUser } from '@/api/user'

export const useUserStore = defineStore(
  'big-user',
  () => {
    const user = ref({})
    const getUser = async () => {
      const res = await getCurrentUser() // 请求获取数据
      user.value = res.data
    }
    const setUser = (obj) => {
      user.value = obj
    }

    return {
      getUser,
      setUser,
      user
    }
  },
  {
    persist: true
  }
)
