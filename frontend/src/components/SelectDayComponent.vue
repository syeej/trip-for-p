<script setup>
import { ref, computed } from 'vue';
import { defineProps, defineEmits } from 'vue';

const props = defineProps({
    selectedRegion: String
});

const emit = defineEmits(['back-to-area', 'dates-selected']);

const currentDate = ref(new Date());
const startDate = ref(null);
const endDate = ref(null);

const daysInMonth = computed(() => {
    const year = currentDate.value.getFullYear();
    const month = currentDate.value.getMonth();
    return new Date(year, month + 1, 0).getDate();
});

const firstDayOfMonth = computed(() => {
    const year = currentDate.value.getFullYear();
    const month = currentDate.value.getMonth();
    return new Date(year, month, 1).getDay();
});

const days = computed(() => {
    const days = [];
    for (let i = 1; i <= daysInMonth.value; i++) {
        days.push(i);
    }
    return days;
});

const handleNext = () => {
    if (startDate.value) {
        emit('dates-selected', {
            start: formatDate(startDate.value),
            end: formatDate(endDate.value || startDate.value)  // endDate가 없으면 startDate로 설정
        });
    }
};


const monthYear = computed(() => {
    const options = {year: 'numeric', month: 'long'};
    return currentDate.value.toLocaleDateString('ko-KR', options);
});

const prevMonth = () => {
    currentDate.value = new Date(currentDate.value.getFullYear(), currentDate.value.getMonth() - 1, 1);
};

const nextMonth = () => {
    currentDate.value = new Date(currentDate.value.getFullYear(), currentDate.value.getMonth() + 1, 1);
};

const selectDate = (day) => {
    const selectedDate = new Date(currentDate.value.getFullYear(), currentDate.value.getMonth(), day);
    if (!startDate.value || (startDate.value && endDate.value)) {
        startDate.value = selectedDate;
        endDate.value = null;
    } else if (selectedDate < startDate.value) {
        startDate.value = selectedDate;
    } else {
        endDate.value = selectedDate;
    }
};

const isToday = (day) => {
    const today = new Date();
    return today.getDate() === day &&
        today.getMonth() === currentDate.value.getMonth() &&
        today.getFullYear() === currentDate.value.getFullYear();
};

const isSelected = (day) => {
    const date = new Date(currentDate.value.getFullYear(), currentDate.value.getMonth(), day);
    return (startDate.value && date.getTime() === startDate.value.getTime()) ||
        (endDate.value && date.getTime() === endDate.value.getTime());
};

const isInRange = (day) => {
    if (!startDate.value || !endDate.value) return false;
    const date = new Date(currentDate.value.getFullYear(), currentDate.value.getMonth(), day);
    return date > startDate.value && date < endDate.value;
};

const formatDate = (date) => {
    if (!date) return '';
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
};

const formatDisplayDate = (date) => {
    if (!date) return '';
    const month = date.getMonth() + 1;
    const day = date.getDate();
    const weekday = ['일', '월', '화', '수', '목', '금', '토'][date.getDay()];
    return `${month}.${day}(${weekday})`;
};

const displayDateRange = computed(() => {
    if (startDate.value && endDate.value) {
        return `${formatDisplayDate(startDate.value)} ~ ${formatDisplayDate(endDate.value)}`;
    } else {
        return formatDisplayDate(startDate.value);
    }
});

const calculateDuration = computed(() => {
    if (startDate.value) {
        const start = new Date(startDate.value);
        const end = new Date(endDate.value || startDate.value);  // endDate가 없으면 startDate로 설정
        const diffTime = Math.abs(end - start);
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
        if (diffDays === 0) {
            return `당일치기`;  // 당일치기인 경우
        } else {
            return `${diffDays}박 ${diffDays + 1}일`;  // 다중일 경우
        }
    }
    return '';
});

const handleBack = () => {
    emit('back-to-area');
};
</script>

<template>
    <div class="select-day-container">
        <h2>{{ props.selectedRegion }}의 여행 날짜를 선택해주세요</h2>
        <div class="calendar">
            <div class="calendar-header">
                <button @click="prevMonth">&lt;</button>
                <h3>{{ monthYear }}</h3>
                <button @click="nextMonth">&gt;</button>
            </div>
            <div class="calendar-body">
                <div class="weekdays">
                    <span>일</span>
                    <span>월</span>
                    <span>화</span>
                    <span>수</span>
                    <span>목</span>
                    <span>금</span>
                    <span>토</span>
                </div>
                <div class="days">
                    <span v-for="_ in firstDayOfMonth" :key="'empty-' + _" class="empty"></span>
                    <span
                        v-for="day in days"
                        :key="day"
                        :class="{
                            'day': true,
                            'today': isToday(day),
                            'selected': isSelected(day),
                            'in-range': isInRange(day)
                        }"
                        @click="selectDate(day)"
                    >
                        {{ day }}
                    </span>
                </div>
            </div>
        </div>
        <div v-if="startDate" class="selected-dates">
            <div class="duration">여행 기간: {{ calculateDuration }}</div>
            <div class="date-range">{{ displayDateRange }}</div>
        </div>
        <div class="button-container">
            <button @click="handleBack" class="back-button">이전</button>
            <button @click="handleNext" class="next-button" :disabled="!startDate">다음</button>
        </div>
    </div>
</template>

<style scoped>
.select-day-container {
    width: 100%;
    max-width: 400px;
    margin: 0 auto;
    display: flex;
    flex-direction: column;
    align-items: center;
    font-family: Arial, sans-serif;
}

h2 {
    text-align: center;
    margin-bottom: 20px;
}

.calendar {
    width: 100%;
    border: 1px solid #ccc;
    border-radius: 5px;
    padding: 10px;
    box-shadow: 0 2px 5px rgba(0,0,0,0.1);
}

.calendar-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 10px;
}

.calendar-header button {
    background: none;
    border: none;
    font-size: 18px;
    cursor: pointer;
    padding: 5px 10px;
}

.calendar-header h3 {
    margin: 0;
    font-size: 18px;
}

.weekdays {
    display: grid;
    grid-template-columns: repeat(7, 1fr);
    text-align: center;
    font-weight: bold;
    margin-bottom: 5px;
}

.weekdays span {
    padding: 5px;
}

.days {
    display: grid;
    grid-template-columns: repeat(7, 1fr);
    gap: 5px;
}

.day, .empty {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 30px;
    border-radius: 50%;
    cursor: pointer;
}

.day:hover {
    background-color: #f0f0f0;
}

.today {
    color: #007bff;
    font-weight: bold;
}

.selected {
    background-color: #007bff;
    color: white;
}

.in-range {
    background-color: #e6f3ff;
}

.selected-dates {
    margin-top: 20px;
    text-align: center;
}

.duration {
    font-weight: bold;
    color: #007bff;
    margin-bottom: 5px;
}

.date-range {
    font-size: 1.2em;
    color: #333;
}
.button-container {
    display: flex;
    gap: 10px;
}
button {
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
button:disabled {
    background-color: #cccccc;
    cursor: not-allowed;
}
button:hover{
    transform: translateY(-2px);
}

button:active{
    transform: translateY(0);
    box-shadow: 0 1px 2px rgba(0,0,0,0.2);
}

@media (max-width: 480px) {
    .select-day-container {
        padding: 10px;
    }

    .calendar {
        padding: 5px;
    }

    .day, .empty {
        height: 25px;
        font-size: 14px;
    }
}
</style>
