package com.test.ojm.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    String address_name;
    String region_1depth_name;
    String region_2depth_name;
    String region_3depth_name;
    String region_3depth_h_name;
    String h_code;
    String b_code;
    String mountain_yn;
    String main_address_no;
    String sub_address_no;
    String x;
    String y;
}
