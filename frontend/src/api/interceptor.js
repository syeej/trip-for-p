import store from "@/store";
import router from "@/router";
import jwtDecoder from 'vue-jwt-decode';
import {refreshTokenAPI} from "@/api/index";

const setInterceptors = function (instance) {
    instance.interceptors.request.use(
        async (config) => {
            const token = store.state.accessToken
            if (token) {
                const decodedToken = jwtDecoder.decode(token);
                const currentTime = Date.now() / 1000;  // 현재 시간(초 단위)

                // 토큰 만료 시간 확인
                if (decodedToken.exp < currentTime) {
                    try {
                        const response = await refreshTokenAPI();
                        const newToken = response.headers.access;
                        store.commit('setAccessToken', newToken);
                        config.headers.access = newToken;
                    } catch (error) {
                        // 리프레시 토큰도 만료된 경우
                        store.commit('clearData');
                        alert('세션이 만료되었습니다. 다시 로그인해주세요.');
                        await router.push('/login');
                        return Promise.reject('Token expired');
                    }
                } else {
                    // 토큰이 유효하면 Authorization 헤더에 추가
                    config.headers.access = token;
                }
            }

            return config;
        },
        (error) => {
            return Promise.reject(error)
        }
    )

    instance.interceptors.response.use(
        (response) => {
            return response;
        },
        async (error) => {
            const originalRequest = error.config;
            console.log(error.response.status)
            // 액세스 토큰 만료로 인한 401 에러 && 재시도하지 않은 요청
            if (error.response.status === 401) {

                try {
                    const response = await refreshTokenAPI();
                    const newToken = response.headers.access;
                    store.commit('setAccessToken', newToken);
                    instance.defaults.headers.common['access'] = newToken;
                    return instance(originalRequest);
                } catch (refreshError) {
                    // 리프레시 토큰도 만료된 경우
                    store.commit('clearData');
                    alert('인증에 실패했습니다. 다시 로그인해주세요.');
                    await router.push('/login');
                    return Promise.reject(refreshError);
                }
            }

            return Promise.reject(error);
        }
    );
    return instance
}

export {setInterceptors}
