<script setup>
import {ref} from "vue";

const isLogin = ref(false);
</script>

<template>
    <div class="header-container">
        <div class="header-logo">
            <router-link to="/">
                <img src="../assets/logo.png" alt="홈 화면으로 이동">
            </router-link>
        </div>
        <ul v-if="!isLogin">
            <li>
                <router-link to="/signup">회원가입 </router-link>
            </li>
            <li>
                |
            </li>
            <li>
                <router-link to="/login">로그인</router-link>
            </li>
        </ul>
        <ul v-if="isLogin">
            <li>
                <router-link to="/signup">마이페이지</router-link>
            </li>
            <li>
                |
            </li>
            <li>
                <router-link to="/signup">로그아웃</router-link>
            </li>
        </ul>
    </div>
</template>

<style scoped>
.header-container {
    width: 60%;
    max-width: 1200px;
    min-width: 320px;
    margin: 0 auto;
    display: flex;
    justify-content: flex-end;
    position: relative;
    height: 170px;
}
.header-logo {
    position: absolute;
    left: 50%;
    top: 50%;
    transform: translate(-50%, -50%);
}
.header-logo img {
    width: 233px;
    height: auto;
    max-width: 100%;
}
ul {
    display: flex;
    flex: 0 0 auto;
    gap: 10px;
    margin-left: auto;
    align-items: flex-end;
    height: 100%;
}
li {
    display: flex;
    flex-direction: column;
    justify-content: flex-end;
    padding-bottom: 24px;
}
a {
    text-decoration: none;
    color: inherit;
}

@media (max-width: 1024px) {
    .header-container {
        width: 80%;
    }
    .header-logo img {
        width: 200px;
    }
}

@media (max-width: 768px) {
    .header-container {
        width: 90%;
        height: auto;
        flex-direction: column;
        align-items: center;
        padding-top: 20px;
        padding-bottom: 20px;
    }
    .header-logo {
        position: static;
        transform: none;
        margin-bottom: 20px;
    }
    ul {
        margin: 0;
        height: auto;
        align-items: center;
    }
    li {
        padding-bottom: 0;
    }
}

@media (max-width: 480px) {
    .header-container {
        width: 95%;
    }
    .header-logo img {
        width: 180px;
    }
    ul {
        flex-wrap: wrap;
        justify-content: center;
    }
}
</style>
