import { createStore } from "vuex";
import createPersistedState from 'vuex-persistedstate';
import jwtDecoder from 'vue-jwt-decode';

const store = createStore({
    state: {
        accessToken: ""
    },
    getters: {
        getAccessToken: function (state) {
            return state.accessToken;
        },
        isAccessTokenValid: function (state) {
            if (!state.accessToken) {
                return false;
            }
            try {
                const decodedToken = jwtDecoder.decode(state.accessToken)
                const currentTime = Date.now() / 1000;
                return decodedToken.exp > currentTime;
            } catch (error) {
                return false;
            }

        },
    },
    mutations: {
        setAccessToken(state, accessToken) {
            state.accessToken = accessToken;
        },
        clearData(state) {
            state.accessToken = "";
        }
    },
    plugins: [ createPersistedState({
        paths: ['accessToken']
    })]
})

export default store;
