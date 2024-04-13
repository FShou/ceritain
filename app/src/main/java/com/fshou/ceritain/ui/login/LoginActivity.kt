package com.fshou.ceritain.ui.login

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
import com.fshou.ceritain.data.remote.response.LoginResult
import com.fshou.ceritain.databinding.ActivityLoginBinding
import com.fshou.ceritain.ui.factory.ViewModelFactory
import com.fshou.ceritain.ui.register.RegisterActivity
import com.fshou.ceritain.data.Result
import com.fshou.ceritain.ui.home.HomeActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private val viewModel: LoginViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    private val validationWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            setLoginButtonEnabled()
        }

        override fun afterTextChanged(s: Editable) {
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
        setupView()




        setLoginButtonEnabled()
        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
        binding.btnLogin.setOnClickListener {
            val email = binding.inputEmail.text.toString()
            val password = binding.inputPassword.text.toString()
            viewModel.login(email,password).observe(this@LoginActivity) {
                handleResult(it)
            }
        }

        binding.inputEmail.addTextChangedListener(validationWatcher)
        binding.inputPassword.addTextChangedListener(validationWatcher)
    }

    private fun handleResult(result: Result<LoginResult>) {
        when(result){
            is Result.Loading -> {
                // Todo: Progress bar & disable input & button
            }
            is Result.Error -> {
               showToast(result.error)
            }
            is Result.Success -> {
                showToast("Login Success")
                val token = result.data.token
                viewModel.saveLoginUser(token)
                startActivity(Intent(this@LoginActivity,HomeActivity::class.java))
                finish()
            }
        }
    }

    private fun showToast(msg: String) = Toast.makeText(this@LoginActivity,msg,Toast.LENGTH_SHORT).show()

    private fun setLoginButtonEnabled() {
        val result = with(binding.inputEmail) {
            text?.isValidEmail() ?: false
        } && with(binding.inputPassword) {
            text?.isValidPassword() ?: false
        }
        binding.btnLogin.isEnabled = result
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



