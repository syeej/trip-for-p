<script setup>
import { ref, computed , onMounted} from 'vue';
// import store from "@/store";
// import axios from "axios";

const plans = ref([]);
const currentPage = ref(1);
const itemsPerPage = ref(5);
const isLoading = ref(false);

const fetchPlans = async () => {

  // const accessToken = store.getters.getAccessToken;
  //
  // if (!store.getters.isAccessTokenValid) {
  //   throw new Error('Invalid or expired token');
  // }
  // try {
  //   const response = await axios.get('/api/mypage/info', {
  //     headers: {
  //       access: `${accessToken}`,
  //     },
  //   });
  //   console.log(response); // 응답을 확인
  //   return response.data;
  // } catch (error) {
  //   console.error('API 호출 에러:', error); // 에러 내용 확인
  //   throw new Error('Error fetching user info');
  // }
};

// 현재 페이지에 해당하는 데이터를 계산
const paginatedPlans = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage.value;
  const end = start + itemsPerPage.value;
  return plans.value.slice(start, end);
});

// 총 페이지 수 계산
const totalPages = computed(() => {
  return Math.ceil(plans.value.length / itemsPerPage.value);
});

// 페이지 변경 함수
const changePage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page;
  }
};
onMounted(() => {
  fetchPlans();
});
</script>

<template>
  <!-- 테이블 화면 -->
  <div>
    <!-- 로딩 중 메시지 -->
    <div v-if="isLoading">로딩 중...</div>

    <!-- 로딩이 끝났을 때만 콘텐츠 표시 -->
    <div v-else>
      <table v-if="paginatedPlans.length > 0">
        <thead>
        <tr>
          <th class="col-author">작성자</th>
          <th class="col-title">제목</th>
          <th class="col-likes"><img src="@/assets/likes.png" alt="좋아요수" /></th>
          <th class="col-views"><img src="@/assets/viewers.png" alt="조회수" /></th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="plan in paginatedPlans" :key="plan.id">
          <td class="col-author">
            <div class="author-name">{{ plan.writer }}</div>
            <div class="author-date">{{ new Date(plan.createdAt).toLocaleDateString() }}</div>
          </td>
          <td class="col-title">{{ plan.title }}</td>
          <td class="col-likes">{{ plan.likes }}</td>
          <td class="col-views">{{ plan.views }}</td>
        </tr>
        </tbody>
      </table>
      <p v-else>내가 등록한 여행 코스가 없습니다.</p>

      <!-- 페이지네이션 버튼 -->
      <div v-if="totalPages > 1" class="pagination">
        <button @click="changePage(currentPage - 1)" :disabled="currentPage === 1">이전</button>
        <button v-for="page in totalPages" :key="page" @click="changePage(page)" :class="{ active: page === currentPage }">
          {{ page }}
        </button>
        <button @click="changePage(currentPage + 1)" :disabled="currentPage === totalPages">다음</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
table {
  width: 100%;
  padding-top: 1em;
  margin: 3em 0;
  border: 1px solid;
  border-radius: 10px;
  border-collapse: separate;
  border-spacing: 0;
  text-align: center;
}

thead {
  position: relative;
}

thead::after {
  content: '';
  position: absolute;
  left: 3%;
  right: 3%;
  bottom: 0;
  border-bottom: 1px solid;
}

thead th {
  border-bottom: none;
}

tr {
  position: relative;
}

td {
  font-size: 13px;
  text-align: center;
  vertical-align: middle;
}

th {
  text-align: center;
  vertical-align: middle;
  font-size: 15px;
}

.col-author {
  padding-top: 1.5em;
  padding-left: 5em;
  padding-right: 7em;
  padding-bottom: 1.5em;
}

.author-name {
  font-size: 13px;
}

.author-date {
  font-size: 10px;
  color: gray;
}

.col-title {
  padding-top: 1.5em;
  padding-left: 12em;
  padding-right: 12em;
  padding-bottom: 1.5em;
}

.col-likes {
  padding-top: 1.5em;
  padding-left: 5em;
  padding-right: 1em;
  padding-bottom: 1.5em;
}

.col-views {
  padding-top: 1.5em;
  padding-left: 1em;
  padding-right: 5em;
  padding-bottom: 1.5em;
}

tbody tr::after {
  content: '';
  position: absolute;
  left: 3%;
  right: 3%;
  bottom: 0;
  border-bottom: 1px solid #a1a1a1;
}

tbody tr:last-child::after {
  border-bottom: none;
}

thead tr:first-child th:first-child {
  border-top-left-radius: 10px;
}

thead tr:first-child th:last-child {
  border-top-right-radius: 10px;
}

tbody tr:last-child td:first-child {
  border-bottom-left-radius: 10px;
}

tbody tr:last-child td:last-child {
  border-bottom-right-radius: 10px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 1em;
}

.pagination button {
  margin: 0 0.5em;
  padding: 0.5em 1em;
  border: 1px solid #000000;
  border-radius: 5px;
  background-color: #f9f9f9;
  cursor: pointer;
}

.pagination button.active {
  background-color: #707070;
  color: white;
}

.pagination button:disabled {
  cursor: not-allowed;
  opacity: 0.5;
}
</style>