<script setup>
import { ref } from "vue";
import { createMagazineAPI } from "@/api";
import router from "@/router";

const title = ref('');
const content = ref('');
const files = ref([]);

const handleFileChange = (event) => {
    files.value = Array.from(event.target.files);
};

const createMagazine = async function () {
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

        const response = await createMagazineAPI(formData);
        await router.push(`/magazine/${response.data.id}`)
    } catch (error) {
        console.error(error);
    }
};
</script>

<template>
    <div class="magazine-registration">
        <h1>매거진 등록</h1>
        <form @submit.prevent="createMagazine" class="registration-form">
            <div class="form-group">
                <label for="title">제목:</label>
                <input id="title" v-model="title" required>
            </div>

            <div class="form-group">
                <label for="content">내용:</label>
                <textarea id="content" v-model="content" required></textarea>
            </div>

            <div class="form-group">
                <label for="files">파일 첨부:</label>
                <input type="file" id="files" @change="handleFileChange" multiple>
                <p v-if="files.length > 0">선택된 파일: <br>{{ files.map(f => f.name).join(', ') }}</p>
            </div>

            <button type="submit" :disabled="!files.length > 0">매거진 등록</button>
        </form>
    </div>
</template>

<style scoped>
.magazine-registration {
    width: 100%;
    margin: 0 auto;
    padding: 20px;
}

.registration-form {
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
}

input, textarea {
    padding: 8px;
    border: 1px solid #ccc;
    border-radius: 4px;
}

textarea {
    height: 150px;
}

button {
    padding: 10px;
    background-color: #4CAF50;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
}

button:hover {
    background-color: #45a049;
}

button:disabled {
    background-color: #cccccc;
    cursor: not-allowed;
}
</style>
