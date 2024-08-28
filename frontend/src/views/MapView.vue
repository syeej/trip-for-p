<script setup>
/* global kakao */
import {onMounted} from "vue";

// const locations = [
//     { name: '태안몽산포 정다운펜션', lat: 36.6731619956, lng: 126.2733483244 },
//     { name: "딴뚝통나무집식당", lat: 36.5094793916, lng: 126.3509844844 },
//     { name: '청포대해수욕장', lat: 36.6387303525, lng: 126.3026354670 },
// ];
//
// const initMap = () => {
//     // 카카오맵 스크립트 로드
//     const script = document.createElement('script');
//     /* global kakao */
//     script.src = `https://dapi.kakao.com/v2/maps/sdk.js?appkey=2340d620795cc18088184113b573947e&autoload=false`;
//     document.head.appendChild(script);
//
//     script.onload = () => {
//         // 카카오맵 API 초기화
//         kakao.maps.load(() => {
//             const container = document.getElementById('map');
//             const options = {
//                 center: new kakao.maps.LatLng(locations[0].lat, locations[0].lng),
//                 level: 8,
//             };
//             const map = new kakao.maps.Map(container, options);
//
//             // 폴리라인 좌표 설정
//             const path = locations.map(loc => new kakao.maps.LatLng(loc.lat, loc.lng));
//
//             // 폴리라인 생성
//             const polyline = new kakao.maps.Polyline({
//                 path: path,
//                 strokeWeight: 5, // 선의 두께
//                 strokeColor: '#FF0000', // 선의 색상
//                 strokeOpacity: 0.7, // 선의 투명도
//                 strokeStyle: 'solid' // 선의 스타일
//             });
//
//             // 폴리라인 지도에 표시
//             polyline.setMap(map);
//
//             // 마커 및 정보 창 추가
//             locations.forEach((loc, index) => {
//                 const markerPosition = new kakao.maps.LatLng(loc.lat, loc.lng);
//                 const marker = new kakao.maps.Marker({
//                     position: markerPosition,
//                     map: map,
//                     title: loc.name
//                 });
//
//                 // 정보 창 설정
//                 const content = `
//           <div style="padding:5px;font-size:12px;">
//             <strong>${index + 1}. ${loc.name}</strong>
//           </div>`;
//
//                 const infowindow = new kakao.maps.InfoWindow({
//                     content: content,
//                     removable: false // 정보 창이 닫히기 가능하도록 설정
//                 });
//
//                 // 정보 창을 기본적으로 마커 위에 표시
//                 infowindow.open(map, marker);
//             });
//             // 모든 위치를 포함하는 LatLngBounds 객체 생성
//             const bounds = new kakao.maps.LatLngBounds();
//
//             // 모든 위치를 경계에 추가
//             locations.forEach(loc => {
//                 bounds.extend(new kakao.maps.LatLng(loc.lat, loc.lng));
//             });
//
//             // 지도의 범위를 경계에 맞게 조정
//             map.setBounds(bounds);
//         });
//     };
// };

