package com.apicta.myoscopealert.network



import com.apicta.myoscopealert.models.user.LoginResponse
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import okhttp3.OkHttpClient
import okio.IOException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object Retro {
//    private const val BASE_URL = "https://myocardial.telekardiologi.com/api/v1/"
    private const val BASE_URL = "https://miocardial.humicprototyping.com/api/"
//    private const val BASE_URL = "http://127.0.0.1:8000/api/v1/"

    // Custom TypeAdapter to handle String responses
    private class StringAdapter : TypeAdapter<LoginResponse>() {
        @Throws(IOException::class)
        override fun write(out: JsonWriter, value: LoginResponse?) {
            // Implementation for writing if needed
            out.nullValue()
        }

        @Throws(IOException::class)
        override fun read(reader: JsonReader): LoginResponse {
            // If it's a string (error message), consume it and return empty response
            if (reader.peek().name == "STRING") {
                reader.nextString()
                return LoginResponse()
            }

            // Otherwise, parse the normal JSON object
            var data: LoginResponse.Data? = null
            var role: String? = ""
            var token: String? = ""

            reader.beginObject()
            while (reader.hasNext()) {
                when (reader.nextName()) {
                    "data" -> {
                        reader.beginObject()
                        var id: Int? = 0
                        var name: String? = ""
                        var email: String? = ""
                        var phone: String? = ""
                        var gender: String? = ""
                        var created_at: String? = ""
                        var updated_at: String? = ""

                        while (reader.hasNext()) {
                            when (reader.nextName()) {
                                "id" -> id = reader.nextInt()
                                "name" -> name = reader.nextString()
                                "email" -> email = reader.nextString()
                                "phone" -> phone = reader.nextString()
                                "gender" -> gender = reader.nextString()
                                "created_at" -> created_at = reader.nextString()
                                "updated_at" -> updated_at = reader.nextString()
                                else -> reader.skipValue()
                            }
                        }
                        reader.endObject()
                        data = LoginResponse.Data(created_at, email, gender, id, name, phone, updated_at)
                    }
                    "role" -> role = reader.nextString()
                    "token" -> token = reader.nextString()
                    else -> reader.skipValue()
                }
            }
            reader.endObject()
            return LoginResponse(data, role, token)
        }
    }

    private val gson = GsonBuilder()
        .setLenient()
        .registerTypeAdapter(LoginResponse::class.java, StringAdapter())
        .create()

    val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    fun getRetroClientInstance(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
}
object RetroML {
//    private const val BASE_URL = "https://myocardial.telekardiologi.com/api/v1/"
//    private const val BASE_URL = "https://myoscope.distancing.my.id/"
    private const val BASE_URL = "https://myoscope.humicprototypingapi.online/"

    private val gson = GsonBuilder()
        .setLenient()
        .create()

//    private val httpClient = OkHttpClient.Builder()
//        .addInterceptor(HttpLoggingIntzerceptor().apply {
//            level = HttpLoggingInterceptor.Level.BODY // This logs the network requests
//        })
//        .build()

    fun getRetroMLlientInstance(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
}