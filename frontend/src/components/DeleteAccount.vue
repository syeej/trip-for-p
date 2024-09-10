<script setup>
import { ref } from 'vue';
import { withdrawalUserAPI } from '@/api';
import { useRouter } from 'vue-router';
import store from '@/store';

const router = useRouter();
const isLoading = ref(false);
const error = ref(null);

const handleDeleteAccount = async () => {
  if (!confirm('정말로 회원 탈퇴하시겠습니까?\n이 작업은 되돌릴 수 없습니다.')) {
    return;
  }

  isLoading.value = true;
  error.value = null;

  try {
    await withdrawalUserAPI();
    alert('회원 탈퇴가 성공적으로 처리되었습니다.');
    await store.commit('clearData');
    router.push('/');
  } catch (err) {
    console.error('회원 탈퇴 중 오류 발생:', err);
    error.value = '회원 탈퇴 처리 중 오류가 발생했습니다. 나중에 다시 시도해주세요.';
  } finally {
    isLoading.value = false;
  }
};
</script>

<template>
  <div class="delete-account-container">
    <h2>회원 탈퇴</h2>
    <div class="info-box">
      <p>회원 탈퇴 시 아래 사항을 숙지해 주시기 바랍니다:</p>
      <ul>
        <li>모든 개인 정보 및 서비스 이용 기록이 삭제됩니다.</li>
        <li>삭제된 데이터는 복구할 수 없습니다.</li>
        <li>작성한 게시물 및 댓글은 삭제되지 않습니다.</li>
        <li>탈퇴 후 1개월간 재가입이 제한됩니다.</li>
        <li>개인정보 보호를 위해 탈퇴 후 1개월간 관련 정보를 보관합니다.</li>
      </ul>
    </div>
    <p>정말 회원을 탈퇴하시겠습니까? 이 작업은 되돌릴 수 없습니다.</p>
    <button @click="handleDeleteAccount" class="delete-button" :disabled="isLoading">
      {{ isLoading ? '처리 중...' : '탈퇴' }}
    </button>
    <p v-if="error" class="error-message">{{ error }}</p>
  </div>
</template>

<style scoped>
.delete-account-container {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
  text-align: center;
}

.info-box {
  background-color: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 5px;
  padding: 15px;
  margin: 20px 0;
  text-align: left;
}

.info-box ul {
  margin-top: 20px;
  padding-left: 20px;
}

.info-box li {
  margin-bottom: 10px;
}

.delete-button {
  margin-top: 20px;
  background-color: #dc3545;
  color: white;
  border: none;
  padding: 10px 15px;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.delete-button:hover {
  background-color: #c82333;
}

.delete-button:disabled {
  background-color: #6c757d;
  cursor: not-allowed;
}

.error-message {
  color: #dc3545;
  margin-top: 10px;
}
</style>