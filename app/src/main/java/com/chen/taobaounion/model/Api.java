package com.chen.taobaounion.model;

import com.chen.taobaounion.model.bean.Categories;

import retrofit2.Call;
import retrofit2.http.GET;

public interface Api {

    @GET("discovery/categories")
    Call<Categories> getCategories();
}
