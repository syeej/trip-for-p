<script setup>
import {computed, onMounted, ref} from 'vue';
import {getMagazineListAPI, getPopularPlaceListAPI} from "@/api";
import locationImage from '@/assets/location.png'

const magazines = ref([]);
const places = ref([]);

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

const getPopularPlaceList = async function () {
    try {
        const response = await getPopularPlaceListAPI();
        places.value = response.data;
    } catch (error) {
        console.log(error);
    }
};

const getImageSrc = computed(() => (imageUrl) => {
    return imageUrl && imageUrl.trim() !== '' ? imageUrl : locationImage;
});

const goToMagazineDetail = (id) => {
    console.log(`매거진 ${id}의 상세 페이지로 이동`);
};

const goToPlaceDetail = (id) => {
    console.log(`장소 ${id}의 상세 페이지로 이동`);
};

onMounted(() => {
    getMagazineList();
    getPopularPlaceList();
})
</script>

<template>
    <div class="container">
        <section class="magazine-section">
            <h1 class="section-title">여행 코스 추천 매거진</h1>
            <div class="grid">
                <div v-for="magazine in magazines" :key="magazine.id"
                     class="item"
                     @click="goToMagazineDetail(magazine.id)">
                    <img :src="magazine.fileUrls[0]" :alt="magazine.title" class="item-image">
                    <div class="item-content">
                        <h2 class="item-title">{{ magazine.title }}</h2>
                        <p class="item-description">{{ magazine.description }}</p>
                    </div>
                </div>
            </div>
        </section>

        <section class="popular-places-section">
            <h1 class="section-title">인기 장소</h1>
            <div class="grid">
                <div v-for="place in places" :key="place.place.id"
                     class="item"
                     @click="goToPlaceDetail(place.place.id)">
                    <img :src="getImageSrc(place.place.imageUrl)"
                         :alt="place.place.placeName"
                         class="item-image">
                    <div class="item-content">
                        <h2 class="item-title">{{ place.place.placeName }}</h2>
                        <p class="item-description">방문 횟수: {{ place.count }}</p>
                    </div>
                </div>
            </div>
        </section>
    </div>
</template>

<style scoped>
.container {
    width: 100%;
    max-width: 1200px;
    margin: 0 auto;
    padding: 2rem 1rem;
}

.section-title {
    font-size: 2.5rem;
    font-weight: bold;
    text-align: center;
    margin-bottom: 2rem;
    color: #333;
}

.grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
    gap: 2rem;
    margin-bottom: 4rem;
}

.item {
    background-color: #fff;
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    transition: transform 0.3s ease, box-shadow 0.3s ease;
    cursor: pointer;
}

.item:hover {
    transform: translateY(-5px);
    box-shadow: 0 6px 12px rgba(0, 0, 0, 0.15);
}

.item-image {
    width: 100%;
    height: 200px;
    object-fit: cover;
}

.item-content {
    padding: 1.5rem;
}

.item-title {
    font-size: 1.25rem;
    font-weight: 600;
    margin-bottom: 0.5rem;
    color: #222;
}

.item-description {
    font-size: 0.9rem;
    color: #666;
}

@media (max-width: 768px) {
    .grid {
        grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    }

    .section-title {
        font-size: 2rem;
    }
}

@media (max-width: 480px) {
    .grid {
        grid-template-columns: 1fr;
    }

    .section-title {
        font-size: 1.75rem;
    }
}
</style>
