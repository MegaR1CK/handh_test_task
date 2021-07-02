package com.example.testtask.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.testtask.R
import com.example.testtask.data.WeatherRepository
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class AuthViewModel(private val app: Application) : AndroidViewModel(app) {

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

    private val _email = MutableLiveData<String>()
    val email: LiveData<String>
        get() = _email

    private val _password = MutableLiveData<String>()
    val password: LiveData<String>
        get() = _password

    private val repository = WeatherRepository()

    /**
     * Для случаев, когда лайдата должна уведомить слушателей только один раз ( например при клике на кнопку),
     * Мы завели класс [SingleLiveEvent]. Можешь использовать его вместо LiveData
     * */
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

    fun requestWeather(email: String, password: String) {
        if (isDataValid(email, password)) {
            viewModelScope.launch {
                try {
                    _isLoading.value = true
                    // Строки и числа выносим в константы. В других местах тоже
                    val response = repository.getWeather("saransk")
                    val clouds = response.weather?.first()?.main
                    val temp = response.main?.temp?.roundToInt()
                    // Тут лучше делать через ресурсы
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
            // Кажется, нет гарантии, что всегда будет 1 заглавная буква, одна строчная и одна цифра.
            // И достаточно было бы сделать 6 символов, а не 20
            repeat(4) {
                passwordString += chars.random()
            }
            if (it != 3) passwordString += '-'
        }
        _password.value = passwordString
    }

    private fun isDataValid(email: String, password: String): Boolean {
        // Паттерны следует вынести в константы
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