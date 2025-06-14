package com.apicta.myoscopealert.downloader

interface Downloader {
    fun downloadFile(url: String): Long
}