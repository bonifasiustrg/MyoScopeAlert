package com.apicta.myoscopealert.data.login


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SignInRequest {
    @SerializedName("email")   @Expose
    var email: String? = null

    @SerializedName("password")   @Expose
    var password: String? = null
}