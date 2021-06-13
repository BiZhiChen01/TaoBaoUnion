package com.chen.taobaounion.model;

import com.chen.taobaounion.model.bean.Categories;
import com.chen.taobaounion.model.bean.HomeCategoryContent;
import com.chen.taobaounion.model.bean.TicketParams;
import com.chen.taobaounion.model.bean.TicketResult;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface Api {

    @GET("discovery/categories")
    Call<Categories> getCategories();

    @GET
    Call<HomeCategoryContent> getHomeCategoryContent(@Url String url);

    @POST("tpwd")
    Call<TicketResult> getTicket(@Body TicketParams ticketParams);
}
