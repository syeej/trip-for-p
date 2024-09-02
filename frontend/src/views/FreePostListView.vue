<script>
import axios from 'axios';

export default {
  name: 'FreePostListView',
  data() {
    return {
      posts: []  // 서버에서 가져온 데이터를 저장할 배열
    };
  },
  mounted() {
    // 컴포넌트가 마운트될 때 서버에서 게시글 데이터를 가져옵니다.
    axios.get('/api/free-posts')  // API 엔드포인트 호출
        .then(response => {
          this.posts = response.data.content;  // 가져온 데이터를 posts 배열에 저장
        })
        .catch(error => {
          console.error('Error fetching posts:', error);
        });
  }
}
</script>

<template>
  <div class="board-container">
    <div v-for="post in posts" :key="post.id" class="post">
      <div class="post-header">
        <span class="post-author">{{ post.author }}</span>
        <span class="post-date">{{ post.date }}</span>
      </div>
      <div class="post-content">
        {{ post.content }}
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

.board-container {
  width: 100%;
  height: 100%;
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
  padding: 20px;
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
  margin-bottom: 10px;
}

.post-author {
  font-weight: bold;
  color: #0066cc;
  font-size: 20px;
}

.post-date {
  color: #888;
  font-size: 17px;
}

.post-content {
  color: #555;
  line-height: 1.6;
  font-size: 1.05em;
}
</style>
