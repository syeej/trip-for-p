<script setup>
/* global kakao */
import {onMounted} from "vue";

const location = "태안";
const locations = [
    { name: location + " 다원 맛집"},
    { name: location + " 딴뚝통나무집"},
    { name: location + " 시골밥상"},
];

const initMap = () => {
    // 카카오 맵 스크립트 로드
    const script = document.createElement('script');
    script.src = `https://dapi.kakao.com/v2/maps/sdk.js?appkey=${process.env.VUE_APP_KAKAO_MAP_API_KEY_JAVASCRIPT}&autoload=false&libraries=services`;
    document.head.appendChild(script);

    script.onload = () => {
        kakao.maps.load(() => {
            const places = new kakao.maps.services.Places();
            const latLngPromises = locations.map(loc => {
                return new Promise((resolve, reject) => {
                    places.keywordSearch(loc.name, (result, status) => {
                        if (status === kakao.maps.services.Status.OK) {
                            console.log(result[0])
                            resolve({
                                ...result[0],
                                latlng: new kakao.maps.LatLng(result[0].y, result[0].x)
                            });
                        } else {
                            reject(status);
                        }
                    });
                });
            });

            Promise.all(latLngPromises)
            .then(results => {
                const map = new kakao.maps.Map(document.getElementById('map'), {
                    center: results[0].latlng, // 기본 중심 좌표 설정
                    level: 8,
                });

                // 카카오 모빌리티 API를 이용한 경로 검색
                const origin = results[0].latlng;
                const destination = results[results.length - 1].latlng;
                const waypoints = results.slice(1, -1).map(result => ({
                    x: result.latlng.getLng(),
                    y: result.latlng.getLat()
                }));

                fetch('https://apis-navi.kakaomobility.com/v1/waypoints/directions', {
                    method: 'POST',
                    headers: {
                        'Authorization': `KakaoAK ${process.env.VUE_APP_KAKAO_MAP_API_KEY_REST}`,
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        origin: { x: origin.getLng(), y: origin.getLat() },
                        destination: { x: destination.getLng(), y: destination.getLat() },
                        waypoints: waypoints
                    })
                })
                .then(response => response.json())
                .then(data => {
                    if (data.routes && data.routes.length > 0) {
                        const path = [];

                        // 각 section을 순회하면서 roads 안의 vertexes 사용
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
                            strokeWeight: 5, // 선의 두께
                            strokeColor: '#FF0000', // 선의 색상
                            strokeOpacity: 0.7, // 선의 투명도
                            strokeStyle: 'solid' // 선의 스타일
                        });

                        polyline.setMap(map);

                        // 마커 및 정보 창 추가
                        results.forEach((result, index) => {
                            const marker = new kakao.maps.Marker({
                                position: result.latlng,
                                map: map,
                                title: result.place_name
                            });

                            const content = `
                    <div style="padding:5px;font-size:12px;">
                      <a href="${result.place_url}">${index + 1}. ${result.place_name}</a>
                    </div>`;

                            const infowindow = new kakao.maps.InfoWindow({
                                content: content,
                                removable: false
                            });

                            infowindow.open(map, marker);
                        });

                        // 지도의 범위를 경계에 맞게 조정
                        const bounds = new kakao.maps.LatLngBounds();
                        results.forEach(result => {
                            bounds.extend(result.latlng);
                        });
                        map.setBounds(bounds);
                    } else {
                        console.error('경로 검색 실패:', data);
                    }
                })
                .catch(error => {
                    console.error('경로 검색 오류:', error);
                });
            })
            .catch(error => {
                console.error('주소 검색 실패:', error);
            });
        });
    };
};

onMounted(() => {
    initMap();
});
</script>

<template>
    <div>
        <div id="map" style="width: 500px; height: 500px;"></div>
    </div>
</template>

<style scoped>

</style>
