<script setup>

import HeaderView from "@/views/HeaderView.vue";
import FooterView from "@/views/FooterView.vue";
</script>

<template>
    <div class="app-header">
        <HeaderView/>
    </div>
    <div class="app-content">
        <RouterView :key="$router.path"/>
    </div>
    <div class="app-footer">
        <FooterView/>
    </div>
</template>

<style scoped>
.app-header {
    display: flex;
    justify-content: center;
    width: 100vw;
    height: 100px;
    flex-shrink: 0;
    border: 1px solid #ccc; /* 테스트용 경계선 */
}
.app-content {
    flex: 1;
    width: 60vw;
    height: auto;
    display: flex;
    justify-content: center;
    border: 1px solid #ccc; /* 테스트용 경계선 */
}
.app-footer {
    display: flex;
    justify-content: center;
    width: 100vw;
    height: 100px;
    flex-shrink: 0;
    border: 1px solid #ccc; /* 테스트용 경계선 */
}
</style>
