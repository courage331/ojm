package com.test.ojm.vo;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BizHourInfo {
    String type;
    String startTime;
    String endTime;
    String description;
    Boolean isDayOff;
}
