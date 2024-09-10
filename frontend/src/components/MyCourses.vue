<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import {getMyPlanCourseListAPI} from "@/api";

const router = useRouter();
const courses = ref([]);
const currentPage = ref(0);
const totalPages = ref(0);
const pageSize = ref(10);
const loading = ref(false);

const fetchCourses = async (page) => {
  loading.value = true;
  try {
    const response = await getMyPlanCourseListAPI({
      page: page,
      size: pageSize.value,
    });
    courses.value = response.data.content;
    currentPage.value = response.data.number;
    totalPages.value = response.data.totalPages;
  } catch (error) {
    console.error('여행 코스를 불러오는 데 실패했습니다:', error);
  } finally {
    loading.value = false;
  }
};

const goToPage = (page) => {
  if (page >= 0 && page < totalPages.value) {
    fetchCourses(page);
  }
};

const formatDate = (dateString) => {
  const date = new Date(dateString);
  return date.toLocaleDateString();
};

const goToCourseDetail = (courseId) => {
  router.push(`/plan/${courseId}`);
};

onMounted(() => {
  fetchCourses(0);
});
</script>

<template>
  <h2>내가 작성한 여행 코스 목록</h2>
  <div class="my-courses-container">
    <div v-if="loading" class="loading">로딩 중...</div>
    <div v-else-if="courses.length" class="course-list">
      <div v-for="course in courses" :key="course.id" class="course-item" @click="goToCourseDetail(course.id)">
        <h3>{{ course.title }}</h3>
        <div class="course-info">
          <span>지역: {{ course.area }}</span>
          <span>작성일: {{ formatDate(course.createdAt) }}</span>
          <span>조회수: {{ course.views }}</span>
          <span>좋아요: {{ course.likes }}</span>
        </div>
      </div>
    </div>
    <p v-else>작성한 여행 코스가 없습니다.</p>
    <div v-if="totalPages > 1" class="pagination">
      <button @click="goToPage(currentPage - 1)" :disabled="currentPage === 0">이전</button>
      <span>{{ currentPage + 1 }} / {{ totalPages }}</span>
      <button @click="goToPage(currentPage + 1)" :disabled="currentPage >= totalPages - 1">다음</button>
    </div>
  </div>
</template>

<style scoped>
.my-courses-container {
  padding: 20px;
}

.loading {
  text-align: center;
  margin: 20px 0;
}

.course-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.course-item {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 15px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.course-item:hover {
  background-color: #f5f5f5;
}

.course-item h3 {
  margin: 0 0 10px 0;
  color: #333;
}

.course-info {
  display: flex;
  justify-content: space-between;
  font-size: 0.9em;
  color: #666;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 20px;
}

.pagination button {
  margin: 0 10px;
  padding: 5px 10px;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.pagination button:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.pagination span {
  margin: 0 10px;
}
</style>