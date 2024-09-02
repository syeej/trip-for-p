import axios from "axios";

const instance = axios.create();

const processAlanAPI = function (request) {
    return instance.get(
        `/api/alan?content=${request.content}&client_id=${request.clientId}`);
}

const createPlanAPI = function (request) {
    return instance.post(`/api/plans`, request);
}

const createUserAPI = function (request) {
    return instance.post(`/api/users/registration`, request);
}

export {processAlanAPI, createPlanAPI, createUserAPI}
