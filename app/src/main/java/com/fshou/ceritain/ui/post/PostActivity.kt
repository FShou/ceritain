package com.fshou.ceritain.ui.post

import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import coil.load
import coil.transform.RoundedCornersTransformation
import com.fshou.ceritain.databinding.ActivityPostBinding
import com.fshou.ceritain.ui.capture.CaptureActivity

class PostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostBinding
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
        binding.postImage.load(imgUri){
            crossfade(true)
            transformations(RoundedCornersTransformation(20f))
        }

        binding.btnPost.setOnClickListener {
            // Todo: Post the story
        }


    }
}