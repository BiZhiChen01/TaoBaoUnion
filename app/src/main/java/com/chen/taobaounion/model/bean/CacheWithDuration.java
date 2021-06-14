package com.chen.taobaounion.model.bean;

public class CacheWithDuration {

    public CacheWithDuration(long duration, String cache) {
        this.duration = duration;
        this.cache = cache;
    }

    private long duration;

    private String cache;

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getCache() {
        return cache;
    }

    public void setCache(String cache) {
        this.cache = cache;
    }
}
