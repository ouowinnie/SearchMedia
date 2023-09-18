package com.example.imagesearch

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SharedViewModel : ViewModel() {
    val selectedRvItem = MutableLiveData<List<RvModel>>()

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val originalRequest = chain.request()
            val newRequest = originalRequest.newBuilder()
                .addHeader("Authorization", "KakaoAK ${BuildConfig.KAKAO_REST_API_KEY}")
                .build()
            chain.proceed(newRequest)
        }
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://dapi.kakao.com")
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    private val kakaoAPI = retrofit.create(KakaoAPI::class.java)

    fun searchImages(query: String): Call<RvModelList> {
        return kakaoAPI.searchImages(query)
    }
    fun searchVideos(query: String): Call<RvModelList> {
        return kakaoAPI.searchVideos(query)
    }
}