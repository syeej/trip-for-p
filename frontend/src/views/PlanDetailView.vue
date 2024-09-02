<script setup>
import {useRoute} from "vue-router";
import {getPlanAPI} from "@/api";
import {onMounted, ref} from "vue";

const route = useRoute();
const planId = route.params.planId;
const plan = ref({});

const getPlan = async function () {
    try {
        const response = await getPlanAPI(planId);
        plan.value = response.data;
    } catch (error) {
        console.log(error);
    }
}
onMounted(() => {
    getPlan();
})
</script>

<template>
    <div>
        여행 코스 상세 조회
        {{plan}}
    </div>
</template>

<style scoped>

</style>
