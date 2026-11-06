# Dynamic Form – Android

An Android application that dynamically generates a multi-page form using configuration data received from a REST API.

## Overview

The application fetches form configuration from an API and generates the UI at runtime. It supports multiple input types, conditional validation, and navigation between form pages.

## Features

- Fetches form configuration using a REST API.
- Dynamically generates form fields based on the API response.
- Supports dropdown, text, phone number, and email inputs.
- Divides the form into multiple pages using separator fields.
- Validates required fields and email format.
- Supports conditional email validation based on the selected enrollment type.
- Preserves entered values when navigating between pages.
- Displays validation errors directly on the corresponding fields.

## Tech Stack

- **Language:** Kotlin
- **UI:** XML, Material Components
- **Architecture:** MVVM
- **Networking:** Retrofit
- **JSON Parsing:** Gson
- **Asynchronous Operations:** Kotlin Coroutines
- **Architecture Components:** ViewModel, LiveData

## Project Structure

```text
com.example.dynamicform
│
├── data
│   ├── api
│   │   ├── ApiClient.kt
│   │   └── ApiServices.kt
│   │
│   ├── model
│   │   ├── EnrollmentOption.kt
│   │   ├── FormDataResponse.kt
│   │   ├── FormField.kt
│   │   ├── FormOption.kt
│   │   └── FormPage.kt
│   │
│   └── repository
│       └── FormRepository.kt
│
├── utils
│   └── FormPageHelper.kt
│
├── view
│   └── form
│       ├── FormAdapter.kt
│       ├── FormValidator.kt
│       └── FormViewModel.kt
│
└── MainActivity.kt
```

## Form Functionality

### Enrollment Type
- Displays available enrollment options from the API.
- The selected option determines whether email is mandatory.

### Name
- Displays a text input using the label provided by the API.
- Validates required input.

### Phone Number
- Uses a phone-specific keyboard.
- Validates required input.

### Email
- Uses an email-specific keyboard.
- Validates email format.
- Required only when the selected enrollment option has `email_mandatory` set to `true`.

### Pagination
- Uses `separator` fields from the API response to divide the form into multiple pages.
- Validates the current page before allowing navigation.
- Preserves entered values when moving between pages.

## API Configuration

The application uses the GET endpoint provided for the assignment:

```text
https://webhook.site/c3e36b7e-80ad-4025-89ea-6674a80edf12
```

The endpoint is configured in `ApiServices.kt` and accessed through Retrofit.

**API Availability Note:** The provided Webhook.site endpoint returned HTTP 429 during testing, indicating that its request quota had been exhausted. The application remains configured to use the assignment-provided endpoint. A working endpoint is required to verify live API loading.

## Setup Instructions

1. Clone the repository:

   ```bash
   git clone https://github.com/iamsag-business/DynamicForm.git
   ```

2. Open the project in Android Studio.

3. Allow Gradle to sync and download the required dependencies.

4. Ensure the configured API endpoint is accessible.

5. Run the application on an emulator or Android device.

## Build Requirements

- Android Studio
- JDK 11
- Android SDK 36
- Minimum Android SDK: 23

## Architecture

The application follows the MVVM architecture:

- **View:** `MainActivity` and `FormAdapter` handle the UI.
- **ViewModel:** `FormViewModel` manages UI state and form data.
- **Repository:** `FormRepository` handles API communication.
- **Data:** Model classes represent the API response and form structure.

## Repository

[GitHub – DynamicForm](https://github.com/iamsag-business/DynamicForm)