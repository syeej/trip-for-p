import axios from "axios";
import {setInterceptors} from "@/api/interceptor";

const instance = axios.create();

const createAuthInstance = function () {
    const instance = axios.create();
    return setInterceptors(instance);
};

const authInstance = createAuthInstance();

const getPlanListAPI = function (request) {
    return instance.get(`/api/plans?area=${request.area}&size=${request.size}&page=${request.page}`)
}

const processAlanAPI = function (request) {
    return instance.get(
        `/api/alan?content=${request.content}&client_id=${request.clientId}`);
}

const getPlanAPI = function (id) {
    return instance.get(`/api/plans/${id}`);
};

const createPlanAPI = function (request) {
    return authInstance.post(`/api/plans`, request);
}

const createUserAPI = async function (request) {
    //return instance.post(`/api/users/registration`, request);
    try {
        const response = await instance.post(`/api/users/registration`, request);
        return response.data;
    } catch (error) {
        console.log("회원가입", error);
        if (error.response) {
            throw error.response.data;
        } else {
            throw { message: "네트워크 오류가 발생했습니다." };
        }
    }
}

const loginAPI = function (formData) {
    return instance.post(`/api/users/signin`, formData,
        {
            headers: {
                "Content-Type": 'multipart/form-data'
            }
        })
};

//[회원가입] 인증코드 이메일 전송
const sendVerificationEmailAPI = async function (request) {
    try {
        const response = await instance.post(`/api/mails/send-verification`, request);
        return response.data;
    } catch (error) {
        if (error.response) {
            throw error.response.data;
        } else {
            throw { message: "네트워크 오류가 발생했습니다." };
        }
    }
}

//[회원가입] 인증코드 검증
const verifyEmailAPI = async function (request) {
    try {
        const response = await instance.post(`/api/mails/verification`, request);
        return response.data;
    } catch (error) {
        if (error.response) {
            throw error.response.data;
        } else {
            throw { message: "네트워크 오류가 발생했습니다." };
        }
    }
}
//[회원가입] 닉네임 중복 검사
const verifyNickNameAPI = async function(request) {
    var nickname = request
    try {
        const response = await instance.get(`/api/users/nickname-verification`, {
            params: { nickname }
        });
        return response.data;
    } catch (error) {
        if (error.response) {
            throw error.response.data;
        } else {
            throw { message: "네트워크 오류가 발생했습니다." };
        }
    }
}

const getMagazineListAPI = function (request) {
    return instance.get(`api/magazines?size=${request.size}&page=${request.page}`);
}

export {processAlanAPI, createPlanAPI, createUserAPI, loginAPI, getPlanAPI, getPlanListAPI, sendVerificationEmailAPI, verifyEmailAPI, verifyNickNameAPI, getMagazineListAPI}
