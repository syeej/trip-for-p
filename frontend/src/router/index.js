import {createRouter, createWebHistory} from 'vue-router'
import MapView from "@/views/MapView.vue";
import ChatView from "@/views/ChatView.vue";

const routes = [
  {
    path: '/',
    name: 'map',
    component: MapView
  },
  {
    path: '/chat',
    name: 'chat',
    component: ChatView
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router
