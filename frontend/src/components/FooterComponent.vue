<script setup>
// 필요한 경우 여기에 스크립트 로직을 추가할 수 있습니다.
</script>

<template>
    <div class="footer-container">
        <div class="footer-content">
            <div class="footer-info">
                <p>트립 포 피 | P를 위한 여행 플래너</p>
            </div>
        </div>
        <div class="footer-bottom">
            <p>&copy; {{ new Date().getFullYear() }} Trip For P. All rights reserved.</p>
        </div>
    </div>
</template>

<style scoped>
.footer-container {
    width: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: end;
    padding: 10px 0;
}

.footer-content {
    width: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    text-align: center;
}

.footer-info p {
    margin: 5px 0;
    color: #333;
}

.footer-bottom {
    width: 100%;
    text-align: center;
    padding: 0 0 5px 0;
    background-color: #fff;
}

.footer-bottom p {
    font-size: 0.9em;
    color: #666;
    margin: 0;
}

@media (max-width: 768px) {
    .footer-content {
        padding: 0 20px;
    }
}

@media (max-width: 480px) {
    .footer-logo img {
        width: 120px;
    }

    .footer-info p {
        font-size: 0.9em;
    }
}
</style>
