package com.fshou.ceritain.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.fshou.ceritain.databinding.ActivityLoginBinding
import com.fshou.ceritain.ui.register.RegisterActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setLoginButtonEnabled()

        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        binding.inputEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setLoginButtonEnabled()
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
    }
    private fun setLoginButtonEnabled() {
        val result = with(binding.inputEmail) {
            text.isValidEmail()
        } && with(binding.inputPassword) {
            ! text.isNullOrEmpty()
        }
        binding.btnLogin.isEnabled = result
    }

}