// const locations = [
//     { name: '태안 올레길펜션', address: '충청남도 태안군 안면읍 해안관광로 796-8' },
//     { name: "터줏대감", address: '충남 태안군 안면읍 방포로 3' },
//     { name: '천리포수목원', address: '충청남도 태안군 소원면 천리포1길 187' },
// ];
//
// const initMap = () => {
//     // 카카오맵 스크립트 로드
//     const script = document.createElement('script');
//     script.src = `https://dapi.kakao.com/v2/maps/sdk.js?appkey=2340d620795cc18088184113b573947e&autoload=false&libraries=services`;
//     document.head.appendChild(script);
//
//     script.onload = () => {
//         // 카카오맵 API 초기화
//         kakao.maps.load(() => {
//             const geocoder = new kakao.maps.services.Geocoder();
//             const latLngPromises = locations.map(loc => {
//                 return new Promise((resolve, reject) => {
//                     geocoder.addressSearch(loc.address, (result, status) => {
//                         if (status === kakao.maps.services.Status.OK) {
//                             resolve(new kakao.maps.LatLng(result[0].y, result[0].x));
//                         } else {
//                             reject(status);
//                         }
//                     });
//                 });
//             });
//
//             Promise.all(latLngPromises)
//             .then(latLngs => {
//                 const container = document.getElementById('map');
//                 const options = {
//                     center: latLngs[0], // 기본 중심 좌표 설정
//                     level: 8,
//                 };
//                 const map = new kakao.maps.Map(container, options);
//
//                 // 폴리라인 좌표 설정
//                 const path = latLngs;
//
//                 // 폴리라인 생성
//                 const polyline = new kakao.maps.Polyline({
//                     path: path,
//                     strokeWeight: 5, // 선의 두께
//                     strokeColor: '#FF0000', // 선의 색상
//                     strokeOpacity: 0.7, // 선의 투명도
//                     strokeStyle: 'solid' // 선의 스타일
//                 });
//
//                 // 폴리라인 지도에 표시
//                 polyline.setMap(map);
//
//                 // 마커 및 정보 창 추가
//                 latLngs.forEach((latlng, index) => {
//                     const marker = new kakao.maps.Marker({
//                         position: latlng,
//                         map: map,
//                         title: locations[index].name
//                     });
//
//                     // 정보 창 설정
//                     const content = `
//               <div style="padding:5px;font-size:12px;">
//                 <strong>${index + 1}. ${locations[index].name}</strong>
//               </div>`;
//
//                     const infowindow = new kakao.maps.InfoWindow({
//                         content: content,
//                         removable: false // 정보 창이 닫히기 가능하도록 설정
//                     });
//
//                     // 정보 창을 기본적으로 마커 위에 표시
//                     infowindow.open(map, marker);
//                 });
//
//                 // 모든 위치를 포함하는 LatLngBounds 객체 생성
//                 const bounds = new kakao.maps.LatLngBounds();
//
//                 // 모든 위치를 경계에 추가
//                 latLngs.forEach(latlng => {
//                     bounds.extend(latlng);
//                 });
//
//                 // 지도의 범위를 경계에 맞게 조정
//                 map.setBounds(bounds);
//             })
//             .catch(error => {
//                 console.error('주소 검색 실패:', error);
//             });
//         });
//     };
// };

const locations = [
    // { name: '태안 올레길펜션', address: '충청남도 태안군 안면읍 해안관광로 796-8' },
    // { name: "터줏대감", address: '충남 태안군 안면읍 방포로 3' },
    // { name: '천리포수목원', address: '충청남도 태안군 소원면 천리포1길 187' },
    // { name: "동백로255 정육식당", address: '충남 태안군 태안읍 동백로 255' },
    { name: "광안리해수욕장", address: '부산 수영구 광안해변로 219' },
    { name: "BEXCO", address: '부산 해운대구 APEC로 55' },
    { name: "부산라메르펜션", address: '부산 기장군 기장읍 당사로1길 17' },
];

const initMap = () => {
    // 카카오 맵 스크립트 로드
    const script = document.createElement('script');
    script.src = `https://dapi.kakao.com/v2/maps/sdk.js?appkey=2340d620795cc18088184113b573947e&autoload=false&libraries=services`;
    document.head.appendChild(script);

    script.onload = () => {
        kakao.maps.load(() => {
            const geocoder = new kakao.maps.services.Geocoder();
            const latLngPromises = locations.map(loc => {
                return new Promise((resolve, reject) => {
                    geocoder.addressSearch(loc.address, (result, status) => {
                        if (status === kakao.maps.services.Status.OK) {
                            resolve(new kakao.maps.LatLng(result[0].y, result[0].x));
                        } else {
                            reject(status);
                        }
                    });
                });
            });

            Promise.all(latLngPromises)
            .then(latLngs => {
                const map = new kakao.maps.Map(document.getElementById('map'), {
                    center: latLngs[0], // 기본 중심 좌표 설정
                    level: 8,
                });

                // 카카오 모빌리티 API를 이용한 경로 검색
                const origin = latLngs[0];
                const destination = latLngs[latLngs.length - 1];
                const waypoints = latLngs.slice(1, -1).map(latlng => ({
                    x: latlng.getLng(),
                    y: latlng.getLat()
                }));

                fetch('https://apis-navi.kakaomobility.com/v1/waypoints/directions', {
                    method: 'POST',
                    headers: {
                        'Authorization': 'KakaoAK c61d842074374aef184da8a41e1765fe',
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
                        latLngs.forEach((latlng, index) => {
                            const marker = new kakao.maps.Marker({
                                position: latlng,
                                map: map,
                                title: locations[index].name
                            });

                            const content = `
                    <div style="padding:5px;font-size:12px;">
                      <strong>${index + 1}. ${locations[index].name}</strong>
                    </div>`;

                            const infowindow = new kakao.maps.InfoWindow({
                                content: content,
                                removable: false
                            });

                            infowindow.open(map, marker);
                        });

                        // 지도의 범위를 경계에 맞게 조정
                        const bounds = new kakao.maps.LatLngBounds();
                        latLngs.forEach(latlng => {
                            bounds.extend(latlng);
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
