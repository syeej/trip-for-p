<script setup>
import router from "@/router";
import {computed, onMounted, ref} from "vue";
import {sendPasswordResetEmailAPI, verifyEmailAPI, resetPasswordAPI} from "@/api";

const email = ref("");
const password = ref("");
const passwordCheck = ref("");
//이메일 인증코드
const verificationCode = ref("");
//이메일 인증여부
const isEmailVerified = ref(false);
//인증코드 입력란
const showVerificationInput = ref(false);
//인증 결과
const verificationMessage = ref("");
const isVerificationFailed = ref(false);

//이메일 인증코드 전송
const sendPasswordResetEmail = async () => {
  try {
    await sendPasswordResetEmailAPI({ email: email.value });
    alert("인증 이메일이 전송되었습니다.");
    showVerificationInput.value = true;
  } catch (error) {
    alert(error.message);
  }
};

//인증코드 검증
const verifyEmail = async () => {
  try {
    await verifyEmailAPI({ email: email.value, code: verificationCode.value.trim() });
    isEmailVerified.value = true;
    verificationMessage.value = "이메일이 성공적으로 인증되었습니다.";
    isVerificationFailed.value = false;
  } catch (error) {
    isEmailVerified.value = false;
    verificationMessage.value = error.message || "인증 코드가 올바르지 않습니다.";
    isVerificationFailed.value = true;
  }
};

// 버튼 활성화 여부를 computed로 계산
const isFormValid = computed(() => {
    return email.value && password.value && passwordCheck.value && isEmailVerified.value;
});

const goLogin = function () {
    router.push("/login");
}

const goHome = function () {
    router.push("/");
}

const resetpw = async function () {
    try {
        const findPasswordRequest = {
            email: email.value,
            newPassword: password.value,
        }
        if (!email.value) {
            document.querySelector('.resetpw-email').focus();
            return;
        }
        if (!password.value) {
            document.querySelector('.resetpw-password').focus();
            return;
        }
        if (!passwordCheck.value) {
            document.querySelector('.resetpw-password-check').focus();
            return;
        }
        if (!isEmailVerified.value) {
          alert("이메일 인증이 필요합니다.");
          return;
        }
        if (!validateEmail()) {
            alert("올바르지 않은 이메일 형식입니다.")
            return;
        } else if (!validatePassword()) {
            alert("영문 대/소문자 + 숫자 + 특수문자 조합의 8~16자리로만 가능합니다.");
            return;
        } else if (!isSamePassword()) {
            alert("비밀번호가 다릅니다.")
            return;
        }
        await resetPasswordAPI(findPasswordRequest);
        alert("비밀번호가 재설정되었습니다.");
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
    const regex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,16}$/;
    return regex.test(password.value)
}

