package com.chen.taobaounion.utils;

public class UrlUtils {

    public static String createHomeCategoryContentUrl(int id, int page) {
        return "discovery/" + id + "/" + page;
    }

    public static String getCoverPath(String pict_url) {
        return "https:" + pict_url;
    }
}
