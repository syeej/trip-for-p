<script setup>
import {defineEmits, onMounted, onUnmounted, ref} from 'vue';
import axios from "axios";

const showResults = ref(false);
const searchQuery = ref('');
const searchResults = ref([]);
const searchInputRef = ref([]);
const searchComponentRef = ref(null);
const isSearching = ref(false);
const emit = defineEmits(['place-selected']);

const getCategoryName = (contentTypeId) => {
    switch (contentTypeId) {
        case '12': return '관광지';
        case '14': return '문화시설';
        case '15': return '축제공연행사';
        case '25': return '여행코스';
        case '28': return '레포츠';
        case '32': return '숙박';
        case '38': return '쇼핑';
        case '39': return '음식점';
        default: return '기타';
    }
};

const searchPlace = async () => {
    if (!searchQuery.value) return;

    showResults.value = true;
    isSearching.value = true;


    try {
        const response = await axios.get('https://apis.data.go.kr/B551011/KorService1/searchKeyword1', {
            params: {
                serviceKey: process.env.VUE_APP_DATA_KEY,
                numOfRows: 100,
                pageNo: 1,
                MobileOS: 'ETC',
                MobileApp: 'AppTest',
                _type: 'json',
                listYN: 'Y',
                arrange: 'A',
                keyword: searchQuery.value
            }
        });

        if (response.data.response.header.resultCode === '0000') {
            searchResults.value = response.data.response.body.items.item
                ? response.data.response.body.items.item
                .filter(item =>
                    item.contentid &&
                    item.addr1 &&
                    getCategoryName(item.contenttypeid) &&
                    item.title
                )
                .map(item => ({
                    id: item.contentid,
                    address_name: item.addr1,
                    category_name: getCategoryName(item.contenttypeid),
                    place_name: item.title,
                    x: item.mapx,
                    y: item.mapy,
                    image_url: item.firstimage || ''
                }))
                : [];
        } else {
            console.error('API 오류:', response.data.response.header.resultMsg);
            searchResults.value = [];
        }
    } catch (error) {
        console.error('검색 중 오류 발생:', error);
        searchResults.value = [];
    } finally {
        isSearching.value = false;
        console.log('Search completed. Results:', searchResults.value.length);
    }
};

const selectPlace = (place) => {
    emit('place-selected', place);
    searchQuery.value = '';
    searchResults.value = [];
    showResults.value = false;
};

const scrollToInput = () => {
    if (searchInputRef.value) {
        searchInputRef.value.scrollIntoView({ behavior: 'smooth', block: 'center' });
    }
};

const handleClickOutside = (event) => {
    if (searchComponentRef.value && !searchComponentRef.value.contains(event.target)) {
        showResults.value = false;
    }
};

onMounted(() => {
    document.addEventListener('click', handleClickOutside);
});

onUnmounted(() => {
    document.removeEventListener('click', handleClickOutside);
});
</script>

<template>
    <div class="search-place" ref="searchComponentRef">
        <div class="search-input-container">
            <input
                ref="searchInputRef"
                v-model="searchQuery"
                @keyup.enter="searchPlace"
                @focus="scrollToInput"
                placeholder="장소를 검색하세요"
                class="search-input"
            >
            <button @click="searchPlace" class="search-button">검색</button>
        </div>
        <div v-if="showResults" class="search-results">
            <div v-if="isSearching" class="searching">검색 중...</div>
            <ul v-else-if="searchResults.length > 0">
                <li v-for="place in searchResults" :key="place.id" @click="selectPlace(place)">
                    <div class="place-name">{{ place.place_name }}</div>
                    <div class="place-address">{{ place.address_name }}</div>
                </li>
            </ul>
            <div v-else class="no-results">
                검색 결과가 없습니다.
            </div>
        </div>
    </div>
</template>

<style scoped>
.search-place {
    margin-bottom: 20px;
    font-family: Arial, sans-serif;
}

.search-input-container {
    display: flex;
    margin-bottom: 10px;
}

.search-input {
    flex-grow: 1;
    padding: 10px;
    font-size: 16px;
    border: 2px solid #ddd;
    border-radius: 4px 0 0 4px;
    transition: border-color 0.3s ease;
}

.search-input:focus {
    outline: none;
    border-color: #4CAF50;
}

.search-button {
    padding: 10px 20px;
    font-size: 16px;
    color: white;
    background-color: #4CAF50;
    border: none;
    border-radius: 0 4px 4px 0;
    cursor: pointer;
    transition: background-color 0.3s ease;
}

.search-button:hover {
    background-color: #45a049;
}

.search-results {
    position: absolute;
    width: 100%;
    background-color: white;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    z-index: 1001;
    max-height: 300px;
    overflow-y: auto;
}

.searching, .no-results {
    padding: 20px;
    text-align: center;
    color: #666;
    font-style: italic;
}

.search-results li {
    cursor: pointer;
    padding: 10px;
    border-bottom: 1px solid #eee;
    transition: background-color 0.3s ease;
}

.search-results li:last-child {
    border-bottom: none;
}

.search-results li:hover {
    background-color: #f5f5f5;
}

.place-name {
    font-weight: bold;
    margin-bottom: 3px;
}

.place-address {
    font-size: 0.9em;
    color: #666;
}

@media (max-width: 600px) {
    .search-input-container {
        flex-direction: column;
    }

    .search-input, .search-button {
        width: 100%;
        border-radius: 4px;
    }

    .search-button {
        margin-top: 10px;
    }
}
</style>
