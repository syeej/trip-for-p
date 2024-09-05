<script setup>
import {computed} from "vue";
import store from "@/store";

const isAccessTokenValid = computed(() => store.getters.isAccessTokenValid);

const logout = function () {
    store.commit('clearData');
};
</script>

<template>
    <header>
        <div class="header-container">
            <div class="header-logo">
                <router-link to="/">
                    <img src="../assets/logo.png" alt="홈 화면으로 이동">
                </router-link>
            </div>
            <ul class="auth-menu">
                <template v-if="!isAccessTokenValid">
                    <li>
                        <router-link to="/signup">회원가입</router-link>
                    </li>
                    <li>|</li>
                    <li>
                        <router-link to="/login">로그인</router-link>
                    </li>
                </template>
                <template v-else>
                    <li>
                        <router-link to="/mypage">마이페이지</router-link>
                    </li>
                    <li>|</li>
                    <li>
                        <router-link to="/" @click="logout">로그아웃</router-link>
                    </li>
                </template>
            </ul>
        </div>
        <nav class="submenu-nav">
            <ul class="submenu">
                <li><router-link to="/plan/write">코스 등록</router-link></li>
                <li><router-link to="/plan">코스 공유</router-link></li>
                <li><router-link to="/free-post">자유게시판</router-link></li>
                <li><router-link to="/review-post">리뷰게시판</router-link></li>
            </ul>
        </nav>
    </header>
</template>

<style scoped>
header {
    z-index:2;
    width: 100%;
    background-color: #fff;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.header-container {
    width: 60%;
    max-width: 1200px;
    min-width: 320px;
    margin: 0 auto;
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

.auth-menu {
    display: flex;
    gap: 10px;
    list-style-type: none;
    padding: 0;
    margin: 0;
    position: absolute;
    right: 0;
    bottom: 24px;
}

.submenu-nav {
    background-color: #f8f8f8;
    border-top: 1px solid #eaeaea;
}

.submenu {
    width: 60%;
    max-width: 1200px;
    min-width: 320px;
    margin: 0 auto;
    display: flex;
    justify-content: space-around;
    list-style-type: none;
    padding: 10px 0;
}

a {
    text-decoration: none;
    color: inherit;
}

@media (max-width: 1024px) {
    .header-container, .submenu {
        width: 80%;
    }
    .header-logo img {
        width: 200px;
    }
}

@media (max-width: 768px) {
    .header-container, .submenu {
        width: 90%;
    }
    .header-container {
        height: auto;
        padding: 20px 0;
    }
    .header-logo {
        position: static;
        transform: none;
        text-align: center;
        margin-bottom: 20px;
    }
    .auth-menu {
        position: static;
        justify-content: center;
    }
    .submenu {
        flex-wrap: wrap;
    }
    .submenu li {
        flex: 0 0 50%;
        text-align: center;
        padding: 5px 0;
    }
}

@media (max-width: 480px) {
    .header-container, .submenu {
        width: 95%;
    }
    .header-logo img {
        width: 180px;
    }
    .auth-menu {
        flex-direction: column;
        align-items: center;
        gap: 5px;
    }
    .auth-menu li:nth-child(2) {
        display: none;
    }
}
</style>
