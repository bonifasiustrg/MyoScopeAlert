<<<<<<< HEAD
package com.apicta.myoscopealert.network



import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Retro {
    private const val BASE_URL = "https://myocardial.telekardiologi.com/api/v1/"

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
=======
package com.apicta.myoscopealert.network



import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Retro {
    private const val BASE_URL = "https://myocardial.telekardiologi.com/api/v1/"

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
>>>>>>> 7f1866f (Implement Depedency Injection with Dagger Hilt for Viewmodel, Datastore (stored token), Repository, and Api Interface.)
}