# RememberEverything   
- 한 번에 모두 기억해주는 똑똑이 어플리케이션

## 프로젝트 계획 이유
> 마트, 아울렛 등의 대형 쇼핑몰과 음식점의 휴무일이 모두 달라서 겪는 불편함을 해소하기 위해서 쇼핑몰과 음식점의 휴무일을 한 번에 볼 수 있는
> 어플리케이션이 있으면 좋겠다는 생각을 하였고 추가로 다른 기능들을 추가하여 틀을 잡고 프로젝트를 구현하였습니다.  
> 전국의 대형 쇼핑몰과 음식점의 정보를 모두 가져오는 것은 무리라고 판단하여, 인천광역시 가게들의 정보를 활용하였습니다.  

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
   ![KakaoTalk_20210626_015935529](https://user-images.githubusercontent.com/71651038/123461103-9b249f00-d623-11eb-9979-3694a22efc65.jpg)
   
2. 로그인 화면  
 ![KakaoTalk_20210626_015924527](https://user-images.githubusercontent.com/71651038/123461169-b8596d80-d623-11eb-854e-8a7580a08970.jpg)

3. 대형쇼핑몰 리스트 (휴무일 한눈에 보이기 위해 밝은 색으로 강조)  
  ![KakaoTalk_20210626_015935371](https://user-images.githubusercontent.com/71651038/123461231-d030f180-d623-11eb-801b-f225076245c3.jpg)

3-1. 리스트 중 아이템 항목 클릭시 상세정보 - 전화걸기, 단골가게 추가  
  ![KakaoTalk_20210626_015934848](https://user-images.githubusercontent.com/71651038/123462823-bf817b00-d625-11eb-9dbe-1acf284b7703.jpg)
> (1) 전화걸기 버튼 클릭시  
> ![KakaoTalk_20210626_015934692](https://user-images.githubusercontent.com/71651038/123463019-fc4d7200-d625-11eb-9111-baa7d425a951.jpg)  
> (2) 단골가게 추가 버튼 클릭시 - 파이어베이스에 저장됨  
> ![KakaoTalk_20210626_015934531](https://user-images.githubusercontent.com/71651038/123463235-3d458680-d626-11eb-9adf-dd4223c81402.jpg)  


4. 음식점 리스트 (휴무일 한눈에 보이기 위해 밝은 색으로 강조)  
  ![KakaoTalk_20210626_015935192](https://user-images.githubusercontent.com/71651038/123461358-fc4c7280-d623-11eb-88c3-ffb0a203098c.jpg)
  
5. 구글맵으로 보기 - 사용자 위치 기반으로 현재 영업중인 가게만 마커로 표시
  ![KakaoTalk_20210626_015924387](https://user-images.githubusercontent.com/71651038/123462216-fa36e380-d624-11eb-8a29-d99072a89f11.jpg)

6. 드로어  
  ![KakaoTalk_20210626_021706348](https://user-images.githubusercontent.com/71651038/123462411-42560600-d625-11eb-9226-277121808e1a.jpg)

7. 단골가게 목록 - 사용자 별로 단골가게 추가 및 삭제 가능(파이어베이스에 사용자 구분해서 저장)  
  ![KakaoTalk_20210626_015934360](https://user-images.githubusercontent.com/71651038/123462528-687ba600-d625-11eb-8b3e-25ca7aa58ac2.jpg)

8. 캘린더 형식의 메모장 - 해당하는 날짜에 파이어베이스 내에 메모가 존재하는 경우 해당 메모 내용 사용자가 확인 및 수정 가능  
  ![KakaoTalk_20210626_015924197](https://user-images.githubusercontent.com/71651038/123463450-809ff500-d626-11eb-8e97-bc2609d99f01.jpg)

9. 날씨와 미세먼지 기반 메뉴 추천  
  ![KakaoTalk_20210626_015924057](https://user-images.githubusercontent.com/71651038/123463766-e0969b80-d626-11eb-8011-4f30b2d8ce64.jpg)

10. 날씨와 미세먼지 기반 주변 음식점 추천  
  ![KakaoTalk_20210626_015923795](https://user-images.githubusercontent.com/71651038/123463898-0de34980-d627-11eb-9edb-6bd1f91d0310.jpg)

