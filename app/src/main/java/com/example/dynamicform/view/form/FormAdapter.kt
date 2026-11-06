
package com.example.dynamicform.view.form

import android.content.Context
import android.text.InputType
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.util.Log
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.example.dynamicform.data.model.EnrollmentOption
import com.example.dynamicform.data.model.FormField
import androidx.core.graphics.toColorInt
import androidx.core.graphics.drawable.toDrawable

class FormAdapter(
    private val context: Context,
    private val savedValues: MutableMap<String, String>,
    private val onEnrollmentChanged: (EnrollmentOption) -> Unit
) {

    private val fieldLayouts = mutableMapOf<String, TextInputLayout>()

    fun createField(
        field: FormField,
        fieldKey: String,
        container: LinearLayout
    ) {
        when (field.type) {

            "enrollment_type" ->
                createEnrollmentField(field, fieldKey, container)

            "name" -> createTextField(
                field,
                fieldKey,
                container,
                InputType.TYPE_CLASS_TEXT or
                        InputType.TYPE_TEXT_FLAG_CAP_WORDS
            )

            "phone_number" -> createTextField(
                field,
                fieldKey,
                container,
                InputType.TYPE_CLASS_PHONE
            )

            "email" -> createTextField(
                field,
                fieldKey,
                container,
                InputType.TYPE_CLASS_TEXT or
                        InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            )
        }
    }

    private fun createEnrollmentField(
        field: FormField,
        fieldKey: String,
        container: LinearLayout
    ) {
        val options = field.meta?.options.orEmpty()

        val textInputLayout = createInputLayout(field.label)

        textInputLayout.endIconMode =
            TextInputLayout.END_ICON_DROPDOWN_MENU

        val dropdown = MaterialAutoCompleteTextView(context).apply {
            setTextColor("#000000".toColorInt())
            setHintTextColor("#6B7280".toColorInt())
            textSize = 16f

            setAdapter(
                object : ArrayAdapter<String>(
                    context,
                    android.R.layout.simple_dropdown_item_1line,
                    options.map { it.option }
                ) {
                    override fun getView(
                        position: Int,
                        convertView: android.view.View?,
                        parent: ViewGroup
                    ): android.view.View {
                        return super.getView(position, convertView, parent).apply {
                            (this as android.widget.TextView).setTextColor(
                                "#000000".toColorInt()
                            )
                        }
                    }

                    override fun getDropDownView(
                        position: Int,
                        convertView: android.view.View?,
                        parent: ViewGroup
                    ): android.view.View {
                        return super.getDropDownView(position, convertView, parent).apply {
                            (this as android.widget.TextView).apply {
                                setTextColor("#000000".toColorInt())
                                setBackgroundColor("#FFFFFF".toColorInt())
                            }
                        }
                    }
                }
            )

            setDropDownBackgroundDrawable(
                "#FFFFFF".toColorInt().toDrawable()
            )

            keyListener = null

            val savedOption = savedValues[fieldKey]
            if (!savedOption.isNullOrEmpty()) {
                setText(savedOption, false)

                options.find { it.option == savedOption }?.let {
                    onEnrollmentChanged(it)
                }
            }

            setOnItemClickListener { _, _, position, _ ->
                if (position in options.indices) {
                    val selectedOption = options[position]

                    savedValues[fieldKey] = selectedOption.option

                    textInputLayout.error = null

                    Log.d(
                        "DynamicForm",
                        "Selected: ${selectedOption.option}, Key: $fieldKey"
                    )

                    onEnrollmentChanged(selectedOption)
                }
            }
        }

        textInputLayout.addView(
            dropdown,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        fieldLayouts[fieldKey] = textInputLayout
        container.addView(textInputLayout)
        addFieldSpacing(container)
    }

    private fun createTextField(
        field: FormField,
        fieldKey: String,
        container: LinearLayout,
        inputType: Int
    ) {
        val textInputLayout = createInputLayout(field.label)

        val editText = TextInputEditText(textInputLayout.context).apply {
            this.inputType = inputType
            setTextColor("#000000".toColorInt())
            setHintTextColor("#6B7280".toColorInt())
            textSize = 16f
            isSingleLine = true
            setText(savedValues[fieldKey].orEmpty())

            addTextChangedListener(
                object : android.text.TextWatcher {

                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) = Unit

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) = Unit

                    override fun afterTextChanged(
                        s: android.text.Editable?
                    ) {
                        savedValues[fieldKey] = s?.toString().orEmpty()

                        if (!s.isNullOrBlank()) {
                            textInputLayout.error = null
                        }
                    }
                }
            )
        }

        textInputLayout.addView(
            editText,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        fieldLayouts[fieldKey] = textInputLayout
        container.addView(textInputLayout)
        addFieldSpacing(container)
    }

    private fun createInputLayout(label: String): TextInputLayout {
        val errorRed = "#D32F2F".toColorInt()
        val primaryColor = "#000000".toColorInt()
        val hintColor = "#6B7280".toColorInt()

        return TextInputLayout(context).apply {
            hint = label
            isHintEnabled = true
            boxBackgroundMode = TextInputLayout.BOX_BACKGROUND_OUTLINE

            setBoxCornerRadii(
                dp(12).toFloat(),
                dp(12).toFloat(),
                dp(12).toFloat(),
                dp(12).toFloat()
            )

            boxStrokeWidth = dp(1)
            boxStrokeWidthFocused = dp(2)

            setBoxStrokeColorStateList(
                android.content.res.ColorStateList(
                    arrayOf(
                        intArrayOf(android.R.attr.state_focused),
                        intArrayOf()
                    ),
                    intArrayOf(primaryColor, hintColor)
                )
            )

            setErrorTextColor(
                android.content.res.ColorStateList.valueOf(errorRed)
            )
            setBoxStrokeErrorColor(
                android.content.res.ColorStateList.valueOf(errorRed)
            )

            setDefaultHintTextColor(
                android.content.res.ColorStateList.valueOf(hintColor)
            )
        }
    }

    fun setFieldError(fieldKey: String, message: String) {
        fieldLayouts[fieldKey]?.error = message
    }

    fun clearFieldError(fieldKey: String) {
        fieldLayouts[fieldKey]?.error = null
    }

    fun clearAllErrors() {
        fieldLayouts.values.forEach {
            it.error = null
        }
    }

    private fun addFieldSpacing(container: LinearLayout) {
        val spacing = android.view.View(context)

        container.addView(
            spacing,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(20)
            )
        )
    }

    private fun dp(value: Int): Int {
        return (value * context.resources.displayMetrics.density).toInt()
    }
}
