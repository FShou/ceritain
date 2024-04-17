package com.fshou.ceritain.ui.register

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.fshou.ceritain.R
import com.fshou.ceritain.databinding.ActivityRegisterBinding
import com.fshou.ceritain.ui.factory.ViewModelFactory
import com.fshou.ceritain.ui.login.LoginActivity
import com.fshou.ceritain.data.Result
import com.fshou.ceritain.data.remote.response.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private val validationWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            setRegisterButtonEnabled()
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    private val passwordWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (!isRepeatPasswordSame()) {
                binding.inputRepeatPassword.setError("Password is unmatched", null)
            }
            setRegisterButtonEnabled()
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupView()

        binding.inputName.addTextChangedListener(validationWatcher)
        binding.inputEmail.addTextChangedListener(validationWatcher)
        binding.inputPassword.addTextChangedListener(passwordWatcher)
        binding.inputRepeatPassword.addTextChangedListener(passwordWatcher)
        binding.btnRegister.setOnClickListener {
            val name = binding.inputName.text.toString()
            val email = binding.inputEmail.text.toString()
            println(email)
            val password = binding.inputPassword.text.toString()

            viewModel.register(name, email, password).observe(this) {
                handleResult(it)
            }

        }
        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

    }

    private fun handleResult(result: Result<Response>) {
        when (result) {
            is Result.Loading -> {
                disableLoginForm()
                binding.progressBar.alpha = 1f
            }

            is Result.Error -> {
                enableLoginForm()
                binding.progressBar.alpha = 0f
                showToast(result.error)
            }

            is Result.Success -> {
                binding.progressBar.alpha = 0f
                showToast("Register Success")
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }

    private fun disableLoginForm() {
        with(binding) {
            inputName.isEnabled = false
            inputEmail.isEnabled = false
            inputPassword.isEnabled = false
            inputRepeatPassword.isEnabled = false
            btnLogin.isEnabled = false
            btnRegister.isEnabled = false
        }
    }

    private fun enableLoginForm() {
        with(binding) {
            inputName.isEnabled = true
            inputEmail.isEnabled = true
            inputPassword.isEnabled = true
            inputRepeatPassword.isEnabled = true
            btnLogin.isEnabled = true
            btnRegister.isEnabled = true
        }
    }

    private fun showToast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()


    private fun isRepeatPasswordSame() =
        binding.inputPassword.text.toString() == binding.inputRepeatPassword.text.toString()

    private fun setRegisterButtonEnabled() {
        val result = with(binding.inputEmail) {
            text?.isValidEmail() ?: false
        } && with(binding.inputPassword) {
            text?.isValidPassword() ?: false
        } && isRepeatPasswordSame() && !binding.inputName.text.isNullOrEmpty()

        binding.btnRegister.isEnabled = result
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }


}