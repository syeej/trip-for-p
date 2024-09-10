<script setup>
import { useRoute } from "vue-router";
import { ref, onMounted } from "vue";
import { updateMagazineAPI, getMagazineAPI } from "@/api";
import router from "@/router";

const title = ref('');
const content = ref('');
const newFiles = ref([]);
const route = useRoute();
const existingFiles = ref([]);
const loading = ref(true);
const error = ref(null);
const previewUrls = ref([]);

const handleFileChange = (event) => {
    previewUrls.value = []
    const addedFiles = Array.from(event.target.files);
    newFiles.value = [...newFiles.value, ...addedFiles];

    addedFiles.forEach(file => {
        const reader = new FileReader();
        reader.onload = (e) => {
            previewUrls.value.push(e.target.result);
        };
        reader.readAsDataURL(file);
    });
};

const updateMagazine = async function () {
    try {
        const formData = new FormData();

        const request = {
            title: title.value,
            content: content.value
        };

        formData.append('request', new Blob([JSON.stringify(request)], {
            type: 'application/json'
        }));

        newFiles.value.forEach((file) => {
            formData.append(`files`, file);
        });

        const response = await updateMagazineAPI(route.params.magazineId, formData);
        await router.push(`/magazine/${response.data.id}`);
    } catch (error) {
        console.error('매거진 수정 중 오류 발생:', error);
    }
};

const loadMagazine = async () => {
    try {
        loading.value = true;
        const response = await getMagazineAPI(route.params.magazineId);
        title.value = response.data.title;
        content.value = response.data.content;
        existingFiles.value = response.data.fileUrls || [];
        previewUrls.value = [...existingFiles.value];
    } catch (err) {
        console.error('매거진 데이터 로딩 중 오류 발생:', err);
        error.value = "매거진 데이터를 불러오는 데 실패했습니다.";
    } finally {
        loading.value = false;
    }
};

onMounted(loadMagazine);
</script>

<template>
    <div class="magazine-edit">
        <h1>매거진 수정</h1>
        <div v-if="loading" class="loading">매거진 데이터를 불러오는 중...</div>
        <div v-else-if="error" class="error">{{ error }}</div>
        <form v-else @submit.prevent="updateMagazine" class="edit-form">
            <div class="form-group">
                <label for="title">제목:</label>
                <input id="title" v-model="title" required>
            </div>

            <div class="form-group">
                <label for="content">내용:</label>
                <textarea id="content" v-model="content" required></textarea>
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

            <button type="submit" class="submit-btn">수정 완료</button>
        </form>
    </div>
</template>

<style scoped>
.magazine-edit {
    width: 100%;
    margin: 0 auto;
    padding: 20px;
}

h1 {
    font-size: 2em;
    color: #333;
    margin-bottom: 20px;
}

.edit-form {
    display: flex;
    flex-direction: column;
    gap: 20px;
}

.form-group {
    display: flex;
    flex-direction: column;
}

label {
    margin-bottom: 5px;
    font-weight: bold;
}

input, textarea {
    padding: 10px;
    border: 1px solid #ddd;
    border-radius: 4px;
    font-size: 1em;
}

textarea {
    height: 200px;
}

.file-preview {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    margin-top: 10px;
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

.submit-btn {
    padding: 10px 20px;
    background-color: #4CAF50;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-size: 1em;
}

.submit-btn:hover {
    background-color: #45a049;
}

.loading, .error {
    text-align: center;
    padding: 20px;
    font-size: 1.2em;
}

.error {
    color: #ff0000;
}
</style>
