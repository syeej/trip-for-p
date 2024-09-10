<script setup>
import {onMounted, ref} from 'vue';
import {useRoute} from 'vue-router';
import {getReviewPostAPI, updateReviewPostAPI} from "@/api";
import router from "@/router";

const route = useRoute();

const title = ref('');
const content = ref('');
const files = ref([]);
const previewUrls = ref([]);
const existingFiles = ref([]);

const getPostData = async () => {
    try {
        const response = await getReviewPostAPI(route.params.postId);
        title.value = response.data.title;
        content.value = response.data.content;
        existingFiles.value = response.data.fileUrls || [];
        previewUrls.value = [...existingFiles.value];
    } catch (error) {
        console.error('리뷰 데이터 로드 실패:', error);
    }
};

const editReviewPost = async function () {
    try {
        const request = {
            title: title.value,
            content: content.value
        }

        const formData = new FormData();
        formData.append('request', new Blob([JSON.stringify(request)], {
            type: 'application/json'
        }));
        files.value.forEach((file) => {
            formData.append(`files`, file);
        });

        await updateReviewPostAPI(route.params.postId, formData);
        await router.push(`/review-post/${route.params.postId}`);
    } catch (error) {
        console.error('리뷰 수정 실패:', error);
    }
};

const handleFileChange = (event) => {
    previewUrls.value = []
    const newFiles = Array.from(event.target.files);
    files.value = [...files.value, ...newFiles];

    newFiles.forEach(file => {
        const reader = new FileReader();
        reader.onload = (e) => {
            previewUrls.value.push(e.target.result);
        };
        reader.readAsDataURL(file);
    });
};


onMounted(() => {
    getPostData();
});
</script>

<template>
    <div class="review-edit-container">
        <h2>리뷰 수정</h2>
        <form @submit.prevent="editReviewPost" class="review-form">
            <div class="form-group">
                <label for="title">제목</label>
                <input type="text" id="title" v-model="title" required placeholder="제목을 입력하세요">
            </div>
            <div class="form-group">
                <label for="content">내용</label>
                <textarea id="content" v-model="content" required
                          placeholder="내용을 입력하세요"></textarea>
            </div>
            <div class="form-group">
                <label for="files">파일 첨부</label>
                <input type="file" id="files" @change="handleFileChange" multiple>
            </div>
            <div class="file-preview" v-if="previewUrls.length > 0">
                <div v-for="(url, index) in previewUrls" :key="index" class="preview-item">
                    <img :src="url" alt="File preview"/>
                </div>
            </div>
            <div class="form-actions">
                <button type="submit" class="submit-btn">수정 완료</button>
                <button type="button" @click="router.go(-1)" class="cancel-btn">취소</button>
            </div>
        </form>
    </div>
</template>

<style scoped>
.review-edit-container {
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

.review-form {
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

.file-preview {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    margin-bottom: 20px;
}

.preview-item {
    position: relative;
    width: 100px;
    height: 100px;
}

.preview-item img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    border-radius: 4px;
}

.remove-btn {
    position: absolute;
    top: 5px;
    right: 5px;
    background-color: rgba(255, 0, 0, 0.7);
    color: white;
    border: none;
    border-radius: 50%;
    width: 20px;
    height: 20px;
    font-size: 12px;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
}

.remove-btn:hover {
    background-color: rgba(255, 0, 0, 0.9);
}
</style>
