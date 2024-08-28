import axios from "axios";

const createInstance = function () {
    const instance = axios.create({})
    return instance;
};
