package com.example.dynamicform.data.model

import com.google.gson.annotations.SerializedName

data class FormOption(
    @SerializedName("option")
    val option: String,

    @SerializedName("email_mandatory")
    val emailMandatory: Boolean
)