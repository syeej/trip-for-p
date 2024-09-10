<script setup>
import {computed, onMounted, ref} from "vue";
import { useRoute } from "vue-router";
import {
    getReviewCommentListAPI,
    getReviewPostAPI,
    createReviewCommentAPI,
    updateReviewCommentAPI, deleteReviewCommentAPI, deleteReviewPostAPI
} from "@/api";
import store from "@/store";
import router from "@/router";

const post = ref({});
const comments = ref([]);
const route = useRoute();
const newComment = ref('');
const currentPage = ref(1);
const totalPages = ref(1);
const pageSize = ref(10);
const editingCommentId = ref(null);
const editingCommentContent = ref('');

// 현재 로그인한 사용자의 닉네임을 가져옵니다.
const currentUserNickname = computed(() => store.getters.getNickname);

// 현재 사용자가 게시글 작성자인지 확인합니다.
const isPostAuthor = computed(() => currentUserNickname.value === post.value.author);
const isAdmin = computed(() => store.getters.getRole==='ADMIN')

const getReviewPost = async function () {
    try {
        const postId = route.params.postId;
        const response = await getReviewPostAPI(postId);
        post.value = response.data;
        await getReviewCommentList();
    } catch (error) {
        console.log(error);
    }
};

const getReviewCommentList = async function () {
    try {
        const request = {
            postId: route.params.postId,
            size: pageSize.value,
            page: currentPage.value - 1,
        }
        const response = await getReviewCommentListAPI(request);
        comments.value = response.data.content;
        totalPages.value = response.data.totalPages === 0 ? 1 : response.data.totalPages;
    } catch (error) {
        console.log(error);
    }
};

