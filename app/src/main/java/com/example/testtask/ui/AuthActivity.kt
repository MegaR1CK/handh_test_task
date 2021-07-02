package com.example.testtask.ui

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.testtask.R
import com.example.testtask.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class AuthActivity : AppCompatActivity() {

    private lateinit var viewModelFactory: AuthViewModelFactory
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModelFactory = AuthViewModelFactory(application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(AuthViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.apply {
            setupToolbar(this)
            setupObservers(this)
            // Можно было бы еще добавить валидацию и показ ошибки при потере фокуса в поле
            editTextEmail.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    viewModel?.validateEmail(editTextEmail.text.toString())
                }
            }
            editTextPassword.setOnFocusChangeListener { _, hasFocus ->
                binding.toolbar.menu.findItem(R.id.menu_create).isVisible = hasFocus
                if (!hasFocus) {
                    viewModel?.validatePassword(editTextPassword.text.toString())
                }
            }
            editTextPassword.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    initiateRequestWeather(this)
                }
                true
            }
            layoutPassword.setEndIconOnClickListener {
                showPasswordHelp()
            }
            // Неплохо было бы по клику на иконку ошибки то же самое показывать
            layoutPassword.setErrorIconOnClickListener {
                showPasswordHelp()
            }
        }
    }

    private fun setupToolbar(binding: ActivityMainBinding) {
        // Мы договорились не пользоваться setSupportActionBar.
        // Меню так же делаем через тулбар. Т.е. toolbar.inflateMenu(int resId) и toolbar.setOnMenuItemClickListener
        binding.toolbar.apply {
            inflateMenu(R.menu.menu_main)
            setTitle(R.string.activity_title)
            setNavigationIcon(R.drawable.ic_arrow_back_black_24_px)
            setNavigationOnClickListener {
                onBackPressed()
            }

            setOnMenuItemClickListener {
                if (it.itemId == R.id.menu_create) {
                    viewModel.onCreateButtonClick()
                }
                true
            }
        }
    }

    private fun setupObservers(binding: ActivityMainBinding) {
        viewModel.onSignInButtonClickLiveEvent.observe(this) {
            initiateRequestWeather(binding)
        }

        viewModel.apiResponseLiveEvent.observe(this) {
            Snackbar.make(binding.buttonSignIn, it, Snackbar.LENGTH_LONG).show()
        }

        viewModel.onCreateButtonClickLiveEvent.observe(this) {
            viewModel.generateRandomPassword()
        }

        viewModel.email.observe(this) {
            binding.editTextEmail.setText(it)
        }

        viewModel.password.observe(this) {
            binding.editTextPassword.setText(it)
        }
    }

    private fun initiateRequestWeather(binding: ActivityMainBinding) {
        hideKeyboard()
        binding.apply {
            editTextEmail.clearFocus()
            editTextPassword.clearFocus()
        }
        viewModel.requestWeather(
            binding.editTextEmail.text.toString(),
            binding.editTextPassword.text.toString()
        )
    }

    private fun hideKeyboard() {
        currentFocus?.let {
            val inputMethodManager = ContextCompat.getSystemService(this, InputMethodManager::class.java)
            inputMethodManager?.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    private fun showPasswordHelp() {
        Toast.makeText(this, getString(R.string.password_help), Toast.LENGTH_LONG).show()
    }
}