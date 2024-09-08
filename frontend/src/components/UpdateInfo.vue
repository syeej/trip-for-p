
<script setup>
import { computed, onMounted, ref, watch } from 'vue';
import axios from 'axios';
import store from '@/store/index.js';
import { verifyNickNameAPI} from '@/api';

const password = ref('');
const passwordCheck = ref('');
const nickname = ref('');
const userInfo = ref(null);
const errorMessage = ref('');

// 닉네임 중복 검사 관련 상태
const isNicknameVerified = ref(false);
const nicknameVerificationMessage = ref('');
const isNicknameVerificationFailed = ref(false);

// 비밀번호 검증 상태
const isPasswordValid = ref(true);
const passwordVerificationMessage = ref('');

// 사용자 정보 가져오기
const getUserInfoAPI = async () => {
  const accessToken = store.getters.getAccessToken;

  if (!store.getters.isAccessTokenValid) {
    throw new Error('Invalid or expired token');
  }

  try {
    const response = await axios.get('/api/users/me', {
      headers: {
        access: `${accessToken}`,
      },
    });
    console.log(response); // 응답을 확인
    return response.data;
  } catch (error) {
    console.error('API 호출 에러:', error); // 에러 내용 확인
    throw new Error('Error fetching user info');
  }
};

onMounted(async () => {
  try {
    const data = await getUserInfoAPI();
    userInfo.value = data;
    nickname.value = data.nickname; // 사용자 정보로 초기화
  } catch (error) {
    errorMessage.value = error.message;
  }
});

// 닉네임 중복 검사
const verifyNickname = async () => {
  try {
    const response = await verifyNickNameAPI(nickname.value);
    if (response.status !== 'success') {
      isNicknameVerified.value = false;
      nicknameVerificationMessage.value = '이미 사용 중인 닉네임입니다.';
      isNicknameVerificationFailed.value = true;
    } else {
      isNicknameVerified.value = true;
      nicknameVerificationMessage.value = '사용 가능한 닉네임입니다.';
      isNicknameVerificationFailed.value = false;
    }
  } catch (error) {
    isNicknameVerified.value = false;
    nicknameVerificationMessage.value = error.message || '닉네임 중복 검사 중 오류가 발생했습니다.';
    isNicknameVerificationFailed.value = true;
  }
};

watch(nickname, (newValue, oldValue) => {
  if (newValue === '') {
    nicknameVerificationMessage.value = ''; // 닉네임이 비었을 때 메시지를 초기화
    isNicknameVerified.value = false;
    isNicknameVerificationFailed.value = false;
  } else if (newValue !== oldValue) {
    isNicknameVerified.value = false;
    isNicknameVerificationFailed.value = false;
  }
});

// 비밀번호 검증
const validatePassword = () => {
  const regex = /^(?=.*[a-zA-Z])(?=.*[0-9]).{8,16}$/;
  return regex.test(password.value);
};

watch([password, passwordCheck], () => {
  if (password.value || passwordCheck.value) {
    if (!validatePassword()) {
      isPasswordValid.value = false;
      passwordVerificationMessage.value = '영문 대/소문자와 숫자 조합으로 8~16자리로 입력하세요.';
    } else if (password.value !== passwordCheck.value) {
      isPasswordValid.value = false;
      passwordVerificationMessage.value = '비밀번호가 일치하지 않습니다.';
    } else {
      isPasswordValid.value = true;
      passwordVerificationMessage.value = '';
    }
  }
});

const isFormValid = computed(() => {
  return (password.value || passwordCheck.value) && passwordCheck.value && nickname.value && isNicknameVerified.value && isPasswordValid.value;
});

