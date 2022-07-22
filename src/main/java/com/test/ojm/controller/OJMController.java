package com.test.ojm.controller;

import com.google.gson.Gson;
import com.test.ojm.service.LocationService;
import com.test.ojm.service.StoreService;
import com.test.ojm.vo.ResponseInfo;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
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
    LocationService locationService;

    @Autowired
    StoreService storeService;

    @ApiOperation(
            value = "위치값 조회해주는 API (테스트 용으로 한번 만들어봄)",
            notes = "도로명 주소 입력하는걸 추천",
            httpMethod = "GET",
            produces = "application/json",
            consumes = "application/json",
            protocols = "http",
//			response = Orders.class,
            responseHeaders = {
                    //header info
            })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "주소명", example = "풍덕천로 172번길 13-6", required = true),
    })
    @RequestMapping(value = "/location", method=RequestMethod.GET)
    public ResponseInfo ojmLocation(@RequestParam(name="name") String name){

        ResponseInfo responseInfo = locationService.ojmLocation(name);

        return responseInfo;
    }


    @ApiOperation(
            value = "가게 정보 조회 API",
            notes = "notes",
            httpMethod = "GET",
            produces = "application/json",
            consumes = "application/json",
            protocols = "http",
//			response = Orders.class,
            responseHeaders = {
                    //header info
            })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "searchCoord", value = "위도;경도", example = "127.3006163;37.6579185", required = true),
    })
    @RequestMapping(value = "/store", method=RequestMethod.GET)
    public ResponseInfo ojmStore(@RequestParam(name="searchCoord") String searchCoord){

        ResponseInfo responseInfo = storeService.storeInfo(searchCoord);

        return responseInfo;
    }

    @ApiOperation(
            value = "가게 정보 상세 조회 API",
            notes = "notes",
            httpMethod = "GET",
            produces = "application/json",
            consumes = "application/json",
            protocols = "http",
//			response = Orders.class,
            responseHeaders = {
                    //header info
            })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "storeId", value = "가게Id", example = "37703991", required = true),
    })
    @RequestMapping(value = "/detail", method=RequestMethod.GET)
    public ResponseInfo ojmStoreDetail(@RequestParam(name="storeId") String storeId){

        ResponseInfo responseInfo = storeService.storeDetailInfo(storeId);

        return responseInfo;
    }

}
