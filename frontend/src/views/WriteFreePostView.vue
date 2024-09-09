<script setup>
import { ref } from 'vue';
import {createFreePostAPI} from "@/api";
import router from "@/router";

const content = ref('');

const createFreePost = async function() {
    try {
        const request = {
            content: content.value
        }
        const response = await createFreePostAPI(request);
        await router.push(`/free-post/${response.data.id}`);
    } catch (error) {
        console.log(error);
    }
};
</script>

<template>
    <div class="post-create-container">
        <h2>자유 게시글 작성</h2>
        <form @submit.prevent="createFreePost" class="post-form">
            <div class="form-group">
                <label for="content">내용</label>
                <textarea id="content" v-model="content" required placeholder="내용을 입력하세요"></textarea>
            </div>
            <div class="form-actions">
                <button type="submit" class="submit-btn">게시글 작성</button>
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

.post-create-container {
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
</style>
