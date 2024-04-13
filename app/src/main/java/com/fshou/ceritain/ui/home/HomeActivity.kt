package com.fshou.ceritain.ui.home

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.fshou.ceritain.R
import com.fshou.ceritain.data.remote.response.Story
import com.fshou.ceritain.databinding.ActivityHomeBinding
import com.fshou.ceritain.ui.factory.ViewModelFactory
import com.fshou.ceritain.data.Result
import com.fshou.ceritain.ui.capture.CaptureActivity
import com.fshou.ceritain.ui.detail.DetailActivity
import com.fshou.ceritain.ui.onboarding.OnBoardingActivity


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

        // Todo: overflow refresh


        viewModel.pref.observe(this) {token ->
            if (token != null) {
                viewModel.getStories(token).observe(this) { result ->
                    handleResult(result)
                }
            }
        }

        binding.fab.setOnClickListener {
            startActivity(Intent(this, CaptureActivity::class.java))
        }

        binding.topAppBar.setOnMenuItemClickListener {menuItem ->
            when(menuItem.itemId){
                R.id.logout -> {
                    viewModel.clearLoginUser()
                    startActivity(Intent(this,OnBoardingActivity::class.java))
                    finish()
                    true
                }
                else -> false
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
//        TODO: Share story
    }

    override fun onStoryClicked(story: Story) {
       startActivity(
           Intent(this@HomeActivity,DetailActivity::class.java).apply {
               putExtra(DetailActivity.EXTRA_STORY, story)
           }
       )
    }


}