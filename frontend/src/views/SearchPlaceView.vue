<script setup>
/* global kakao */
import {onMounted, ref} from "vue";

const mapContainer = ref(null);
const keyword = ref('');
const selectedPlace = ref('');

let map = null;
let markers = [];
let infowindow = null;

const initializeMap = () => {
    const script = document.createElement('script');
    script.src = `//dapi.kakao.com/v2/maps/sdk.js?appkey=${process.env.VUE_APP_KAKAO_MAP_API_KEY_JAVASCRIPT}&libraries=services&autoload=false`;
    script.async = true;
    script.onload = () => {
        kakao.maps.load(() => {
            const options = {
                center: new kakao.maps.LatLng(37.566826, 126.9786567),
                level: 3
            };
            map = new kakao.maps.Map(mapContainer.value, options);

            const ps = new kakao.maps.services.Places();
            infowindow = new kakao.maps.InfoWindow({zIndex:1});

            const searchPlaces = () => {
                if (!keyword.value.trim()) {
                    alert('키워드를 입력해주세요!');
                    return;
                }
                // 이전 검색 결과 제거
                removeMarkers();
                ps.keywordSearch(keyword.value, placesSearchCB);
            }

            const placesSearchCB = (data, status) => {
                if (status === kakao.maps.services.Status.OK) {
                    const bounds = new kakao.maps.LatLngBounds();

                    for (let i = 0; i < data.length; i++) {
                        displayMarker(data[i]);
                        bounds.extend(new kakao.maps.LatLng(data[i].y, data[i].x));
                    }

                    map.setBounds(bounds);
                }
            }

            const displayMarker = (place) => {
                const marker = new kakao.maps.Marker({
                    map: map,
                    position: new kakao.maps.LatLng(place.y, place.x)
                });
                markers.push(marker);

                kakao.maps.event.addListener(marker, 'click', () => {

                    selectedPlace.value = place.place_name;
                    console.log(place)
                    infowindow.setContent(`
                    <div style="padding:5px;font-size:12px;">
                      <a href="${place.place_url}">${place.place_name}</a>
                    </div>`);
                    infowindow.open(map, marker);
                });
            }



            const removeMarkers = () => {
                for (let i = 0; i < markers.length; i++) {
                    markers[i].setMap(null);
                }
                markers = [];
                infowindow.close();
                selectedPlace.value = '';
            }

            // 검색 버튼 클릭 이벤트를 바인딩
            const searchButton = document.getElementById('search-button');
            if (searchButton) {
                searchButton.addEventListener('click', searchPlaces);
            }
        });
    };

    document.head.appendChild(script);
};

onMounted(() => {
    initializeMap();
});
</script>

<template>
    <div>
        <input v-model="keyword" placeholder="장소를 입력하세요" />
        <button id="search-button">검색</button>
        <div ref="mapContainer" style="width:100%;height:400px;"></div>
        <p v-if="selectedPlace">선택된 장소: {{ selectedPlace }}</p>
    </div>
</template>

<style scoped>

</style>
