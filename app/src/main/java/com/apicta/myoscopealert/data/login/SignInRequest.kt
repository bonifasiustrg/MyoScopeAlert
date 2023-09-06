<<<<<<< HEAD
package com.apicta.myoscopealert.data.login


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SignInRequest {
    @SerializedName("email")   @Expose
    var email: String? = null

    @SerializedName("password")   @Expose
    var password: String? = null
=======
package com.apicta.myoscopealert.data.login


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SignInRequest {
    @SerializedName("email")   @Expose
    var email: String? = null

    @SerializedName("password")   @Expose
    var password: String? = null
>>>>>>> 7f1866f (Implement Depedency Injection with Dagger Hilt for Viewmodel, Datastore (stored token), Repository, and Api Interface.)
}