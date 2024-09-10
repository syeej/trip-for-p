import {createRouter, createWebHistory} from 'vue-router'
import WritePlanView from "@/views/WritePlanView.vue";
import LoginView from "@/views/LoginView.vue";
import SignupView from "@/views/SignupView.vue";
import PlanDetailView from "@/views/PlanDetailView.vue";
import FreePostListView from "@/views/FreePostListView.vue";
import HomeView from "@/views/HomeView.vue";
import MypageView from "@/views/MypageView.vue";
import PlanListView from "@/views/PlanListView.vue";
import ReviewPostListView from "@/views/ReviewPostListView.vue";
import store from "@/store";
import WriteFreePostView from "@/views/WriteFreePostView.vue";
import FreePostDetailView from "@/views/FreePostDetailView.vue";
import EditFreePostView from "@/views/EditFreePostView.vue";
import EditPlanView from "@/views/EditPlanView.vue";
import ResetPasswordView from "@/views/ResetPasswordView.vue";
import AdminView from "@/views/AdminView.vue";
import WriteMagazineView from "@/views/WriteMagazineView.vue";
import MagazineDetailView from "@/views/MagazineDetailView.vue";
import EditMagazineView from "@/views/EditMagazineView.vue";

const routes = [
    {
        path: '/',
        name: 'Home',
        component: HomeView
    },
    {
        path: '/mypage',
        name: 'MyPage',
        component: MypageView,
        meta: { requiresAuth: true }
    },
    {
        path: '/login',
        name: 'Login',
        component: LoginView
    },
    {
        path: '/signup',
        name: 'Signup',
        component: SignupView
    },
    {
        path: '/plan/list/:area?',
        name: 'PlanList',
        component: PlanListView
    },
    {
        path: '/plan/write',
        name: 'WritePlan',
        component: WritePlanView,
        meta: { requiresAuth: true }
    },
    {
        path: '/plan/:planId/edit',
        name: 'EditPlan',
        component: EditPlanView,
        meta: { requiresAuth: true }
    },
    {
        path: '/plan/:planId',
        name: 'PlanDetail',
        component: PlanDetailView
    },
    {
        path: '/free-post',
        name: 'FreePostList',
        component: FreePostListView
    },
    {
        path: '/free-post/write',
        name: 'WriteFreePost',
        component: WriteFreePostView,
        meta: { requiresAuth: true }
    },
    {
        path: '/free-post/:postId/edit',
        name: 'EditFreePost',
        component: EditFreePostView,
        meta: { requiresAuth: true }
    },
    {
        path: '/free-post/:postId',
        name: 'FreePostDetail',
        component: FreePostDetailView
    },
    {
        path: '/review-post',
        name: 'ReviewPostList',
        component: ReviewPostListView
    },
    {
        path: '/resetpassword',
        name: 'ResetPassword',
        component: ResetPasswordView
    },
    {
        path: '/admin',
        name: 'Admin',
        component: AdminView,
        meta: { requiresAdminAuth: true }
    },
    {
        path: '/admin/magazine/write',
        name: 'WriteMagazine',
        component: WriteMagazineView,
        meta: { requiresAdminAuth: true }
    },
    {
        path: '/magazine/:magazineId',
        name: 'MagazineDetail',
        component: MagazineDetailView,
    },
    {
        path: '/admin/magazine/:magazineId/edit',
        name: 'EditMagazine',
        component: EditMagazineView,
        meta: { requiresAdminAuth: true }
    },
]

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes
})

router.beforeEach((to, from, next) => {
    if (to.matched.some(record => record.meta.requiresAuth)) {
        if (!store.getters.isAccessTokenValid) {
            if (window.confirm("로그인이 필요한 서비스입니다.\n로그인 페이지로 이동하시겠습니까?")) {
                next('/login')
            } else {
                next(false);
            }
        } else {
            next()
        }
    } else if (to.matched.some(record => record.meta.requiresAdminAuth)) {
        if (store.getters.getRole === 'ADMIN') {
            next()
        } else {
            alert('관리자만 접근 가능합니다.');
            next('/')
        }
    } else {
        next()
    }
})

export default router
