package com.test.ojm.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.test.ojm.vo.Location;
import com.test.ojm.vo.ResponseInfo;
import com.test.ojm.vo.Store;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/test/ojm")
public class testController {

    @Autowired
    Gson gson;

    @Value("${spring.location.url}")
    String locationUrl;
    @Value("${spring.location.key}")
    String restAPIKEY;

    @RequestMapping("/location")
    public String ojmLocation(@RequestParam(name="name") String name){

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/json");
        headers.add("Authorization", "KakaoAK "+restAPIKEY);

        HttpEntity<String> entity = new HttpEntity(headers);

        ResponseEntity<String> response = restTemplate.exchange(locationUrl+name, HttpMethod.GET, entity, String.class);

        JsonObject jsonObject = gson.fromJson(response.getBody(), JsonObject.class);
        String documents = String.valueOf(jsonObject.get("documents"));
//        System.out.println(documents);
        List<Location> locationList = stringToArray(documents, Location[].class);
        System.out.println(locationList);
        System.out.println(locationList.get(0).getX());
        System.out.println(locationList.get(0).getY());
        System.out.println(locationList.get(0).getAddress());
        System.out.println(locationList.get(0).getAddress_name());
        System.out.println(locationList.get(0).getAddress_type());
        System.out.println(locationList.get(0).getRoad_address());

        return response.getBody();
    }

    /**  json String 을 list Object 로 변환 */
    public static <Location> List<Location> stringToArray(String s, Class<Location[]> clazz) {
        Location[] arr = new Gson().fromJson(s, clazz);
        return Arrays.asList(arr); //or return Arrays.asList(new Gson().fromJson(s, clazz)); for a one-liner
    }

    // 최초 접속시 모든 음식점 선택
    /*
- 가게 이미지(url)  ⇒ string

ex)
    - thumUrl : [https://ldb-phinf.pstatic.net/20220318_83/1647570614790jNrs4_JPEG/%BC%F6%C1%A4%B5%CA_%B0%A5%BA%F1%BE%E7%B3%E4%B5%A4%B9%E4.jpg](https://ldb-phinf.pstatic.net/20220318_83/1647570614790jNrs4_JPEG/%BC%F6%C1%A4%B5%CA_%B0%A5%BA%F1%BE%E7%B3%E4%B5%A4%B9%E4.jpg)
    - 가게 이름 : name or display ⇒ string
    - 가게 연락처 : tel ⇒ string
    - 가게와의 거리 : distance ⇒ string
    - 영업 상태 ( 영업 중 : true, 영업 안함 : false ) ⇒ bool
    - 영업시작시간 ( 자료조사) (bizhourInfo) ⇒
    - 리뷰점수 or 리뷰 개수( **reviewCount ) → 셀레니움테스트**
    - 주소 (address, abbrAddress, 도로명 : roadAddress)
    - 가게 아이디값(id)
    - 카테고리 (category) ⇒ ex) 한식 : 1, 일식 : 2

     */
    @RequestMapping("/store")
    public ResponseInfo ojmStore(
            @RequestParam(name="searchCoord") String searchCoord
    ){
        ResponseInfo responseInfo = new ResponseInfo();

        RestTemplate restTemplate = new RestTemplate();
        String url = "https://map.naver.com/v5/api/search?caller=pcweb&";
        String queryParameter = "query=음식점&";
        String typeParameter = "type=all&";
        String searchCoordParameter = "searchCoord="+searchCoord+"&"; // searchCoord=127.25040539999999;37.657918499999795&
        String pageParameter = "page=1&";
        String displayCountParameter = "displayCount=100&";
        String isPlaceRecommendationReplaceParameter = "isPlaceRecommendationReplace=true&";


        url = url + queryParameter + typeParameter + searchCoordParameter + pageParameter + displayCountParameter + isPlaceRecommendationReplaceParameter;
        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/json");

        HttpEntity<String> entity = new HttpEntity(headers);
        ResponseEntity<String> response = restTemplate.exchange(url+"lang=ko", HttpMethod.GET, entity, String.class);
        //System.out.println(response.getBody());
        //{"error":{"code":"XE400","msg":"Bad Request.","displayMsg":"잘못된 요청입니다.","extraInfo":null}}


        List<Store> storeList = new ArrayList<>();

        JsonObject jsonObject = gson.fromJson(response.getBody(), JsonObject.class);
        JsonObject result = gson.fromJson(jsonObject.getAsJsonObject().get("result"), JsonObject.class);
        JsonObject place = gson.fromJson(result.getAsJsonObject().get("place"),JsonObject.class);
        JsonArray placeList = gson.fromJson(place.getAsJsonObject().get("list"), JsonArray.class);

        for(int i=0; i<placeList.size(); i++){
            Store store = Store.builder()
                    .storeId(placeList.get(i).getAsJsonObject().get("id").getAsString())
                    .storeName(placeList.get(i).getAsJsonObject().get("name").getAsString())
                    .storeCatetory(placeList.get(0).getAsJsonObject().get("category").getAsJsonArray().toString()
                            .replace("[","").replace("]","").replaceAll("\"","").trim())
                    .storeAddress(placeList.get(i).getAsJsonObject().get("roadAddress").getAsString())
                    .storeTel(placeList.get(i).getAsJsonObject().get("tel").getAsString())
                    .storeBizhourInfo(!placeList.get(i).getAsJsonObject().get("bizhourInfo").isJsonNull() ? placeList.get(i).getAsJsonObject().get("bizhourInfo").getAsString() : "null")
                    .storeSales(true)
                    .storeDistance(placeList.get(i).getAsJsonObject().get("distance").getAsString())
                    .storeThumUrl(!placeList.get(i).getAsJsonObject().get("thumUrl").isJsonNull() ? placeList.get(i).getAsJsonObject().get("thumUrl").getAsString() : "null")
                    .build();
            storeList.add(store);
        }
        responseInfo.setResponseCode(0);
        responseInfo.setResponseMsg(placeList.size() + "건 성공.");
        responseInfo.setData(storeList);

        return responseInfo;
    }

}
