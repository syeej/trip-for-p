import axios from "axios";

const instance = axios.create();

const processAlanAPI = function (request) {
    return instance.get(`/api/alan?content=${request.content}&client_id=${request.clientId}`);
}

export {processAlanAPI}
