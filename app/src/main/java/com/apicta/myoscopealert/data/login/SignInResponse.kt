package com.apicta.myoscopealert.data.login


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SignInResponse {
    @SerializedName("message")   @Expose
    var message: String? = null

    @SerializedName("success")   @Expose
    var success: Boolean? = null

    @SerializedName("data")   @Expose
    var data: Data? = null

    class Data {
        @SerializedName("user")   @Expose
        var user: User? = null

        @SerializedName("access_token")   @Expose
        var token: String? = null

        @SerializedName("error")   @Expose
        var error: String? = null
    }

    class User {
        @SerializedName("id")
        @Expose
        var id: String? = null


        @SerializedName("email")
        @Expose
        var email: String? = null
    }
}