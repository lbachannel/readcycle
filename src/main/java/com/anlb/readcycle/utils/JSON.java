package com.anlb.readcycle.utils;

import com.google.gson.Gson;

public class JSON {
    
    private static final Gson gson = new Gson();

    public static String toJson(Object payload) {
        return gson.toJson(payload);
    }
}
