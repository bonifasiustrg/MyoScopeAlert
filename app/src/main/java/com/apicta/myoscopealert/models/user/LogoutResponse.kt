package com.apicta.myoscopealert.models.user

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LogoutResponse {
    @SerializedName("message")   @Expose
    var message: String? = null

    @SerializedName("success")   @Expose
    var success: Boolean? = null

    @SerializedName("data")   @Expose
    var data: Data? = null

    @SerializedName("error")   @Expose
    var error: String? = null
    class Data {
        @SerializedName("id")   @Expose
        var id: String? = null

        @SerializedName("email")   @Expose
        var email: String? = null
    }
}