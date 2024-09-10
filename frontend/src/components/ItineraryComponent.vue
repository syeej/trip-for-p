<script setup>
import { ref, computed, defineProps, defineEmits } from 'vue';
import draggable from 'vuedraggable';

const props = defineProps({
    places: {
        type: Array,
        required: true
    },
    routeInfo: {
        type: Object,
        default: () => ({})
    },
    currentDate: {
        type: String,
        required: true
    }
});

const emit = defineEmits(['update:memo', 'reorder', 'delete']);

const formatDuration = (durationInSeconds) => {
    if (durationInSeconds == null) return 'N/A';

    const hours = Math.floor(durationInSeconds / 3600);
    const minutes = Math.floor((durationInSeconds % 3600) / 60);
    const seconds = durationInSeconds % 60;

    const parts = [];

    if (hours > 0) {
        parts.push(`${hours}시간`);
    }
    if (minutes > 0) {
        parts.push(`${minutes}분`);
    }
    if (seconds > 0 || parts.length === 0) {
        parts.push(`${seconds}초`);
    }

    return parts.join(' ');
};

const dragOptions = ref({
    animation: 200,
    group: "description",
    disabled: false,
    ghostClass: "ghost"
});

const updateMemo = (index, event) => {
    emit('update:memo', index, event.target.value);
};

const deletePlace = (index) => {
    emit('delete', index);
};

const onEnd = (event) => {
    emit('reorder', props.currentDate, event.oldIndex, event.newIndex);
};

const totalDuration = computed(() => {
    return props.routeInfo?.summary?.duration ?? 0;
});

const getSectionDuration = (index) => {
    return props.routeInfo?.sections?.[index]?.duration ?? null;
};

</script>

<template>
    <div class="itinerary">
        <draggable
            :list="places"
            v-bind="dragOptions"
            @end="onEnd"
            item-key="id"
            handle=".drag-handle"
        >
            <template #item="{ element, index }">
                <div class="itinerary-item">
                    <div class="place-item">
                        <div class="drag-handle">&#9776;</div>
                        <div class="place-info">
                            <span class="sequence">{{ index + 1 }}</span>
                            <div>
                                <h3>{{ element.place.place_name }}</h3>
                                <p>{{ element.place.address_name }}</p>
                            </div>
                        </div>
                        <textarea
                            :value="element.memo"
                            @input="updateMemo(index, $event)"
                            placeholder="메모를 입력하세요"
                        ></textarea>
                        <button @click="deletePlace(index)" class="delete-btn">삭제</button>
                    </div>
                    <div v-if="index < places.length - 1" class="duration-info">
                        예상 소요 시간: {{ formatDuration(getSectionDuration(index)) }}
                    </div>
                </div>
            </template>
        </draggable>
        <div v-if="totalDuration > 0" class="total-duration">
            총 예상 소요 시간: {{ formatDuration(totalDuration) }}
        </div>
    </div>
</template>

<style scoped>
.itinerary {
    margin-top: 20px;
}

.itinerary-item {
    margin-bottom: 10px;
}

.place-item {
    display: flex;
    align-items: center;
    padding: 10px;
    background-color: #f0f0f0;
    border-radius: 5px;
}

.drag-handle {
    cursor: move;
    padding: 5px;
    margin-right: 10px;
    color: #888;
}

.place-info {
    flex-grow: 1;
    display: flex;
    align-items: center;
}

.sequence {
    font-size: 18px;
    font-weight: bold;
    margin-right: 10px;
}

textarea {
    width: 200px;
    height: 60px;
    margin: 0 10px;
}

.delete-btn {
    padding: 5px 10px;
    background-color: #ff4444;
    color: white;
    border: none;
    border-radius: 3px;
    cursor: pointer;
}

.duration-info {
    margin: 10px 0;
    padding: 5px;
    background-color: #e6f7ff;
    border-radius: 3px;
}

.total-duration {
    margin-top: 20px;
    font-weight: bold;
    text-align: right;
}

.ghost {
    opacity: 0.5;
    background: #c8ebfb;
}
</style>
