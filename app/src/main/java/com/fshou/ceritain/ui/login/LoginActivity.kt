package com.fshou.ceritain.ui.login

import android.content.Intent
import android.graphics.drawable.Animatable2.AnimationCallback
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.fshou.ceritain.R
import com.fshou.ceritain.data.Result
import com.fshou.ceritain.data.remote.response.LoginResult
import com.fshou.ceritain.databinding.ActivityLoginBinding
import com.fshou.ceritain.ui.factory.ViewModelFactory
import com.fshou.ceritain.ui.home.HomeActivity
import com.fshou.ceritain.ui.register.RegisterActivity

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
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupView()




        setLoginButtonEnabled()
        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
        binding.btnLogin.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()
            viewModel.login(email, password).observe(this@LoginActivity) {
                handleResult(it)
            }
        }

        binding.edLoginEmail.addTextChangedListener(validationWatcher)
        binding.edLoginPassword.addTextChangedListener(validationWatcher)
    }

    private fun handleResult(result: Result<LoginResult>) {
        when (result) {
            is Result.Loading -> {
                disableLoginForm()
                with(binding) {
                    progressBar.visibility = View.VISIBLE
                    edLoginEmail.error = null
                    edLoginPassword.error = null
                }
            }

            is Result.Error -> {
                enableLoginForm()
                binding.progressBar.visibility = View.GONE
                when {
                    result.error.contains("password") -> {
                        binding.edLoginPassword.apply {
                            error = result.error
                            requestFocus()
                            setSelection(text?.length ?: 0)
                        }
                    }

                    result.error.contains("email") || result.error.contains("User") -> {
                        binding.edLoginEmail.apply {
                            error = result.error
                            requestFocus()
                            setSelection(text?.length ?: 0)
                        }
                    }

                    result.error.isNotEmpty() -> {
                        showToast(result.error)
                    }
                }
            }

            is Result.Success -> {
                val token = result.data.token
                viewModel.saveLoginUser(token)

                val animatedCheck = binding.checkAnim.drawable as AnimatedVectorDrawable
                animatedCheck.registerAnimationCallback(object : AnimationCallback(){
                    override fun onAnimationEnd(drawable: Drawable?) {
                        super.onAnimationEnd(drawable)
                        startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                        finish()
                    }
                })

                with(binding) {
                    tvEmail.visibility = View.GONE
                    tvPassword.visibility = View.GONE
                    tvOther.visibility = View.GONE
                    edLoginEmail.visibility = View.GONE
                    edLoginPassword.visibility = View.GONE
                    btnLogin.visibility = View.GONE
                    btnContinue.visibility = View.GONE
                    btnRegister.visibility = View.GONE
                    progressBar.visibility = View.GONE
                    checkAnim.visibility= View.VISIBLE
                    loginSucces.visibility= View.VISIBLE
                }

                animatedCheck.start()


            }
        }
    }

    private fun disableLoginForm() {
        with(binding) {
            edLoginEmail.isEnabled = false
            edLoginPassword.isEnabled = false
            btnLogin.isEnabled = false
            btnContinue.isEnabled = false
            btnRegister.isEnabled = false
        }
    }

    private fun enableLoginForm() {
        with(binding) {
            edLoginEmail.isEnabled = true
            edLoginPassword.isEnabled = true
            btnLogin.isEnabled = true
            btnContinue.isEnabled = true
            btnRegister.isEnabled = true
        }
    }


    private fun showToast(msg: String) =
        Toast.makeText(this@LoginActivity, msg, Toast.LENGTH_SHORT).show()

    private fun setLoginButtonEnabled() {
        val result = with(binding.edLoginEmail) {
            text?.isValidEmail() ?: false
        } && with(binding.edLoginPassword) {
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



