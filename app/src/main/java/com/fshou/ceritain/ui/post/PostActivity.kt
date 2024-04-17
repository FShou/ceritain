package com.fshou.ceritain.ui.post

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.transform.RoundedCornersTransformation
import com.fshou.ceritain.data.Result
import com.fshou.ceritain.data.remote.response.Response
import com.fshou.ceritain.databinding.ActivityPostBinding
import com.fshou.ceritain.ui.capture.CaptureActivity
import com.fshou.ceritain.ui.factory.ViewModelFactory
import com.fshou.ceritain.ui.home.HomeActivity
import com.fshou.ceritain.uriToFile
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.InputStream

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

        binding.topAppBar.setNavigationOnClickListener { finish() }
        val imgUri = Uri.parse(intent.getStringExtra(CaptureActivity.EXTRA_IMG_URI))
        binding.postImage.load(imgUri) {
            crossfade(true)
            transformations(RoundedCornersTransformation(20f))
        }

        binding.btnPost.setOnClickListener {
            postStory(imgUri)
        }

        //Todo: handle back confirmation
    }

    private fun postStory(uri: Uri) {
        val imgFile = uriToFile(uri, this)
        val description = binding.inputDescription.text.toString()

        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imgFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imgFile.name,
            requestImageFile
        )
        lifecycleScope.launch {
            viewModel.postStory(multipartBody, requestBody).observe(this@PostActivity) {
                handleResult(it)
            }
        }
    }

    private fun handleResult(response: Result<Response>) {
        when (response) {
            is Result.Loading -> {
                // Todo: Loading
                binding.btnPost.isEnabled = false
            }

            is Result.Success -> {
                binding.btnPost.isEnabled = true
                Toast.makeText(this@PostActivity, "Post Success", Toast.LENGTH_SHORT).show()
                startActivity(
                    Intent(
                        this,
                        HomeActivity::class.java
                    ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            }

            is Result.Error -> {
                binding.btnPost.isEnabled = true

            }

        }
    }
}