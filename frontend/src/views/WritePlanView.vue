<script setup>
import {computed, ref} from 'vue';
import SelectAreaComponent from "@/components/SelectAreaComponent.vue";
import SelectDayComponent from "@/components/SelectDayComponent.vue";
import SelectPlaceComponent from "@/components/SelectPlaceComponent.vue";

const currentStep = ref('area'); // 'area', 'day', or 'place'
const selectedRegion = ref(null);
const startDate = ref(null);
const endDate = ref(null);

const currentComponent = computed(() => {
    switch (currentStep.value) {
        case 'area':
            return SelectAreaComponent;
        case 'day':
            return SelectDayComponent;
        case 'place':
            return SelectPlaceComponent;
        default:
            return SelectAreaComponent;
    }
});

const handleAreaSelected = (region) => {
    selectedRegion.value = region;
    currentStep.value = 'day';
};

const handleBackToArea = () => {
    currentStep.value = 'area';
    startDate.value = null;
    endDate.value = null;
};

const handleDatesSelected = (dates) => {
    startDate.value = dates.start;
    endDate.value = dates.end;
    currentStep.value = 'place';
};

const handleBackToDay = () => {
    currentStep.value = 'day';
};

const handleTripPlanCompleted = () => {
    // 여행 계획 완료 후 처리 로직
    console.log('Trip plan completed');
    // 예: 데이터 저장, 결과 페이지로 이동 등
};
</script>

<template>
    <div class="write-plan-container">
        <component
            :is="currentComponent"
            :selectedRegion="selectedRegion"
            :startDate="startDate"
            :endDate="endDate"
            @area-selected="handleAreaSelected"
            @back-to-area="handleBackToArea"
            @dates-selected="handleDatesSelected"
            @back-to-day="handleBackToDay"
            @trip-plan-completed="handleTripPlanCompleted"
        />
    </div>
</template>

<style scoped>
.write-plan-container {
    width: 100%;
    display: flex;
    flex-direction: column;
    margin-top: 2em;
}
</style>
