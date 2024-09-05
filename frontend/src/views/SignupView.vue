<script setup>
import router from "@/router";
import {computed, onMounted, ref, watch} from "vue";
import {createUserAPI, sendVerificationEmailAPI, verifyEmailAPI, verifyNickNameAPI} from "@/api";

const email = ref("");
const password = ref("");
const passwordCheck = ref("");
const nickname = ref("");
//이메일 인증코드
const verificationCode = ref("");
//이메일 인증여부
const isEmailVerified = ref(false);
//인증코드 입력란
const showVerificationInput = ref(false);
//인증 결과
const verificationMessage = ref("");
const isVerificationFailed = ref(false);
// 닉네임 중복 검사 관련 상태
const isNicknameVerified = ref(false);
const nicknameVerificationMessage = ref("");
const isNicknameVerificationFailed = ref(false);

//이메일 인증코드 전송
const sendVerificationEmail = async () => {
  try {
    await sendVerificationEmailAPI({ email: email.value });
    alert("인증 이메일이 전송되었습니다.");
    showVerificationInput.value = true;
  } catch (error) {
    alert(error.message);
  }
};

//인증코드 검증
const verifyEmail = async () => {
  try {
    await verifyEmailAPI({ email: email.value, code: verificationCode.value });
    isEmailVerified.value = true;
    verificationMessage.value = "이메일이 성공적으로 인증되었습니다.";
    isVerificationFailed.value = false;
  } catch (error) {
    isEmailVerified.value = false;
    verificationMessage.value = error.message || "인증 코드가 올바르지 않습니다.";
    isVerificationFailed.value = true;
  }
};
// 닉네임 중복 검사
const verifyNickname = async () => {
  try {
    const isDuplicated = await verifyNickNameAPI(nickname.value);
    if (isDuplicated.status !== 'success') {
      isNicknameVerified.value = false;
      nicknameVerificationMessage.value = "이미 사용 중인 닉네임입니다.";
      isNicknameVerificationFailed.value = true;
    } else {
      isNicknameVerified.value = true;
      nicknameVerificationMessage.value = "사용 가능한 닉네임입니다.";
      isNicknameVerificationFailed.value = false;
    }
  } catch (error) {
    isNicknameVerified.value = false;
    nicknameVerificationMessage.value = error.message || "닉네임 중복 검사 중 오류가 발생했습니다.";
    isNicknameVerificationFailed.value = true;
  }
};

// 닉네임 입력 필드에 대한 감시
watch(nickname, (newValue, oldValue) => {
  if (newValue !== oldValue) {
    isNicknameVerified.value = false;
    nicknameVerificationMessage.value = "닉네임 중복 확인이 필요합니다.";
    isNicknameVerificationFailed.value = false;
  }
});

// 버튼 활성화 여부를 computed로 계산
const isFormValid = computed(() => {
    return email.value && password.value && passwordCheck.value && nickname.value && isEmailVerified.value && isNicknameVerified.value;
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
        if (!isEmailVerified.value) {
          alert("이메일 인증이 필요합니다.");
          return;
        }
        if (!isNicknameVerified.value) {
          alert("닉네임 중복 확인이 필요합니다.");
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
        console.log("회원가입", error);
        if (error.status === 409 || error.status === 403) {
            alert(error.message);
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
        <!-- 이메일 입력 -->
        <div class="email-verification-container">
          <input type="email" class="signup-email" placeholder="이메일" v-model="email">
          <button @click="sendVerificationEmail" :disabled="!email" class="verification-button">인증</button>
        </div>
        <!-- 이메일 인증코드 확인(검증) -->
        <div v-if="showVerificationInput" class="verification-code-container">
          <input type="text" class="signup-verification" placeholder="인증 코드" v-model="verificationCode">
          <button @click="verifyEmail" :disabled="!verificationCode" class="verify-button">확인</button>
        </div>
        <!-- 인증 메시지 -->
        <p v-if="verificationMessage" :class="[
              'verification-message',
              { 'verification-success': isEmailVerified, 'verification-failed': isVerificationFailed }
            ]">
          {{ verificationMessage }}
        </p>
        <input type="password" class="signup-password" placeholder="비밀번호" v-model="password">
        <input type="password" class="signup-password-check" placeholder="비밀번호 확인" v-model="passwordCheck">
<!--        <input type="text" class="signup-nickname" placeholder="닉네임" v-model="nickname">-->
        <!-- 닉네임 입력 및 중복 검사 -->
        <div class="nickname-verification-container">
          <input type="text" class="signup-nickname" placeholder="닉네임" v-model="nickname">
          <button @click="verifyNickname" :disabled="!nickname" class="verification-button">중복 확인</button>
        </div>
        <!-- 닉네임 중복 검사 메시지 -->
        <p v-if="nicknameVerificationMessage" :class="[
        'verification-message',
        { 'verification-success': isNicknameVerified, 'verification-failed': isNicknameVerificationFailed }
      ]">
          {{ nicknameVerificationMessage }}
        </p>
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
/* 공통 스타일 */
.signup-wrap {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
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

/* 입력 필드 공통 스타일 */
.signup-email,
.signup-password,
.signup-password-check,
.signup-nickname,
.signup-verification {
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
.signup-email {
  background-image: url("@/assets/email.png");
  margin-top: 0;
}

.signup-password,
.signup-password-check {
  background-image: url("@/assets/password.png");
}

.signup-nickname {
  background-image: url("@/assets/nickname.png");
}

.signup-verification {
  background-image: url("@/assets/email.png");
}

/* 포커스 스타일 */
.signup-email:focus,
.signup-password:focus,
.signup-password-check:focus,
.signup-nickname:focus,
.signup-verification:focus {
  outline: none;
  border: 2px solid #007bff;
  box-shadow: 0 0 5px rgba(0, 123, 255, 0.5);
}

/* 버튼 스타일 */
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
  cursor: pointer;
}

.signup-form button:disabled {
  background-color: #D9D9D9;
  border: 1px solid #D9D9D9;
  cursor: not-allowed;
}

/* 이메일 인증 컨테이너 */
.email-verification-container,
.verification-code-container {
  display: flex;
  width: 100%;
  max-width: 450px;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}
/* 이메일 인증 전송, 확인 버튼 */
.verification-button,
.verify-button {
  width: 80px;
  height: 60px;
  border-radius: 10px;
  margin: 0;
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
.signup-email::placeholder,
.signup-password::placeholder,
.signup-password-check::placeholder,
.signup-nickname::placeholder,
.signup-verification::placeholder {
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
  margin-bottom: 10px;
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
  .signup-email,
  .signup-password,
  .signup-password-check,
  .signup-nickname,
  .signup-verification {
    height: 50px;
    font-size: 14px;
  }
  .verification-button,
  .verify-button,
  .signup-form button {
    height: 45px;
    font-size: 14px;
  }
}

@media (max-width: 480px) {
  .signup-email,
  .signup-password,
  .signup-password-check,
  .signup-nickname,
  .signup-verification {
    height: 45px;
    font-size: 12px;
    padding-left: 40px;
  }
  .verification-button,
  .verify-button,
  .signup-form button {
    height: 40px;
    font-size: 12px;
  }
}
</style>