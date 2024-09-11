<script setup>
import { ref, onMounted } from 'vue';
import { getReviewPostListAPI } from "@/api";
import router from "@/router";

const posts = ref([]);
const currentPage = ref(1);
const totalPages = ref(1);
const searchKeyword = ref('');
const pageSize = ref(10);

const getReviewPostList = async function () {
    try {
        const request = {
            keyword: searchKeyword.value,
            size: pageSize.value,
            page: currentPage.value - 1,
        }

        const response = await getReviewPostListAPI(request);
        posts.value = response.data.content;
        totalPages.value = response.data.totalPages === 0 ? 1 : response.data.totalPages;
    } catch (error) {
        console.error('Error fetching posts:', error);
    }
};

// 상대적 시간 포맷팅 함수 추가
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

// 날짜 포맷팅 함수 추가
const formatDate = (dateString) => {
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
};

const search = () => {
    currentPage.value = 1;
    getReviewPostList();
};

const changePage = (page) => {
    currentPage.value = page;
    getReviewPostList();
};

const createPost = () => {
    router.push('/review-post/write');
};

const goToPostDetail = (postId) => {
    router.push(`/review-post/${postId}`);
};

onMounted(() => {
    getReviewPostList();
});
</script>

<template>
    <div class="review-post-board-container">
        <h2>리뷰게시판</h2>

        <div class="board-header">
            <button @click="createPost" class="create-post-btn">리뷰 작성</button>
        </div>

        <div v-for="post in posts" :key="post.id" class="review-post" @click="goToPostDetail(post.id)">
            <div class="post-header">
                <span class="post-author">{{ post.author }}</span>
                <span class="post-date">{{ formatRelativeTime(post.createdAt) }}</span>
            </div>
            <div class="post-title">{{ post.title }}</div>
            <div class="post-footer">
                <span class="post-views">조회수: {{ post.views }}</span>
                <span class="post-comments">댓글: {{ post.comments.length }}</span>
            </div>
        </div>

        <div class="search-container">
            <input v-model="searchKeyword" placeholder="검색어를 입력하세요" />
            <button @click="search" class="search-btn">검색</button>
        </div>

        <div class="pagination">
            <button @click="changePage(currentPage - 1)" :disabled="currentPage === 1">이전</button>
            <span>{{ currentPage }} / {{ totalPages }}</span>
            <button @click="changePage(currentPage + 1)" :disabled="currentPage === totalPages">다음</button>
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

.review-post-board-container {
    width: 100%;
    background-color: #ffffff;
    border-radius: 20px;
    padding: 20px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    overflow-y: auto;
    display: flex;
    flex-direction: column;
    margin-top: 50px;
}

.board-header {
    display: flex;
    justify-content: flex-end;
    margin-bottom: 20px;
}

.search-container {
    display: flex;
    margin-top: 20px;
    margin-bottom: 20px;
}

.search-container input {
    flex-grow: 1;
    padding: 10px;
    border: 1px solid #ddd;
    border-radius: 4px 0 0 4px;
    font-size: 16px;
}

.search-btn {
    padding: 10px 20px;
    background-color: #0066cc;
    color: white;
    border: none;
    border-radius: 0 4px 4px 0;
    cursor: pointer;
    font-size: 16px;
}

.search-btn:hover {
    background-color: #0056b3;
}

.create-post-btn {
    padding: 10px 20px;
    background-color: #4CAF50;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-size: 16px;
}

.create-post-btn:hover {
    background-color: #45a049;
}

.review-post {
    border-bottom: 1px solid #e0e0e0;
    padding: 15px;
    margin-bottom: 10px;
    background-color: #fafafa;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
    cursor: pointer;
}

.review-post:last-child {
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

.post-title {
    font-size: 20px;
    font-weight: bold;
    color: #333;
    margin-bottom: 10px;
    width: 60%;
    white-space: nowrap;      /* 텍스트를 한 줄로 표시 */
    overflow: hidden;         /* 넘치는 텍스트를 숨김 */
    text-overflow: ellipsis;  /* 넘친 부분을 ...으로 표시 */
}

.post-date {
    color: #888;
    font-size: 16px;
}

.post-views {
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

.post-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: 10px;
    font-size: 14px;
    color: #888;
}

.post-views, .post-comments {
    display: flex;
    align-items: center;
}

.post-views::before {
    content: '\1F441'; /* 눈 이모지 */
    margin-right: 5px;
}

.post-comments::before {
    content: '\1F4AC'; /* 말풍선 이모지 */
    margin-right: 5px;
}
</style>
