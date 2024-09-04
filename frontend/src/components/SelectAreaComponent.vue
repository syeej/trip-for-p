<script setup>
import { ref, onMounted, watch } from 'vue';
import * as d3 from 'd3';
import * as topojson from 'topojson';
import { defineEmits } from 'vue';

const svgPathList = ref([]);
const hoveredRegion = ref(null);
const selectedRegion = ref(null);

const emit = defineEmits(['area-selected']);

const drawMap = () => {
    import('../components/korea.json').then((mapData) => {
        const geoJson = topojson.feature(mapData, mapData.objects.korea);
        const projection = d3.geoMercator().center([129.380886, 37.024502]).scale(7200);
        const pathProjection = d3.geoPath().projection(projection);

        svgPathList.value = geoJson.features.map((data) => ({
            name: data.properties.name,
            path: pathProjection(data)
        }));

        const svg = d3.select(".map");

        svg.append("rect")
        .attr("width", "100%")
        .attr("height", "100%")
        .attr("fill", "#C4E1E5");

        const path = d3.geoPath().projection(projection);

        const regions = svg.append("g")
        .attr("class", "regions");

        regions.selectAll("path")
        .data(geoJson.features)
        .enter()
        .append("path")
        .attr("d", path)
        .attr("fill", "#A4C069")
        .attr("stroke", "#ffffff")
        .attr("stroke-width", 0.5)
        .attr("data-region", d => d.properties.name)
        .on("mouseover", (event, d) => {
            hoveredRegion.value = d.properties.name;
            updateTextVisibility();
        })
        .on("mouseout", () => {
            hoveredRegion.value = null;
            updateTextVisibility();
        })
        .on("click", handleRegionClick);

        const textLayer = svg.append("g")
        .attr("class", "region-labels");

        textLayer.selectAll("text")
        .data(geoJson.features)
        .enter()
        .append("text")
        .attr("data-region", d => d.properties.name)
        .attr("transform", d => `translate(${path.centroid(d)})`)
        .attr("dy", ".35em")
        .attr("text-anchor", "middle")
        .text(d => d.properties.name)
        .attr("font-size", "25px")
        .attr("fill", "#333")
        .style("visibility", "hidden");
    });
};

const updateSelectedRegion = () => {
    d3.selectAll("path")
    .attr("fill", d => d.properties.name === selectedRegion.value ? "#6a994e" : "#A4C069");
};

const updateTextVisibility = () => {
    d3.selectAll(".region-labels text")
    .style("visibility", d =>
        d.properties.name === selectedRegion.value || d.properties.name === hoveredRegion.value
            ? "visible"
            : "hidden"
    );
};

const handleNext = () => {
    if (selectedRegion.value) {
        emit('area-selected', selectedRegion.value);
    }
};

const handleRegionClick = (event, d) => {
    selectedRegion.value = d.properties.name;
    updateSelectedRegion();
    updateTextVisibility();

    // 버튼으로 스크롤
    setTimeout(() => {
        const button = document.querySelector('.next-button');
        if (button) {
            button.scrollIntoView({ behavior: 'smooth', block: 'center' });
        }
    }, 100);
};

onMounted(() => {
    drawMap();
});

watch(selectedRegion, () => {
    updateSelectedRegion();
    updateTextVisibility();
});
</script>

<template>
    <div class="select-area-container">
        <h2>지역을 선택해주세요</h2>
        <div class="map-container">
            <svg width="400" height="600" viewBox="0 0 600 800" class="map"></svg>
        </div>
        <div v-if="selectedRegion" class="selected-region">
            선택된 지역: {{ selectedRegion }}
        </div>
        <button
            @click="handleNext"
            :disabled="!selectedRegion"
            class="next-button"
        >
            다음
        </button>
    </div>
</template>

<style scoped>
.select-area-container {
    width: 100%;
    display: flex;
    flex-direction: column;
}

h2 {
    text-align: center;
    line-height: 67px;
}

.map-container {
    display: flex;
    justify-content: center;
    background-color: #C4E1E5;
}

.map {
    max-width: 100%;
    height: auto;
}

:deep(path) {
    transition: fill 0.3s ease, filter 0.3s ease;
}

:deep(path:hover) {
    filter: brightness(90%);
    cursor: pointer;
}

:deep(.region-labels text) {
    pointer-events: none;
    font-weight: bold;
    transition: visibility 0.3s ease;
}

.selected-region {
    margin-top: 20px;
    font-size: 18px;
    font-weight: bold;
    text-align: center;
}

.next-button {
    margin-top: 20px;
    padding: 10px 20px;
    background-color: #4CAF50;
    color: white;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    font-size: 16px;
    align-self: center;
}

.next-button:disabled {
    background-color: #cccccc;
    cursor: not-allowed;
}
.next-button:hover{
    transform: translateY(-2px);
}

.next-button:active{
    transform: translateY(0);
    box-shadow: 0 1px 2px rgba(0,0,0,0.2);
}
</style>