onMounted(() => {
    const inputs = document.querySelectorAll(
        '.resetpw-email, .resetpw-password, .resetpw-password-check, .resetpw-nickname');

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
  <div class="resetpw-wrap">
    <div class="resetpw-title">
      <img src="@/assets/biglogo.png" alt="흠으로 이동" @click="goHome">
    </div>
    <div class="resetpw-content">
      <div class="resetpw-form" @keyup.enter="resetpw">
        <!-- 이메일 입력 -->
        <div class="email-verification-container">
          <input type="email" class="resetpw-email" placeholder="이메일" v-model="email">
          <button @click="sendPasswordResetEmail" :disabled="!email" class="verification-button">인증</button>
        </div>
        <!-- 이메일 인증코드 확인(검증) -->
        <div v-if="showVerificationInput" class="verification-code-container">
          <input type="text" class="resetpw-verification" placeholder="인증 코드" v-model="verificationCode">
          <button @click="verifyEmail" :disabled="!verificationCode" class="verify-button">확인</button>
        </div>
        <!-- 인증 메시지 -->
        <p v-if="verificationMessage" :class="[
              'verification-message',
              { 'verification-success': isEmailVerified, 'verification-failed': isVerificationFailed }
            ]">
          {{ verificationMessage }}
        </p>
        <input type="password" v-if="isEmailVerified" class="resetpw-password" placeholder="비밀번호 (영문 대/소문자, 숫자, 특수문자 조합 8~16자)" v-model="password">
        <input type="password" v-if="isEmailVerified" class="resetpw-password-check" placeholder="비밀번호 확인" v-model="passwordCheck">
        <button @click="resetpw" v-if="isEmailVerified" :disabled="!isFormValid" class="resetpw-button">비밀번호 재설정</button>
      </div>

      <div class="go-login-button" @click="goLogin">
        <img src="@/assets/signup.png" alt="">
        <span>로그인으로 이동</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 공통 스타일 */
.resetpw-wrap {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.resetpw-title img {
    width: 100%;
    max-width: 450px; /* 최대 크기 제한 */
    height: auto; /* 높이 자동 조절 */
    cursor: pointer;
}

.resetpw-content {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  width: 100%;
    margin-bottom: 100px;
}

.resetpw-form {
  width: 100%;
  max-width: 450px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* 입력 필드 공통 스타일 */
.resetpw-email,
.resetpw-password,
.resetpw-password-check,
.resetpw-verification {
  width: 100%;
  height: 60px;
  border: 1px solid #C5CCD2;
  border-radius: 10px;
  padding-left: 50px;
  font-family: 'Pretendard Variable', sans-serif;
  font-size: 16px;
  background-repeat: no-repeat;
  background-position: 20px center;
  margin-top: 10px;
}

/* 특정 입력 필드 스타일 */
.resetpw-email {
  background-image: url("@/assets/email.png");
  margin-top: 0;
}
.resetpw-password,
.resetpw-password-check {
  background-image: url("@/assets/password.png");
}
.resetpw-verification {
  background-image: url("@/assets/email.png");
}

/* 포커스 스타일 */
.resetpw-email:focus,
.resetpw-password:focus,
.resetpw-password-check:focus,
.resetpw-verification:focus {
  outline: none;
    border: 2px solid #333333;
    box-shadow: 0 0 5px rgba(51, 51, 51, 0.5);
}

/* 버튼 스타일 */
.resetpw-form button {
  background-color: #000000;
  width: 100%;
  height: 50px;
  color: #FFFFFF;
  font-family: 'Pretendard Variable', sans-serif;
  font-size: 16px;
  border-radius: 10px;
  border: 1px solid #000000;
  cursor: pointer;
}

.resetpw-form button:disabled {
  background-color: #D9D9D9;
  border: 1px solid #D9D9D9;
  cursor: not-allowed;
}

/* 이메일 인증 컨테이너 */
.email-verification-container,
.verification-code-container{
  display: flex;
  width: 100%;
  max-width: 450px;
  align-items: center;
  gap: 10px;
  margin-top: 10px;
}
.resetpw-email, .resetpw-verification {
    flex: 7;
}
.verification-button, .verify-button {
    flex: 3;
}
.resetpw-verification {
    margin-top: 0;
}

/* 이메일 인증 전송, 확인 버튼 */
.verification-button,
.verify-button {
  width: 80px;
  height: 60px;
  border-radius: 10px;
  margin: 0;
}

.resetpw-button {
    margin: 25px 0;
}

/* 로그인 버튼 */
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

/* 플레이스홀더 스타일 */
.resetpw-email::placeholder,
.resetpw-password::placeholder,
.resetpw-password-check::placeholder,
.resetpw-verification::placeholder {
  font-size: 15px;
  color: #C5CCD2;
  font-family: 'Pretendard Variable', sans-serif;
}
/* 닉네임 중복 검사 컨테이너 */
.nickname-verification-container {
  display: flex;
  width: 100%;
  max-width: 450px;
  align-items: center;
  gap: 10px;
}
/* 인증 메시지 스타일 */
.verification-message {
  font-size: 14px;
  margin-top: 5px;
  text-align: left;
  width: 100%;
}
/* 인증 메시지 결과에 따른 색상 */
.verification-success {
  color: #28a745;
}

.verification-failed {
  color: #dc3545;
}

/* 반응형 스타일 */
@media (max-width: 768px) {
  .resetpw-email,
  .resetpw-password,
  .resetpw-password-check,
  .resetpw-verification {
    height: 50px;
    font-size: 14px;
  }
  .verification-button,
  .verify-button,
  .resetpw-form button {
    height: 45px;
    font-size: 14px;
  }
}

@media (max-width: 480px) {
  .resetpw-email,
  .resetpw-password,
  .resetpw-password-check,
  .resetpw-verification {
    height: 45px;
    font-size: 12px;
    padding-left: 40px;
  }
  .verification-button,
  .verify-button,
  .resetpw-form button {
    height: 40px;
    font-size: 12px;
  }
}
</style>
