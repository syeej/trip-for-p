import store from "@/store";
import router from "@/router";
import jwtDecoder from 'vue-jwt-decode';

const setInterceptors = function (instance) {
    instance.interceptors.request.use(
        async (config) => {
            const token = store.state.accessToken
            if (token) {
                const decodedToken = jwtDecoder.decode(token);
                const currentTime = Date.now() / 1000;  // 현재 시간(초 단위)

                // 토큰 만료 시간 확인
                if (decodedToken.exp < currentTime) {
                    // 토큰이 만료된 경우: 로그아웃하거나 토큰 갱신
                    store.commit('clearData');
                    alert('세션이 만료되었습니다. 다시 로그인해주세요.');
                    await router.push('/login');
                    // 추가적으로, 토큰 갱신 로직을 넣을 수 있음
                    return Promise.reject('Token expired');
                }

                // 토큰이 유효하면 Authorization 헤더에 추가
                config.headers.access = token;
            }

            return config;
        },
        (error) => {
            return Promise.reject(error)
        }
    )

    instance.interceptors.response.use(
        (response) => {
            return response
        },
        async (error) => {
            // 인증 실패 처리 (401 에러 처리)
            if (error.response && error.response.status === 401) {
                store.commit('clearData');  // 토큰 삭제
                alert('인증에 실패했습니다. 다시 로그인해주세요.');
                await router.push('/login');
            }

            return Promise.reject(error);
        }
    )
    return instance
}

export {setInterceptors}