const submitComment = async () => {
    if (!store.getters.isAccessTokenValid) {
        if (window.confirm("로그인이 필요한 서비스입니다.\n로그인 페이지로 이동하시겠습니까?")) {
            await router.push('/login');
        }
    } else {
        try {
            const request = {
                postId: route.params.postId,
                content: newComment.value
            };
            await createReviewCommentAPI(request);
            newComment.value = '';
            await getReviewCommentList();
        } catch (error) {
            console.log(error);
        }
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

const formatDateFull = (dateString) => {
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    return `${year}.${month}.${day} ${hours}:${minutes}`;
};

const changePage = async (page) => {
    currentPage.value = page;
    await getReviewCommentList();
};

const startEditComment = (comment) => {
    editingCommentId.value = comment.id;
    editingCommentContent.value = comment.content;
};

const cancelEditComment = () => {
    editingCommentId.value = null;
    editingCommentContent.value = '';
};

const saveEditComment = async (id) => {
    try {
        const request = {
            postId: route.params.postId,
            content: editingCommentContent.value,
        }
        await updateReviewCommentAPI(id, request);
        await getReviewCommentList(); // 댓글 목록 새로고침
        cancelEditComment();
    } catch (error) {
        console.error('댓글 수정 실패:', error);
    }
};

const deleteComment = async (id) => {
    if (window.confirm('댓글을 삭제하시겠습니까?')) {
        try {
            await deleteReviewCommentAPI(id, route.params.postId);
            await getReviewCommentList();
        } catch (error) {
            console.log(error);
        }
    }
};

const editPost = () => {
    router.push(`/review-post/${route.params.postId}/edit`);
};

const deletePost = async (id) => {
    if (window.confirm("리뷰를 삭제하시겠습니까?")) {
        try {
            await deleteReviewPostAPI(id);
            await router.push(`/review-post`)
        } catch (error) {
            console.log(error);
        }
    }
};

const goToPlan = () => {
    if (post.value.planId) {
        router.push(`/plan/${post.value.planId}`);
    }
};

onMounted(() => {
    getReviewPost();
});
</script>

<template>
    <div class="review-detail-container">
        <div class="review-content">
            <h2 class="review-title">{{ post.title }}</h2>
            <p class="review-info">
                <span>작성자: {{ post.author }}</span>
                <span>작성일: {{ formatDateFull(post.createdAt) }}</span>
                <span>조회수: {{ post.views }}</span>
            </p>
            <div class="review-body">
                <pre class="review-content-text">{{ post.content }}</pre>
                <div v-if="post.fileUrls && post.fileUrls.length > 0" class="review-images">
                    <img v-for="(image, index) in post.fileUrls" :key="index" :src="image" :alt="`Review image ${index + 1}`">
                </div>
                <div v-if="post.planId" class="plan-link-container">
                    <button @click="goToPlan" class="plan-link">이 리뷰와 관련된 여행 코스 보기</button>
                </div>
            </div>
            <div v-if="isPostAuthor || isAdmin" class="review-actions">
                <button v-if="isPostAuthor" @click="editPost" class="edit-btn">수정</button>
                <button @click="deletePost(post.id)" class="delete-btn">삭제</button>
            </div>
        </div>

        <div class="comments-section">
            <h3>댓글</h3>
            <div class="comment-form">
                <textarea v-model="newComment" placeholder="댓글을 입력하세요"></textarea>
                <button @click="submitComment">댓글 등록</button>
            </div>
            <div v-for="comment in comments" :key="comment.id" class="comment">
                <p class="comment-author">{{ comment.author }}</p>
                <template v-if="editingCommentId === comment.id">
                    <textarea v-model="editingCommentContent" class="edit-comment-textarea"></textarea>
                    <div class="edit-comment-actions">
                        <button @click="saveEditComment(comment.id)" class="save-btn">저장</button>
                        <button @click="cancelEditComment" class="cancel-btn">취소</button>
                    </div>
                </template>
                <template v-else>
                    <p class="comment-content">{{ comment.content }}</p>
                    <p class="comment-date">{{ formatRelativeTime(comment.createdAt) }}</p>
                    <div v-if="currentUserNickname === comment.author || isPostAuthor || isAdmin" class="comment-actions">
                        <button @click="startEditComment(comment)" class="edit-btn" v-if="currentUserNickname === comment.author">수정</button>
                        <button @click="deleteComment(comment.id)" class="delete-btn">삭제</button>
                    </div>
                </template>
            </div>
            <div class="pagination">
                <button @click="changePage(currentPage - 1)" :disabled="currentPage === 1">이전</button>
                <span>{{ currentPage }} / {{ totalPages }}</span>
                <button @click="changePage(currentPage + 1)" :disabled="currentPage === totalPages">다음</button>
            </div>
        </div>
    </div>
</template>

<style scoped>
.review-detail-container {
    width: 100%;
    padding: 20px;
    margin-top: 50px;
}

.review-title {
    font-size: 24px;
    font-weight: bold;
    color: #333;
    margin-bottom: 15px;
}

.review-content {
    background-color: #ffffff;
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    margin-bottom: 20px;
    min-height: 300px;
    position: relative;
}

.review-actions {
    position: absolute;
    bottom: 20px;
    right: 20px;
}
.review-info {
    color: #666;
    font-size: 14px;
    margin-bottom: 15px;
    display: flex;
    flex-wrap: wrap;
}

.review-info span {
    margin-right: 15px;
    margin-bottom: 5px;
}
.review-body {
    line-height: 1.6;
    color: #333;
    margin-bottom: 40px;
}

.review-content-text {
    white-space: pre-wrap;
    word-wrap: break-word;
    text-align: left;
    margin: 30px 0;
    padding: 0;
    font-family: inherit;
    font-size: 20px;
    line-height: inherit;
    max-width: 100%;
    overflow-x: auto;

}

.review-images {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    margin-top: 20px;
    margin-bottom: 20px;  /* 이미지와 plan-link 사이의 간격 */
}

.review-images img {
    max-width: 300px;
    max-height: 300px;
    object-fit: cover;
    border-radius: 4px;
}

.plan-link-container {
    margin-top: 20px;  /* plan-link 위의 간격 */
    width: 100%;
}

.plan-link {
    display: block;
    width: 100%;
    padding: 10px;
    background-color: #0066cc;
    color: white;
    text-align: center;
    text-decoration: none;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-size: 16px;
    transition: background-color 0.3s;
}

.plan-link:hover {
    background-color: #004499;
}

.comments-section {
    background-color: #f9f9f9;
    border-radius: 8px;
    padding: 20px;
}

h3 {
    color: #333;
    margin-bottom: 15px;
}

.comment-form {
    margin-bottom: 20px;
}

.comment-form textarea {
    width: 100%;
    height: 100px;
    padding: 10px;
    border: 1px solid #ddd;
    border-radius: 4px;
    resize: vertical;
}

.comment-form button {
    margin-top: 10px;
    padding: 8px 15px;
    background-color: #4CAF50;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
}

.comment {
    background-color: #ffffff;
    border-radius: 4px;
    padding: 10px;
    margin-bottom: 10px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
    position: relative;
}

.comment-author {
    font-weight: bold;
    color: #444;
}

.comment-content {
    margin-bottom: 30px; /* 버튼을 위한 여백 */
}

.comment-actions {
    position: absolute;
    bottom: 10px;
    right: 10px;
}

.comment-date {
    font-size: 12px;
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

.review-actions, .comment-actions {
    margin-top: 10px;
}

.edit-btn, .delete-btn {
    padding: 5px 10px;
    margin-left: 10px;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-size: 12px;
}

.edit-btn {
    background-color: #4CAF50;
    color: white;
}

.delete-btn {
    background-color: #f44336;
    color: white;
}

.edit-btn:hover, .delete-btn:hover {
    opacity: 0.8;
}

.edit-comment-textarea {
    width: 100%;
    min-height: 60px;
    padding: 8px;
    margin-bottom: 10px;
    border: 1px solid #ddd;
    border-radius: 4px;
    resize: vertical;
}

.edit-comment-actions {
    display: flex;
    justify-content: flex-end;
    margin-top: 10px;
}

.save-btn, .cancel-btn {
    padding: 5px 10px;
    margin-left: 10px;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-size: 12px;
}

.save-btn {
    background-color: #4CAF50;
    color: white;
}

.cancel-btn {
    background-color: #f44336;
    color: white;
}

.save-btn:hover, .cancel-btn:hover {
    opacity: 0.8;
}
</style>
