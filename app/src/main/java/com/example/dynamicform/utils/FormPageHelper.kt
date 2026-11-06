package com.example.dynamicform.utils

import com.example.dynamicform.data.model.FormField
import com.example.dynamicform.data.model.FormPage

object FormPageHelper {

    fun createPages(fields: List<FormField>): List<FormPage> {

        val pages = mutableListOf<FormPage>()
        var currentFields = mutableListOf<FormField>()

        fields.forEach { field ->
            if (field.type == "separator") {
                if (currentFields.isNotEmpty()) {
                    pages.add(FormPage(currentFields.toList()))
                    currentFields = mutableListOf()
                }

            } else {
                currentFields.add(field)
            }
        }

        if (currentFields.isNotEmpty()) {
            pages.add(FormPage(currentFields.toList()))
        }

        return pages
    }
}