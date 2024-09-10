<script setup>
import {onMounted, ref} from "vue";
import {getMyReviewListAPI} from "@/api";

const posts = ref([]);
const currentPage = ref(0);
const totalPages = ref(0);
const pageSize = ref(10);
const loading = ref(false);

const getMyReviewList = async (page) => {
  loading.value = true;
  try {
    const response = await getMyReviewListAPI({
      page: page,
      size: pageSize.value,
    });
    posts.value = response.data.content;
    currentPage.value = response.data.number;
    totalPages.value = response.data.totalPages;
  } catch (error) {
    console.error('리뷰 게시글을 불러오는 데 실패했습니다:', error);
  } finally {
    loading.value = false;
  }
};

const goToPage = (page) => {
  if (page >= 0 && page < totalPages.value) {
    getMyReviewList(page);
  }
};

onMounted(() => {
  getMyReviewList(0);
});
</script>

<template>
  <h2>나의 리뷰 게시글</h2>
  <div class="review-board-container">
    <div v-if="loading" class="loading">로딩 중...</div>
    <div v-else-if="posts.length">
      <div v-for="post in posts" :key="post.id" class="review">
        <div class="review-header">
          <span class="review-author">{{ post.userId }}</span>
          <span class="review-date">{{ new Date(post.createdAt).toLocaleDateString() }}</span>
        </div>
        <div class="review-content">
          <div class="review-title">{{ post.title }}</div>
          <div class="review-text">{{ post.content.substring(0, 100) }}{{ post.content.length > 100 ? '...' : '' }}</div>
          <div class="review-views">조회수: {{ post.views }}</div>
        </div>
      </div>
    </div>
    <p v-else>작성한 리뷰 게시글이 없습니다.</p>

    <div v-if="totalPages > 1" class="pagination">
      <button @click="goToPage(currentPage - 1)" :disabled="currentPage === 0">이전</button>
      <span>{{ currentPage + 1 }} / {{ totalPages }}</span>
      <button @click="goToPage(currentPage + 1)" :disabled="currentPage >= totalPages - 1">다음</button>
    </div>
  </div>
</template>

<style scoped>
body, html {
  margin: 0;
  padding: 0;
  width: 100%;
  height: 100%;
  font-family: 'Malgun Gothic', sans-serif;
}

.review-board-container {
  width: 100%;
  background-color: #ffffff;
  border-radius: 20px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  margin: auto;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  margin-top: 20px;
}

.review {
  border-bottom: 1px solid #e0e0e0;
  padding: 20px;
  margin-bottom: 10px;
  background-color: #fafafa;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.review:last-child {
  border-bottom: none;
  margin-bottom: 0;
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.review-author {
  font-weight: bold;
  color: #0066cc;
  font-size: 20px;
}

.review-date {
  color: #888;
  font-size: 17px;
}

.review-content {
  color: #555;
  line-height: 1.6;
  font-size: 1.05em;
}

.review-title {
  font-size: 1.2em;
  font-weight: bold;
  margin-bottom: 5px;
}

.review-text {
  margin-bottom: 10px;
}

.review-views {
  font-size: 1.1em;
  color: #f39c12;
}

.loading {
  text-align: center;
  margin: 20px 0;
  font-size: 1.1em;
  color: #888;
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
  background-color: #0066cc;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 1em;
}

.pagination button:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.pagination span {
  font-size: 1em;
  color: #555;
}
</style>