package com.example.testtask.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.testtask.AuthViewModel
import com.example.testtask.AuthViewModelFactory
import com.example.testtask.R
import com.example.testtask.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

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
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
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
                viewModel.requestWeather(
                        binding.fieldEmail.text?.toString() ?: "",
                        binding.fieldPassword.text?.toString() ?: ""
                )
                viewModel.onSignInButtonClicked()
            }
        }

        viewModel.apiResponse.observe(this) {
            Snackbar.make(binding.buttonSignIn, it, Snackbar.LENGTH_LONG).show()
        }
    }
}