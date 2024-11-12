package com.apicta.myoscopealert.utils

import android.util.Log

fun logLargeArray(tag: String, array: FloatArray) {
    val chunkSize = 1000  // Define the chunk size
    val featureString = array.contentToString()  // Convert array to string

    var i = 0
    while (i < featureString.length) {
        // Log each chunk of the feature string
        if (i + chunkSize < featureString.length) {
            Log.d(tag, featureString.substring(i, i + chunkSize))
        } else {
            Log.d(tag, featureString.substring(i))
        }
        i += chunkSize
    }
}