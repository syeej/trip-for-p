<script setup>
import {computed, onMounted, ref} from 'vue';
import SelectAreaComponent from "@/components/SelectAreaComponent.vue";
import SelectDayComponent from "@/components/SelectDayComponent.vue";
import SelectPlaceComponent from "@/components/SelectPlaceComponent.vue";
import {getAIInfoAPI, getAIUserAPI} from "@/api";

const currentStep = ref('area');
const selectedRegion = ref(null);
const startDate = ref(null);
const endDate = ref(null);
const userAnswer = ref(null);
const infoAnswer = ref(null);
const showRightSidebar = ref(true);
const showLeftSidebar = ref(false);
const isLoading = ref(true);
const isInfoLoading = ref(false);
const message = ref(null);
const infoMessage = ref(null);
const parsedUserData = ref(null);
const parsedInfoData = ref(null);
const userErrorMessage = ref(null);
const infoErrorMessage = ref(null);

const currentComponent = computed(() => {
    switch (currentStep.value) {
        case 'area': return SelectAreaComponent;
        case 'day': return SelectDayComponent;
        case 'place': return SelectPlaceComponent;
        default: return SelectAreaComponent;
    }
});

const recommendationKey = computed(() => {
    return selectedRegion.value ? `${selectedRegion.value} 여행 추천` : '';
});

const weatherIcon = computed(() => (weather) => {
    switch (weather) {
        case '비': return '☔';
        case '소나기': return '🌦️';
        case '흐림': return '☁️';
        case '구름많음': return '⛅';
        default: return '🌤️';
    }
});

const getRecommendationIcon = (key) => {
    switch (key.toLowerCase()) {
        case '체험행사': return '🎭';
        case '축제': return '🎉';
        case '여행지': return '🏞️';
        case '맛집': return '🍽️';
        case '숙소': return '🏨';
        default: return '📌';
    }
};

const formatRecommendationType = (key) => {
    return key.charAt(0).toUpperCase() + key.slice(1);
};

const isWithinOneWeek = computed(() => {
    if (!endDate.value) return false;
    const today = new Date();
    const oneWeekLater = new Date(today.getTime() + 7 * 24 * 60 * 60 * 1000);
    const endDateObj = new Date(endDate.value);
    return endDateObj <= oneWeekLater;
});

const weatherData = computed(() => {
    if (!parsedInfoData.value || !isWithinOneWeek.value) return null;
    return Object.entries(parsedInfoData.value).filter(([key]) => key !== recommendationKey.value);
});


const getAIUser = async function () {
    try {
        message.value = '추천 정보를 불러오는 중';
        userErrorMessage.value = null;
        const response = await getAIUserAPI('22b6f30e-e43a-4b66-a7f1-320e7b724c96');
        userAnswer.value = response.data.content;
        if (userAnswer.value !== '사용자 데이터가 부족합니다.') {
            isLoading.value = false;
            parseUserAnswer(userAnswer.value)
        } else {
            message.value = userAnswer.value;
        }
    } catch (error) {
        isLoading.value = false;
        userErrorMessage.value = '정보를 불러오는 중 오류가 발생하였습니다';
        console.log(error);
    }
};

const getAIInfo = async function () {
    try {
        isInfoLoading.value = true;
        infoMessage.value = '지역 정보를 불러오는 중';
        infoErrorMessage.value = null;
        const request = {
            clientId: '4e37d38c-a72c-4cf8-8fa2-0a945e964b1c',
            area: selectedRegion.value,
            startDate: startDate.value,
            endDate: endDate.value,
        }
        const response = await getAIInfoAPI(request);
        infoAnswer.value = response.data.content;
        parseInfoAnswer(infoAnswer.value);
    } catch (error) {
        infoErrorMessage.value = '정보를 불러오는 중 오류가 발생하였습니다';
        console.log(error);
    } finally {
        isInfoLoading.value = false;
    }
};

const parseJsonFromString = (jsonString) => {
    try {
        // JSON 형식의 데이터를 찾기 위한 정규 표현식
        const jsonRegex = /```json([\s\S]*?)```/;
        const match = jsonString.match(jsonRegex);
        console.log(match)
        if (match) {
            const jsonData = JSON.parse(match[1].trim());
            return jsonData;
        } else {
            console.error('No valid JSON found in the string');
            return null;
        }
    } catch (error) {
        console.error('JSON 파싱 중 오류 발생:', error);
        console.log('파싱 시도한 문자열:', jsonString);
        return null;
    }
};

const parseUserAnswer = (jsonString) => {
    const parsedData = parseJsonFromString(jsonString);
    if (parsedData) {
        parsedUserData.value = parsedData;
        console.log(parsedUserData.value)
    }
};

const parseInfoAnswer = (jsonString) => {
    const parsedData = parseJsonFromString(jsonString);
    if (parsedData) {
        parsedInfoData.value = parsedData;
        console.log(parsedInfoData.value)
    }
};

const toggleRightSidebar = () => {
    showRightSidebar.value = !showRightSidebar.value;
};

