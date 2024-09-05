<script setup>
const handleDeleteAccount = () => {
  // 여기에 탈퇴 처리 로직을 추가하세요
  alert("회원 탈퇴가 처리되었습니다.");
};
</script>

<template>
    <div>정말 회원을 탈퇴하시겠습니까? </div>
  <div><button @click="handleDeleteAccount" class="delete-button">탈퇴</button></div>
</template>

<style scoped>

.delete-button {
  margin-left:20px;
  background-color: #424242;
  color: white;
  border: none;
  padding: 10px 15px;
  border-radius: 10px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.delete-button:hover {
  background-color: #737373;
}

</style>