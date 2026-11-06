package com.example.dynamicform

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.dynamicform.data.model.FormField
import com.example.dynamicform.data.model.FormPage
import com.example.dynamicform.databinding.ActivityMainBinding
import com.example.dynamicform.utils.FormPageHelper
import com.example.dynamicform.view.form.FormAdapter
import com.example.dynamicform.view.form.FormUiState
import com.example.dynamicform.view.form.FormValidator
import com.example.dynamicform.view.form.FormViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: FormViewModel by viewModels()

    private var pages: List<FormPage> = emptyList()
    private var allFields: List<FormField> = emptyList()
    private var currentPage = 0
    private val savedValues = mutableMapOf<String, String>()

    private lateinit var formAdapter: FormAdapter

    private val formValidator = FormValidator()

    private var emailMandatory = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        formAdapter = FormAdapter(
            context = this,
            savedValues = savedValues,
            onEnrollmentChanged = { selectedOption ->
                emailMandatory = selectedOption.emailMandatory
            }
        )

        //viewModel.loadPreviewForm()
        if (viewModel.uiState.value == null) {
            viewModel.fetchForm()
        }

        binding.btnRetry.setOnClickListener {
            viewModel.fetchForm()
            //viewModel.loadPreviewForm()
        }

        binding.btnBack.setOnClickListener {
            if (currentPage > 0) {
                currentPage--
                showPage()
            }
        }

        binding.btnContinue.setOnClickListener {
            if (!validateCurrentPage()) {
                return@setOnClickListener
            }

            if (currentPage < pages.lastIndex) {
                currentPage++
                showPage()
            } else {
                Toast.makeText(
                    this,
                    resources.getString(R.string.form_validated),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        observeForm()
    }

    private fun validateCurrentPage(): Boolean {

        val fields = pages[currentPage].fields

        val fieldKeys = fields.associateWith { field ->
            val originalIndex = allFields.indexOfFirst {
                it.id == field.id &&
                        it.type == field.type &&
                        it.label == field.label
            }
            "field_$originalIndex"
        }

        Log.d(
            "DynamicForm",
            "Saved values: $savedValues"
        )

        Log.d(
            "DynamicForm",
            "Field keys: $fieldKeys"
        )

        val error = formValidator.validate(
            fields = fields,
            values = savedValues,
            fieldKeys = fieldKeys,
            emailMandatory = emailMandatory
        )

        Log.d(
            "DynamicForm",
            "All fields: ${allFields.map { "${it.id}-${it.type}-${it.label}" }}"
        )

        formAdapter.clearAllErrors()

        error.forEach { (fieldKey, message) ->
            formAdapter.setFieldError(fieldKey, message)
        }

        return error.isEmpty()
    }

    private fun showPage() {
        binding.fieldContainer.removeAllViews()

        val fields = pages[currentPage].fields

        fields.forEach { field ->
            val originalIndex = allFields.indexOfFirst {
                it.id == field.id &&
                        it.type == field.type &&
                        it.label == field.label
            }

            val fieldKey = "field_$originalIndex"

            formAdapter.createField(
                field = field,
                fieldKey = fieldKey,
                container = binding.fieldContainer
            )
        }

        binding.txtPage.text = "Page ${currentPage + 1} of ${pages.size}"

        val progress = ((currentPage + 1) * 100) / pages.size
        binding.progressBar.progress = progress

        binding.btnBack.visibility =
            if (currentPage == 0) View.GONE else View.VISIBLE

        binding.btnContinue.text =
            if (currentPage == pages.lastIndex) "Submit" else "Next"
    }

    private fun observeForm() {

        viewModel.uiState.observe(this) { state ->

            when(state) {

                is FormUiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.txtStatus.visibility = View.VISIBLE
                    binding.btnRetry.visibility = View.GONE
                    binding.formContainer.visibility = View.GONE
                }

                is FormUiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.txtStatus.visibility = View.GONE
                    binding.btnRetry.visibility = View.GONE

                    allFields = state.formData.data
                    pages = FormPageHelper.createPages(allFields)
                    currentPage = 0

                    if (pages.isNotEmpty()) {
                        binding.formContainer.visibility = View.VISIBLE
                        showPage()
                    } else {
                        binding.formContainer.visibility = View.GONE
                        binding.txtStatus.text = resources.getString(R.string.no_form_fields_available)
                        binding.txtStatus.visibility = View.VISIBLE
                    }

                    Log.d("FormError", "${state.formData.data}")
                }

                is FormUiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.txtStatus.visibility = View.VISIBLE
                    binding.txtStatus.text = state.message
                    binding.btnRetry.visibility = View.VISIBLE
                    binding.formContainer.visibility = View.GONE

                    Log.d("FormError", state.message)
                }

                null -> Unit
            }
        }
    }
}