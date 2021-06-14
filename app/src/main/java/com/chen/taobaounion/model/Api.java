package com.chen.taobaounion.model;

import com.chen.taobaounion.model.bean.HomeCategories;
import com.chen.taobaounion.model.bean.HomeCategoryContent;
import com.chen.taobaounion.model.bean.OnSellContent;
import com.chen.taobaounion.model.bean.SearchRecommend;
import com.chen.taobaounion.model.bean.SearchResult;
import com.chen.taobaounion.model.bean.SelectedCategories;
import com.chen.taobaounion.model.bean.SelectedCategoryContent;
import com.chen.taobaounion.model.bean.TicketResult;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface Api {

    @GET("discovery/categories")
    Call<HomeCategories> getCategories();

    @GET
    Call<HomeCategoryContent> getHomeCategoryContent(@Url String url);

    @POST("tpwd")
    Call<TicketResult> getTicket(@Body TicketParams ticketParams);

    @GET("recommend/categories")
    Call<SelectedCategories> getSelectedCategories();

    @GET
    Call<SelectedCategoryContent> getSelectedCategoryContent(@Url String url);

    @GET
    Call<OnSellContent> getOnSellContent(@Url String url);

    @GET("search/recommend")
    Call<SearchRecommend> getSearchRecommend();

    @GET("search")
    Call<SearchResult> getSearchResult(@Query("page") int page, @Query("keyword") String keyword);
}
