<script setup>
/* global kakao */
import {computed, onMounted, ref, watch} from 'vue';
import {useRoute} from "vue-router";
import {checkPlanLikeAPI, deletePlanAPI, getPlanAPI, likePlanAPI} from "@/api";
import router from "@/router";
import store from "@/store";

const mapContainer = ref(null);
const currentDateIndex = ref(0);
const map = ref(null);
const markers = ref([]);
const polyline = ref(null);
const infowindows = ref([]);
const plan = ref(null);
const route = useRoute();
const isMapInitialized = ref(false);
const isLiked = ref(false);
const routeData = ref(null);

const formatDuration = (durationInSeconds) => {
    if (durationInSeconds < 60) {
        return `${durationInSeconds}초`;
    }

    const hours = Math.floor(durationInSeconds / 3600);
    const minutes = Math.floor((durationInSeconds % 3600) / 60);

    const parts = [];

    if (hours > 0) {
        parts.push(`${hours}시간`);
    }
    if (minutes > 0) {
        parts.push(`${minutes}분`);
    }

    return parts.join(' ');
};

const loadKakaoMapScript = () => {
    return new Promise((resolve, reject) => {
        if (window.kakao && window.kakao.maps) {
            resolve();
            return;
        }
        const script = document.createElement('script');
        script.src = `//dapi.kakao.com/v2/maps/sdk.js?appkey=${process.env.VUE_APP_KAKAO_MAP_API_KEY_JAVASCRIPT}&autoload=false`;
        script.onload = () => {
            kakao.maps.load(() => {
                resolve();
            });
        };
        script.onerror = reject;
        document.head.appendChild(script);
    });
};

// 카카오 모빌리티 API 호출 함수
const getRouteData = async (origin, destination, waypoints) => {
    const url = 'https://apis-navi.kakaomobility.com/v1/waypoints/directions';
    const headers = {
        'Authorization': `KakaoAK ${process.env.VUE_APP_KAKAO_MAP_API_KEY_REST}`,
        'Content-Type': 'application/json'
    };
    const body = JSON.stringify({
        origin: origin,
        destination: destination,
        waypoints: waypoints,
        priority: 'RECOMMEND',
        car_type: '1',
        avoid: [],
        options: 'trafast'
    });

    try {
        const response = await fetch(url, { method: 'POST', headers: headers, body: body });
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return await response.json();
    } catch (error) {
        console.error('Error fetching route data:', error);
        return null;
    }
};


// 좌표가 유효한지 확인하는 함수
const isValidCoordinate = (coord) => {
    return typeof coord === 'number' && !isNaN(coord) && isFinite(coord);
};

// place 객체가 유효한 좌표를 가지고 있는지 확인하는 함수
const hasValidCoordinates = (place) => {
    return isValidCoordinate(place.y) && isValidCoordinate(place.x);
};

// getPlan 함수 수정
const getPlan = async function () {
    try {
        const response = await getPlanAPI(route.params.planId);
        plan.value = response.data;
        console.log(plan.value);

        // 좌표 데이터 유효성 검사 및 로그
        if (plan.value && plan.value.planItems) {
            plan.value.planItems.forEach(item => {
                if (!hasValidCoordinates(item.place)) {
                    console.warn(`Invalid coordinates for place: ${item.place.placeName}`, item.place);
                }
            });
        }

        await loadKakaoMapScript();
        await initMap();
        await updateMap();
        if (store.getters.isAccessTokenValid) {
            await checkPlanLike();
        }
    } catch (error) {
        console.log(error);
    }
};

const dates = computed(() => {
    if (!plan.value || !plan.value.startDate || !plan.value.endDate) return [];
    const start = new Date(plan.value.startDate);
    const end = new Date(plan.value.endDate);
    const dateArray = [];
    for (let dt = new Date(start); dt <= end; dt.setDate(dt.getDate() + 1)) {
        dateArray.push(dt.toISOString().split('T')[0]);
    }
    return dateArray;
});

const currentDate = computed(() => {
    if (dates.value.length === 0) return null;
    return dates.value[currentDateIndex.value];
});

