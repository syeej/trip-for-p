<script setup>
import {defineEmits, onMounted, ref} from 'vue';

const searchQuery = ref('');
const searchResults = ref([]);
const searchInputRef = ref(null);
const emit = defineEmits(['place-selected']);

const searchPlace = () => {
    if (!searchQuery.value) return;

    const places = new kakao.maps.services.Places();
    places.keywordSearch(searchQuery.value, (result, status) => {
        if (status === kakao.maps.services.Status.OK) {
            searchResults.value = result;
        }
    });
};

const selectPlace = (place) => {
    emit('place-selected', place);
    searchQuery.value = '';
    searchResults.value = [];
};
const scrollToInput = () => {
    if (searchInputRef.value) {
        searchInputRef.value.scrollIntoView({ behavior: 'smooth', block: 'center' });
    }
};
onMounted(() => {
    /* global kakao */
})
</script>

<template>
    <div class="search-place">
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
        <ul v-if="searchResults.length > 0" class="search-results">
            <li v-for="place in searchResults" :key="place.id" @click="selectPlace(place)">
                <div class="place-name">{{ place.place_name }}</div>
                <div class="place-address">{{ place.address_name }}</div>
            </li>
        </ul>
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
    list-style-type: none;
    padding: 0;
    max-height: 300px;
    overflow-y: auto;
    border: 1px solid #ddd;
    border-radius: 4px;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
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
