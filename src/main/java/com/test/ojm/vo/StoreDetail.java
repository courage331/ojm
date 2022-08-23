package com.test.ojm.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StoreDetail {
    List<Menus> storeMenuList; //메뉴리스트 - menus
    List<MenuImages> storeMenuImage; //가게 메뉴 이미지 - menuInages
    List<StoreImages> storeImage; //가게 이미지 - images
    String storeTel; // 가게 번호 - phone
    String storeName; // 가게 이름 - name
    String storeAddress; // 가게 주소 - fullRoadAddress
//    boolean bizHourInfo;
    String storeBizHourInfo; //가게 영업 시간 내용 - bizHourInfo
    StoreLocation storeCoords; //- 네이버 detail 호출 할 때 x, y 값으로 나옴
    boolean storeBizState; //가게 영업 상태
}
