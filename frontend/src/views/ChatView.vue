<script setup>
import { ref } from 'vue';
import axios from 'axios';

// 상태 변수 선언
const question = ref('');
const messages = ref([]);

// 메시지 전송 함수
const sendMessage = async () => {
    if (!question.value.trim()) return;

    // 사용자가 입력한 질문 추가
    messages.value.push({ sender: 'You', content: question.value });

    try {
        const response = await axios.get(`/api/v1/question`, {
            params: {
                content: question.value,
                client_id: 'c95c87c1-7647-486e-9e55-a4c5e9bdb742',
            },
        });

        // 응답을 메시지로 추가
        messages.value.push({ sender: 'Bot', content: response.data.content });
    } catch (error) {
        messages.value.push({ sender: 'Bot', content: 'Error: Unable to get a response from the server.' });
    }

    // 입력 필드 초기화
    question.value = '';
};
</script>

<template>
    <div class="chat-container">
        <div class="chat-box">
            <div v-for="(message, index) in messages" :key="index" class="message">
                <span class="message-sender">{{ message.sender }}:</span>
                <span class="message-content">{{ message.content }}</span>
            </div>
        </div>
        <div class="chat-input">
            <input
                v-model="question"
                type="text"
                placeholder="Type your question here..."
                @keyup.enter="sendMessage"
            />
            <button @click="sendMessage">Send</button>
        </div>
    </div>
</template>

<style scoped>
.chat-container {
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    width: 400px;
    height: 500px;
    border: 1px solid #ddd;
    padding: 20px;
    margin: 0 auto;
}

.chat-box {
    flex: 1;
    overflow-y: auto;
    padding-bottom: 10px;
    border-bottom: 1px solid #ddd;
}

.message {
    margin-bottom: 10px;
}

.message-sender {
    font-weight: bold;
}

.chat-input {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.chat-input input {
    width: 80%;
    padding: 10px;
    font-size: 16px;
    border: 1px solid #ddd;
}

.chat-input button {
    padding: 10px 15px;
    font-size: 16px;
    background-color: #007BFF;
    color: white;
    border: none;
    cursor: pointer;
}

.chat-input button:hover {
    background-color: #0056b3;
}
</style>
