package com.example.testtask

import android.app.Application
import android.util.Log
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.lifecycle.*
import com.example.testtask.data.WeatherRepository
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class AuthViewModel(private val app: Application) : AndroidViewModel(app) {

    companion object Observer : BaseObservable() {

        @get:Bindable
        var email: String = ""
            set(value) {
                field = value
                notifyPropertyChanged(BR.email)
            }

        @get:Bindable
        var password: String = ""
            set(value) {
                field = value
                notifyPropertyChanged(BR.password)
            }
    }

    private val _onSignInButtonClickEvent = MutableLiveData<Boolean>()
    val onSignInButtonClickEvent: LiveData<Boolean>
        get() = _onSignInButtonClickEvent

    private val _onCreateButtonClickEvent = MutableLiveData<Boolean>()
    val onCreateButtonClickEvent: LiveData<Boolean>
        get() = _onCreateButtonClickEvent

    private val _emailErrorText = MutableLiveData<String?>()
    val emailErrorText: LiveData<String?>
        get() = _emailErrorText

    private val _passwordErrorText = MutableLiveData<String?>()
    val passwordErrorText: LiveData<String?>
        get() = _passwordErrorText

    private val _apiResponse = MutableLiveData<String>()
    val apiResponse: LiveData<String>
        get() = _apiResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    //TODO: create

    private val repository = WeatherRepository()

    fun onSignInButtonClick() {
        _onSignInButtonClickEvent.value = true
    }

    fun onSignInButtonClicked() {
        _onSignInButtonClickEvent.value = false
    }

    fun onCreateButtonClick() {
        _onCreateButtonClickEvent.value = true
    }

    fun onCreateButtonClicked() {
        _onCreateButtonClickEvent.value = false
    }

    fun requestWeather() {
        if (isDataValid(email, password)) {
            viewModelScope.launch {
                try {
                    _isLoading.value = true
                    val response = repository.getWeather("saransk")
                    val clouds = response.weather?.first()?.main
                    val temp = response.main?.temp?.roundToInt()
                    _apiResponse.value = "$clouds, $temp °С"
                } catch (e: Exception) {
                    _apiResponse.value = e.localizedMessage
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }

    fun generateRandomPassword() {
        val chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
        var passwordString = ""
        repeat(4) {
            repeat(4) {
                passwordString += chars.random()
            }
            if (it != 3) passwordString += '-'
        }
        password = passwordString
    }

    private fun isDataValid(email: String, password: String): Boolean {
        val emailRegex = Regex("[A-Za-z0-9]+@[a-z]+.[a-z]{2,4}")
        val passwordRegex = Regex("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,}\$")
        var isEmailCorrect = true
        var isPasswordCorrect = true
        hideErrors()
        when {
            email.isBlank() -> {
                _emailErrorText.value = app.resources.getString(R.string.error_empty_field)
                isEmailCorrect = false
            }
            !email.matches(emailRegex) -> {
                _emailErrorText.value = app.resources.getString(R.string.error_incorrect_email)
                isEmailCorrect = false
            }
        }
        when {
            password.isBlank() -> {
                _passwordErrorText.value = app.resources.getString(R.string.error_empty_field)
                isPasswordCorrect = false
            }
            !password.matches(passwordRegex) -> {
                _passwordErrorText.value = app.resources.getString(R.string.error_weak_password)
                isPasswordCorrect = false
            }
        }
        return isEmailCorrect && isPasswordCorrect
    }

    private fun hideErrors() {
        _emailErrorText.value = null
        _passwordErrorText.value = null
    }
}