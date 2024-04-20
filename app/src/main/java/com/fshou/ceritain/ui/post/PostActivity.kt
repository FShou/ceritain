package com.fshou.ceritain.ui.post

import android.content.Intent
import android.graphics.drawable.Animatable2
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.transform.RoundedCornersTransformation
import com.fshou.ceritain.R
import com.fshou.ceritain.data.Result
import com.fshou.ceritain.data.remote.response.BaseResponse
import com.fshou.ceritain.databinding.ActivityPostBinding
import com.fshou.ceritain.reduceFileImage
import com.fshou.ceritain.ui.capture.CaptureActivity
import com.fshou.ceritain.ui.factory.ViewModelFactory
import com.fshou.ceritain.ui.home.HomeActivity
import com.fshou.ceritain.uriToFile
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class PostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostBinding
    private val viewModel: PostViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPostBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        WindowCompat.getInsetsController(window,binding.root).isAppearanceLightStatusBars = true

        val imgUri = Uri.parse(intent.getStringExtra(CaptureActivity.EXTRA_IMG_URI))
        with(binding){
            topAppBar.setNavigationOnClickListener { showExitAlert() }
            buttonAdd.setOnClickListener {
                postStory(imgUri)
            }
            postImage.load(imgUri) {
                crossfade(true)
                transformations(RoundedCornersTransformation(20f))
            }

            edAddDescription.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    setPostButtonEnabled()
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }
        setPostButtonEnabled()
        this.onBackPressedDispatcher.addCallback(this@PostActivity) {
            isEnabled = true
            showExitAlert()
        }


    }

    private fun setPostButtonEnabled() {
        with(binding) {
            buttonAdd.isEnabled = !edAddDescription.text.isNullOrEmpty()
        }
    }


    private fun showExitAlert() {
        MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_App_MaterialAlertDialog)
            .setTitle(getString(R.string.are_you_sure)).setMessage(getString(R.string.this_will_not_be_saved))
            .setPositiveButton(getString(R.string.continu)) { _, _ -> startIntentHome() }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun startIntentHome() {
        val intent = Intent(this@PostActivity, HomeActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }


    private fun postStory(uri: Uri) {
        binding.progressBar.visibility = View.VISIBLE
        enablePostForm(false)

        CoroutineScope(Dispatchers.IO).launch {
            val imgFile = uriToFile(uri, this@PostActivity).reduceFileImage()
            val description = binding.edAddDescription.text.toString()

            val requestBody = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imgFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo", imgFile.name, requestImageFile
            )

            lifecycleScope.launch {
                viewModel.postStory(multipartBody, requestBody).observe(this@PostActivity) {
                    handleResult(it)
                }
            }
        }

    }

    private fun handleResult(result: Result<BaseResponse>) {
        when (result) {
            is Result.Loading -> {
                binding.progressBar.visibility = View.VISIBLE
                enablePostForm(false)
            }

            is Result.Success -> {
                binding.progressBar.visibility = View.GONE
                hidePostForm()
                showSuccessAnim()

            }

            is Result.Error -> {
                binding.progressBar.visibility = View.GONE
                enablePostForm(true)
                Toast.makeText(this@PostActivity, result.error, Toast.LENGTH_LONG).show()
            }

        }
    }

    private fun showSuccessAnim() {
        with(binding) {
            checkAnim.visibility = View.VISIBLE
            postSuccess.visibility = View.VISIBLE
        }
        val animatedCheck = binding.checkAnim.drawable as AnimatedVectorDrawable
        animatedCheck.registerAnimationCallback(object : Animatable2.AnimationCallback() {
            override fun onAnimationEnd(drawable: Drawable?) {
                super.onAnimationEnd(drawable)
                startIntentHome()
            }
        })
        animatedCheck.start()
    }

    private fun hidePostForm() {
        with(binding) {
            postImage.visibility = View.GONE
            edAddDescription.visibility = View.GONE
            buttonAdd.visibility = View.GONE
        }
    }

    private fun enablePostForm(isEnable: Boolean) {
        with(binding) {
            buttonAdd.isEnabled = isEnable
            edAddDescription.isEnabled = isEnable
        }
    }

}