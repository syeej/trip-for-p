<script setup>
import router from "@/router";
import {computed, onMounted, ref} from "vue";
import {createUserAPI} from "@/api";

const email = ref("");
const password = ref("");
const passwordCheck = ref("");
const nickname = ref("");

// 버튼 활성화 여부를 computed로 계산
const isFormValid = computed(() => {
    return email.value && password.value && passwordCheck.value && nickname.value;
});

const goLogin = function () {
    router.push("/login");
}

const goHome = function () {
    router.push("/");
}

const signup = async function () {
    try {
        const createUserRequest = {
            email: email.value,
            password: password.value,
            passwordCheck: passwordCheck.value,
            nickname: nickname.value
        }
        if (!email.value) {
            document.querySelector('.signup-email').focus();
            return;
        }
        if (!password.value) {
            document.querySelector('.signup-password').focus();
            return;
        }
        if (!passwordCheck.value) {
            document.querySelector('.signup-password-check').focus();
            return;
        }
        if (!nickname.value) {
            document.querySelector('.signup-nickname').focus();
            return;
        }
        if (!validateEmail()) {
            alert("올바르지 않은 이메일 형식입니다.")
            return;
        } else if (!validatePassword()) {
            alert("영문 대/소문자 + 숫자 조합의 8~16자리로만 가능합니다.");
            return;
        } else if (!isSamePassword()) {
            alert("비밀번호가 다릅니다.")
            return;
        }
        await createUserAPI(createUserRequest);
        alert("가입이 완료되었습니다.");
        await router.replace('/login');
    } catch (error) {
        console.log(error.response.data);
        if (error.response.data.status == 409) {
            alert(error.response.data.message);
        }
    }
}

const isSamePassword = function () {
    return password.value === passwordCheck.value;
}

const validateEmail = function () {
    const regex = /^[A-Za-z0-9_\\.\\-]+@[A-Za-z0-9\\-]+\.[A-Za-z0-9\\-]+/;
    return regex.test(email.value);
}

const validatePassword = function () {
    const regex = /^(?=.*[a-zA-z])(?=.*[0-9]).{8,16}$/;
    return regex.test(password.value)
}

onMounted(() => {
    const inputs = document.querySelectorAll(
        '.signup-email, .signup-password, .signup-password-check, .signup-nickname');

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
})
</script>

<template>
    <div class="signup-wrap">
        <div class="signup-title">
            <img src="@/assets/biglogo.png" alt="흠으로 이동" @click="goHome">
        </div>
        <div class="signup-content">
            <div class="signup-form" @keyup.enter="signup">
                <input type="email" class="signup-email" placeholder="이메일" v-model="email">
                <input type="password" class="signup-password" placeholder="비밀번호" v-model="password">
                <input type="password" class="signup-password-check" placeholder="비밀번호 확인"
                       v-model="passwordCheck">
                <input type="text" class="signup-nickname" placeholder="닉네임" v-model="nickname">

                <button @click="signup" :disabled="!isFormValid">가입하기</button>
            </div>
            <div class="go-login-button" @click="goLogin">
                <img src="@/assets/signup.png" alt="">
                <span>이미 계정이 있으신가요?</span>
            </div>
        </div>

    </div>
</template>

<style scoped>
.signup-wrap {
    width: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
}

.signup-title {
    display: flex;
    flex-direction: column;
    justify-content: center;
    flex: 0 0 auto;
    margin-bottom: 20px;
}

.signup-title img {
    width: 100%;
    max-width: 450px; /* 최대 크기 제한 */
    height: auto; /* 높이 자동 조절 */
    cursor: pointer;
}

.signup-content {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    width: 100%;
}

.signup-form {
    width: 100%;
    max-width: 450px;
    display: flex;
    flex-direction: column;
    align-items: center;
}
.signup-form button {
    cursor: pointer;
}

.signup-email,
.signup-password,
.signup-password-check,
.signup-nickname {
    width: 100%;
    height: 60px;
    border: 1px solid #C5CCD2;
    padding-left: 50px;
    font-family: 'Pretendard Variable', sans-serif;
    font-size: 16px;
    background-repeat: no-repeat;
    background-position: 20px center;
}

.signup-email {
    border-radius: 10px 10px 0 0;
    background-image: url("@/assets/email.png");
}

.signup-password,
.signup-password-check {
    border-top: 0;
    background-image: url("@/assets/password.png");
}

.signup-nickname {
    border-top: 0;
    border-radius: 0 0 10px 10px;
    background-image: url("@/assets/nickname.png");
}

.signup-email:focus{
    outline: none;
    border: 3px solid #D9D9D9;
}
.signup-password:focus, .signup-password-check:focus, .signup-nickname:focus {
    outline: none;
    border: 3px solid #D9D9D9;
    border-top: 2px solid #D9D9D9;
}

.signup-form button {
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

.signup-form button:disabled {
    background-color: #D9D9D9;
    border: 1px solid #D9D9D9;
    cursor: not-allowed;
}

.go-login-button {
    cursor: pointer;
    display: flex;
    justify-content: center;
    margin-top: 20px;
}

.go-login-button span {
    font-family: 'Pretendard Variable', sans-serif;
    font-size: 15px;
    color: #888888;
    margin-left: 5px;
    line-height: 16px;
}

.signup-email::placeholder, .signup-password::placeholder, .signup-password-check::placeholder, .signup-nickname::placeholder {
    font-size: 15px;
    color: #C5CCD2;
    font-family: 'Pretendard Variable', sans-serif;
}

.signup-password, .signup-password-check {
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
    .signup-email,
    .signup-password,
    .signup-password-check,
    .signup-nickname {
        height: 50px;
        font-size: 14px;
    }

    .signup-form button {
        height: 45px;
        font-size: 14px;
    }

    .signup-title img {
        height: 350px;
    }
}

@media (max-width: 480px) {
    .signup-email,
    .signup-password,
    .signup-password-check,
    .signup-nickname {
        height: 45px;
        font-size: 12px;
        padding-left: 40px;
    }

    .signup-form button {
        height: 40px;
        font-size: 14px;
    }

    .signup-title img {
        height: 250px;
    }
}

</style>
