<script setup>
/* global kakao */
import {computed, defineEmits, defineProps, nextTick, onMounted, ref, watch} from "vue";
import ItineraryComponent from "@/components/ItineraryComponent.vue";
import SearchPlaceComponent from "@/components/SearchPlaceComponent.vue";
import {createPlanAPI} from "@/api";
import router from "@/router";

const props = defineProps({
    startDate: String,
    endDate: String,
    selectedRegion: String
});

const title = ref(`${props.selectedRegion} 여행`);
const selectedPlaces = ref({});
const maps = ref({});
const currentDateIndex = ref(0);

const polylines = ref({});
const markers = ref({});
const infowindows = ref({});


const dates = computed(() => {
    const start = new Date(props.startDate);
    const end = new Date(props.endDate);
    const dateArray = [];
    for (let dt = new Date(start); dt <= end; dt.setDate(dt.getDate() + 1)) {
        dateArray.push(new Date(dt));
    }
    return dateArray;
});

dates.value.forEach(date => {
    const dateString = date.toISOString().split('T')[0];
    selectedPlaces.value[dateString] = [];
    maps.value[dateString] = null;
    polylines.value[dateString] = null;
    markers.value[dateString] = [];
    infowindows.value[dateString] = [];
});

const addPlace = (place, date) => {
    const dateString = date.toISOString().split('T')[0];

    // 중복 여부 확인
    const isPlaceAlreadyAdded = selectedPlaces.value[dateString].some(
        selectedPlace => selectedPlace.place.id === place.id
    );

    if (!isPlaceAlreadyAdded) {
        selectedPlaces.value[dateString].push({
            place: place,
            memo: '',
            sequence: selectedPlaces.value[dateString].length + 1
        });
        console.log(`Place added for ${dateString}:`, place);
        updateRoute(dateString);
    } else {
        console.log(`Place already added for ${dateString}:`, place);
    }
};


const deletePlace = (dateString, index) => {
    selectedPlaces.value[dateString].splice(index, 1);
    selectedPlaces.value[dateString].forEach((item, i) => {
        item.sequence = i + 1;
    });

    clearRoute(dateString);
    if (selectedPlaces.value[dateString].length >= 2) {
        updateRoute(dateString);
    } else {
        resetMapView(dateString);
    }
};

const clearRoute = (dateString) => {
    if (polylines.value[dateString]) {
        polylines.value[dateString].forEach(polyline => {
            polyline.setMap(null);
        });
        polylines.value[dateString] = [];
    }

    if (markers.value[dateString]) {
        markers.value[dateString].forEach(marker => marker.setMap(null));
        markers.value[dateString] = [];
    }

    if (infowindows.value[dateString]) {
        infowindows.value[dateString].forEach(infowindow => infowindow.close());
        infowindows.value[dateString] = [];
    }
};

const resetMapView = (dateString) => {
    const map = maps.value[dateString];
    if (map) {
        map.setCenter(new kakao.maps.LatLng(37.559224, 128.031876)); // 서울 시청
        map.setLevel(13);
    }
};

const shouldShowMap = (date) => {
    const dateString = date.toISOString().split('T')[0];
    return selectedPlaces.value[dateString] && selectedPlaces.value[dateString].length >= 2;
};

const initMap = (dateString) => {
    console.log(`Initializing map for ${dateString}`);
    const container = document.getElementById(`map-${dateString}`);
    if (!container) {
        console.error(`Map container not found for ${dateString}`);
        return;
    }

    if (!maps.value[dateString]) {
        maps.value[dateString] = new kakao.maps.Map(container, {
            center: new kakao.maps.LatLng(37.559224, 128.031876), // 한반도 중앙 부근
            level: 13 // 한반도 전체가 보이는 확대 레벨
        });
        console.log(`New map instance created for ${dateString}`);
    } else {
        console.log(`Reusing existing map instance for ${dateString}`);
        maps.value[dateString].setCenter(new kakao.maps.LatLng(37.559224, 128.031876));
        maps.value[dateString].setLevel(13);
    }

    maps.value[dateString].relayout();
    updateRoute(dateString);
};

