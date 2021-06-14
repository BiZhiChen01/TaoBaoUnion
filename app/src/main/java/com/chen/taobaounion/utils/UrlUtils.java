package com.chen.taobaounion.utils;

public class UrlUtils {

    public static String createHomeCategoryContentUrl(int id, int page) {
        return "discovery/" + id + "/" + page;
    }

    public static String getCoverPath(String pict_url) {
        return "https:" + pict_url;
    }

    public static String getCoverPath(String pict_url, int size) {
        return "https:" + pict_url + "_" + size + "x" + size + ".jpg";
    }

    public static String getTicketUrl(String url) {
        if (url.startsWith("http") || url.startsWith("https")) {
            return url;
        } else {
            return "https:" + url;
        }
    }

    public static String getSelectedCategoryContentUrl(Integer favorites_id) {
        return "recommend/" + favorites_id;
    }

    public static String getOnSellUrl(int currentPage) {
        return "onSell/" + currentPage;
    }
}
