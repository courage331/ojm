package com.test.ojm.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.test.ojm.vo.Location;
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

    // json String 을 list Object 로 변환
    public static <Location> List<Location> stringToArray(String s, Class<Location[]> clazz) {
        Location[] arr = new Gson().fromJson(s, clazz);
        return Arrays.asList(arr); //or return Arrays.asList(new Gson().fromJson(s, clazz)); for a one-liner
    }

    @RequestMapping("/store")
    public String ojmStore(
            @RequestParam(name="query") String query,
            @RequestParam(name="type") String type,
            @RequestParam(name="searchCoord") String searchCoord,
            @RequestParam(name="page") String page,
            @RequestParam(name="displayCount") String displayCount,
            @RequestParam(name="isPlaceRecommendationReplace") String isPlaceRecommendationReplace
    ){

        RestTemplate restTemplate = new RestTemplate();
        String url = "https://map.naver.com/v5/api/search?caller=pcweb&";
        String queryParameter = "query="+query+"&"; // query=음식점&
        String typeParameter = "type="+type+"&"; // type=all&
        String searchCoordParameter = "searchCoord="+searchCoord+"&"; // searchCoord=127.25040539999999;37.657918499999795;
        String pageParameter = "page="+page+"&"; // page=2&
        String displayCountParameter = "displayCount="+displayCount+"&";// displayCount=100&
        String isPlaceRecommendationReplaceParameter = "isPlaceRecommendationReplace"+isPlaceRecommendationReplace+"&"; //isPlaceRecommendationReplace=true&

        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/json");

        HttpEntity<String> entity = new HttpEntity(headers);
        ResponseEntity<String> response = restTemplate.exchange(url+"lang=ko", HttpMethod.GET, entity, String.class);

        return response.getBody();
    }

}
