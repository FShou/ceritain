package com.fshou.ceritain.ui.home

import android.view.LayoutInflater
import android.view.RoundedCorner
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.fshou.ceritain.data.remote.response.Story
import com.fshou.ceritain.databinding.StoryItemLayoutBinding

class StoryAdapter(val story: List<Story>, val storyListener: StoryListener) :
    RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {

    inner class StoryViewHolder(private val binding: StoryItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(story: Story) {
            binding.root.setOnClickListener {
                storyListener.onStoryClicked()
            }
            binding.btnShare.setOnClickListener {
                storyListener.onShareClicked(story)
            }

            binding.storyPicture.load(story.photoUrl) {
                crossfade(true)
                transformations(RoundedCornersTransformation(16f))
            }
            binding.userName.text = story.name
            binding.postedAt.text = story.createdAt
            binding.description.text = story.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding =
            StoryItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return StoryViewHolder(binding)
    }

    override fun getItemCount() = story.size

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        holder.bind(story[position])
    }

    interface StoryListener {
        fun onShareClicked(story: Story)
        fun onStoryClicked()

    }
}