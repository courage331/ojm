package com.test.ojm.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.test.ojm.vo.Location;
import com.test.ojm.vo.Menus;
import com.test.ojm.vo.ResponseInfo;
import com.test.ojm.vo.Store;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@CrossOrigin(origins ="*")
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
        List<Store> storeList = new ArrayList<>();


        String urlParameter = "https://map.naver.com/v5/api/search?caller=pcweb&";
        String queryParameter = "query=음식점&";
        String typeParameter = "type=all&";
        String searchCoordParameter = "searchCoord="+searchCoord+"&"; // searchCoord=127.25040539999999;37.657918499999795&
        int pageNum = 1;
        String displayCountParameter = "displayCount=100&";
        String isPlaceRecommendationReplaceParameter = "isPlaceRecommendationReplace=true";

        int maxPage = checkMaxPage(urlParameter,queryParameter,typeParameter,searchCoordParameter,pageNum,displayCountParameter,isPlaceRecommendationReplaceParameter);

        System.out.println("maxPage :" + maxPage);

        while(pageNum<maxPage){
            RestTemplate restTemplate = new RestTemplate();
            String pageParameter = "page="+pageNum+"&";
            String url = urlParameter + queryParameter + typeParameter + searchCoordParameter + pageParameter + displayCountParameter + isPlaceRecommendationReplaceParameter;
            restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
            HttpHeaders headers = new HttpHeaders();
            headers.add("Accept", "application/json");
            System.out.println("url : " + url);
            HttpEntity<String> entity = new HttpEntity(headers);
            ResponseEntity<String> response = restTemplate.exchange(url+"lang=ko", HttpMethod.GET, entity, String.class);
            //System.out.println(response.getBody());
            //{"error":{"code":"XE400","msg":"Bad Request.","displayMsg":"잘못된 요청입니다.","extraInfo":null}}
            JsonObject jsonObject = gson.fromJson(response.getBody(), JsonObject.class);
            JsonObject result = gson.fromJson(jsonObject.getAsJsonObject().get("result"), JsonObject.class);
            JsonObject place = gson.fromJson(result.getAsJsonObject().get("place"),JsonObject.class);
            JsonArray placeList = gson.fromJson(place.getAsJsonObject().get("list"), JsonArray.class);

            for(int i=0; i<placeList.size(); i++){
                Store store = Store.builder()
                        .storeId(!placeList.get(i).getAsJsonObject().get("id").isJsonNull() ? placeList.get(i).getAsJsonObject().get("id").getAsString() : "null")
                        .storeName(!placeList.get(i).getAsJsonObject().get("name").isJsonNull() ? placeList.get(i).getAsJsonObject().get("name").getAsString() : "null")
                        .storeCategory(parseStoreCategory(!placeList.get(i).getAsJsonObject().get("category").isJsonNull() ? placeList.get(i).getAsJsonObject().get("category").getAsJsonArray().toString() : "null"))
                        .storeAddress(!placeList.get(i).getAsJsonObject().get("roadAddress").isJsonNull() ? placeList.get(i).getAsJsonObject().get("roadAddress").getAsString() : "null")
                        .storeTel(!placeList.get(i).getAsJsonObject().get("tel").isJsonNull() ? placeList.get(i).getAsJsonObject().get("tel").getAsString() : "null")
                        .storeBizhourInfo(!placeList.get(i).getAsJsonObject().get("bizhourInfo").isJsonNull() ? placeList.get(i).getAsJsonObject().get("bizhourInfo").getAsString() : "null")
                        .storeSales(true)
                        .storeDistance(!placeList.get(i).getAsJsonObject().get("distance").isJsonNull() ? placeList.get(i).getAsJsonObject().get("distance").getAsString() : "null")
                        .storeThumUrl(!placeList.get(i).getAsJsonObject().get("thumUrl").isJsonNull() ? placeList.get(i).getAsJsonObject().get("thumUrl").getAsString() : "null")
                        .build();
                storeList.add(store);
            }
            pageNum++;
        }
        responseInfo.setResponseCode(0);
        responseInfo.setResponseMsg(storeList.size() + "건 성공.");
        responseInfo.setData(storeList);

        return responseInfo;
    }

    private String parseStoreCategory(String s) {

        String returnString = s.equals("null") ? "null" : s.replace("[","").replace("]","").replaceAll("\"","").trim();

        return returnString;
    }

    private int checkMaxPage(String url, String queryParameter, String typeParameter, String searchCoordParameter, int pageNum, String displayCountParameter, String isPlaceRecommendationReplaceParameter) {
        int returnNum = pageNum;
        int maxPage = 1;
        while(returnNum<=maxPage){
            RestTemplate restTemplate = new RestTemplate();
            String loopUrl = url + queryParameter + typeParameter + searchCoordParameter + "page="+returnNum+"&" + displayCountParameter + isPlaceRecommendationReplaceParameter;
            restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
            HttpHeaders headers = new HttpHeaders();
            headers.add("Accept", "application/json");
            HttpEntity<String> entity = new HttpEntity(headers);
            ResponseEntity<String> response = restTemplate.exchange(loopUrl+"lang=ko", HttpMethod.GET, entity, String.class);
            //{"error":{"code":"XE400","msg":"Bad Request.","displayMsg":"잘못된 요청입니다.","extraInfo":null}}
            if(maxPage==1){
                JsonObject jsonObject = gson.fromJson(response.getBody(), JsonObject.class);
                JsonObject result = gson.fromJson(jsonObject.getAsJsonObject().get("result"), JsonObject.class);
                JsonObject place = gson.fromJson(result.getAsJsonObject().get("place"),JsonObject.class);
                maxPage = (place.getAsJsonObject().get("totalCount").getAsInt());
            }

            if(response.getBody().contains("XE400")){
                break;
            }else{
                returnNum ++;
            }

        }
        return returnNum;
    }



    // 37703991
    @RequestMapping("/detail")
    public ResponseInfo ojmStoreDetail(@RequestParam(name="storeId") String storeId){

        ResponseInfo responseInfo = new ResponseInfo();
        RestTemplate restTemplate = new RestTemplate();

        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/json");
        HttpEntity<String> entity = new HttpEntity(headers);

        String url = "https://map.naver.com/v5/api/sites/summary/"+storeId+"?lang=ko";

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        //System.out.println(response.getBody());

        //responseInfo.getData(responseInfo.getBody());
        JsonObject jsonObject = gson.fromJson(response.getBody(), JsonObject.class);
        JsonArray jsonArray = gson.fromJson(jsonObject.getAsJsonObject().get("menus"), JsonArray.class);
        System.out.println(jsonArray);
        List<Menus> menusList = new ArrayList<>();
        for(int i =0 ; i<jsonArray.size(); i++){
            Menus menus = gson.fromJson(jsonArray.get(i),Menus.class);
            menusList.add(menus);
        }
        return new ResponseInfo(0,"Success",menusList);
        //return response.getBody();
    }

}
