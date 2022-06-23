package com.example.duqr.data.remote

import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @Multipart
    @POST(value = "/")
    suspend fun getQrCode(
        @Part("url") url: String,
        @Part("color") color: String,
        @Part user_image: MultipartBody.Part? = null
    ): QrCodeDto
}