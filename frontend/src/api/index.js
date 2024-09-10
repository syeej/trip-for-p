import axios from "axios";
import {setInterceptors} from "@/api/interceptor";

const instance = axios.create();

const createAuthInstance = function () {
    const instance = axios.create();
    return setInterceptors(instance);
};

const authInstance = createAuthInstance();

const getPlanListAPI = function (request) {
    return instance.get(
        `/api/plans?area=${request.area}&size=${request.size}&page=${request.page}`)
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
        const response = await instance.post(`/api/users/registration`,
            request);
        return response.data;
    } catch (error) {
        console.log("회원가입", error);
        if (error.response) {
            throw error.response.data;
        } else {
            throw {message: "네트워크 오류가 발생했습니다."};
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

//[회원가입, 비밀번호 재설정] 인증코드 이메일 전송
const sendVerificationEmailAPI = async function (request) {
    try {
        const response = await instance.post(`/api/mails/send-verification`,
            request);
        return response.data;
    } catch (error) {
        if (error.response) {
            throw error.response.data;
        } else {
            throw {message: "네트워크 오류가 발생했습니다."};
        }
    }
}

//[회원가입, 비밀번호 재설정] 인증코드 검증
const verifyEmailAPI = async function (request) {
    try {
        const response = await instance.post(`/api/mails/verification`,
            request);
        return response.data;
    } catch (error) {
        if (error.response) {
            throw error.response.data;
        } else {
            throw {message: "네트워크 오류가 발생했습니다."};
        }
    }
}
//[회원가입, 개인정보수정] 닉네임 중복 검사
const verifyNickNameAPI = async function (request) {
    var nickname = request
    try {
        const response = await instance.get(`/api/users/nickname-verification`,
            {
                params: {nickname}
            });
        return response.data;
    } catch (error) {
        if (error.response) {
            throw error.response.data;
        } else {
            throw {message: "네트워크 오류가 발생했습니다."};
        }
    }
}

const getMagazineListAPI = function (request) {
    return instance.get(
        `/api/magazines?keyword=&size=${request.size}&page=${request.page}`);
}

const getPopularPlaceListAPI = function () {
    return instance.get(`/api/plans/popular-places`);
};

const getPopularPlanListAPI = function () {
    return instance.get(`/api/plans/popular-plans`);
};

const getFreePostListAPI = function (request) {
    return instance.get(
        `/api/free-posts?keyword=${request.keyword}&size=${request.size}&page=${request.page}`);
}

const createFreePostAPI = function (request) {
    return authInstance.post(`/api/free-posts`, request);
};

const updateFreePostAPI = function (id, request) {
    return authInstance.put(`/api/free-posts/${id}`, request)
};

const deleteFreePostAPI = function (id) {
    return authInstance.delete(`/api/free-posts/${id}`);
};

const createFreeCommentAPI = function (request) {
    return authInstance.post(`/api/free-posts/${request.postId}/comments`,
        request);
};

const updateFreeCommentAPI = function (id, request) {
    return authInstance.put(
        `/api/free-posts/${request.postId}/comments/${id}`,
        request);
};

const deleteFreeCommentAPI = function (id, postId) {
    return authInstance.delete(`/api/free-posts/${postId}/comments/${id}`);
};

const getFreePostAPI = function (id) {
    return instance.get(`/api/free-posts/${id}`);
};

const getFreeCommentListAPI = function (request) {
    return instance.get(
        `/api/free-posts/${request.postId}/comments?&sort=createdAt,desc&size=${request.size}&page=${request.page}`)
}

const likePlanAPI = function (request) {
    return authInstance.post(`/api/plan-likes`, request);
};

const checkPlanLikeAPI = function (planId) {
    return authInstance.get(`/api/plan-likes/check?planId=${planId}`);
};

const updatePlanAPI = function (id, request) {
    return authInstance.put(`/api/plans/${id}`, request);
};

const deletePlanAPI = function (id) {
    return authInstance.delete(`/api/plans/${id}`);
};

//[비밀번호 재설정]이메일 인증코드 전송
const sendPasswordResetEmailAPI = async function (request) {
    try {
        const response = await instance.post(
            `/api/mails/password-reset-request`,
            request);
        return response.data;
    } catch (error) {
        if (error.response) {
            throw error.response.data;
        } else {
            throw {message: "네트워크 오류가 발생했습니다."};
        }
    }
}
//[비밀번호 재설정]비밀번호 재설정
const resetPasswordAPI = async function (request) {
    try {
        const response = await instance.post(`/api/users/password/renewal`,
            request);
        return response.data;
    } catch (error) {
        if (error.response) {
            throw error.response.data;
        } else {
            throw {message: "네트워크 오류가 발생했습니다."};
        }
    }
}

const createMagazineAPI = function (formData) {
    return authInstance.post(`/api/magazines`, formData,
        {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        })
}

const updateMagazineAPI = function (id, formData) {
    return authInstance.put(`/api/magazines/${id}`, formData,
        {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        })
}

const getMagazineAPI = function (id) {
    return instance.get(`/api/magazines/${id}`);
}

const deleteMagazineAPI = function (id) {
    return authInstance.delete(`/api/magazines/${id}`);
};

export {
    getPopularPlanListAPI,
    getPopularPlaceListAPI,
    processAlanAPI,
    createPlanAPI,
    createUserAPI,
    loginAPI,
    getPlanAPI,
    getPlanListAPI,
    sendVerificationEmailAPI,
    verifyEmailAPI,
    verifyNickNameAPI,
    getMagazineListAPI,
    getFreePostListAPI,
    createFreePostAPI,
    getFreePostAPI,
    getFreeCommentListAPI,
    createFreeCommentAPI,
    updateFreeCommentAPI,
    deleteFreeCommentAPI,
    updateFreePostAPI,
    deleteFreePostAPI,
    likePlanAPI,
    checkPlanLikeAPI,
    updatePlanAPI,
    deletePlanAPI,
    sendPasswordResetEmailAPI,
    resetPasswordAPI,
    createMagazineAPI,
    getMagazineAPI,
    deleteMagazineAPI,
    updateMagazineAPI
}
