package com.example.dynamicform.data.model

import com.google.gson.annotations.SerializedName

data class FormField(
    @SerializedName("id")
    val id: Int,

    @SerializedName("type")
    val type: String,

    @SerializedName("label")
    val label: String,

    @SerializedName("meta")
    val meta: FieldMeta? = null,

    @SerializedName("validations")
    val validations: FieldValidations? = null
)

data class FieldMeta(
    @SerializedName("options")
    val options: List<EnrollmentOption>? = null
)

data class FieldValidations(
    @SerializedName("required")
    val required: Boolean = false
)