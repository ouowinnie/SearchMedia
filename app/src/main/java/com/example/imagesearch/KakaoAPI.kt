package com.example.imagesearch

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface KakaoAPI {
    @GET("v2/search/image")
    fun searchImages(
        @Query("query") query: String,
        @Query("sort") sort: String = "recency"
    ): Call<RvModelList>
    @GET("v2/search/vclip")
    fun searchVideos(
        @Query("query") query: String,
        @Query("sort") sort: String = "recency"
    ): Call<RvModelList>
}