package com.example.imagesearch

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface KakaoAPI {
    @GET("v2/search/image")
    fun requestImageData(
        @Query("query") query: String
    ): Call<RvModelList>
}