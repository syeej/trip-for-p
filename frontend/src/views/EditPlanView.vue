<script setup>
import {onMounted, ref} from 'vue';
import SelectPlaceComponent from "@/components/SelectPlaceComponent.vue";
import {getPlanAPI} from "@/api";
import {useRoute} from "vue-router";

const route = useRoute();

const selectedRegion = ref(null);
const startDate = ref(null);
const endDate = ref(null);
const planItems = ref([]);
const isLoading = ref(true);

const formatDate = (date) => {
    if (!date) {
        return '';
    }
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
};

const getPlan = async function () {
    try {
        const response = await getPlanAPI(route.params.planId);
        const plan = response.data;
        selectedRegion.value = plan.area;
        // `startDate`와 `endDate`를 Date 객체로 변환
        const start = new Date(plan.startDate);
        const end = plan.endDate ? new Date(plan.endDate) : start;

        // 변환된 날짜를 포맷에 맞게 가공
        startDate.value = formatDate(start);
        endDate.value = formatDate(end);

        planItems.value = plan.planItems.map(item => ({
            id: item.id,
            place: {
                id: item.place.id,
                place_name: item.place.placeName,
                address_name: item.place.addressName,
                category_name: item.place.categoryName,
                x: item.place.x,
                y: item.place.y
            },
            tripDate: item.tripDate,
            sequence: item.sequence,
            memo: item.memo
        }));
        isLoading.value = false;
    } catch (error) {
        console.log(error)
        isLoading.value = false;
    }
};

onMounted(() => {
    getPlan();
})
</script>

<template>
    <div class="update-plan-container">
        <div v-if="isLoading">Loading...</div>
        <SelectPlaceComponent
            v-else
            :key="selectedRegion"
            :startDate="startDate"
            :endDate="endDate"
            :selectedRegion="selectedRegion"
            :planItems="planItems"
            mode="update"
        />
    </div>
</template>

<style scoped>
.update-plan-container {
    width: 100%;
    display: flex;
    flex-direction: column;
    margin-top: 50px;
}
</style>
