import { createStore } from "vuex";
import createPersistedState from 'vuex-persistedstate';

const store = createStore({
    state: {
        accessToken: ""
    },
    getters: {
        getAccessToken: function (state) {
            return state.accessToken;
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
