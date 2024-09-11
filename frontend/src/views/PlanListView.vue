<script setup>
import {computed, onMounted, onUnmounted, ref, watch} from 'vue';
import SelectAreaComponent from "@/components/SelectAreaComponent.vue";
import router from "@/router";
import {useRoute} from "vue-router";
import {getPlanListAPI} from "@/api";

const route = useRoute();

const plans = ref([]);
const currentPage = ref(1);
const itemsPerPage = ref(10);
const isLoading = ref(false);
const selectedArea = ref(null);
const showSelection = ref(true);
const windowWidth = ref(window.innerWidth);
const totalElements = ref(0);
const totalPages = ref(0);

// 라우트 파라미터에서 지역 정보를 불러오는 함수
const loadSelectedAreaFromRoute = () => {
    const areaFromRoute = route.params.area;
    if (areaFromRoute) {
        selectedArea.value = areaFromRoute;
        showSelection.value = false;
        fetchPlans();
    } else {
        showSelection.value = true;
    }
};

const fetchPlans = async () => {
    if (!selectedArea.value) {
        return;
    }

    try {
        isLoading.value = true;
        const getPlanListRequest = {
            area: selectedArea.value,
            size: itemsPerPage.value,
            page: currentPage.value - 1
        }
        const response = await getPlanListAPI(getPlanListRequest);
        plans.value = response.data.content;
        totalElements.value = response.data.totalElements;
        totalPages.value = response.data.totalPages;
    } catch (error) {
        console.error('Error fetching plans:', error);
    } finally {
        isLoading.value = false;
    }
};

const changePage = (page) => {
    if (page >= 1 && page <= totalPages.value) {
        currentPage.value = page;
    }
};

const handleAreaSelected = (area) => {
    selectedArea.value = area;
    showSelection.value = false;
    currentPage.value = 1;
    router.push(`/plan/list/${area}`);  // URL 업데이트
    fetchPlans();
};

const goBackToSelection = () => {
    showSelection.value = true;
    selectedArea.value = null;
    router.push('/plan/list');
};

const updateWindowWidth = () => {
    windowWidth.value = window.innerWidth;
};

// 상대적 시간 포맷팅 함수 추가
const formatRelativeTime = (dateString) => {

    const now = new Date().toISOString();
    const cleanDateString = dateString.replace(/\[.*\]$/, '');
    const past = new Date(cleanDateString).toISOString();
    const diffInMilliseconds = new Date(now) - new Date(past);
    const diffInSeconds = Math.floor(diffInMilliseconds / 1000);

    if (diffInSeconds < 60) {
        return '방금 전';
    } else if (diffInSeconds < 3600) {
        const minutes = Math.floor(diffInSeconds / 60);
        return `${minutes}분 전`;
    } else if (diffInSeconds < 86400) {
        const hours = Math.floor(diffInSeconds / 3600);
        return `${hours}시간 전`;
    } else {
        return formatDate(dateString);
    }
};

// 날짜 포맷팅 함수 추가
const formatDate = (dateString) => {
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
};

// 새로운 함수: 계획 상세 페이지로 이동
const navigateToPlanDetails = (planId) => {
    router.push(`/plan/${planId}`);
};

onMounted(() => {
    window.addEventListener('resize', updateWindowWidth);
    loadSelectedAreaFromRoute();  // 컴포넌트 마운트 시 라우트에서 지역 정보 불러오기
});

onUnmounted(() => {
    window.removeEventListener('resize', updateWindowWidth);
});

const isMobile = computed(() => windowWidth.value < 768);

watch(currentPage, fetchPlans);

// 라우트 변경 감지
watch(
    () => route.params.area,
    (newArea) => {
        if (newArea) {
            selectedArea.value = newArea;
            showSelection.value = false;
            currentPage.value = 1;
            fetchPlans();
        } else {
            showSelection.value = true;
            selectedArea.value = null;
        }
    }
);
</script>

