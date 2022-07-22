package com.test.ojm.vo;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Store {

    int storeKey;
    String storeThumUrl;    // [https://ldb-phinf.pstatic.net/20220318_83/1647570614790jNrs4_JPEG/%BC%F6%C1%A4%B5%CA_%B0%A5%BA%F1%BE%E7%B3%E4%B5%A4%B9%E4.jpg](https://ldb-phinf.pstatic.net/20220318_83/1647570614790jNrs4_JPEG/%BC%F6%C1%A4%B5%CA_%B0%A5%BA%F1%BE%E7%B3%E4%B5%A4%B9%E4.jpg)
    String storeName;       // - 가게 이름 : name or display ⇒ string
    String storeTel;        //    - 가게 연락처 : tel ⇒ string
    String storeDistance;   //    - 가게와의 거리 : distance ⇒ string
    Boolean storeSales;      //    - 영업 상태 ( 영업 중 : true, 영업 안함 : false ) ⇒ bool
    String storeBizhourInfo;//    - 영업시작시간 ( 자료조사) (bizhourInfo) ⇒
//    String storeReviewCount;//            - 리뷰점수 or 리뷰 개수( **reviewCount ) → 셀레니움테스트**
    String storeAddress;    //          - 주소 (address, abbrAddress, 도로명 : roadAddress)
    String storeId;         //  - 가게 아이디값(id)
    String storeCategory;   //- 카테고리 (category) ⇒
    int storeCategoryCode; // 카테고리 코드값
}
