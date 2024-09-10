<script setup>
import { ref, onMounted } from 'vue';
import router from "@/router";
import {getMyReviewCommentListAPI} from "@/api";

const comments = ref([]);
const currentPage = ref(0);
const totalPages = ref(0);
const pageSize = ref(10);
const loading = ref(false);

const getMyReviewComments = async (page) => {
  loading.value = true;
  try {
    const response = await getMyReviewCommentListAPI({
      page: page,
      size: pageSize.value,
    });
    comments.value = response.data.content;
    currentPage.value = response.data.number;
    totalPages.value = response.data.totalPages;
  } catch (error) {
    console.error('댓글을 불러오는 데 실패했습니다:', error);
  } finally {
    loading.value = false;
  }
};

const goToPage = (page) => {
  if (page >= 0 && page < totalPages.value) {
    getMyReviewComments(page);
  }
};

const formatRelativeTime = (dateString) => {
  const now = new Date().toISOString();
  const cleanDateString = dateString.replace(/\[.*\]$/, '');
  const past = new Date(cleanDateString).toISOString();
  const diffInMilliseconds = new Date(now) - new Date(past);
  const diffInSeconds = Math.floor(diffInMilliseconds / 1000);

  if (diffInSeconds < 60) {
    return '방금 전';
  } else if (diffInSeconds < 3600) {
    const minutes = Math.floor(diffInSeconds / 60);
    return `${minutes}분 전`;
  } else if (diffInSeconds < 86400) {
    const hours = Math.floor(diffInSeconds / 3600);
    return `${hours}시간 전`;
  } else {
    return formatDate(dateString);
  }
};

const formatDate = (dateString) => {
  const date = new Date(dateString);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
};
/* 리뷰 상세 글 이동(수정 필요)*/
const goToReviewDetail = (postId) => {
  router.push(`/review-post/${postId}`);
};

onMounted(() => {
  getMyReviewComments(0);
});
</script>

<template>
  <h2>나의 리뷰게시글 댓글</h2>
  <div class="comment-board-container">
    <div v-if="loading" class="loading">로딩 중...</div>
    <div v-else-if="comments.length" class="comment-list">
      <div v-for="comment in comments" :key="comment.id" class="comment" @click="goToReviewDetail(comment.postId)">
        <div class="comment-content">{{ comment.content }}</div>
        <div class="comment-footer">
          <span class="comment-post-id">게시글 ID: {{ comment.postId }}</span>
          <span class="comment-date">{{ formatRelativeTime(comment.createdAt) }}</span>
        </div>
      </div>
    </div>
    <p v-else>작성한 댓글이 없습니다.</p>

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

.comment-board-container {
  width: 100%;
  background-color: #ffffff;
  border-radius: 20px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  margin-top: 20px;
}

.loading {
  text-align: center;
  margin: 20px 0;
}

.comment-list {
  list-style-type: none;
  padding: 0;
}

.comment {
  border-bottom: 1px solid #e0e0e0;
  padding: 15px;
  margin-bottom: 10px;
  background-color: #fafafa;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  cursor: pointer;
}

.comment:last-child {
  border-bottom: none;
  margin-bottom: 0;
}

.comment-content {
  color: #555;
  font-size: 16px;
  margin-bottom: 10px;
}

.comment-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
  color: #888;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 20px;
}

.pagination button {
  padding: 5px 10px;
  margin: 0 5px;
  background-color: #0066cc;
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
.comment-post-id{
  display: none;
}
</style>