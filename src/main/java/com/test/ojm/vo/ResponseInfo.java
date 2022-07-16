package com.test.ojm.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseInfo {

    int responseCode;
    String responseMsg;
    Object data;
}
