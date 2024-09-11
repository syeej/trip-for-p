<script setup>
import {computed, onMounted, ref} from "vue";
import { useRoute } from "vue-router";
import {
    getFreeCommentListAPI,
    getFreePostAPI,
    createFreeCommentAPI,
    updateFreeCommentAPI, deleteFreeCommentAPI, deleteFreePostAPI
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

const getFreePost = async function () {
    try {
        const postId = route.params.postId;
        const response = await getFreePostAPI(postId);
        post.value = response.data;
        await getFreeCommentList();
    } catch (error) {
        console.log(error);
    }
};

const getFreeCommentList = async function () {
    try {
        const request = {
            postId: route.params.postId,
            size: pageSize.value,
            page: currentPage.value - 1,
        }
        const response = await getFreeCommentListAPI(request);
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
            await createFreeCommentAPI(request);
            newComment.value = '';
            await getFreeCommentList();
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
    await getFreeCommentList();
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
        await updateFreeCommentAPI(id, request);
        await getFreeCommentList(); // 댓글 목록 새로고침
        cancelEditComment();
    } catch (error) {
        console.error('댓글 수정 실패:', error);
    }
};

const deleteComment = async (id) => {
    if (window.confirm('댓글을 삭제하시겠습니까?')) {
        try {
            await deleteFreeCommentAPI(id, route.params.postId);
            await getFreeCommentList();
        } catch (error) {
            console.log(error);
        }
    }
};

const editPost = () => {
    router.push(`/free-post/${route.params.postId}/edit`);
};

const deletePost = async (id) => {
    if (window.confirm("게시글을 삭제하시겠습니까?")) {
        try {
            await deleteFreePostAPI(id);
            await router.push(`/free-post`)
        } catch (error) {
            console.log(error);
        }
    }
};



onMounted(() => {
    getFreePost();
});
</script>

<template>
    <div class="post-detail-container">
        <div class="post-content">
            <p class="post-info">
                <span>작성자: {{ post.author }}</span>
                <span>작성일: {{ formatDateFull(post.createdAt) }}</span>
                <span>조회수: {{ post.views }}</span>
            </p>
            <pre class="post-body">{{ post.content }}</pre>
            <div v-if="isPostAuthor || isAdmin" class="post-actions">
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
                    <pre class="comment-content">{{ comment.content }}</pre>
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
.post-detail-container {
    width: 100%;
    padding: 20px;
    margin-top: 50px;
}

.post-content {
    background-color: #ffffff;
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    margin-bottom: 20px;
    min-height: 300px;
    position: relative;
}

.post-actions {
    position: absolute;
    bottom: 20px;
    right: 20px;
}

h2 {
    color: #333;
    margin-bottom: 10px;
}

.post-info {
    color: #666;
    font-size: 14px;
    margin-bottom: 15px;
}

.post-info span {
    margin-right: 15px;
}

.post-body {
    white-space: pre-wrap;
    word-wrap: break-word;
    line-height: 1.6;
    color: #333;
    margin-bottom: 40px; /* 버튼을 위한 여백 */
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
    white-space: pre-wrap
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

.post-actions, .comment-actions {
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
