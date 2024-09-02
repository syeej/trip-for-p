<script setup>
import {useRoute} from "vue-router";

const route = useRoute();
const planId = route.params.planId;
</script>

<template>
    <div>
        여행 코스 상세 조회
        {{planId}}
    </div>
</template>

<style scoped>

</style>
