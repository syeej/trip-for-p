import axios from "axios";
import {setInterceptors} from "@/api/interceptor";

const instance = axios.create();

const createAuthInstance = function () {
    const instance = axios.create();
    return setInterceptors(instance);
};

const authInstance = createAuthInstance();

const processAlanAPI = function (request) {
    return instance.get(
        `/api/alan?content=${request.content}&client_id=${request.clientId}`);
}

const createPlanAPI = function (request) {
    return authInstance.post(`/api/plans`, request);
}

const createUserAPI = function (request) {
    return instance.post(`/api/users/registration`, request);
}

const loginAPI = function (formData) {
    return instance.post(`/api/users/signin`, formData,
        {
            headers: {
                "Content-Type": 'multipart/form-data'
            }
        })
};


export {processAlanAPI, createPlanAPI, createUserAPI, loginAPI}