const updateRoute = (dateString) => {
    console.log(`Updating route for ${dateString}`);
    const map = maps.value[dateString];
    if (!map) {
        console.error(`Map not found for ${dateString}`);
        return;
    }

    // Clear existing route, markers, and infowindows
    clearRoute(dateString);

    const places = selectedPlaces.value[dateString];
    if (places.length < 2) {
        console.log(`Not enough places for ${dateString}`);
        resetMapView(dateString);
        return;
    }

    const origin = places[0].place;
    const destination = places[places.length - 1].place;
    const waypoints = places.slice(1, -1).map(item => ({
        x: item.place.x,
        y: item.place.y
    }));

    console.log('Fetching route data...');
    fetch('https://apis-navi.kakaomobility.com/v1/waypoints/directions', {
        method: 'POST',
        headers: {
            'Authorization': `KakaoAK ${process.env.VUE_APP_KAKAO_MAP_API_KEY_REST}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            origin: { x: origin.x, y: origin.y },
            destination: { x: destination.x, y: destination.y },
            waypoints: waypoints
        })
    })
    .then(response => response.json())
    .then(data => {
        console.log('Route data received:', data);
        if (data.routes && data.routes.length > 0) {
            const path = [];
            data.routes[0].sections.forEach(section => {
                section.roads.forEach(road => {
                    for (let i = 0; i < road.vertexes.length; i += 2) {
                        const lat = road.vertexes[i + 1];
                        const lng = road.vertexes[i];
                        path.push(new kakao.maps.LatLng(lat, lng));
                    }
                });
            });

            const polyline = new kakao.maps.Polyline({
                path: path,
                strokeWeight: 5,
                strokeColor: '#FF0000',
                strokeOpacity: 0.7,
                strokeStyle: 'solid'
            });

            polyline.setMap(map);
            if (!polylines.value[dateString]) {
                polylines.value[dateString] = [];
            }
            polylines.value[dateString].push(polyline);

            const bounds = new kakao.maps.LatLngBounds();

            places.forEach((item, index) => {
                const position = new kakao.maps.LatLng(item.place.y, item.place.x);
                bounds.extend(position);

                const marker = new kakao.maps.Marker({
                    position: position,
                    map: map
                });

                const infowindow = new kakao.maps.InfoWindow({
                    content: `<div style="padding:5px;font-size:12px;width:150px;text-align:center;">
                        <strong>${index + 1}. ${item.place.place_name}</strong>
                    </div>`,
                    removable: false
                });

                infowindow.open(map, marker);

                markers.value[dateString].push(marker);
                infowindows.value[dateString].push(infowindow);
            });

            const padding = 100;
            map.setBounds(bounds, padding);

            // 지도 레벨 조정
            let currentLevel = map.getLevel();
            while (currentLevel > 8 && !areAllMarkersVisible(map, markers.value[dateString])) {
                currentLevel--;
                map.setLevel(currentLevel);
            }

            console.log(`Route updated for ${dateString}`);
        } else {
            console.log(`No routes found for ${dateString}`);
        }
    })
    .catch(error => {
        console.error('경로 검색 오류:', error);
    });
};

// 모든 마커가 지도에 보이는지 확인하는 함수
const areAllMarkersVisible = (map, markers) => {
    const bounds = map.getBounds();
    return markers.every(marker => bounds.contain(marker.getPosition()));
};


const updateMemo = (date, index, memo) => {
    selectedPlaces.value[date][index].memo = memo;
};

const reorderPlaces = (date) => {
    selectedPlaces.value[date].forEach((item, index) => {
        item.sequence = index + 1;
    });
    updateRoute(date);
};

const changeDate = (newIndex) => {
    currentDateIndex.value = newIndex;
    const currentDate = dates.value[newIndex];
    const dateString = currentDate.toISOString().split('T')[0];
    nextTick(() => {
        initMap(dateString);
    });
};

const goToPreviousDate = () => {
    if (currentDateIndex.value > 0) {
        changeDate(currentDateIndex.value - 1);
    }
};

const goToNextDate = () => {
    if (currentDateIndex.value < dates.value.length - 1) {
        changeDate(currentDateIndex.value + 1);
    }
};

onMounted(() => {
    const script = document.createElement('script');
    script.src = `https://dapi.kakao.com/v2/maps/sdk.js?appkey=${process.env.VUE_APP_KAKAO_MAP_API_KEY_JAVASCRIPT}&autoload=false&libraries=services`;
    document.head.appendChild(script);

    script.onload = () => {
        kakao.maps.load(() => {
            dates.value.forEach(date => {
                const dateString = date.toISOString().split('T')[0];
                initMap(dateString);
            });
        });
    };
});

// selectedPlaces의 변경을 감지하는 watch 함수 (메모 변경 제외)
watch(() => {
    // 각 날짜별 장소 목록의 길이와 각 장소의 id만을 포함하는 객체를 반환
    return Object.entries(selectedPlaces.value).reduce((acc, [date, places]) => {
        acc[date] = places.map(place => ({
            id: place.place.id,
            sequence: place.sequence
        }));
        return acc;
    }, {});
}, () => {
    const currentDate = dates.value[currentDateIndex.value];
    const dateString = currentDate.toISOString().split('T')[0];
    if (shouldShowMap(currentDate)) {
        nextTick(() => {
            initMap(dateString);
        });
    }
}, { deep: true });

const generatePlan = async () => {
    try {
        const planItems = Object.entries(selectedPlaces.value).flatMap(([date, places]) =>
            places.map(item => ({
                place: {
                    addressName: item.place.address_name,
                    categoryName: item.place.category_name.split(' > ')[0] === '음식점' ? '음식점' : item.place.category_name.split(' > ')[1],
                    placeName: item.place.place_name,
                    placeUrl: item.place.place_url,
                    x: item.place.x,
                    y: item.place.y
                },
                tripDate: date,
                sequence: item.sequence,
                memo: item.memo
            }))
        );

        const createPlanRequest = {
            startDate: props.startDate,
            endDate: props.endDate,
            title: title.value,
            area: props.selectedRegion,
            planItems: planItems
        };

        const response = await createPlanAPI(createPlanRequest);
        const planId = response.data.id;
        await router.push(`/plan/${planId}`);
    } catch (error) {
        console.log(error);
    }

};
const emit = defineEmits(['back-to-day']);

const handleBack = () => {
    emit('back-to-day');
};
const isSaveButtonEnabled = computed(() => {
    return Object.values(selectedPlaces.value).every(places => places.length > 0);
});
</script>
<template>
    <div class="trip-planner">
        <input v-model="title" placeholder="여행 제목" class="title-input">
        <div class="date-navigation">
            <button @click="goToPreviousDate" :disabled="currentDateIndex === 0" class="nav-button prev-button">&lt; 이전</button>
            <h3>{{ dates[currentDateIndex].toLocaleDateString() }}</h3>
            <button @click="goToNextDate" :disabled="currentDateIndex === dates.length - 1" class="nav-button next-button">다음 &gt;</button>
        </div>
        <div class="date-content">
            <div class="map-search-container">
                <div v-for="(date, index) in dates" :key="date.toISOString()"
                     :id="'map-' + date.toISOString().split('T')[0]"
                     :class="{ 'map-container': true, 'active': index === currentDateIndex }"
                     :style="{ display: index === currentDateIndex ? 'block' : 'none' }">
                </div>
            </div>
            <SearchPlaceComponent @place-selected="place => addPlace(place, dates[currentDateIndex])"/>
            <ItineraryComponent
                :places="selectedPlaces[dates[currentDateIndex].toISOString().split('T')[0]]"
                @update:memo="(index, memo) => updateMemo(dates[currentDateIndex].toISOString().split('T')[0], index, memo)"
                @reorder="reorderPlaces(dates[currentDateIndex].toISOString().split('T')[0])"
                @delete="index => deletePlace(dates[currentDateIndex].toISOString().split('T')[0], index)"
            />
        </div>
        <div class="button-container">
            <button @click="handleBack" class="back-button">이전</button>
            <button @click="generatePlan" class="save-plan-button" :disabled="!isSaveButtonEnabled">일정 저장</button>
        </div>
    </div>
</template>
<style scoped>
.trip-planner {
    display: flex;
    flex-direction: column;
    width: 100%;
    margin: 0 auto;
}

.map-search-container {
    margin-bottom: 20px;
}

.title-input {
    font-size: 1.5em;
    margin-bottom: 20px;
    padding: 10px;
    border: 2px solid #ddd;
    border-radius: 4px;
    transition: border-color 0.3s ease;
}

.title-input:focus {
    outline: none;
    border-color: #4CAF50;
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

.prev-button {
    margin-right: 10px;
}

.next-button {
    margin-left: 10px;
}

.date-navigation h3 {
    font-size: 18px;
    margin: 0;
}

.map-search-container {
    margin-bottom: 20px;
}

.map-container {
    width: 100%;
    height: 400px;
    border-radius: 4px;
    overflow: hidden;
}
.button-container {
    display: flex;
    gap: 10px;
    justify-content: center;
}
.save-plan-button, .back-button {
    margin-top: 20px;
    padding: 12px 24px;
    font-size: 16px;
    font-weight: bold;
    color: white;
    background-color: #4CAF50;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    transition: background-color 0.3s ease, transform 0.1s ease;
    align-self: center;
    box-shadow: 0 2px 4px rgba(0,0,0,0.2);
}

.save-plan-button:hover, .back-button:hover {
    background-color: #45a049;
    transform: translateY(-2px);
}

.save-plan-button:active, .back-button:hover {
    transform: translateY(0);
    box-shadow: 0 1px 2px rgba(0,0,0,0.2);
}
.save-plan-button:disabled {
    background-color: #cccccc;
    cursor: not-allowed;
}

@media (max-width: 600px) {
    .date-navigation {
        flex-direction: column;
        align-items: stretch;
    }

    .nav-button {
        margin: 10px 0;
    }

    .date-navigation h3 {
        order: -1;
        margin-bottom: 10px;
        text-align: center;
    }

    .save-plan-button {
        width: 100%;
        padding: 15px;
    }
}
</style>
