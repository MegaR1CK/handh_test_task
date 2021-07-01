package com.example.testtask.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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
    private lateinit var optionsMenu: Menu

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
            fieldPassword.setOnFocusChangeListener { _, hasFocus ->
                if (::optionsMenu.isInitialized) {
                    optionsMenu.findItem(R.id.menu_create).isVisible = hasFocus
                }
            }
            boxPassword.setEndIconOnClickListener {
                Toast.makeText(this@AuthActivity, getString(R.string.password_help), Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        if (menu != null) optionsMenu = menu
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_create) {
            viewModel.onCreateButtonClick()
        }
        return true
    }

    private fun setupToolbar(binding: ActivityMainBinding) {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setTitle(R.string.activity_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupObservers(binding: ActivityMainBinding) {
        viewModel.onSignInButtonClickEvent.observe(this) {
            if (it) {
                hideKeyboard()
                binding.apply {
                    fieldEmail.clearFocus()
                    fieldPassword.clearFocus()
                }
                viewModel.requestWeather(
                        binding.fieldEmail.text.toString(),
                        binding.fieldPassword.text.toString()
                )
                viewModel.onSignInButtonClicked()
            }
        }

        viewModel.apiResponse.observe(this) {
            Snackbar.make(binding.buttonSignIn, it, Snackbar.LENGTH_LONG).show()
        }

        viewModel.onCreateButtonClickEvent.observe(this) {
            if (it) {
                viewModel.generateRandomPassword()
                viewModel.onCreateButtonClicked()
            }
        }

        viewModel.email.observe(this) {
            binding.fieldEmail.setText(it)
        }

        viewModel.password.observe(this) {
            binding.fieldPassword.setText(it)
        }
    }

    private fun hideKeyboard() {
        currentFocus?.let {
            val inputMethodManager = ContextCompat.getSystemService(this, InputMethodManager::class.java)
            inputMethodManager?.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }
}