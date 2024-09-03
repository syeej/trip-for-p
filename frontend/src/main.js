import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import axios from "axios";
import store from "@/store/index.js";

const app = createApp(App);
app.config.globalProperties.$axios = axios;
app.config.globalProperties.$store = store;
app.use(router).use(store).mount('#app')
