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
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

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
                    val message = getUserFriendlyMessage(error)

                    error.printStackTrace()

                    _uiState.value = FormUiState.Error(message)
                }
            )
        }
    }

    private fun getUserFriendlyMessage(error: Throwable): String {

        return when (error) {

            is HttpException -> {
                when (error.code()) {
                    400 -> "Invalid request. Please try again."
                    401, 403 -> "You are not authorized to access this form."
                    404 -> "The requested form could not be found."
                    408 -> "The request took too long. Please try again."
                    429 -> "The form service has reached its request limit. Please try again later."
                    in 500..599 -> "Our server is having trouble right now. Please try again later."
                    else -> "Unable to load the form. Please try again."
                }
            }

            is UnknownHostException ->
                "No internet connection. Please check your connection and try again."

            is SocketTimeoutException ->
                "The connection timed out. Please try again."

            is ConnectException ->
                "Unable to connect to the server. Please try again."

            is IOException ->
                "A network error occurred. Please check your connection and try again."

            else ->
                "Something went wrong while loading the form. Please try again."
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

