<script setup>
import HeaderComponent from "@/components/HeaderComponent.vue";
import FooterComponent from "@/components/FooterComponent.vue";
import {useRoute} from "vue-router";

const route = useRoute();

</script>

<template>
    <div v-if="route.path !== '/signup' && route.path !== '/login' && route.path !== '/resetpassword'" class="app-header" :key="$route.path">
        <HeaderComponent/>
    </div>
    <div class="app-content">
        <RouterView :key="$router.path"/>
    </div>
    <div v-if="route.path !== '/signup' && route.path !== '/login'" class="app-footer">
        <FooterComponent/>
    </div>
</template>

<style scoped>
.app-header {
    display: flex;
    justify-content: center;
    width: 100%;
    flex-shrink: 0;
    border-bottom: 1px solid #ccc; /* 테스트용 경계선 */
}
.app-content {
    flex: 1;
    width: 60%;
    max-width: 1200px;
    height: auto;
    display: flex;
    justify-content: center;
}
.app-footer {
    display: flex;
    justify-content: center;
    width: 100%;
    flex-shrink: 0;
    margin-top: 50px;
    border-top: 1px solid #ccc; /* 테스트용 경계선 */
}
@media (max-width: 1024px) {
    .app-content {
        width: 80%;
    }
}

@media (max-width: 768px) {
    .app-content {
        width: 90%;
    }
}

@media (max-width: 480px) {
    .app-content {
        width: 95%;
        padding: 10px;
    }
}
</style>
