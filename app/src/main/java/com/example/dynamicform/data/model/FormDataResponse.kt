package com.example.dynamicform.data.model

import com.google.gson.annotations.SerializedName

data class FormDataResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: List<FormField>
)