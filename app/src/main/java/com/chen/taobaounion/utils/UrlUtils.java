package com.chen.taobaounion.utils;

public class UrlUtils {

    public static String createHomeCategoryContentUrl(int id, int page) {
        return "discovery/" + id + "/" + page;
    }

    public static String getCoverPath(String pict_url, int size) {
        return "https:" + pict_url + "_" + size + "x" + size + ".jpg";
    }
}