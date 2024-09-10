<script setup>
import { useRoute } from "vue-router";
import { ref, onMounted } from "vue";
import { updateMagazineAPI, getMagazineAPI } from "@/api";
import router from "@/router";

const title = ref('');
const content = ref('');
const files = ref([]);
const route = useRoute();
const existingFiles = ref([]);
const loading = ref(true);
const error = ref(null);

const handleFileChange = (event) => {
    files.value = Array.from(event.target.files);
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

        files.value.forEach((file) => {
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
                <label>기존 파일:</label>
                <div v-if="existingFiles.length > 0" class="existing-files">
                    <div v-for="fileUrl in existingFiles" :key="fileUrl" class="existing-file">
                        <img :src="fileUrl" alt="기존 첨부 파일">
                    </div>
                </div>
                <p v-else>기존 첨부 파일이 없습니다.</p>
            </div>

            <div class="form-group">
                <label for="files">새 파일 첨부:</label>
                <input type="file" id="files" @change="handleFileChange" multiple>
                <p v-if="files.length > 0">선택된 파일: {{ files.map(f => f.name).join(', ') }}</p>
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

.existing-files {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    margin-top: 10px;
}

.existing-file img {
    width: 100px;
    height: 100px;
    object-fit: cover;
    border-radius: 4px;
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