const currentDatePlans = computed(() => {
    if (!plan.value || !plan.value.planItems || !Array.isArray(plan.value.planItems)) {
        return [];
    }
    const currentDateStr = currentDate.value;
    if (!currentDateStr) {
        return [];
    }
    return plan.value.planItems.filter(item => {
        const itemDate = typeof item.tripDate === 'string' ? item.tripDate : new Date(item.tripDate).toISOString().split('T')[0];
        return itemDate === currentDateStr;
    });
});

const prevDate = () => {
    if (currentDateIndex.value > 0) {
        currentDateIndex.value--;
    }
};

const nextDate = () => {
    if (currentDateIndex.value < dates.value.length - 1) {
        currentDateIndex.value++;
    }
};

const initMap = async () => {
    if (typeof kakao === 'undefined' || !kakao.maps) {
        console.error('Kakao maps SDK not loaded');
        return;
    }

    await new Promise(resolve => setTimeout(resolve, 100)); // 짧은 지연 추가

    const options = {
        center: new kakao.maps.LatLng(33.450701, 126.570667),
        level: 3
    };
    map.value = new kakao.maps.Map(mapContainer.value, options);
    isMapInitialized.value = true;
};

const updateMap = async () => {
    if (!isMapInitialized.value || !map.value) {
        console.error('Map not initialized');
        return;
    }

    // 기존 마커, 폴리라인, 인포윈도우 제거
    markers.value.forEach(marker => marker.setMap(null));
    markers.value = [];
    if (polyline.value) {
        polyline.value.setMap(null);
    }
    infowindows.value.forEach(infowindow => infowindow.close());
    infowindows.value = [];

    const bounds = new kakao.maps.LatLngBounds();
    const places = currentDatePlans.value.filter(item => hasValidCoordinates(item.place));

    if (places.length === 0) {
        console.warn('No valid places to display');
        return;
    }

    places.forEach((item, index) => {
        const position = new kakao.maps.LatLng(item.place.y, item.place.x);
        const marker = new kakao.maps.Marker({
            position: position,
            map: map.value
        });

        markers.value.push(marker);
        bounds.extend(position);

        // 인포윈도우 생성 및 즉시 표시
        const infowindow = new kakao.maps.InfoWindow({
            content: `<div style="padding:5px;font-size:12px;width:150px;text-align:center;">
                    <strong>${index + 1}. ${item.place.placeName}</strong>
                </div>`,
            removable: false // 사용자가 닫을 수 없게 설정
        });
        infowindow.open(map.value, marker);
        infowindows.value.push(infowindow);
    });

    // 장소가 두 개 이상일 때만 경로를 그립니다
    if (places.length >= 2) {
        const origin = { x: places[0].place.x, y: places[0].place.y };
        const destination = { x: places[places.length - 1].place.x, y: places[places.length - 1].place.y };
        const waypoints = places.slice(1, -1).map(item => ({ x: item.place.x, y: item.place.y }));

        try {
            routeData.value = await getRouteData(origin, destination, waypoints);

            if (routeData.value && routeData.value.routes && routeData.value.routes.length > 0) {
                const path = [];
                routeData.value.routes[0].sections.forEach(section => {
                    section.roads.forEach(road => {
                        for (let i = 0; i < road.vertexes.length; i += 2) {
                            path.push(new kakao.maps.LatLng(road.vertexes[i + 1], road.vertexes[i]));
                        }
                    });
                });

                if (path.length > 0) {
                    polyline.value = new kakao.maps.Polyline({
                        path: path,
                        strokeWeight: 5,
                        strokeColor: '#FF0000',
                        strokeOpacity: 0.7,
                        strokeStyle: 'solid'
                    });
                    polyline.value.setMap(map.value);
                }
            }
        } catch (error) {
            console.error('Error updating map:', error);
        }
    }

    // 지도 범위 설정
    const padding = 100;
    map.value.setBounds(bounds, padding);
};
const goBackToSelection = () => {
    router.push(`/plan/list/${plan.value.area}`);
};

