import { createApp } from 'vue'
import { createRouter, createWebHistory } from 'vue-router'
import App from './App.vue'
import Home from './components/Home.vue'
import LoveChat from './components/LoveChat.vue'
import './style.css'

const routes = [
  { path: '/', component: Home },
  { path: '/love-chat', component: LoveChat }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

const app = createApp(App)
app.use(router)
app.mount('#app')