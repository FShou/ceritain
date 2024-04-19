package com.fshou.ceritain.ui.detail

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import coil.load
import coil.transform.RoundedCornersTransformation
import com.fshou.ceritain.data.remote.response.Story
import com.fshou.ceritain.databinding.ActivityDetailBinding
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val story = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_STORY, Story::class.java) as Story
        } else {
            intent.getParcelableExtra(EXTRA_STORY)!!
        }
        val parsedCreated = Instant.parse(story.createdAt)
        val createdAtZonedTime = ZonedDateTime.ofInstant(parsedCreated, ZoneId.systemDefault())
        val createdAtLocalFormat =
            createdAtZonedTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM))


        with(binding) {
            tvDetailName.text = story.name
            tvDetailDescription.text = story.description
            createdAt.text = createdAtLocalFormat
            ivDetailPhoto.load(story.photoUrl) {
                crossfade(true)
                transformations(RoundedCornersTransformation(16f))
            }

            topAppBar.setNavigationOnClickListener { finishAfterTransition() }
        }

    }

    companion object {
        const val EXTRA_STORY = "story"
    }
}