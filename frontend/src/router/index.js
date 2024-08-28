import {createRouter, createWebHistory} from 'vue-router'
import MapView from "@/views/MapView.vue";
import ChatView from "@/views/ChatView.vue";
import SearchPlaceView from "@/views/SearchPlaceView.vue";

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
  },
  {
    path: '/search',
    name: 'search',
    component: SearchPlaceView
  },
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router
