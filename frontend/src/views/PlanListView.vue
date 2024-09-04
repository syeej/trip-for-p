<script setup>
import { ref, computed } from 'vue';
import SelectAreaComponent from "@/components/SelectAreaComponent.vue";

const plans = ref([]);
const currentPage = ref(1);
const itemsPerPage = ref(5); // 페이지당 항목 수
const isLoading = ref(false); // 로딩 상태 변수
const selectedArea = ref(null); // 선택된 지역
const showSelection = ref(true); // 지역 선택 화면을 보여줄지 여부

const fetchPlans = async () => {
  if (!selectedArea.value) return;

  try {
    isLoading.value = true; // 로딩 상태 시작
    const response = await fetch(`/api/plans?area=${selectedArea.value}`);
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    const data = await response.json();
    console.log('API 응답 데이터:', data);
    plans.value = data.content;
  } catch (error) {
    console.error('Error fetching plans:', error);
  } finally {
    isLoading.value = false; // 로딩 완료
  }
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

// 지역이 선택된 경우 처리
const handleAreaSelected = (area) => {
  selectedArea.value = area;
  showSelection.value = false; // 지역 선택 화면 숨기기
  fetchPlans();
};

// 뒤로가기 버튼 클릭 처리
const goBackToSelection = () => {
  showSelection.value = true; // 지역 선택 화면 보이기
  selectedArea.value = null; // 선택된 지역 초기화
};

</script>

<template>
  <div>
    <!-- 지역 선택 화면 -->
    <div v-if="showSelection">
      <SelectAreaComponent @area-selected="handleAreaSelected" />
    </div>

    <!-- 테이블 화면 -->
    <div v-else>
      <img
          @click="goBackToSelection"
          class="back-button"
          src="@/assets/backbutton.png"
          alt="뒤로가기 버튼"
      />
      <div v-if="isLoading">로딩 중...</div>
      <div v-else-if="selectedArea">
        <h3>{{ selectedArea }}의 여행 코스</h3>
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
        <p v-else>등록된 여행 코스가 없습니다.</p>

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

.back-button {
  margin: 1em 0em 0em -4em; /* 수정된 부분: 왼쪽 마진을 0으로 설정 */
  border: 0px;
  border-radius: 5px;
  cursor: pointer;
}

.back-button:hover {
  background-color: #e0e0e0;
}
</style>