const toggleLeftSidebar = () => {
    showLeftSidebar.value = !showLeftSidebar.value;
};

const handleAreaSelected = (region) => {
    selectedRegion.value = region;
    currentStep.value = 'day';
    // 버튼으로 스크롤
    setTimeout(() => {
        const button = document.querySelector('.calendar');
        if (button) {
            button.scrollIntoView({ behavior: 'smooth', block: 'center' });
        }
    }, 100);
};

const handleBackToArea = () => {
    currentStep.value = 'area';
    startDate.value = null;
    endDate.value = null;
};

const handleDatesSelected = (dates) => {
    startDate.value = dates.start;
    endDate.value = dates.end;
    currentStep.value = 'place';
    showLeftSidebar.value = true;
    setTimeout(() => {
        const button = document.querySelector('.map-search-container');
        if (button) {
            button.scrollIntoView({ behavior: 'smooth', block: 'center' });
        }
    }, 100);
    getAIInfo();
};

const handleBackToDay = () => {
    currentStep.value = 'day';
};

const handleTripPlanCompleted = () => {
    console.log('Trip plan completed');
};

onMounted(() => {
    getAIUser();
});
</script>

<template>
    <div class="travel-planner-container">
        <div class="main-content">
            <component
                :is="currentComponent"
                :selectedRegion="selectedRegion"
                :startDate="startDate"
                :endDate="endDate"
                @area-selected="handleAreaSelected"
                @back-to-area="handleBackToArea"
                @dates-selected="handleDatesSelected"
                @back-to-day="handleBackToDay"
                @trip-plan-completed="handleTripPlanCompleted"
            />
        </div>

        <!-- 왼쪽 사이드바 -->
        <div class="sidebar-container left">
            <button v-if="!showLeftSidebar" class="open-sidebar left" @click="toggleLeftSidebar">
                &#9776;
            </button>
            <div v-show="showLeftSidebar" class="sidebar left" :class="{ hidden: !showLeftSidebar }">
                <button class="close-sidebar" @click="toggleLeftSidebar">&times;</button>
                <h2 class="sidebar-title">지역 정보</h2>
                <div v-if="isInfoLoading" class="loading-spinner">
                    {{ infoMessage }}<span class="loading-dots"></span>
                </div>
                <div v-else-if="infoErrorMessage" class="error-message">
                    {{ infoErrorMessage }}
                    <button @click="getAIInfo" class="retry-button">다시 시도</button>
                </div>
                <div v-else-if="parsedInfoData" class="info-content">
                    <div v-if="isWithinOneWeek && weatherData">
                        <div v-for="[key, data] in weatherData" :key="key" class="info-item">
                            <h3>{{ key }}</h3>
                            <p>{{ weatherIcon(data.날씨) }} 날씨: {{ data.날씨 }}</p>
                            <p>🌡️ 기온: {{ data.최저기온 }} ~ {{ data.최고기온 }}</p>
                            <p>💧 강수확률: {{ data.강수확률 }}</p>
                        </div>
                    </div>
                    <div v-else-if="!isWithinOneWeek" class="info-message">
                        날씨 정보는 1주일 이내의 여행에 대해서만 제공됩니다.
                    </div>
                    <div v-if="parsedInfoData && parsedInfoData[recommendationKey]" class="travel-recommendations">
                        <h3 class="recommendations-title">{{ recommendationKey }}</h3>
                        <div class="recommendations-list">
                            <div v-for="(value, key) in parsedInfoData[recommendationKey]" :key="key" class="recommendation-item">
                                <span class="recommendation-icon">{{ getRecommendationIcon(key) }}</span>
                                <div class="recommendation-content">
                                    <h4 class="recommendation-type">{{ formatRecommendationType(key) }}</h4>
                                    <p class="recommendation-value">{{ value }}</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- 오른쪽 사이드바 -->
        <div class="sidebar-container right">
            <button v-if="!showRightSidebar" class="open-sidebar right" @click="toggleRightSidebar">
                &#9776;
            </button>
            <div v-show="showRightSidebar" class="sidebar right" :class="{ hidden: !showRightSidebar }">
                <button class="close-sidebar" @click="toggleRightSidebar">&times;</button>
                <h2 class="sidebar-title">AI 추천</h2>
                <div v-if="isLoading" class="loading-spinner">
                    {{ message }}<span v-if="message !== userAnswer" class="loading-dots"></span>
                </div>
                <div v-else-if="userErrorMessage" class="error-message">
                    {{ userErrorMessage }}
                    <button @click="getAIUser" class="retry-button">다시 시도</button>
                </div>
                <div v-else-if="parsedUserData" class="region-list">
                    <div v-for="(places, region) in parsedUserData" :key="region" class="region-item">
                        <h3 class="region-name">{{ region }}</h3>
                        <ul class="place-list">
                            <li v-for="place in places" :key="place.명소" class="place-item">
                                <h4 class="place-name">{{ place.명소 }}</h4>
                                <p class="place-description">{{ place.설명 }}</p>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
