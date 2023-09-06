package com.apicta.myoscopealert

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Inisialisasi atau konfigurasi awal yang diperlukan
        // Misalnya, Anda dapat menginisialisasi logger, mengonfigurasi preferensi, atau mengatur lingkungan pengembangan.
    }
}