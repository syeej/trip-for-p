# Trip For P
p들을 위한 맞춤형 여행 계획 서비스


## 목차
1. [팀원 소개 및 역할](#-팀원-소개-및-역할)
2. [프로젝트 개요](#-프로젝트-개요) 
4. [개발 기술 및 환경](#️-개발-기술-및-환경)
5. [개발 일정](#️-개발-일정)
6. [배포 URL 및 테스트 계정](#️-배포-URL-및-테스트-계정)
7. [Architecture](#️-architecture)
8. [요구사항 및 기능 명세](#️-요구사항-및-기능-명세)
9. [ERD](#️-erd)
10. [프로젝트 구조](#️-프로젝트-구조)
11. [API 명세서](#️-API-명세서)
12. [트러블 슈팅](#️-트러블-슈팅)


## 팀원 소개 및 역할
<div align="center">
  
|<img src=" " width="150" height="150"/>|<img src=" " width="150" height="150"/>|<img src=" " width="150" height="150"/>|
|:-:|:-:|:-:|
|박정균(팀장)<br/>[@junggyun](https://github.com/junggyun)|문석준<br/>[@SaMiGong](https://github.com/SaMiGong)|박준호<br/>[@junhobark](https://github.com/junhobark)|
| **#설계**<br>- ERD 설계<br><br>**#Frontend**<br>- 개발 총괄<br><br>**#Backend**<br>- 여행코스 API 개발<br>- AI 관련 서비스 개발<br>- 첨부파일(AWS S3) 등록 API 개발<br><br>| **#설계**<br>- 화면 설계<br><br>**#Frontend**<br>- 프론트엔드 개발<br><br>**#Backend**<br>- 여행코스 API 개발<br><br> | **#설계**<br>- 화면 설계<br><br>**#Frontend**<br>- 프론트엔드 개발<br><br>**#Backend**<br>- 여행코스 API 개발<br>- AI 개인 맞춤형 여행 코스 추천<br>서비스 개발<br><br>|
|<img src=" " width="150" height="150"/>|<img src=" " width="150" height="150"/>|<img src=" " width="150" height="150"/>|
|이남경<br/>[@NamK666](https://github.com/NamK666)|조성윤<br/>[@syeej](https://github.com/syeej)|허영윤<br/>[@cloudisme99](https://github.com/cloudisme99)|
| **#설계**<br>- 화면 설계<br><br>**#Backend**<br>- 리뷰 게시글 + 댓글 API 개발<br>- 자유 게시글 + 댓글 API 개발<br>- 매거진 게시글(첨부파일) API 개발<br><br>| **#설계**<br>- ERD 및 배포 구조 설계<br><br>**#Frontend**<br>- 마이페이지 프론트엔드 개발<br><br>**#Backend**<br>- 회원가입 및 이메일 인증 API 개발<br>- 마이페이지 API 개발<br><br>**#기타**<br>- GitHub Actions 활용 CI/CD 구축<br><br>| **#설계**<br>- ERD 설계<br><br>**#Backend**<br>- 로그인, 로그아웃<br>(Spring Security & JWT) 개발<br>- 비밀번호 재설정 API 개발<br>- AI 장소 기반 추천 서비스 API 개발<br><br>|
</div>

</br>

## 💡 프로젝트 개요
- 주제 : 여행 계획 세우기가 어려운 **MBTI가 P인** 사람들을 위한 여행 계획 서비스
- 선정 배경
    - 코로나19 이후 여행 및 관광 사업이 활기를 되찾으며 꾸준히 성장하고 있고 여행자들의 기대와 요구도 더욱 다양하고 세분화되면서 여행 계획의 필요성이 증가하고 있음
    - 하지만 많은 사람들이 여행 경로 설정, 숙박, 관광지 찾기 등 여행을 계획하는데 부담을 느끼고 있음.
    - 계획을 세우는 것이 어려운 사람들을 위해 맞춤형으로 여행 일정을 제안하고자 함
- 기대 효과
    - 국내 여행 및 관광 산업 활성화
    - 경로, 숙박, 관광지 등 여행 계획 설정의 간편화로 사용자 편의성 증진

<br>

## ⚙️ 개발 기술 및 환경

**BE**

<img src="https://img.shields.io/badge/JAVA-007396?style=for-the-badge&logo=java&logoColor=white"> <img src="https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=Spring&logoColor=white">
<img src="https://img.shields.io/badge/SpringBoot-6DB33F?style=for-the-badge&logo=SpringBoot&logoColor=white">
<img src="https://img.shields.io/badge/Spring%20Data%20JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white">
<img src="https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=Spring-Security&logoColor=white">
<img src="https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=JSON%20web%20tokens&logoColor=white">
<img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white">
<img src="https://img.shields.io/badge/Alan%20AI-blue?style=for-the-badge">
<img src="https://img.shields.io/badge/AWS%20S3-569A31?style=for-the-badge&logo=amazon-s3&logoColor=white">
<img src="https://img.shields.io/badge/redis-%23DD0031.svg?&style=for-the-badge&logo=redis&logoColor=white">

**DevOps**

<img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white"><img src="https://img.shields.io/badge/docker--compose-2496ED?style=for-the-badge&logo=docker&logoColor=white">
<img src="https://img.shields.io/badge/GitHub%20Actions-2088FF?style=for-the-badge&logo=github-actions&logoColor=white">
<img src="https://img.shields.io/badge/Nginx-009639?style=for-the-badge&logo=nginx&logoColor=white">
<img src="https://img.shields.io/badge/AWS%20EC2-FF9900?style=for-the-badge&logo=amazon-aws&logoColor=white">
<img src="https://img.shields.io/badge/AWS%20RDS-527FFF?style=for-the-badge&logo=amazon-aws&logoColor=white">


**FE**

<img src="https://img.shields.io/badge/Vue.js-4FC08D?style=for-the-badge&logo=vue.js&logoColor=white">

**TOOLS**

<img src="https://img.shields.io/badge/IntelliJ%20IDEA-000000?style=for-the-badge&logo=intellij-idea&logoColor=white"><img src="https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white"><img src="https://img.shields.io/badge/Notion-000000?style=for-the-badge&logo=notion&logoColor=white">

<br>

## 🗓️ 개발 일정
<div align="center">
  
  ![image](https://github.com/user-attachments/assets/235cad51-a0df-49d9-bf50-9c3a5f0f5e63)

</div>

8/21 ~ 22 : 아이디어 기획

8/23 ~ 26 : 

- 요구사항 정의서, ERD 작성
- 플로우차트, UI 설계
- 배포구조, API 설계 작성

8/27 : 중간 점검

8.27 ~ 9/10 :

- BE & FE 구현
- CI/CD 구축

9/11 ~ 9/12 : 테스트 및 최종 점검

9/13 : 프로젝트 발표

<br>

## 배포 URL 및 테스트 계정
- 배포 URL: http://54.180.168.123/ 
- 테스트 계정:
  
## ⚙️ Architecture
![image](https://github.com/user-attachments/assets/ff5914f6-937f-4752-8eaa-9c96c29e371d)

## 🖥 요구사항 및 기능 명세

<br>

## ⚙️ ERD
![image](https://github.com/user-attachments/assets/775773d7-42a4-4ac4-969e-f427fceede93)

## 프로젝트 구조
- 사진 or 코드

## API 명세서

## 구현 화면 GIF

## 트러블 슈팅
