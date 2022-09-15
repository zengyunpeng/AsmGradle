package com.intellif.asmgradle;

import java.util.List;

public class OriginalClassV2 {
    public static List<String> tagList;

    static {
        tagList.add("KKYS");
    }

    public void i(String tag, String content) {
        if (tagList.contains(tag)) {
            return;
        }

//        System.out.println("....");
    }
}
