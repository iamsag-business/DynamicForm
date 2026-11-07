package com.example.dynamicform.data.repository

import com.example.dynamicform.data.api.ApiClient
import com.example.dynamicform.data.model.FormDataResponse
import retrofit2.HttpException

class FormRepository {

    private val apiService = ApiClient.apiServices

    suspend fun getForm(): Result<FormDataResponse> {

        return try {

            val response = apiService.getForm()

            if (response.isSuccessful) {

                val body = response.body()

                if (body != null && body.status == "success") {
                    Result.success(body)
                } else {
                    Result.failure(
                        Exception(body?.message ?: "Empty response received")
                    )
                }

            } else {
                Result.failure(HttpException(response))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}