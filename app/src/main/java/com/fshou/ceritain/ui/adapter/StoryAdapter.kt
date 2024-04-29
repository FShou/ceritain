package com.fshou.ceritain.ui.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation
import com.fshou.ceritain.R
import com.fshou.ceritain.data.remote.response.Story
import com.fshou.ceritain.databinding.StoryItemLayoutBinding
import com.fshou.ceritain.ui.detail.DetailActivity
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class StoryAdapter :
    PagingDataAdapter<Story,StoryAdapter.StoryViewHolder>(DIFF_CALLBACK) {

    inner class StoryViewHolder(private val binding: StoryItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(story: Story) {
            binding.root.setOnClickListener {
                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        binding.root.context as Activity,
                        Pair(binding.ivItemPhoto, "profile"),
                        Pair(binding.tvItemName, "name"),
                        Pair(binding.description, "description"),
                        Pair(binding.postedAt, "createdAt")
                    )

                binding.root.context.startActivity(
                    Intent(binding.root.context, DetailActivity::class.java).putExtra(DetailActivity.EXTRA_STORY, story),
                    optionsCompat.toBundle()
                )
            }

            binding.profilePicture.load(R.drawable.profile) {
                crossfade(true)
                transformations(CircleCropTransformation())
            }

            binding.ivItemPhoto.load(story.photoUrl) {
                crossfade(true)
                transformations(RoundedCornersTransformation(16f))
            }
            val parsedCreated = Instant.parse(story.createdAt)
            val createdAtZonedTime = ZonedDateTime.ofInstant(parsedCreated, ZoneId.systemDefault())
            binding.tvItemName.text = story.name
            binding.postedAt.text = createdAtZonedTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM))
            binding.description.text = story.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding =
            StoryItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return StoryViewHolder(binding)
    }



    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null){
            holder.bind(data)
        }
    }


    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem.id == newItem.id
            }
        }

    }

}