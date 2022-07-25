package com.test.ojm.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.test.ojm.vo.Location;
import com.test.ojm.vo.ResponseInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class LocationService {

    @Autowired
    Gson gson;

    @Value("${spring.location.url}")
    String locationUrl;
    @Value("${spring.location.key}")
    String restAPIKEY;

    public ResponseInfo ojmLocation(String name){

        RestTemplate restTemplate = new RestTemplate();
        ResponseInfo responseInfo = new ResponseInfo();

        try{
            restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
            HttpHeaders headers = new HttpHeaders();
            headers.add("Accept", "application/json");
            headers.add("Authorization", "KakaoAK "+restAPIKEY);

            HttpEntity<String> entity = new HttpEntity(headers);

            ResponseEntity<String> response = restTemplate.exchange(locationUrl+name, HttpMethod.GET, entity, String.class);

            JsonObject jsonObject = gson.fromJson(response.getBody(), JsonObject.class);
            List<Location> locationList = stringToArray(String.valueOf(jsonObject.get("documents")), Location[].class);

            responseInfo.setResponseCode(0);
            responseInfo.setResponseMsg("ojmLocation Success");
            responseInfo.setData(locationList);

        }catch(Exception e){
            responseInfo.setResponseCode(-1);
            responseInfo.setResponseMsg("ojmLocation Fail");
            responseInfo.setData(e.getMessage());
        }
        return responseInfo;
    }

    public static <Location> List<Location> stringToArray(String s, Class<Location[]> clazz) {
        Location[] arr = new Gson().fromJson(s, clazz);
        return Arrays.asList(arr); //or return Arrays.asList(new Gson().fromJson(s, clazz)); for a one-liner
    }
}
