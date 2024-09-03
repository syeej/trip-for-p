<script setup>
import router from "@/router";
import {computed, onMounted, ref} from "vue";
import {loginAPI} from "@/api";
import store from "@/store";

const email = ref("");
const password = ref("");

// 버튼 활성화 여부를 computed로 계산
const isFormValid = computed(() => {
    return email.value && password.value;
});

const goSignup = function () {
    router.push("/signup");
}

const goHome = function () {
    router.push("/");
}

const login = async function () {
    try {
        const formData = new FormData();
        formData.append('username', email.value);
        formData.append('password', password.value);

        const response = await loginAPI(formData);
        store.commit('setAccessToken', response.headers.access)
        console.log('Login successful');
        await router.replace('/');
    } catch (error) {
        alert("회원정보가 올바르지 않습니다.");
        console.log('Login failed: ', error);
    }
}


onMounted(() => {
    const inputs = document.querySelectorAll('.login-email, .login-password');

    inputs.forEach(input => {
        input.addEventListener('focus', () => {
            input.dataset.placeholder = input.placeholder;
            input.placeholder = '';
        });

        input.addEventListener('blur', () => {
            if (input.dataset.placeholder !== undefined) {
                input.placeholder = input.dataset.placeholder;
                delete input.dataset.placeholder;
            }
        });
    });

    document.querySelector('.login-email').focus();
})
</script>

<template>
    <div class="login-wrap">
        <div class="login-title">
            <img src="@/assets/biglogo.png" alt="흠으로 이동" @click="goHome">
        </div>
        <div class="login-content">
            <div class="login-form" @keyup.enter="login">
                <input type="email" class="login-email" placeholder="이메일" v-model="email">
                <input type="password" class="login-password" placeholder="비밀번호" v-model="password">
                <button @click="login" :disabled="!isFormValid">로그인</button>

            </div>
            <div class="go-signup-button" @click="goSignup">
                <img src="@/assets/signup.png" alt="">
                <span>회원가입</span>
            </div>
        </div>

    </div>
</template>

<style scoped>
.login-wrap {
    width: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
}

.login-title {
    display: flex;
    flex-direction: column;
    justify-content: center;
    flex: 0 0 auto;
    margin-bottom: 20px;
}

.login-title img {
    width: 100%;
    max-width: 450px; /* 최대 크기 제한 */
    height: auto; /* 높이 자동 조절 */
    cursor: pointer;
}

.login-content {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    width: 100%;
}

.login-form {
    width: 100%;
    max-width: 450px;
    display: flex;
    flex-direction: column;
    align-items: center;
}
.login-form button {
    cursor: pointer;
}

.login-email,
.login-password {
    width: 100%;
    height: 60px;
    border: 1px solid #C5CCD2;
    padding-left: 50px;
    font-family: 'Pretendard Variable', sans-serif;
    font-size: 16px;
    background-repeat: no-repeat;
    background-position: 20px center;
}

.login-email {
    border-radius: 10px 10px 0 0;
    background-image: url("@/assets/email.png");
}

.login-password {
    border-top: 0;
    background-image: url("@/assets/password.png");
}

.login-email:focus{
    outline: none;
    border: 3px solid #D9D9D9;
}
.login-password:focus {
    outline: none;
    border: 3px solid #D9D9D9;
    border-top: 2px solid #D9D9D9;
}

.login-form button {
    background-color: #000000;
    width: 100%;
    height: 50px;
    color: #FFFFFF;
    font-family: 'Pretendard Variable', sans-serif;
    font-size: 16px;
    border-radius: 10px;
    margin: 25px 0;
    border: 1px solid #000000;
}

.login-form button:disabled {
    background-color: #D9D9D9;
    border: 1px solid #D9D9D9;
    cursor: not-allowed;
}

.go-signup-button {
    cursor: pointer;
    display: flex;
    justify-content: center;
    margin-top: 20px;
}

.go-signup-button span {
    font-family: 'Pretendard Variable', sans-serif;
    font-size: 15px;
    color: #888888;
    margin-left: 5px;
    line-height: 16px;
}

.login-email::placeholder, .login-password::placeholder {
    font-size: 15px;
    color: #C5CCD2;
    font-family: 'Pretendard Variable', sans-serif;
}

.login-password {
    width: 450px;
    height: 60px;
    border: 1px solid #C5CCD2;
    border-top: 0;
    padding-left: 50px;
    font-family: 'Pretendard Variable', sans-serif;
    font-size: 16px;
    background-image: url("@/assets/password.png");
    background-repeat: no-repeat;
    background-position: 20px center;
}

/* 반응형 미디어 쿼리 */
@media (max-width: 768px) {
    .login-email,
    .login-password {
        height: 50px;
        font-size: 14px;
    }

    .login-form button {
        height: 45px;
        font-size: 14px;
    }

    .login-title img {
        height: 350px;
    }
}

@media (max-width: 480px) {
    .login-email,
    .login-password {
        height: 45px;
        font-size: 12px;
        padding-left: 40px;
    }

    .login-form button {
        height: 40px;
        font-size: 14px;
    }

    .login-title img {
        height: 250px;
    }
}

</style>
