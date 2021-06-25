# RememberEverything - 한 번에 모두 기억해주는 똑똑이 어플리케이션

## 프로젝트 계획 이유
> 마트, 아울렛 등의 대형 쇼핑몰과 음식점의 휴무일이 모두 달라서 겪는 불편함을 해소하기 위해서 쇼핑몰과 음식점의 휴무일을 한 번에 볼 수 있는
> 어플리케이션이 있으면 좋겠다는 생각을 하였고 추가로 다른 기능들을 추가하여 틀을 잡고 프로젝트를 구현하였습니다.

## RememberEverything 앱 기능 설명
---------------------------------
1. 카카오 로그인 기능 : 카카오 로그인과 로그아웃 기능, 사용자 동의 시에 이메일 정보로 사용자 구분
2. 백화점 및 대형쇼핑몰 휴무일 등 정보를 Recyclerview로 보여줌 : 인천투어 사이트를 웹 크롤링하여 정보 획득
3. 음식점의 휴무일 등 정보를 Recyclerview로 보여줌 : 공공 데이터 포털 인천광역시 일반 음식점 API를 이용하여 정보 획득
4. 구글맵을 이용한 사용자 위치 기반으로 영업중인 주변 가게 마커로 표시 : 휴무일인 가게는 보이지 않아 한눈에 영업중인 가게 파악 가능
5. 상점 각각의 항목 클릭시 상세정보 창  
    -> 사용자 지정 단골가게 추가 버튼  
    -> 해당 가게로 전화하기 기능
6. 현재 위치 기반 날씨와 미세먼지 정보 알려주는 기능 : API 이용하여 사용자 위치 기반으로 알려줌  
   -> 메뉴 추천 버튼 클릭시 날씨와 미세먼지 정보 기반으로 음식 메뉴 랜덤 추천  
   -> 음식점 추천 클릭시 날씨와 미세먼지 정보 기반으로 근처 음식점 추천
7. 캘린더 모양의 메모장 기능 : 파이어베이스에 사용자별로 구분하여 메모 내용을 저장하여 날짜별로 메모 가능


## 어플 실행 화면
-----------------
1. 스플래시 화면  
   ![KakaoTalk_20210626_015935529](https://user-images.githubusercontent.com/71651038/123460519-b6db7580-d622-11eb-9734-5bad23d1bae1.jpg)

2. 로그인 화면  
 ![KakaoTalk_20210626_015924527](https://user-images.githubusercontent.com/71651038/123460793-25203800-d623-11eb-923b-13b84a130c57.jpg)

3. 대형쇼핑몰 리스트 (휴무일 한눈에 보이기 위해 밝은 색으로 강조)
