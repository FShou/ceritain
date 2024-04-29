package com.fshou.ceritain.ui.home

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.fshou.ceritain.R
import com.fshou.ceritain.databinding.ActivityHomeBinding
import com.fshou.ceritain.ui.adapter.StoryAdapter
import com.fshou.ceritain.ui.capture.CaptureActivity
import com.fshou.ceritain.ui.factory.ViewModelFactory
import com.fshou.ceritain.ui.maps.MapsActivity
import com.fshou.ceritain.ui.onboarding.OnBoardingActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class HomeActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var binding: ActivityHomeBinding
    private val viewModel: HomeViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        WindowCompat.getInsetsController(window, binding.root).isAppearanceLightStatusBars = true


        showStories()

        binding.swipeRefresh.setOnRefreshListener(this)

        binding.fab.setOnClickListener {
            startActivity(
                Intent(this, CaptureActivity::class.java),
                ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle()
            )
        }

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_logout -> {
                    showLogoutAlert()
                    true
                }

                R.id.action_maps -> {
                    startActivity(
                        Intent(this@HomeActivity, MapsActivity::class.java),
                        ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle()
                    )
                    true
                }

                else -> false
            }
        }


    }

    private fun showStories() {
        // Todo: Loading & Error State
        val adapter = StoryAdapter()
        val layout = LinearLayoutManager(this)
        binding.rvStories.adapter = adapter
        binding.rvStories.layoutManager = layout
        viewModel.stories.observe(this) {
            adapter.submitData(lifecycle,it)
        }
    }

    private fun showLogoutAlert() {
        MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_App_MaterialAlertDialog)
            .setTitle(getString(R.string.are_you_sure))
            .setMessage(getString(R.string.confirm_to_log_out))
            .setPositiveButton(getText(R.string.log_out)) { _, _ ->
                viewModel.clearLoginUser()
                startActivity(
                    Intent(
                        this,
                        OnBoardingActivity::class.java
                    ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }




    private fun showToast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

    override fun onRefresh() { }


}