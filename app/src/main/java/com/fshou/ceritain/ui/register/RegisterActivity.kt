package com.fshou.ceritain.ui.register

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.fshou.ceritain.R
import com.fshou.ceritain.databinding.ActivityRegisterBinding
import com.fshou.ceritain.ui.login.LoginActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
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
                binding.inputRepeatPassword.setError("Password is unmatched",null)
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

        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }

    }



    private fun isRepeatPasswordSame() =
        binding.inputPassword.text.toString() == binding.inputRepeatPassword.text.toString()

    private fun setRegisterButtonEnabled(){
        val result = with(binding.inputEmail) {
            text?.isValidEmail() ?: false
        } && with(binding.inputPassword){
            text?.isValidPassword() ?: false
        } && isRepeatPasswordSame()    && !binding.inputName.text.isNullOrEmpty()

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