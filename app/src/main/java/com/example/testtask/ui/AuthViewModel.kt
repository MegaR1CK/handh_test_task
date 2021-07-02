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

    companion object {
        private const val CITY = "saransk"
        private const val PASSWORD_LOWER_LETTERS = "abcdefghijklmnopqrstuvwxyz"
        private const val PASSWORD_UPPER_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        private const val PASSWORD_NUMBERS = "0123456789"
        private const val EMAIL_PATTERN = "[A-Za-z0-9]+@[a-z]+.[a-z]{2,4}"
        private const val PASSWORD_PATTERN = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,}\$"
    }

    private val _onSignInButtonClickLiveEvent = SingleLiveEvent<Unit>()
    val onSignInButtonClickLiveEvent: LiveData<Unit> = _onSignInButtonClickLiveEvent

    private val _onCreateButtonClickLiveEvent = SingleLiveEvent<Unit>()
    val onCreateButtonClickLiveEvent: LiveData<Unit> = _onCreateButtonClickLiveEvent

    private val _apiResponseLiveEvent = SingleLiveEvent<String>()
    val apiResponseLiveEvent: LiveData<String> = _apiResponseLiveEvent

    private val _emailErrorText = MutableLiveData<String?>()
    val emailErrorText: LiveData<String?>
        get() = _emailErrorText

    private val _passwordErrorText = MutableLiveData<String?>()
    val passwordErrorText: LiveData<String?>
        get() = _passwordErrorText

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
        _onSignInButtonClickLiveEvent.call()
    }

    fun onCreateButtonClick() {
        _onCreateButtonClickLiveEvent.call()
    }

    fun requestWeather(email: String, password: String) {
        if (isDataValid(email, password)) {
            viewModelScope.launch {
                try {
                    _isLoading.value = true
                    // Строки и числа выносим в константы. В других местах тоже
                    val response = repository.getWeather(CITY)
                    val clouds = response.weather?.first()?.main
                    val temp = response.main?.temp?.roundToInt()
                    // Тут лучше делать через ресурсы
                    _apiResponseLiveEvent.value = app.resources.getString(R.string.weather_info, clouds, temp)
                } catch (e: Exception) {
                    _apiResponseLiveEvent.value = e.localizedMessage
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }

    fun generateRandomPassword() {
        val passwordString = StringBuilder()
        val passwordChars = PASSWORD_LOWER_LETTERS + PASSWORD_UPPER_LETTERS + PASSWORD_NUMBERS
        passwordString.append(PASSWORD_LOWER_LETTERS.random())
        passwordString.append(PASSWORD_UPPER_LETTERS.random())
        passwordString.append(PASSWORD_NUMBERS.random())
        repeat(5) {
            // Кажется, нет гарантии, что всегда будет 1 заглавная буква, одна строчная и одна цифра.
            // И достаточно было бы сделать 6 символов, а не 20
            passwordString.append(passwordChars.random())
        }
        _password.value = passwordString.toString()
    }

    fun validateEmail(email: String): Boolean {
        // Паттерны следует вынести в константы
        val emailRegex = Regex(EMAIL_PATTERN)
        return when {
            email.isBlank() -> {
                _emailErrorText.value = app.resources.getString(R.string.error_empty_field)
                false
            }
            !email.matches(emailRegex) -> {
                _emailErrorText.value = app.resources.getString(R.string.error_incorrect_email)
                false
            }
            else -> {
                _emailErrorText.value = null
                true
            }
        }
    }

    fun validatePassword(password: String): Boolean {
        // Паттерны следует вынести в константы
        val passwordRegex = Regex(PASSWORD_PATTERN)
        return when {
            password.isBlank() -> {
                _passwordErrorText.value = app.resources.getString(R.string.error_empty_field)
                false
            }
            !password.matches(passwordRegex) -> {
                _passwordErrorText.value = app.resources.getString(R.string.error_weak_password)
                false
            }
            else -> {
                _passwordErrorText.value = null
                true
            }
        }
    }

    private fun isDataValid(email: String, password: String): Boolean {
        return validateEmail(email) && validatePassword(password)
    }
}