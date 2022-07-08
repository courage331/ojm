package com.test.ojm.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoadAddress {
    String address_name;
    String region_1depth_name;
    String region_2depth_name;
    String region_3depth_name;
    String road_name;
    String underground_yn;
    String main_building_no;
    String sub_building_no;
    String building_name;
    String zone_no;
    String x;
    String y;
}
