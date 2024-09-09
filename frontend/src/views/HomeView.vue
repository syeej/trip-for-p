<script setup>
import {onMounted, ref} from 'vue';
import {getMagazineListAPI} from "@/api";

const magazines = ref([]);

const getMagazineList = async function() {
    try {
        const request = {
            size: 6,
            page: 0,
        }
        const response = await getMagazineListAPI(request);
        magazines.value = response.data.content;

    } catch (error) {
        console.log(error);
    }
}

const goToDetail = (id) => {
    // 실제 구현 시 라우터를 사용하여 상세 페이지로 이동
    console.log(`매거진 ${id}의 상세 페이지로 이동`);
};

onMounted(() => {
    getMagazineList();
})
</script>

<template>
    <div class="magazine-container">
        <h1 class="magazine-title">여행 코스 추천 매거진</h1>
        <div class="magazine-grid">
            <div v-for="magazine in magazines" :key="magazine.id"
                 class="magazine-item"
                 @click="goToDetail(magazine.id)">
                <img :src="magazine.fileUrls[0]" :alt="magazine.title" class="magazine-image">
                <div class="magazine-content">
                    <h2 class="magazine-item-title">{{ magazine.title }}</h2>
                    <p class="magazine-description">{{ magazine.description }}</p>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
.magazine-container {
    width: 100%;
    max-width: 1200px;
    margin: 0 auto;
    padding: 2rem 1rem;
}

.magazine-title {
    font-size: 2.5rem;
    font-weight: bold;
    text-align: center;
    margin-bottom: 2rem;
    color: #333;
}

.magazine-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
    gap: 2rem;
}

.magazine-item {
    background-color: #fff;
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    transition: transform 0.3s ease, box-shadow 0.3s ease;
    cursor: pointer;
}

.magazine-item:hover {
    transform: translateY(-5px);
    box-shadow: 0 6px 12px rgba(0, 0, 0, 0.15);
}

.magazine-image {
    width: 100%;
    height: 200px;
    object-fit: cover;
}

.magazine-content {
    padding: 1.5rem;
}

.magazine-item-title {
    font-size: 1.25rem;
    font-weight: 600;
    margin-bottom: 0.5rem;
    color: #222;
}

.magazine-description {
    font-size: 0.9rem;
    color: #666;
}

@media (max-width: 768px) {
    .magazine-grid {
        grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    }

    .magazine-title {
        font-size: 2rem;
    }
}

@media (max-width: 480px) {
    .magazine-grid {
        grid-template-columns: 1fr;
    }

    .magazine-title {
        font-size: 1.75rem;
    }
}
</style>
