package com.apicta.myoscopealert.utils

import kotlin.math.abs

class NoiseReducer {
    companion object {
        private const val NOISE_THRESHOLD = 500 // Adjust this threshold based on your needs

        fun applyNoiseReduction(inputArray: ShortArray) {
            for (i in inputArray.indices) {
                if (abs(inputArray[i].toInt()) < NOISE_THRESHOLD) {
                    inputArray[i] = 0
                }
            }
        }
    }
}