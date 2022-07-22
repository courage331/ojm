package com.test.ojm.controller;

import com.google.gson.Gson;
import com.test.ojm.service.LocationService;
import com.test.ojm.service.StoreService;
import com.test.ojm.vo.ResponseInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@CrossOrigin(origins ="*")
@RequestMapping("/test/ojm")
public class OJMController {

    @Autowired
    Gson gson;

    @Value("${spring.location.url}")
    String locationUrl;
    @Value("${spring.location.key}")
    String restAPIKEY;

    @Autowired
    LocationService locationService;

    @Autowired
    StoreService storeService;

    //TestAPI
    @RequestMapping("/location")
    public ResponseInfo ojmLocation(@RequestParam(name="name") String name){

        ResponseInfo responseInfo = locationService.ojmLocation(name);

        return responseInfo;
    }

    // 최초 접속시 모든 음식점 선택
    /*
- 가게 이미지(url)  ⇒ string
    */

    @RequestMapping("/store")
    public ResponseInfo ojmStore(@RequestParam(name="searchCoord") String searchCoord){

        ResponseInfo responseInfo = storeService.storeInfo(searchCoord);

        return responseInfo;
    }

    // 37703991
    @RequestMapping("/detail")
    public ResponseInfo ojmStoreDetail(@RequestParam(name="storeId") String storeId){

        ResponseInfo responseInfo = storeService.storeDetailInfo(storeId);

        return responseInfo;
    }

}
