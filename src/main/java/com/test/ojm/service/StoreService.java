package com.test.ojm.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.test.ojm.vo.Menus;
import com.test.ojm.vo.ResponseInfo;
import com.test.ojm.vo.Store;
import com.test.ojm.vo.StoreCategory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.spring.web.json.Json;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class StoreService {

    @Autowired
    Gson gson;

    static int storeCategoryCode = 0;

    /**
     * ex)
     * - thumUrl : [https://ldb-phinf.pstatic.net/20220318_83/1647570614790jNrs4_JPEG/%BC%F6%C1%A4%B5%CA_%B0%A5%BA%F1%BE%E7%B3%E4%B5%A4%B9%E4.jpg](https://ldb-phinf.pstatic.net/20220318_83/1647570614790jNrs4_JPEG/%BC%F6%C1%A4%B5%CA_%B0%A5%BA%F1%BE%E7%B3%E4%B5%A4%B9%E4.jpg)
     * - 가게 이름 : name or display ⇒ string
     * - 가게 연락처 : tel ⇒ string
     * - 가게와의 거리 : distance ⇒ string
     * - 영업 상태 ( 영업 중 : true, 영업 안함 : false ) ⇒ bool
     * - 영업시작시간 ( 자료조사) (bizhourInfo) ⇒
     * - 리뷰점수 or 리뷰 개수( **reviewCount ) → 셀레니움테스트**
     * - 주소 (address, abbrAddress, 도로명 : roadAddress)
     * - 가게 아이디값(id)
     * - 카테고리 (category) ⇒ ex) 한식 : 1, 일식 : 2
     */

    public ResponseInfo storeInfo(String searchCoord) {
        ResponseInfo responseInfo = new ResponseInfo();
        List<Store> storeList = new ArrayList<>();
        try {
            int storeIdx = 0;
            int pageNum = 1;

            String urlParameter = "https://map.naver.com/v5/api/search?caller=pcweb&";
            String queryParameter = "query=음식점&";
            String typeParameter = "type=all&";
            String searchCoordParameter = "searchCoord=" + searchCoord + "&"; // searchCoord=127.25040539999999;37.657918499999795&
            String displayCountParameter = "displayCount=100&";
            String isPlaceRecommendationReplaceParameter = "isPlaceRecommendationReplace=true";

            int maxPage = checkMaxPage(urlParameter, queryParameter, typeParameter, searchCoordParameter, pageNum, displayCountParameter, isPlaceRecommendationReplaceParameter);

            //maxPage를 구하는 과정에서 Error가 발생한 경우
            if (maxPage == -1) {
                responseInfo.setResponseCode(-2);
                responseInfo.setResponseMsg("storeInfo Page를 찾는 과정에서 에러 발생");
                return responseInfo;
            }

            while (pageNum < maxPage) {
                RestTemplate restTemplate = new RestTemplate();
                String pageParameter = "page=" + pageNum + "&";
                String url = urlParameter + queryParameter + typeParameter + searchCoordParameter + pageParameter + displayCountParameter + isPlaceRecommendationReplaceParameter;
                restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
                HttpHeaders headers = new HttpHeaders();
                headers.add("Accept", "application/json");
                HttpEntity<String> entity = new HttpEntity(headers);
                ResponseEntity<String> response = restTemplate.exchange(url + "lang=ko", HttpMethod.GET, entity, String.class);

                JsonObject jsonObject = gson.fromJson(response.getBody(), JsonObject.class);
                JsonObject result = gson.fromJson(jsonObject.getAsJsonObject().get("result"), JsonObject.class);
                JsonObject place = gson.fromJson(result.getAsJsonObject().get("place"), JsonObject.class);
                JsonArray placeList = gson.fromJson(place.getAsJsonObject().get("list"), JsonArray.class);
                for (JsonElement jsonElement : placeList) {
                    Store store = Store.builder()
                            .storeKey(storeIdx++)
                            .storeId(!jsonElement.getAsJsonObject().get("id").isJsonNull() ? jsonElement.getAsJsonObject().get("id").getAsString() : null)
                            .storeName(!jsonElement.getAsJsonObject().get("name").isJsonNull() ? jsonElement.getAsJsonObject().get("name").getAsString() : null)
                            .storeCategory(parseStoreCategory(!jsonElement.getAsJsonObject().get("category").isJsonNull() ? jsonElement.getAsJsonObject().get("category").getAsJsonArray().toString() : null))
                            .storeCategoryCode(storeCategoryCode)
                            .storeAddress(!jsonElement.getAsJsonObject().get("roadAddress").isJsonNull() ? jsonElement.getAsJsonObject().get("roadAddress").getAsString() : null)
                            .storeTel(!jsonElement.getAsJsonObject().get("tel").isJsonNull() ? jsonElement.getAsJsonObject().get("tel").getAsString() : null)
                            .storeBizhourInfo(!jsonElement.getAsJsonObject().get("bizhourInfo").isJsonNull() ? jsonElement.getAsJsonObject().get("bizhourInfo").getAsString() : null)
                            .storeSales(true)
                            .storeDistance(!jsonElement.getAsJsonObject().get("distance").isJsonNull() ? jsonElement.getAsJsonObject().get("distance").getAsString() : null)
                            .storeThumUrl(!jsonElement.getAsJsonObject().get("thumUrl").isJsonNull() ? jsonElement.getAsJsonObject().get("thumUrl").getAsString() : null)
                            .build();
                    storeList.add(store);
                }
                pageNum++;
            }
            responseInfo.setResponseCode(0);
            responseInfo.setResponseMsg(storeList.size() + "건 성공.");
            responseInfo.setData(storeList);
        } catch (Exception e) {
            responseInfo.setResponseCode(-1);
            responseInfo.setResponseMsg("storeInfo Fail");
            responseInfo.setData(e.getMessage());
        }
        return responseInfo;
    }

    private String parseStoreCategory(String s) {

        String[] storeCategoryList = {
                "전체", // 0
                "한식", // 1
                "일식", // 2
                "중식", // 3
                "양식", // 4
                "아시아 음식", // 5
                "뷔페", // 6
                "분식", // 7
                "카페", // 8
                "기타", // 9
                "카테고리없음" //10
        };

        String returnString = s.equals("null") ? "null" : s.replace("[", "").replace("]", "").replaceAll("\"", "").trim();

        boolean categoryCodeChk = false;

        if (!returnString.equals("null")) {
            String[] categoryArray = returnString.split(",");

            for (int i = 0; i < categoryArray.length; i++) {
                for (int j = 0; j < storeCategoryList.length; j++) {
                    if (categoryArray[i].equals(storeCategoryList[j])) {
                        storeCategoryCode = j;
                        categoryCodeChk = true;
                        break;
                    }
                }
                if (categoryCodeChk) {
                    break;
                }
            }
            if (!categoryCodeChk) {
                storeCategoryCode = 9; // 카테고리는 있지만 해당하는 카테고리가 없음 기타
            }
            return returnString;
        } else { // 카테고리없음 -> 분류없음
            storeCategoryCode = 10;
            return returnString;
        }
    }

    private int checkMaxPage(String url, String queryParameter, String typeParameter, String searchCoordParameter, int pageNum, String displayCountParameter, String isPlaceRecommendationReplaceParameter) {
        int returnNum = pageNum;
        int maxPage = 1;
        try {
            while (returnNum <= maxPage) {
                RestTemplate restTemplate = new RestTemplate();
                String loopUrl = url + queryParameter + typeParameter + searchCoordParameter + "page=" + returnNum + "&" + displayCountParameter + isPlaceRecommendationReplaceParameter;
                restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
                HttpHeaders headers = new HttpHeaders();
                headers.add("Accept", "application/json");
                HttpEntity<String> entity = new HttpEntity(headers);
                ResponseEntity<String> response = restTemplate.exchange(loopUrl + "lang=ko", HttpMethod.GET, entity, String.class);
                //{"error":{"code":"XE400","msg":"Bad Request.","displayMsg":"잘못된 요청입니다.","extraInfo":null}}
                if (maxPage == 1) {
                    JsonObject jsonObject = gson.fromJson(response.getBody(), JsonObject.class);
                    JsonObject result = gson.fromJson(jsonObject.getAsJsonObject().get("result"), JsonObject.class);
                    JsonObject place = gson.fromJson(result.getAsJsonObject().get("place"), JsonObject.class);
                    maxPage = (place.getAsJsonObject().get("totalCount").getAsInt());
                }

                if (response.getBody().contains("XE400")) {
                    break;
                } else {
                    returnNum++;
                }

            }
        } catch (Exception e) {
            /**
             * Error 발생시에 -1로 리터하기
             * */
            returnNum = -1;
            log.info("checkMaxPage Try Catch Error : " + e.getMessage());
        }

        return returnNum;
    }

    public ResponseInfo storeDetailInfo(String storeId) {
        ResponseInfo responseInfo = new ResponseInfo();
        RestTemplate restTemplate = new RestTemplate();
        try {
            restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
            HttpHeaders headers = new HttpHeaders();
            headers.add("Accept", "application/json");
            HttpEntity<String> entity = new HttpEntity(headers);

            String url = "https://map.naver.com/v5/api/sites/summary/" + storeId + "?lang=ko";

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            JsonObject jsonObject = gson.fromJson(response.getBody(), JsonObject.class);
            JsonArray jsonArray = gson.fromJson(jsonObject.getAsJsonObject().get("menus"), JsonArray.class);

            List<Menus> menusList = new ArrayList<>();
            for (JsonElement jsonElement : jsonArray) {
                menusList.add(gson.fromJson(jsonElement, Menus.class));
            }

            responseInfo.setResponseCode(0);
            responseInfo.setResponseMsg("storeDetailInfo Success");
            responseInfo.setData(menusList);
        } catch (Exception e) {
            responseInfo.setResponseCode(-1);
            responseInfo.setResponseMsg("storeDetailInfo Fail");
            responseInfo.setData(e.getMessage());
        }
        return responseInfo;
    }
}
