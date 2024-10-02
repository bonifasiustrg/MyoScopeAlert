package com.apicta.myoscopealert.data

data class PredictPublicResponse(
    val `data`: Data,
    val error: Any,
    val message: String,
    val status: String
)