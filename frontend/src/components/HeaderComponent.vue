<script setup>
import {computed} from "vue";
import store from "@/store";

const isAccessTokenValid = computed(() => store.getters.isAccessTokenValid);
const isAdmin = computed(() => store.getters.getRole==='ADMIN')

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
                    <li v-if="!isAdmin">
                        <router-link to="/mypage">마이페이지</router-link>
                    </li>
                    <li v-else>
                        <router-link to="/admin">관리자페이지</router-link>
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
                <li><router-link to="/">메인</router-link></li>
                <li><router-link to="/plan/list">코스 찾기</router-link></li>
                <li><router-link to="/plan/write">코스 등록</router-link></li>
                <li><router-link to="/free-post">자유게시판</router-link></li>
                <li><router-link to="/review-post">리뷰게시판</router-link></li>
            </ul>
        </nav>
    </header>
</template>

<style scoped>
header {
    z-index: 2;
    width: 100%;
    background-color: #fff;
    box-shadow: 0 2px 10px rgba(0,0,0,0.1);
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
    background-color: #f8f9fa;
    border-top: 1px solid #e9ecef;
}

.submenu {
    width: 40%;
    max-width: 1200px;
    min-width: 320px;
    margin: 0 auto;
    display: flex;
    justify-content: center;
    list-style-type: none;
    padding: 0;
}

.submenu li {
    position: relative;
    flex: 1;
    text-align: center;
    white-space: nowrap;
}

.submenu li a {
    display: block;
    padding: 15px 20px;
    color: #495057;
    font-weight: 500;
    transition: color 0.3s ease, background-color 0.3s ease;
}

.submenu li a:hover {
    color: #007bff;
    background-color: #e9ecef;
}

.submenu li a::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 50%;
    width: 0;
    height: 3px;
    background-color: #007bff;
    transition: width 0.3s ease, left 0.3s ease;
}

.submenu li a:hover::after {
    width: 100%;
    left: 0;
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
    }
    .submenu li a {
        text-align: center;
        padding: 10px 0;
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
    .submenu li {
        flex: 0 0 100%;
    }
}
</style>
