package com.chen.taobaounion.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.chen.taobaounion.base.BaseApplication;
import com.chen.taobaounion.model.bean.CacheWithDuration;
import com.google.gson.Gson;

public class JsonCacheUtil {

    public static final String JSON_CACHE_SP_NAME = "json_cache_sp_name";

    private final SharedPreferences mSp;
    private final Gson mGson;

    private JsonCacheUtil() {
        mSp = BaseApplication.getAppContext().getSharedPreferences(JSON_CACHE_SP_NAME, Context.MODE_PRIVATE);
        mGson = new Gson();
    }

    private static JsonCacheUtil sJsonCacheUtil = null;

    public static JsonCacheUtil getInstance() {
        if (sJsonCacheUtil == null) {
            sJsonCacheUtil = new JsonCacheUtil();
        }
        return sJsonCacheUtil;
    }

    public void saveCache(String key, Object value) {
        this.saveCache(key, value, -1);
    }

    public void saveCache(String key, Object value, long duration) {
        String valueStr = mGson.toJson(value);
        if (duration != -1L) {
            duration += System.currentTimeMillis();
        }
        CacheWithDuration cacheWithDuration = new CacheWithDuration(duration, valueStr);
        String cacheWithTime = mGson.toJson(cacheWithDuration);

        SharedPreferences.Editor editor = mSp.edit();
        editor.putString(key, cacheWithTime);
        editor.apply();
    }

    public void delCache(String key) {
        mSp.edit().remove(key).apply();
    }

    public <T> T getValue(String key, Class<T> clazz) {
        String valueWithDuration = mSp.getString(key, null);
        if (valueWithDuration == null) {
            return null;
        }

        CacheWithDuration cacheWithDuration = mGson.fromJson(valueWithDuration, CacheWithDuration.class);
        long duration = cacheWithDuration.getDuration();
        if (duration != -1 && duration - System.currentTimeMillis() <= 0) {
            //过期
            return null;
        } else {
            //没过期
            String cache = cacheWithDuration.getCache();
            T result = mGson.fromJson(cache, clazz);
            return result;
        }
    }

}
