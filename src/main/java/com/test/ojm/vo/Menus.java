package com.test.ojm.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Menus {
    String name;
    String price;
    boolean isRecommended;
    boolean change;
}
