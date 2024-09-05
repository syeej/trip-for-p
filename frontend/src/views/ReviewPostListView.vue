<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

// reviews 배열을 ref로 선언 (Vue의 반응형 데이터)
const reviews = ref([]);

// 컴포넌트가 마운트될 때 서버에서 데이터를 가져옴
onMounted(() => {
  axios.get('/api/review-posts') // API 엔드포인트 호출
      .then(response => {
        reviews.value = response.data.content; // 가져온 데이터를 reviews 배열에 저장
      })
      .catch(error => {
        console.error('Error fetching reviews:', error);
      });
});
</script>



<template>
  <div class="review-board-container">
    <h2>리뷰 게시글 목록</h2>
    <!-- 데이터가 없을 경우 표시 -->
    <p v-if="reviews.length === 0">리뷰 게시글이 없습니다.</p>
    <div v-for="review in reviews" :key="review.id" class="review">
      <div class="review-header">
        <span class="review-author">{{ review.userId }}</span>
        <span class="review-date">{{ new Date(review.createdAt).toLocaleDateString() }}</span>
      </div>
      <div class="review-content">
        <div class="review-title">{{ review.title }}</div>
        <div class="review-text">{{ review.content }}</div>
        <div class="review-views">Views: {{ review.views }}</div>
      </div>
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
</style>
