
package com.example.dynamicform.view.form

import android.util.Patterns
import com.example.dynamicform.data.model.FormField

class FormValidator {

    fun validate(
        fields: List<FormField>,
        values: Map<String, String>,
        fieldKeys: Map<FormField, String>,
        emailMandatory: Boolean
    ): Map<String, String> {

        val errors = mutableMapOf<String, String>()

        for (field in fields) {
            val fieldKey = fieldKeys[field] ?: continue
            val value = values[fieldKey]?.trim().orEmpty()

            val error = when (field.type) {

                "enrollment_type" -> {
                    if (value.isEmpty()) {
                        "Please select an enrollment type."
                    } else {
                        null
                    }
                }

                "email" -> {
                    when {
                        emailMandatory && value.isEmpty() ->
                            "Please enter your email address."

                        value.isNotEmpty() &&
                                !Patterns.EMAIL_ADDRESS.matcher(value).matches() ->
                            "Please enter a valid email address."

                        else -> null
                    }
                }

                else -> {
                    if (field.validations?.required == true && value.isEmpty()) {
                        "Please enter ${field.label}."
                    } else {
                        null
                    }
                }
            }

            if (error != null) {
                errors[fieldKey] = error
            }
        }

        return errors
    }
}
