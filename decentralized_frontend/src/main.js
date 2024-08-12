import { createApp } from 'vue'
import { createPinia } from 'pinia'
import persist from 'pinia-plugin-persistedstate'
import App from './App.vue'
import router from './router'
import 'element-plus/dist/index.css'

const app = createApp(App)

app.use(createPinia().use(persist))
app.use(router)

app.mount('#app')
