package com.revolhope.presentation.feature.dashboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.revolhope.presentation.databinding.ActivityDashboardBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ActivityDashboardBinding.inflate(layoutInflater).run {
            setContentView(root)
            binding = this
        }

    }



}