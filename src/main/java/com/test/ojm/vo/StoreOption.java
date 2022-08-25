package com.test.ojm.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StoreOption {
    String id;
    String name;
    String isCheck;
    String order;
    String iconURL;
    String desc;
}