const totalDuration = computed(() => {
    if (!routeData.value || !routeData.value.routes || routeData.value.routes.length === 0) {
        return 0;
    }
    return routeData.value.routes[0].summary.duration;
});

const formatDate = (dateString) => {
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    return `${year}.${month}.${day} ${hours}:${minutes}`;
};

const likePlan = async function () {
    if (!store.getters.isAccessTokenValid) {
        if (window.confirm("로그인이 필요한 서비스입니다.\n로그인 페이지로 이동하시겠습니까?")) {
            await router.push('/login');
        }
    } else {
        try {
            const request = {
                planId: route.params.planId
            }
            await likePlanAPI(request);
            isLiked.value = !isLiked.value; // 좋아요 상태 토글
            plan.value.likeCount += isLiked.value ? 1 : -1; // 좋아요 수 업데이트
        } catch (error) {
            console.log(error);
        }
    }
};

const checkPlanLike = async function () {
    try {
        const response = await checkPlanLikeAPI(route.params.planId);
        isLiked.value = response.data; // API 응답에 따라 isLiked 상태 설정
    } catch (error) {
        console.log(error);
    }
};

const goUpdatePlan = function (id) {
    router.push(`/plan/${id}/edit`);
};

const deletePlan = async function (id) {
    if (window.confirm('여행 코스를 삭제하시겠습니까?')) {
        try {
            await deletePlanAPI(id);
            await router.push('/plan/list')
        } catch (error) {
            console.log(error)
        }
    }
};

// 현재 로그인한 사용자의 닉네임을 가져옵니다.
const currentUserNickname = computed(() => store.getters.getNickname);

// 현재 사용자가 게시글 작성자인지 확인합니다.
const isPostAuthor = computed(() => currentUserNickname.value === plan.value.writer);

onMounted(() => {
    getPlan();
});

watch(currentDate, () => {
    if (isMapInitialized.value) {
        updateMap();
    }
});
</script>

<template>
    <div class="plan-detail-view" v-if="plan">
        <img
            @click="goBackToSelection"
            class="back-button"
            src="@/assets/backbutton.png"
            alt="뒤로가기 버튼"
        />
        <div class="title-info">
            <h2 v-if="plan.startDate !== plan.endDate">{{ plan.title }} / {{ plan.startDate }} ~ {{ plan.endDate }}</h2>
            <h2 v-else>{{ plan.title }} / {{ plan.startDate }}</h2>
            <p>{{ formatDate(plan.createdAt) }}</p>
        </div>
        <div class="plan-info">
            <span>{{ plan.writer }}</span>
            <p>
                <span>조회 {{ plan.views }}</span>
            </p>
        </div>

        <div v-if="isPostAuthor" class="author-actions">
            <button @click="goUpdatePlan(plan.id)" class="edit-button">수정</button>
            <button @click="deletePlan(plan.id)" class="delete-button">삭제</button>
        </div>

        <div class="date-navigation" v-if="dates.length > 0">
            <button @click="prevDate" :disabled="currentDateIndex === 0" class="nav-button prev-button">&lt; 이전</button>
            <span>{{ currentDate }}</span>
            <button @click="nextDate" :disabled="currentDateIndex === dates.length - 1" class="nav-button next-button">다음 &gt;</button>
        </div>

        <div class="map-container" ref="mapContainer"></div>

        <div class="itinerary">
            <h2>일정</h2>
            <div v-for="(item, index) in currentDatePlans" :key="item.sequence" class="itinerary-container">
                <div class="itinerary-item">
                    <div class="sequence">{{ item.sequence }}</div>
                    <div class="place-info">
                        <h3>{{ item.place.placeName }}</h3>
                        <p>{{ item.place.addressName }}</p>
                        <p v-if="item.memo">메모: {{ item.memo }}</p>
                    </div>
                </div>
                <div v-if="index < currentDatePlans.length - 1 && routeData && routeData.routes" class="duration-info">
                    <div class="duration-line"></div>
                    <div class="duration-text">
                        {{ formatDuration(routeData.routes[0].sections[index].duration) }}
                    </div>
                    <div class="duration-line"></div>
                </div>
            </div>
            <div v-if="totalDuration > 0" class="total-duration">
                총 예상 소요 시간: {{ formatDuration(totalDuration) }}
            </div>
        </div>

        <div class="like-button-container">
            <button @click="likePlan" class="like-button" :class="{ 'liked': isLiked }">
                {{ isLiked ? '♥' : '♡' }} 좋아요 {{ plan.likeCount }}
            </button>
        </div>
    </div>
