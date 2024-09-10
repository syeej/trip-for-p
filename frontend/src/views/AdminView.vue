<script setup>
import {ref, onMounted} from 'vue';
import {getMagazineListAPI} from "@/api";
import router from "@/router";

const magazines = ref([]);
const currentPage = ref(1);
const pageSize = ref(10);
const totalItems = ref(0);

const getMagazineList = async function () {
    try {
        const request = {
            keyword: '',
            size: pageSize.value,
            page: currentPage.value - 1,
        };
        const response = await getMagazineListAPI(request);
        magazines.value = response.data.content;
        totalItems.value = response.data.totalElements;
    } catch (error) {
        console.error('매거진 목록을 불러오는 중 오류 발생:', error);
    }
};

const changePage = async (page) => {
    currentPage.value = page;
    await getMagazineList();
};

const navigateToRegistration = () => {
    router.push('/admin/magazine/write')
};

const navigateToDetail = (magazineId) => {
    router.push(`/magazine/${magazineId}`);
};

onMounted(getMagazineList);
</script>

<template>
    <div class="admin-page">
        <h1>관리자 페이지</h1>
        <button @click="navigateToRegistration" class="register-button">매거진 등록</button>

        <div class="magazine-list">
            <h2>매거진 목록</h2>
            <table>
                <thead>
                <tr>
                    <th>ID</th>
                    <th>제목</th>
                    <th>작성일</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="magazine in magazines" :key="magazine.id"
                    @click="navigateToDetail(magazine.id)" class="clickable-row">
                    <td>{{ magazine.id }}</td>
                    <td>{{ magazine.title }}</td>
                    <td>{{ new Date(magazine.createdAt).toLocaleDateString() }}</td>
                </tr>
                </tbody>
            </table>
        </div>

        <div class="pagination">
            <button
                @click="changePage(currentPage - 1)"
                :disabled="currentPage === 1"
            >
                이전
            </button>
            <span>{{ currentPage }} / {{ Math.ceil(totalItems / pageSize) }}</span>
            <button
                @click="changePage(currentPage + 1)"
                :disabled="currentPage >= Math.ceil(totalItems / pageSize)"
            >
                다음
            </button>
        </div>
    </div>
</template>

<style scoped>
.admin-page {
    width: 100%;
    padding: 20px;
}

.register-button {
    background-color: #4CAF50;
    border: none;
    color: white;
    padding: 10px 20px;
    text-align: center;
    text-decoration: none;
    display: inline-block;
    font-size: 16px;
    margin: 4px 2px;
    cursor: pointer;
}

.magazine-list {
    margin-top: 20px;
}

table {
    width: 100%;
    border-collapse: collapse;
}

th, td {
    border: 1px solid #ddd;
    padding: 8px;
    text-align: left;
}

th {
    background-color: #f2f2f2;
}

.clickable-row {
    cursor: pointer;
    transition: background-color 0.3s;
}

.clickable-row:hover {
    background-color: #f5f5f5;
}

.pagination {
    margin-top: 20px;
    display: flex;
    justify-content: center;
    align-items: center;
}

.pagination button {
    margin: 0 10px;
    padding: 5px 10px;
    cursor: pointer;
}

.pagination span {
    margin: 0 10px;
}
</style>
