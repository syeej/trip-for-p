<script setup>
import {onMounted, ref} from 'vue';
import {useRoute} from 'vue-router';
import {getFreePostAPI, updateFreePostAPI} from "@/api";
import router from "@/router";

const route = useRoute();

const content = ref('');

const getPostData = async () => {
    try {
        const response = await getFreePostAPI(route.params.postId);
        content.value = response.data.content;
    } catch (error) {
        console.error('게시글 데이터 로드 실패:', error);
    }
};

const editFreePost = async function () {
    try {
        const request = {
            content: content.value,
        };
        await updateFreePostAPI(route.params.postId, request);
        await router.push(`/free-post/${route.params.postId}`); // 수정 후 상세 페이지로 이동
    } catch (error) {
        console.error('게시글 수정 실패:', error);
    }
};

onMounted(() => {
    getPostData();
});
</script>

<template>
    <div class="post-edit-container">
        <h2>게시글 수정</h2>
        <form @submit.prevent="editFreePost" class="post-form">
            <div class="form-group">
                <label for="content">내용</label>
                <textarea id="content" v-model="content" required placeholder="내용을 입력하세요"></textarea>
            </div>
            <div class="form-actions">
                <button type="submit" class="submit-btn">수정 완료</button>
                <button type="button" @click="router.go(-1)" class="cancel-btn">취소</button>
            </div>
        </form>
    </div>
</template>

<style scoped>
.post-edit-container {
    width: 100%;
    max-width: 800px;
    background-color: #ffffff;
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    margin: 20px auto;
}

h2 {
    color: #333;
    margin-bottom: 20px;
}

.post-form {
    display: flex;
    flex-direction: column;
}

.form-group {
    margin-bottom: 20px;
}

label {
    display: block;
    margin-bottom: 5px;
    font-weight: bold;
    color: #555;
}

input[type="text"],
textarea {
    width: 100%;
    padding: 10px;
    border: 1px solid #ddd;
    border-radius: 4px;
    font-size: 16px;
}

textarea {
    height: 200px;
    resize: vertical;
}

.form-actions {
    display: flex;
    justify-content: flex-end;
}

.submit-btn, .cancel-btn {
    padding: 10px 20px;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-size: 16px;
    transition: background-color 0.3s;
}

.submit-btn {
    background-color: #4CAF50;
    color: white;
    margin-right: 10px;
}

.cancel-btn {
    background-color: #f44336;
    color: white;
}

.submit-btn:hover, .cancel-btn:hover {
    opacity: 0.8;
}
</style>
