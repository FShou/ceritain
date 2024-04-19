package com.fshou.ceritain.ui.register

import android.content.Intent
import android.graphics.drawable.Animatable2
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
import com.fshou.ceritain.data.remote.response.Response
import com.fshou.ceritain.databinding.ActivityRegisterBinding
import com.fshou.ceritain.ui.factory.ViewModelFactory
import com.fshou.ceritain.ui.login.LoginActivity

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

        binding.edRegisterName.addTextChangedListener(validationWatcher)
        binding.edRegisterEmail.addTextChangedListener(validationWatcher)
        binding.edRegisterPassword.addTextChangedListener(passwordWatcher)
        binding.inputRepeatPassword.addTextChangedListener(passwordWatcher)


        binding.btnRegister.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()

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
                disableRegisterForm()
                binding.progressBar.visibility = View.VISIBLE
            }

            is Result.Error -> {
                enableRegisterForm()
                binding.progressBar.visibility = View.GONE
                showToast(result.error)
            }

            is Result.Success -> {
                val animatedCheck = binding.checkAnim.drawable as AnimatedVectorDrawable
                animatedCheck.registerAnimationCallback(object : Animatable2.AnimationCallback(){
                    override fun onAnimationEnd(drawable: Drawable?) {
                        super.onAnimationEnd(drawable)
                        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                        finish()
                    }
                })

                with(binding) {
                    tvName.visibility = View.GONE
                    tvEmail.visibility = View.GONE
                    tvPassword.visibility = View.GONE
                    tvRepeatPassword.visibility = View.GONE
                    tvOther.visibility = View.GONE
                    edRegisterName.visibility = View.GONE
                    edRegisterEmail.visibility = View.GONE
                    edRegisterPassword.visibility = View.GONE
                    inputRepeatPassword.visibility = View.GONE
                    btnRegister.visibility = View.GONE
                    btnLogin.visibility = View.GONE
                    progressBar.visibility = View.GONE
                    checkAnim.visibility= View.VISIBLE
                    registerSuccess.visibility= View.VISIBLE
                }

                animatedCheck.start()
            }
        }
    }

    private fun disableRegisterForm() {
        with(binding) {
            edRegisterName.isEnabled = false
            edRegisterEmail.isEnabled = false
            edRegisterPassword.isEnabled = false
            inputRepeatPassword.isEnabled = false
            btnLogin.isEnabled = false
            btnRegister.isEnabled = false
        }
    }

    private fun enableRegisterForm() {
        with(binding) {
            edRegisterName.isEnabled = true
            edRegisterEmail.isEnabled = true
            edRegisterPassword.isEnabled = true
            inputRepeatPassword.isEnabled = true
            btnLogin.isEnabled = true
            btnRegister.isEnabled = true
        }
    }

    private fun showToast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()


    private fun isRepeatPasswordSame() =
        binding.edRegisterPassword.text.toString() == binding.inputRepeatPassword.text.toString()

    private fun setRegisterButtonEnabled() {
        val result = with(binding.edRegisterEmail) {
            text?.isValidEmail() ?: false
        } && with(binding.edRegisterPassword) {
            text?.isValidPassword() ?: false
        } && isRepeatPasswordSame() && !binding.edRegisterName.text.isNullOrEmpty()

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