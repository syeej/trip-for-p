<script setup>
import { defineProps, defineEmits, toRefs } from 'vue';
import draggable from 'vuedraggable';

const props = defineProps({
    places: {
        type: Array,
        required: true
    }
});

// props의 반응성을 유지하면서 places를 추출
const { places } = toRefs(props);

const emit = defineEmits(['update:memo', 'reorder', 'delete']);


const onDragEnd = () => {
    emit('reorder');
};

const deletePlace = (index) => {
    emit('delete', index);
};
</script>

<template>
    <div class="itinerary">
        <draggable
            :list="places"
            item-key="sequence"
            @end="onDragEnd"
            handle=".drag-handle"
        >
            <template #item="{ element, index }">
                <div class="itinerary-item">
                    <div class="drag-handle">&#8942;</div>
                    <div class="content-wrapper">
                        <div class="place-info">
                            <div class="place-name">{{ element.place.place_name }}</div>
                            <div class="place-address">{{ element.place.address_name }}</div>
                        </div>
                        <div class="memo-wrapper">
                            <textarea
                                v-model="element.memo"
                                @input="emit('update:memo', index, $event.target.value)"
                                placeholder="메모를 입력하세요"
                            ></textarea>
                        </div>
                    </div>
                    <button @click="deletePlace(index)" class="delete-btn">삭제</button>
                </div>
            </template>
        </draggable>
    </div>
</template>

<style scoped>
.itinerary {
    background-color: #f5f5f5;
    padding: 20px;
    border-radius: 8px;
}

.itinerary-item {
    display: flex;
    align-items: center;
    margin-bottom: 15px;
    padding: 10px;
    background-color: white;
    border-radius: 4px;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.drag-handle {
    cursor: move;
    padding: 0 10px;
    font-size: 20px;
    color: #888;
}

.content-wrapper {
    display: flex;
    flex-grow: 1;
    align-items: center;
    overflow: hidden;
}

.place-info {
    flex-grow: 1;
    min-width: 0; /* 이를 통해 flex item이 내용에 따라 축소될 수 있음 */
    margin-right: 10px;
}

.place-name {
    font-weight: bold;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}

.place-address {
    font-size: 0.9em;
    color: #666;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}

.memo-wrapper {
    width: 250px; /* 메모 영역의 너비 제한 */
    flex-shrink: 0;
}

textarea {
    width: 100%;
    height: 60px; /* 높이 제한 */
    padding: 5px;
    resize: vertical; /* 세로 방향으로만 크기 조절 가능 */
    overflow-y: auto; /* 내용이 넘칠 경우 스크롤 표시 */
}

.delete-btn {
    background-color: #ff4d4d;
    color: white;
    border: none;
    padding: 5px 10px;
    border-radius: 3px;
    cursor: pointer;
    margin-left: 10px;
    flex-shrink: 0;
}

.delete-btn:hover {
    background-color: #ff3333;
}
</style>
