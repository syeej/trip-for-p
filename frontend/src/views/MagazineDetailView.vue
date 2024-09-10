<script setup>
import { computed, onMounted, ref } from "vue";
import { getMagazineAPI, deleteMagazineAPI } from "@/api";
import { useRoute, useRouter } from "vue-router";
import store from "@/store";

const magazine = ref({});
const route = useRoute();
const router = useRouter();
const loading = ref(true);
const error = ref(null);

const isAdmin = computed(() => store.getters.getRole === 'ADMIN');

const getMagazine = async function () {
    try {
        loading.value = true;
        const response = await getMagazineAPI(route.params.magazineId);
        magazine.value = response.data;
    } catch (err) {
        console.error(err);
        error.value = "매거진을 불러오는 데 실패했습니다.";
    } finally {
        loading.value = false;
    }
};

const editMagazine = () => {
    router.push(`/admin/magazine/${magazine.value.id}/edit`);
};

const deleteMagazine = async () => {
    if (confirm('매거진을 삭제하시겠습니까?')) {
        try {
            await deleteMagazineAPI(magazine.value.id);
            alert('매거진이 성공적으로 삭제되었습니다.');
            await router.push('/admin');
        } catch (err) {
            console.error(err);
            alert('매거진 삭제 중 오류가 발생했습니다.');
        }
    }
};

onMounted(() => {
    getMagazine();
});
</script>

<template>
    <div class="magazine-detail">
        <div v-if="loading" class="loading">매거진을 불러오는 중...</div>
        <div v-else-if="error" class="error">{{ error }}</div>
        <div v-else>
            <h1>{{ magazine.title }}</h1>
            <div class="meta-info">
                <p>작성일: {{ new Date(magazine.createdAt).toLocaleDateString() }}</p>
                <p>조회수: {{ magazine.views }}</p>
            </div>
            <pre class="content">{{ magazine.content }}</pre>
            <div v-if="magazine.fileUrls && magazine.fileUrls.length > 0" class="attachments">
                <div class="image-gallery">
                    <img v-for="fileUrl in magazine.fileUrls" :key="fileUrl" :src="fileUrl"
                         alt="첨부 이미지">
                </div>
            </div>
            <div class="admin-controls" v-if="isAdmin">
                <button @click="editMagazine" class="edit-btn">수정</button>
                <button @click="deleteMagazine" class="delete-btn">삭제</button>
            </div>
        </div>
    </div>
</template>

<style scoped>
.magazine-detail {
    width: 100%;
    margin: 0 auto;
    padding: 20px;
}

h1 {
    font-size: 2.5em;
    color: #333;
    margin-bottom: 10px;
}

.meta-info {
    font-size: 0.9em;
    color: #666;
    margin-bottom: 20px;
}

.content {
    line-height: 1.6;
    margin-bottom: 30px;
    white-space: pre-wrap;
    word-wrap: break-word;
}

.attachments {
    margin-top: 20px;
}

.image-gallery {
    display: flex;
    justify-content: space-between;
    flex-wrap: wrap;
    gap: 50px;
}

.image-gallery img {
    width: 40%;
    height: auto;
    transition: transform 0.3s ease;
}

.loading, .error {
    text-align: center;
    padding: 20px;
    font-size: 1.2em;
}

.error {
    color: #ff0000;
}

.admin-controls {
    margin-top: 50px;
    display: flex;
    width: 100%;
    justify-content: end;
}

.edit-btn, .delete-btn {
    padding: 10px 20px;
    margin-right: 10px;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    font-weight: bold;
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
</style>
