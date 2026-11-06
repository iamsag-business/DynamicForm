package com.example.dynamicform.data.model

import com.google.gson.annotations.SerializedName

data class EnrollmentOption(

    @SerializedName("option")
    val option: String,

    @SerializedName("email_mandatory")
    val emailMandatory: Boolean
)