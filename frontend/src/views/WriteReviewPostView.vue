<script setup>
import {ref, onMounted} from 'vue';
import {createReviewPostAPI, getMyPlanListAPI} from "@/api";
import router from "@/router";

const title = ref('');
const content = ref('');
const files = ref([]);
const previewUrls = ref([]);
const plans = ref([]);
const selectedPlanId = ref('');

const getMyPlanList = async function () {
    try {
        const request = {
            size: 100,  // 적절한 크기로 조정하세요
            page: 0,
        }
        const response = await getMyPlanListAPI(request);
        plans.value = response.data.content;
    } catch (error) {
        console.log(error);
    }
};

const createReviewPost = async function () {
    try {
        const request = {
            title: title.value,
            content: content.value,
            planId: selectedPlanId.value
        }
        const formData = new FormData();
        formData.append('request', new Blob([JSON.stringify(request)], {
            type: 'application/json'
        }));

        files.value.forEach((file) => {
            formData.append(`files`, file);
        });

        const response = await createReviewPostAPI(formData);
        await router.push(`/review-post/${response.data.id}`);
    } catch (error) {
        console.log(error);
    }
};

const handleFileChange = (event) => {
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

const removeFile = (index) => {
    files.value.splice(index, 1);
    previewUrls.value.splice(index, 1);
};

onMounted(() => {
    getMyPlanList();
});
</script>

<template>
    <div class="review-create-container">
        <h2>리뷰 작성</h2>
        <form @submit.prevent="createReviewPost" class="review-form">
            <div class="form-group">
                <label for="plan">여행 계획 선택</label>
                <select id="plan" v-model="selectedPlanId" required>
                    <option value="">여행 계획을 선택하세요</option>
                    <option v-for="plan in plans" :key="plan.id" :value="plan.id">
                        {{ plan.title }}
                    </option>
                </select>
            </div>
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
                    <button @click.prevent="removeFile(index)" class="remove-btn">삭제</button>
                </div>
            </div>
            <div class="form-actions">
                <button type="submit" class="submit-btn">리뷰 작성</button>
            </div>
        </form>
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

.review-create-container {
    width: 100%;
    max-width: 800px;
    background-color: #ffffff;
    border-radius: 20px;
    padding: 20px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
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
textarea,
select {
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

input[type="file"] {
    margin-top: 5px;
}

.form-actions {
    display: flex;
    justify-content: flex-end;
}

.submit-btn {
    padding: 10px 20px;
    background-color: #4CAF50;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-size: 16px;
    transition: background-color 0.3s;
}

.submit-btn:hover {
    background-color: #45a049;
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
