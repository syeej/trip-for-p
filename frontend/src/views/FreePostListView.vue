<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

// 자유 게시글 배열을 ref로 선언 (Vue의 반응형 데이터)
const posts = ref([]);

// 컴포넌트가 마운트될 때 서버에서 데이터를 가져옴
onMounted(() => {
  axios.get('/api/free-posts') // 자유 게시판 API 엔드포인트 호출
      .then(response => {
        posts.value = response.data.content; // 가져온 데이터를 posts 배열에 저장
      })
      .catch(error => {
        console.error('Error fetching posts:', error);
      });
});
</script>

<template>
  <div class="post-board-container">
    <h2>게시글 목록</h2>
    <div v-for="post in posts" :key="post.id" class="post">
      <div class="post-header">
        <span class="post-author">{{ post.author }}</span>
      </div>
      <div class="post-content-container">
        <div class="post-content">{{ post.content }}</div>
        <div class="post-date">{{ new Date(post.createdAt).toLocaleDateString() }}</div>
      </div>
      <div class="post-views">Views: {{ post.views }}</div>
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

.post-board-container {
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

.post {
  border-bottom: 1px solid #e0e0e0;
  padding: 15px;
  margin-bottom: 10px;
  background-color: #fafafa;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.post:last-child {
  border-bottom: none;
  margin-bottom: 0;
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 5px;
}

.post-author {
  font-weight: bold;
  color: #0066cc;
  font-size: 18px;
}

.post-content-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.post-content {
  color: #555;
  font-size: 16px;
}

.post-date {
  color: #888;
  font-size: 16px;
}

.post-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 5px;
}

.post-views {
  font-size: 14px;
  color: #f39c12;
}
</style>
