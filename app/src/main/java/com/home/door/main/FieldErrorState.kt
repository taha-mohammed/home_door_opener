package com.home.door.main

import androidx.annotation.StringRes

data class FieldErrorState(
    @StringRes val nameError: Int? = null,
    @StringRes val ipError: Int? = null,
    @StringRes val userError: Int? = null,
    @StringRes val passwordError: Int? = null,
)