<template>
    <div class="container">
        <div v-if="showSelection">
            <SelectAreaComponent @area-selected="handleAreaSelected" class="SelectAreaComponent"/>
        </div>

        <div v-else class="plans-container">
            <img
                @click="goBackToSelection"
                class="back-button"
                src="@/assets/backbutton.png"
                alt="뒤로가기 버튼"
            />
            <div v-if="isLoading">로딩 중...</div>
            <div v-else-if="selectedArea">
                <h3>{{ selectedArea }}의 여행 코스</h3>
                <div v-if="plans.length > 0" class="table-responsive">
                    <table>
                        <thead>
                        <tr>
                            <th class="col-title">제목</th>
                            <th class="col-author">작성자</th>
                            <th class="col-date">작성일</th>
                            <th class="col-likes"><img src="@/assets/likes.png" alt="좋아요수"/></th>
                            <th class="col-views"><img src="@/assets/viewers.png" alt="조회수"/></th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr v-for="plan in plans" :key="plan.id"
                            @click="navigateToPlanDetails(plan.id)" class="clickable-row">
                            <td class="col-title">{{ plan.title }}</td>
                            <td class="col-author">{{ plan.writer }}</td>
                            <td class="col-date">{{ formatRelativeTime(plan.createdAt) }}</td>
                            <td class="col-likes">{{ plan.likes }}</td>
                            <td class="col-views">{{ plan.views }}</td>
                        </tr>
                        </tbody>
                    </table>
                </div>
                <p v-else>등록된 여행 코스가 없습니다.</p>

                <div v-if="totalPages > 1" class="pagination">
                    <button @click="changePage(currentPage - 1)" :disabled="currentPage === 1">이전
                    </button>
                    <button
                        v-for="page in totalPages"
                        :key="page"
                        @click="changePage(page)"
                        :class="{ active: page === currentPage }"
                        v-show="!isMobile || (page >= currentPage - 1 && page <= currentPage + 1) || page === 1 || page === totalPages"
                    >
                        {{ page }}
                    </button>
                    <button @click="changePage(currentPage + 1)"
                            :disabled="currentPage === totalPages">다음
                    </button>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
.container {
    width: 100%;
}

.SelectAreaComponent {
    margin-top: 2em;
}

.plans-container {
    margin-top: 2em;
}

.table-responsive {
    overflow-x: auto;
}

table {
    width: 100%;
    margin: 1em 0;
    border: 1px solid #ddd;
    border-radius: 10px;
    border-collapse: separate;
    border-spacing: 0;
}

th, td {
    padding: 12px;
    text-align: left;
    border-bottom: 1px solid #ddd;
}

th {
    background-color: #f2f2f2;
    font-weight: bold;
    text-align: center;
}

.col-title {
    width: 40%;
}
.col-title, col-author {
    white-space: nowrap;
    overflow-x: hidden;
    text-overflow: ellipsis;
}
.col-author, .col-date {
    width: 15%;
}

.col-date {
    text-align: center;
}

.col-likes, .col-views {
    width: 5%;
    text-align: center;
}

.col-views {
    padding-right: 20px;
}

td.col-title {
    padding-left: 30px;
}

.pagination {
    display: flex;
    justify-content: center;
    flex-wrap: wrap;
    margin-top: 1em;
}

.pagination button {
    margin: 0.25em;
    padding: 0.5em 1em;
    border: 1px solid #ddd;
    background-color: #f9f9f9;
    cursor: pointer;
    transition: background-color 0.3s;
}

.pagination button.active {
    background-color: #007bff;
    color: white;
}

.pagination button:hover:not(:disabled) {
    background-color: #e9ecef;
}

.pagination button:disabled {
    opacity: 0.5;
    cursor: not-allowed;
}

.back-button {
    margin-bottom: 1em;
    cursor: pointer;
}

.clickable-row {
    cursor: pointer;
    transition: background-color 0.3s ease;
}

.clickable-row:hover {
    background-color: #f5f5f5;
}

@media (max-width: 768px) {
    .col-author, .col-date, .col-likes, .col-views {
        display: none;
    }

    .col-title {
        width: 100%;
    }

    th, td {
        padding: 8px;
    }

    .pagination button {
        padding: 0.3em 0.6em;
        font-size: 0.9em;
    }
}
</style>
