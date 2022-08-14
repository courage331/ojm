package com.test.ojm.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StoreDetail {
    List<Menus> menuList;
    boolean bizHourInfo;
}
