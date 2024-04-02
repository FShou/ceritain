package com.fshou.ceritain.ui.home

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.fshou.ceritain.data.remote.response.Story
import com.fshou.ceritain.databinding.ActivityHomeBinding
import com.fshou.ceritain.ui.factory.ViewModelFactory
import com.fshou.ceritain.data.Result


class HomeActivity : AppCompatActivity(), StoryAdapter.StoryListener {

    private lateinit var binding: ActivityHomeBinding
    private val viewModel: HomeViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        viewModel.pref.observe(this) {
            val token = it?.toList()?.get(2)
            if (token != null) {
                viewModel.getStories(token).observe(this) { result ->
                    handleResult(result)
                }
            }
        }



    }

    private fun handleResult(result: Result<List<Story>>) {
        when(result){
            is Result.Loading -> {

            }
            is Result.Success -> {
                showStories(result.data)
            }
            is Result.Error -> {
                showToast(result.error)
            }
        }

    }
    fun showToast(msg: String) = Toast.makeText(this, msg,Toast.LENGTH_SHORT).show()
    private fun showStories(stories: List<Story>) {
        val rvLayout = LinearLayoutManager(this)
        val rvAdapter = StoryAdapter(stories, this)

        binding.rvStories.apply {
            layoutManager = rvLayout
            adapter = rvAdapter
        }
    }

    override fun onShareClicked(story: Story) {
//        TODO("Not yet implemented")
    }

    override fun onStoryClicked() {
//        TODO("Not yet implemented")
    }


}