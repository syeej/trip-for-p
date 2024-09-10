<script setup>
import { ref } from 'vue';
import UpdateInfo from '@/components/UpdateInfo.vue';
import MyCourses from '@/components/MyCourses.vue';
import MyReviewPosts from '@/components/MyReviewPosts.vue';
import MyFreePosts from '@/components/MyFreePosts.vue';
import MyReviewComments from '@/components/MyReviewComments.vue';
import MyFreeComments from '@/components/MyFreeComments.vue';
import DeleteAccount from '@/components/DeleteAccount.vue';
import LikedPlans from "@/components/LikedPlans.vue";

const currentPage = ref('updateInfo');
const subPage = ref(null);

const navigate = (page) => {
  currentPage.value = page;
  if (page === 'myPosts') {
    subPage.value = 'myReviewPosts';
  } else if (page === 'myComments') {
    subPage.value = 'myReviewComments';
  } else {
    subPage.value = null;
  }
};

const navigateSubPage = (page) => {
  subPage.value = page;
};

const isActive = (page) => {
  return currentPage.value === page;
};

const isSubActive = (page) => {
  return subPage.value === page;
};
</script>

<template>
  <div class="page-container">
    <!-- 네비게이션 바 -->
    <nav class="sidebar">
      <button @click="navigate('updateInfo')" :class="{'nav-button': true, 'active': isActive('updateInfo')}">회원정보 수정</button>
      <button @click="navigate('myCourses')" :class="{'nav-button': true, 'active': isActive('myCourses')}">내 코스</button>
      <div>
        <button @click="navigate('myPosts')" :class="{'nav-button': true, 'active': isActive('myPosts')}">내 게시글</button>
        <div v-if="currentPage === 'myPosts'" class="sub-menu">
          <button @click="navigateSubPage('myReviewPosts')" :class="{'nav-button': true, 'active': isSubActive('myReviewPosts')}"><img src="@/assets/dot.png" alt="점" class="dot-icon" />리뷰 게시판</button>
          <button @click="navigateSubPage('myFreePosts')" :class="{'nav-button': true, 'active': isSubActive('myFreePosts')}"><img src="@/assets/dot.png" alt="점" class="dot-icon" />자유 게시판</button>
        </div>
      </div>
      <div>
        <button @click="navigate('myComments')" :class="{'nav-button': true, 'active': isActive('myComments')}">내 댓글</button>
        <div v-if="currentPage === 'myComments'" class="sub-menu">
          <button @click="navigateSubPage('myReviewComments')" :class="{'nav-button': true, 'active': isSubActive('myReviewComments')}"><img src="@/assets/dot.png" alt="점" class="dot-icon" />리뷰 게시판</button>
          <button @click="navigateSubPage('myFreeComments')" :class="{'nav-button': true, 'active': isSubActive('myFreeComments')}"><img src="@/assets/dot.png" alt="점" class="dot-icon" />자유 게시판</button>
        </div>
      </div>
      <button @click="navigate('likedPosts')" :class="{'nav-button': true, 'active': isActive('likedPosts')}">좋아요한 여행코스</button>
      <!-- 회원탈퇴 버튼 -->
      <button @click="navigate('deleteAccount')" :class="{'nav-button delete-account-button': true, 'active': isActive('deleteAccount')}">회원탈퇴</button>
    </nav>
    <!-- 컨텐츠 영역 -->
    <div class="content">
      <UpdateInfo v-if="currentPage === 'updateInfo'" />
      <MyCourses v-else-if="currentPage === 'myCourses'" />
      <MyReviewPosts v-if="currentPage === 'myPosts' && subPage === 'myReviewPosts'" />
      <MyFreePosts v-if="currentPage === 'myPosts' && subPage === 'myFreePosts'" />
      <MyReviewComments v-if="currentPage === 'myComments' && subPage === 'myReviewComments'" />
      <MyFreeComments v-if="currentPage === 'myComments' && subPage === 'myFreeComments'" />
      <LikedPlans v-if="currentPage === 'likedPosts'" />
      <DeleteAccount v-else-if="currentPage === 'deleteAccount'" />
    </div>
  </div>
</template>

<style scoped>
.page-container {
  width: 100%; /* 새로 추가 */
  display: flex;
  margin-top: 60px;
}
.content{
  width: 100%;
}
.sidebar {
  width: 200px;
  background-color: #f8f8f8;
  color: #000000;
  display: flex;
  flex-direction: column;
  border-radius: 5px;

  padding-top: 230px;
  position: fixed;
  top: 0px;
  left: 0;
  height: calc(100vh);
  box-shadow: 2px 0 5px rgba(0, 0, 0, 0.1);
  overflow-y: auto;
  z-index:0;
}

.nav-button {
  margin-top:5px;
  padding: 12px;
  border: none;
  background-color: #f8f8f8;
  color: #000000;
  font-size: 15px;
  cursor: pointer;
  text-decoration: none;
  transition: background-color 0.3s ease, transform 0.3s ease;
  text-align: left;
}

.nav-button.active {
  text-decoration: underline;
}

.nav-button:active {
  transform: scale(0.98);
}

.sub-menu {
  display: flex;
  flex-direction: column;
  padding-left: 22px;
}

.sub-menu .nav-button {
  padding: 4px;
  font-size: 12px;
  color: #555555;
}

.dot-icon{
  width: 10px;
  height: 10px;
  margin-right: 1px;
}

.delete-account-button {
  font-size: 12px;
  margin-top: 120px;
  padding: 8px;
  color: #656565;
}
</style>