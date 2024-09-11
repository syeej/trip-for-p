<script setup>
import {computed, onMounted, ref} from 'vue';
import SelectAreaComponent from "@/components/SelectAreaComponent.vue";
import SelectDayComponent from "@/components/SelectDayComponent.vue";
import SelectPlaceComponent from "@/components/SelectPlaceComponent.vue";
import {getAIUserAPI} from "@/api";

const currentStep = ref('area');
const selectedRegion = ref(null);
const startDate = ref(null);
const endDate = ref(null);
const answer = ref(null);
const showSidebar = ref(true);
const isLoading = ref(true);
const message = ref(null);
const parsedData = ref(null);

const currentComponent = computed(() => {
    switch (currentStep.value) {
        case 'area': return SelectAreaComponent;
        case 'day': return SelectDayComponent;
        case 'place': return SelectPlaceComponent;
        default: return SelectAreaComponent;
    }
});

const getAIUser = async function () {
    try {
        message.value = '데이터를 불러오는 중';
        const response = await getAIUserAPI('ebaa62fc-99af-4a4b-850a-99cfde16f7eb');
        answer.value = response.data.content;
        if (answer.value !== '사용자 데이터가 부족합니다.') {
            parseAnswer(answer.value)
        }
    } catch (error) {
        message.value = 'AI가 여행 계획을 생성 중에 오류가 발생하였습니다';
        console.log(error);
        console.log(error.response);

    } finally {
        if (answer.value !== '사용자 데이터가 부족합니다.') {
            isLoading.value = false;
        } else {
            message.value = answer.value;
        }

    }
};

const parseAnswer = (jsonString) => {
    try {
        const cleanJson = jsonString.replace(/^```json\s?/, '').replace(/```$/, '').trim();
        parsedData.value = JSON.parse(cleanJson);
    } catch (error) {
        console.error('JSON 파싱 중 오류 발생:', error);
        console.log('파싱 시도한 문자열:', jsonString);
    }
};

const toggleSidebar = () => {
    showSidebar.value = !showSidebar.value;
};


const handleAreaSelected = (region) => {
    selectedRegion.value = region;
    currentStep.value = 'day';
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

        <div class="sidebar-container">
            <button v-if="!showSidebar" class="open-sidebar" @click="toggleSidebar">
                &#9776; <!-- 햄버거 아이콘 -->
            </button>
            <div v-show="showSidebar" class="sidebar" :class="{ hidden: !showSidebar }">
                <button class="close-sidebar" @click="toggleSidebar">&times;</button>
                <h2 class="sidebar-title">AI 추천</h2>
                <div v-if="isLoading" class="loading-spinner">
                    {{ message }}<span v-if="message !== answer" class="loading-dots"></span>
                </div>
                <div v-else-if="parsedData" class="region-list">
                    <div v-for="(places, region) in parsedData" :key="region" class="region-item">
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
.open-sidebar {
    position: fixed;
    right: 20px;
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
.open-sidebar:hover {
    background-color: #0056b3;
}

.sidebar {
    position: fixed;
    right: 0;
    top: 0;
    width: 320px;
    height: 100vh;
    background-color: #f8f9fa;
    box-shadow: -2px 0 10px rgba(0,0,0,0.1);
    overflow-y: auto;
    padding: 20px;
    z-index: 1000;
    transition: transform 0.3s ease;
}

.sidebar.hidden {
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

.region-list {
    margin-top: 20px;
}

.region-item {
    margin-bottom: 25px;
    background-color: #fff;
    border-radius: 8px;
    padding: 15px;
    box-shadow: 0 2px 5px rgba(0,0,0,0.05);
}

.region-name {
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

.place-item {
    margin-bottom: 15px;
}

.place-name {
    font-size: 1.1em;
    color: #007bff;
    margin-bottom: 5px;
}

.place-description {
    font-size: 0.9em;
    color: #555;
    line-height: 1.4;
}
</style>
