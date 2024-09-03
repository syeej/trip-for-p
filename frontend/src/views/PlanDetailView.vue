<script setup>
/* global kakao */
import {computed, onMounted, ref, watch} from 'vue';
import {useRoute} from "vue-router";
import {getPlanAPI} from "@/api";

const mapContainer = ref(null);
const currentDateIndex = ref(0);
const map = ref(null);
const markers = ref([]);
const polyline = ref(null);
const infowindows = ref([]);
const plan = ref(null);
const route = useRoute();
const isMapInitialized = ref(false);

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
        updateMap();
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

    if (places.length < 2) {
        console.warn('Not enough valid places to draw a route');
        return;
    }

    const origin = { x: places[0].place.x, y: places[0].place.y };
    const destination = { x: places[places.length - 1].place.x, y: places[places.length - 1].place.y };
    const waypoints = places.slice(1, -1).map(item => ({ x: item.place.x, y: item.place.y }));

    try {
        const routeData = await getRouteData(origin, destination, waypoints);

        if (!routeData || !routeData.routes || routeData.routes.length === 0) {
            throw new Error('No route data available');
        }

        const path = [];
        routeData.routes[0].sections.forEach(section => {
            section.roads.forEach(road => {
                for (let i = 0; i < road.vertexes.length; i += 2) {
                    path.push(new kakao.maps.LatLng(road.vertexes[i + 1], road.vertexes[i]));
                }
            });
        });

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
            infowindows.value.push(infowindow); // 인포윈도우를 배열에 추가
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
            const padding = 100;
            map.value.setBounds(bounds, padding);
        } else {
            throw new Error('No path data available');
        }
    } catch (error) {
        console.error('Error updating map:', error);
        map.value.setCenter(new kakao.maps.LatLng(33.450701, 126.570667));
        map.value.setLevel(3);
    }
};

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
        <h1>{{ plan.title }}</h1>
        <div class="plan-info">
            <p>여행자: {{ plan.writer }}</p>
            <p>여행 지역: {{ plan.area }}</p>
            <p>여행 기간: {{ plan.startDate }} ~ {{ plan.endDate }}</p>
        </div>

        <div class="date-navigation" v-if="dates.length > 0">
            <button @click="prevDate" :disabled="currentDateIndex === 0" class="nav-button prev-button">&lt; 이전</button>
            <span>{{ currentDate }}</span>
            <button @click="nextDate" :disabled="currentDateIndex === dates.length - 1" class="nav-button next-button">다음 &gt;</button>
        </div>

        <div class="map-container" ref="mapContainer"></div>

        <div class="itinerary">
            <h2>일정</h2>
            <div v-for="item in currentDatePlans" :key="item.sequence" class="itinerary-item">
                <div class="sequence">{{ item.sequence }}</div>
                <div class="place-info">
                    <h3>{{ item.place.placeName }}</h3>
                    <p>{{ item.place.addressName }}</p>
                    <p v-if="item.memo">메모: {{ item.memo }}</p>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
.plan-detail-view {
    width: 100%;
    margin-top: 60px;
}

.plan-info {
    margin-bottom: 20px;
}

.date-navigation {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
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

.itinerary-item {
    display: flex;
    margin-bottom: 15px;
    padding: 10px;
    border: 1px solid #ddd;
    border-radius: 4px;
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
</style>
