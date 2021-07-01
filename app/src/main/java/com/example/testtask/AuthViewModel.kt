package com.example.testtask

import android.app.Application
import androidx.lifecycle.*
import com.example.testtask.data.WeatherRepository
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class AuthViewModel(private val app: Application) : AndroidViewModel(app) {

    private val _onSignInButtonClickEvent = MutableLiveData<Boolean>()
    val onSignInButtonClickEvent: LiveData<Boolean>
        get() = _onSignInButtonClickEvent

    private val _emailErrorText = MutableLiveData<String?>()
    val emailErrorText: LiveData<String?>
        get() = _emailErrorText

    private val _passwordErrorText = MutableLiveData<String?>()
    val passwordErrorText: LiveData<String?>
        get() = _passwordErrorText

    private val _apiResponse = MutableLiveData<String>()
    val apiResponse: LiveData<String>
        get() = _apiResponse

    //TODO: tw binding
    //TODO: spinner
    //TODO: hide keyboard
    //TODO: hide create
    //TODO: create
    //TODO: help btn

    private val repository = WeatherRepository()

    fun onSignInButtonClick() {
        _onSignInButtonClickEvent.value = true
    }

    fun onSignInButtonClicked() {
        _onSignInButtonClickEvent.value = false
    }

    fun requestWeather(email: String, password: String) {
        if (isDataValid(email, password)) {
            viewModelScope.launch {
                try {
                    val response = repository.getWeather("saransk")
                    val clouds = response.weather?.first()?.main
                    val temp = response.main?.temp?.roundToInt()
                    _apiResponse.value = "$clouds, $temp °С"
                } catch (e: Exception) {
                    _apiResponse.value = e.localizedMessage
                }
            }
        }
    }

    private fun isDataValid(email: String, password: String): Boolean {
        val emailRegex = Regex("[A-Za-z0-9]+@[a-z]+.[a-z]{2,4}")
        val passwordRegex = Regex("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,}\$")
        hideErrors()
        when {
            email.isBlank() -> {
                _emailErrorText.value = app.resources.getString(R.string.error_empty_field)
                return false
            }
            password.isBlank() -> {
                _passwordErrorText.value = app.resources.getString(R.string.error_empty_field)
                return false
            }
            !email.matches(emailRegex) -> {
                _emailErrorText.value = app.resources.getString(R.string.error_incorrect_email)
                return false
            }
            !password.matches(passwordRegex) -> {
                _passwordErrorText.value = app.resources.getString(R.string.error_weak_password)
                return false
            }
            else -> {
                hideErrors()
                return true
            }
        }
    }

    private fun hideErrors() {
        _emailErrorText.value = null
        _passwordErrorText.value = null
    }
}