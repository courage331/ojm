package com.test.ojm.vo;

public enum StoreCategory {
    All("전체"),
    Korean("한식"),
    Japanese(" 일식");

    //,중식(3),아시아_음식(4),뷔페(5),분식(6),카페(7),기타(8)
//
//    {
//        code: "japanese",
//                name: "일식"
//    },
//    {
//        code: "chinese",
//                name: "중식"
//    },
//    {
//        code: "asian",
//                name: "아시아 음식"
//    },
//    {
//        code: "buffet",
//                name: "뷔페"
//    },
//    {
//        code: "koreanSnack",
//                name: "분식"
//    },
//    {
//        code: "cafe",
//                name: "카페"
//    },
//    {
//        code: "theOthers",
//                name: "기타"
//    }

    private final String value;

    StoreCategory(String value) { this.value = value; }

    public String getValue() { return value; }

    public String getKey() {return name(); }
}
