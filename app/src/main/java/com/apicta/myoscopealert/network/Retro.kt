package com.apicta.myoscopealert.network



import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Retro {
    private const val BASE_URL = "https://myocardial.telekardiologi.com/api/v1/"
//    private const val BASE_URL = "http://127.0.0.1:8000/api/v1/"

    private val gson = GsonBuilder()
        .setLenient()
        .create()

//    private val httpClient = OkHttpClient.Builder()
//        .addInterceptor(HttpLoggingIntzerceptor().apply {
//            level = HttpLoggingInterceptor.Level.BODY // This logs the network requests
//        })
//        .build()

    fun getRetroClientInstance(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

}
//object RetroML {
////    private const val BASE_URL = "https://myocardial.telekardiologi.com/api/v1/"
//    private const val BASE_URL = "https://myoscope.distancing.my.id/"
//
//    private val gson = GsonBuilder()
//        .setLenient()
//        .create()
//
////    private val httpClient = OkHttpClient.Builder()
////        .addInterceptor(HttpLoggingIntzerceptor().apply {
////            level = HttpLoggingInterceptor.Level.BODY // This logs the network requests
////        })
////        .build()
//
//    fun getRetroClientInstance(): Retrofit {
//        return Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create(gson))
//            .build()
//    }
//}