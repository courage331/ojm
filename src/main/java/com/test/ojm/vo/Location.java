package com.test.ojm.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    String address_name;
    String address_type;
    String x;
    String y;
    Address address;
    RoadAddress road_address;

}