.travel-planner-container {
    display: flex;
    width: 100%;
    height: 100vh;
    position: relative;
}

.main-content {
    flex-grow: 1;
    width: 100%;
    padding-top: 2em;
}

.sidebar-container {
    position: relative;
}
.info-message {
    padding: 15px;
    background-color: #f8d7da;
    color: #721c24;
    border: 1px solid #f5c6cb;
    border-radius: 4px;
    margin-bottom: 20px;
}

.open-sidebar {
    position: fixed;
    top: 20px;
    background-color: #007bff;
    color: white;
    border: none;
    border-radius: 5px;
    padding: 10px 15px;
    font-size: 18px;
    cursor: pointer;
    z-index: 999;
    transition: background-color 0.3s;
}

.open-sidebar.left {
    left: 20px;
}

.open-sidebar.right {
    right: 20px;
}

.open-sidebar:hover {
    background-color: #0056b3;
}

.sidebar {
    position: fixed;
    top: 0;
    width: 320px;
    height: 100vh;
    background-color: #f8f9fa;
    overflow-y: auto;
    padding: 20px;
    z-index: 1000;
    transition: transform 0.3s ease;
}

.sidebar.left {
    left: 0;
    box-shadow: 2px 0 10px rgba(0,0,0,0.1);
}

.sidebar.right {
    right: 0;
    box-shadow: -2px 0 10px rgba(0,0,0,0.1);
}

.sidebar.hidden.left {
    transform: translateX(-100%);
}

.sidebar.hidden.right {
    transform: translateX(100%);
}

.close-sidebar {
    position: absolute;
    top: 10px;
    right: 10px;
    background: none;
    border: none;
    font-size: 24px;
    cursor: pointer;
    color: #333;
    transition: color 0.2s;
}

.close-sidebar:hover {
    color: #007bff;
}

.sidebar-title {
    font-size: 1.5em;
    color: #007bff;
    margin-bottom: 20px;
    padding-bottom: 10px;
    border-bottom: 2px solid #007bff;
}

.loading-spinner {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100px;
    font-size: 1.1em;
    color: #666;
}

.loading-dots::after {
    content: '.';
    animation: dots 1.5s steps(5, end) infinite;
}

@keyframes dots {
    0%, 20% {
        color: rgba(0,0,0,0);
        text-shadow:
            .25em 0 0 rgba(0,0,0,0),
            .5em 0 0 rgba(0,0,0,0);
    }
    40% {
        color: #666;
        text-shadow:
            .25em 0 0 rgba(0,0,0,0),
            .5em 0 0 rgba(0,0,0,0);
    }
    60% {
        text-shadow:
            .25em 0 0 #666,
            .5em 0 0 rgba(0,0,0,0);
    }
    80%, 100% {
        text-shadow:
            .25em 0 0 #666,
            .5em 0 0 #666;
    }
}

.region-list, .info-content {
    margin-top: 20px;
}

.region-item, .info-item {
    margin-bottom: 25px;
    background-color: #fff;
    border-radius: 8px;
    padding: 15px;
    box-shadow: 0 2px 5px rgba(0,0,0,0.05);
}

.region-name, .info-item h3 {
    font-size: 1.3em;
    color: #333;
    margin-bottom: 15px;
    padding-bottom: 5px;
    border-bottom: 1px solid #e0e0e0;
}

.place-list {
    list-style-type: none;
    padding: 0;
}

.place-item, .info-item p {
    margin-bottom: 15px;
}

.place-name {
    font-size: 1.1em;
    color: #007bff;
    margin-bottom: 5px;
}

.place-description, .info-item p {
    font-size: 0.9em;
    color: #555;
    line-height: 1.4;
}

.travel-recommendations {
    background-color: #f0f8ff;
    border-radius: 8px;
    padding: 15px;
    margin-top: 20px;
}

.recommendations-title {
    color: #007bff;
    font-size: 1.3em;
    margin-bottom: 15px;
    border-bottom: 2px solid #007bff;
    padding-bottom: 5px;
}

.recommendations-list {
    display: flex;
    flex-direction: column;
    gap: 15px;
}

.recommendation-item {
    display: flex;
    align-items: flex-start;
    background-color: white;
    border-radius: 6px;
    padding: 10px;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.recommendation-icon {
    font-size: 1.5em;
    margin-right: 10px;
}

.recommendation-content {
    flex: 1;
}

.recommendation-type {
    font-size: 1.1em;
    color: #333;
    margin: 0 0 5px 0;
}

.recommendation-value {
    font-size: 0.9em;
    color: #555;
    margin: 0;
    line-height: 1.4;
}
.error-message {
    padding: 15px;
    background-color: #f8d7da;
    color: #721c24;
    border: 1px solid #f5c6cb;
    border-radius: 4px;
    margin-bottom: 20px;
    text-align: center;
}

.retry-button {
    margin-top: 10px;
    padding: 8px 15px;
    background-color: #007bff;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    transition: background-color 0.3s;
}

.retry-button:hover {
    background-color: #0056b3;
}
</style>
