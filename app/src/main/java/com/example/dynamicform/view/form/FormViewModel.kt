package com.example.dynamicform.view.form

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dynamicform.data.model.EnrollmentOption
import com.example.dynamicform.data.model.FieldMeta
import com.example.dynamicform.data.model.FieldValidations
import com.example.dynamicform.data.model.FormDataResponse
import com.example.dynamicform.data.model.FormField
import com.example.dynamicform.data.repository.FormRepository
import kotlinx.coroutines.launch

sealed class FormUiState {

    object Loading : FormUiState()

    data class Success(
        val formData: FormDataResponse
    ) : FormUiState()

    data class Error(
        val message: String
    ) : FormUiState()
}

class FormViewModel(
    private val repository: FormRepository = FormRepository()
) : ViewModel() {

    private val _uiState = MutableLiveData<FormUiState>()
    val uiState: LiveData<FormUiState> = _uiState

    fun fetchForm() {

        viewModelScope.launch {

            _uiState.value = FormUiState.Loading

            val result = repository.getForm()

            result.fold(
                onSuccess = { response ->
                    _uiState.value = FormUiState.Success(response)
                },
                onFailure = { error ->
                    _uiState.value = FormUiState.Error(
                        error.message ?: "Something went wrong"
                    )
                }
            )
        }
    }

    fun loadPreviewForm() {
        val previewData = FormDataResponse(
            status = "success",
            message = "Preview form loaded",
            data = listOf(
                FormField(
                    id = 1,
                    type = "enrollment_type",
                    label = "Enrollment Type",
                    meta = FieldMeta(
                        options = listOf(
                            EnrollmentOption("Paper Enrollment", true),
                            EnrollmentOption("Electric Enrollment", false)
                        )
                    ),
                    validations = FieldValidations(required = true)
                ),
                FormField(
                    id = 2,
                    type = "separator",
                    label = "separator"
                ),
                FormField(
                    id = 3,
                    type = "name",
                    label = "Authorized Individual",
                    validations = FieldValidations(required = true)
                ),
                FormField(
                    id = 4,
                    type = "phone_number",
                    label = "Mobile Phone Number",
                    validations = FieldValidations(required = true)
                ),
                FormField(
                    id = 5,
                    type = "email",
                    label = "Email",
                    validations = FieldValidations(required = true)
                )
            )
        )

        _uiState.value = FormUiState.Success(previewData)
    }
}

