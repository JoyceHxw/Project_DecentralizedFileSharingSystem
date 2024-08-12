<script setup>
import { ref, watch } from 'vue'
import { User, Lock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { userRegisterService, userLoginService } from '@/api/user'

const form = ref()

// do not use same name with ref
const formModelRegister = ref({
  username: '',
  firstPassword: '',
  secondPassword: ''
})
const formModelLogin = ref({
  username: '',
  password: ''
})
const isRegister = ref(false)

//校验规则
const ruleRegister = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    {
      pattern: /^[a-zA-Z0-9_.@]+$/,
      message: '账户不能包含特殊字符',
      trigger: 'blur'
    }
  ],
  firstPassword: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { pattern: /^.{6,}$/, message: '密码长度不能小于6位' }
  ],
  secondPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { pattern: /^.{6,}$/, message: '密码长度不能小于6位' },
    {
      validator: (rule, value, callback) => {
        // 判断 value 和 当前 form 中收集的 password 是否一致
        if (value !== formModelRegister.value.firstPassword) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback() // 就算校验成功，也需要callback
        }
      },
      trigger: 'blur'
    }
  ]
}

const ruleLogin = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    {
      pattern: /^[a-zA-Z0-9_.@]+$/,
      message: '账户不能包含特殊字符',
      trigger: 'blur'
    },
    { pattern: /^.{6,}$/, message: '用户名长度不能小于6位' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { pattern: /^.{6,}$/, message: '密码长度不能小于6位' }
  ]
}

const register = async () => {
  // 注册成功之前，先进行校验，校验成功 → 请求，校验失败 → 自动提示
  await form.value.validate()
  await userRegisterService(formModelRegister.value)
  ElMessage.success('注册成功')
  isRegister.value = false
}

// const userStore = useUserStore()
const router = useRouter()

const login = async () => {
  await form.value.validate()
  await userLoginService(formModelLogin.value)
  ElMessage.success('登录成功')
  router.push('/')
}

watch(isRegister, () => {
  formModelRegister.value = {
    username: '',
    firstPassword: '',
    secondPassword: ''
  }
})
</script>

<template>
  <el-row class="login-page">
    <el-col :span="14" class="bg"></el-col>
    <el-col :span="6" :offset="2" class="form">
      <!-- 注册 -->
      <el-form
        :model="formModelRegister"
        :rules="ruleRegister"
        ref="form"
        size="large"
        autocomplete="off"
        v-if="isRegister"
      >
        <el-form-item>
          <h1>注册</h1>
        </el-form-item>
        <el-form-item prop="username">
          <el-input
            v-model="formModelRegister.username"
            :prefix-icon="User"
            placeholder="请输入用户名"
          ></el-input>
        </el-form-item>
        <el-form-item prop="firstPassword">
          <el-input
            v-model="formModelRegister.firstPassword"
            :prefix-icon="Lock"
            type="password"
            placeholder="请输入密码"
          >
          </el-input>
        </el-form-item>
        <el-form-item prop="secondPassword">
          <el-input
            v-model="formModelRegister.secondPassword"
            :prefix-icon="Lock"
            type="password"
            placeholder="请再次输入密码"
          >
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button
            @click="register"
            auto-insert-space
            style="background-color: rgb(0, 154, 150); color: white"
            >注册</el-button
          >
        </el-form-item>
        <el-form-item class="flex">
          <el-link type="info" :underline="false" @click="isRegister = false">
            ← 返回
          </el-link>
        </el-form-item>
      </el-form>
      <!-- 登录 -->
      <el-form
        :model="formModelLogin"
        :rules="ruleLogin"
        ref="form"
        size="large"
        autocomplete="off"
        v-else
      >
        <el-form-item>
          <h1>登录</h1>
        </el-form-item>
        <el-form-item prop="username">
          <el-input
            v-model="formModelLogin.username"
            :prefix-icon="User"
            placeholder="请输入用户名"
          ></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="formModelLogin.password"
            :prefix-icon="Lock"
            type="password"
            placeholder="请输入密码"
          >
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button @click="login" color="#009A96">登录</el-button>
        </el-form-item>
        <el-form-item class="flex">
          <el-link type="info" :underline="false" @click="isRegister = true">
            注册 →
          </el-link>
        </el-form-item>
      </el-form>
    </el-col>
  </el-row>
</template>

<style scoped>
.login-page {
  height: 100vh;
  background-color: #fff;
}
.login-page .bg {
  background: url('@/assets/logo2.png') no-repeat center / cover;
  border-radius: 0 20px 20px 0;
  z-index: 0;
}
.login-page .form {
  display: flex;
  flex-direction: column;
  justify-content: center;
  user-select: none;
  .title {
    margin: 0 auto;
  }
}
.login-page .form .button {
  width: 100%;
}
.login-page .form .flex {
  width: 100%;
  display: flex;
  justify-content: space-between;
}
</style>