const updateUserInfo = async () => {
  try {
    if (!nickname.value) {
      alert('닉네임을 입력하세요.');
      return;
    }
    if (!isNicknameVerified.value) {
      alert('닉네임 중복 확인이 필요합니다.');
      return;
    }
    if (password.value && !validatePassword()) {
      alert('영문 대/소문자와 숫자 조합으로 8~16자리로 입력하세요.');
      return;
    }
    if (password.value && password.value !== passwordCheck.value) {
      alert('비밀번호가 일치하지 않습니다.');
      return;
    }

    const updateUserRequest = {
      password: passwordCheck.value,
      nickname: nickname.value,
    };

    // 사용자 정보 업데이트 API 호출
    await axios.patch('/api/users/me', updateUserRequest, {
      headers: {
        access: `${store.getters.getAccessToken}`,
      },
    });
    alert('회원 정보가 수정되었습니다.');
  } catch (error) {
    alert(error.message || '회원 정보 수정 중 오류가 발생했습니다.');
  }
  window.location.reload();
};
</script>

<template>
  <div class="update-info">
    <div class="update-content">
      <div class="update-form">
        <!-- 닉네임 입력 -->
        <div class="nickname-verification-container">
          <input type="text" class="update-nickname" placeholder="닉네임" v-model="nickname">
          <button @click="verifyNickname" :disabled="!nickname" class="verification-button">중복확인</button>
        </div>
        <!-- 닉네임 중복 검사 메시지 -->
        <p v-if="nicknameVerificationMessage" :class="[
          'verification-message',
          { 'verification-success': isNicknameVerified, 'verification-failed': isNicknameVerificationFailed }
        ]">
          {{ nicknameVerificationMessage }}
        </p>

        <!-- 비밀번호 입력 -->
        <input type="password" class="update-password" placeholder="변경할 비밀번호" v-model="password">
        <input type="password" class="update-password-check" placeholder="비밀번호 확인" v-model="passwordCheck">

        <!-- 비밀번호 검증 메시지 -->
        <p v-if="passwordVerificationMessage" :class="[
          'verification-message',
          { 'verification-success': isPasswordValid, 'verification-failed': !isPasswordValid }
        ]">
          {{ passwordVerificationMessage }}
        </p>

        <button @click="updateUserInfo" :disabled="!isFormValid">수정하기</button>
      </div>
    </div>
  </div>
</template>


<style scoped>
.update-content {
  width: 100%;
}

.update-form {
  width: 100%;
  max-width: 300px;
}

/* 입력 필드 공통 스타일 */
.update-password,
.update-password-check,
.update-nickname {
  width: 100%;
  height: 45px;
  border: 1px solid #C5CCD2;
  border-radius: 10px;
  padding-left: 50px;
  font-family: 'Pretendard Variable', sans-serif;
  font-size: 12px;
  background-repeat: no-repeat;
  background-position: 20px center;
  margin-top: 10px;
}

.update-password,
.update-password-check {
  background-image: url("@/assets/password.png");
}

.update-nickname {
  background-image: url("@/assets/nickname.png");
}

/* 포커스 스타일 */
.update-password:focus,
.update-password-check:focus,
.update-nickname:focus {
  outline: none;
  border: 2px solid #007bff;
  box-shadow: 0 0 5px rgba(0, 123, 255, 0.5);
}

/* 버튼 스타일 */
.update-form button {
  background-color: #000000;
  width: 70px;
  height: 35px;
  color: #FFFFFF;
  font-family: 'Pretendard Variable', sans-serif;
  font-size: 10px;
  border-radius: 10px;
  margin: 10px 0;
  border: 1px solid #000000;
  cursor: pointer;
  display: inline-block;
  vertical-align: middle;
}

.update-form button:disabled {
  background-color: #D9D9D9;
  border: 1px solid #D9D9D9;
  cursor: not-allowed;
}

/* 닉네임 중복 검사 컨테이너 */
.nickname-verification-container {
  display: flex;
  width: 100%;
  align-items: center;
  gap: 10px;
}

/* 인증 메시지 스타일 */
.verification-message {
  font-size: 10px;
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
  .update-password,
  .update-password-check,
  .update-nickname {
    height: 50px;
    font-size: 14px;
  }
  .update-form button {
    height: 45px;
    font-size: 14px;
  }
}

@media (max-width: 480px) {
  .update-password,
  .update-password-check,
  .update-nickname {
    height: 45px;
    font-size: 12px;
    padding-left: 40px;
  }
  .update-form button {
    height: 40px;
    font-size: 12px;
  }
}

</style>