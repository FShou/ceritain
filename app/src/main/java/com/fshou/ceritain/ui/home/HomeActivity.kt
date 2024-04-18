package com.fshou.ceritain.ui.home

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.fshou.ceritain.R
import com.fshou.ceritain.data.Result
import com.fshou.ceritain.data.remote.response.Story
import com.fshou.ceritain.databinding.ActivityHomeBinding
import com.fshou.ceritain.ui.capture.CaptureActivity
import com.fshou.ceritain.ui.factory.ViewModelFactory
import com.fshou.ceritain.ui.onboarding.OnBoardingActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch


class HomeActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

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



        lifecycleScope.launch {
            viewModel.getStories().observe(this@HomeActivity) { result ->
                handleResult(result)
            }
        }

        binding.swipeRefresh.setOnRefreshListener(this)

        binding.fab.setOnClickListener {
            startActivity(Intent(this, CaptureActivity::class.java), ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle())
        }

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.logout -> {
                    showExitAlert()
                    true
                }

                else -> false
            }
        }


    }

    private fun showExitAlert() {
        MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_App_MaterialAlertDialog)
            .setTitle("Are you sure ?")
            .setMessage("Confirm to Log Out")
            .setPositiveButton("Log out") {_,_ ->
                viewModel.clearLoginUser()
                startActivity(
                    Intent(
                        this,
                        OnBoardingActivity::class.java
                    ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            }
            .setNegativeButton("Cancel") {dialog,_ ->
                dialog.dismiss()
            }
            .show()
    }


    private fun handleResult(result: Result<List<Story>>) {
        when (result) {
            is Result.Loading -> {
                binding.tvError.alpha = 0f
                binding.swipeRefresh.isRefreshing = true
            }

            is Result.Success -> {
                showStories(result.data)
                binding.swipeRefresh.isRefreshing = false
            }

            is Result.Error -> {
                binding.tvError.alpha = 1f
                showToast(result.error)
                binding.swipeRefresh.isRefreshing = false
            }
        }

    }

    private fun showToast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    private fun showStories(stories: List<Story>) {
        val rvLayout = LinearLayoutManager(this)
        val rvAdapter = StoryAdapter(stories)

        binding.rvStories.apply {
            layoutManager = rvLayout
            adapter = rvAdapter
        }
    }

    override fun onRefresh() {
        lifecycleScope.launch {
            viewModel.getStories().observe(this@HomeActivity) { result ->
                handleResult(result)
            }

        }
    }


}