</template>

<style scoped>
.plan-detail-view {
    width: 100%;
    margin-top: 2em;
}
.title-info {
    display: flex;
    padding: 20px;
    border-top: 1px solid #ccc;
    border-bottom: 1px solid #ccc;
    background: #fcfcfc;
}
.title-info p {
    display: flex;
    flex-direction: column;
    justify-content: center;
    margin-left: auto;
}
.plan-info {
    display: flex;
    margin-bottom: 20px;
    padding: 10px 20px 10px 20px;
    justify-content: space-between;
    border-bottom: 1px solid #eee;
}
.plan-info p {
    display: flex;
    gap: 10px;
}

.date-navigation {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
}
.date-navigation span {
    font-size: 20px;
}
.nav-button {
    padding: 10px 20px;
    font-size: 14px;
    font-weight: bold;
    color: #fff;
    background-color: #2196F3;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    transition: background-color 0.3s ease, opacity 0.3s ease;
}

.nav-button:hover:not(:disabled) {
    background-color: #1E88E5;
}

.nav-button:disabled {
    opacity: 0.5;
    cursor: not-allowed;
}

.map-container {
    width: 100%;
    height: 400px;
    margin-bottom: 20px;
}

.itinerary-container {
    display: flex;
    flex-direction: column;
    align-items: stretch;
}

.itinerary-item {
    display: flex;
    margin-bottom: 15px;
    padding: 10px;
    border: 1px solid #ddd;
    border-radius: 4px;
}

.duration-info {
    display: flex;
    align-items: center;
    margin: -5px 0 15px 0;
    padding: 0 10px;
}

.duration-line {
    flex-grow: 1;
    height: 1px;
    background-color: #ddd;
}

.duration-text {
    margin: 0 10px;
    padding: 5px 10px;
    background-color: #f0f0f0;
    border-radius: 15px;
    font-size: 14px;
    color: #666;
}

.sequence {
    font-size: 24px;
    font-weight: bold;
    margin-right: 15px;
}

.place-info {
    flex-grow: 1;
}

.place-info h3 {
    margin: 0 0 5px 0;
}

.place-info p {
    margin: 0;
    color: #666;
}

.total-duration {
    margin-top: 20px;
    font-weight: bold;
    text-align: right;
}
.back-button {
    margin-bottom: 1em;
    cursor: pointer;
}
.like-button-container {
    display: flex;
    justify-content: center;
    margin-top: 30px;
    margin-bottom: 30px;
}

.like-button {
    padding: 10px 20px;
    font-size: 16px;
    color: #fff;
    background-color: #ff4081;
    border: none;
    border-radius: 20px;
    cursor: pointer;
    transition: background-color 0.3s ease, transform 0.1s ease;
}

.like-button:hover {
    background-color: #e91e63;
    transform: scale(1.05);
}

.like-button.liked {
    background-color: #e91e63;
}

.like-button.liked:hover {
    background-color: #c2185b;
}

.plan-detail-view {
    width: 100%;
    margin-top: 2em;
}
.author-actions {
    display: flex;
    justify-content: flex-end;
    margin-top: 20px;
    margin-bottom: 20px;
}

.edit-button, .delete-button {
    padding: 10px 20px;
    font-size: 14px;
    font-weight: bold;
    color: #fff;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    transition: background-color 0.3s ease;
    margin-left: 10px;
}

.edit-button {
    background-color: #4CAF50;
}

.edit-button:hover {
    background-color: #45a049;
}

.delete-button {
    background-color: #f44336;
}

.delete-button:hover {
    background-color: #d32f2f;
}
</style>
