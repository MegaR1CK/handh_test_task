package com.example.testtask.ui

import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout

@BindingAdapter("errorText")
fun setErrorText(textInputLayout: TextInputLayout, text: String?) {
    textInputLayout.error = text
}