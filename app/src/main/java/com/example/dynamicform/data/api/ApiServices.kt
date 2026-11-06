package com.example.dynamicform.data.api

import com.example.dynamicform.data.model.FormDataResponse
import retrofit2.Response
import retrofit2.http.GET

interface ApiServices {

    @GET("c3e36b7e-80ad-4025-89ea-6674a80edf12")
    suspend fun getForm(): Response<FormDataResponse>
}