package com.revolhope.presentation.feature.dashboard

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.revolhope.presentation.R
import com.revolhope.presentation.databinding.ActivityDashboardBinding
import com.revolhope.presentation.feature.dashboard.adapter.DashboardContentAdapter
import com.revolhope.presentation.feature.dashboard.adapter.PagerLayoutManager
import com.revolhope.presentation.library.base.BaseActivity
import com.revolhope.presentation.library.extensions.observe
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DashboardActivity : BaseActivity() {

    companion object {
        // Request codes
        private const val REQUEST_READ_PERMISSION_CODE = 0x234
        private const val REQUEST_FILE_CHOOSER_CODE = 0x760

        // Inner constants
        private const val INTENT_FILE_TYPE = "text/plain"
    }

    private val viewModel: DashboardViewModel by viewModels()
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var contentAdapter: DashboardContentAdapter

    private val isReadPermissionGranted
        get() =
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED

    override fun inflateView(): View =
        ActivityDashboardBinding.inflate(layoutInflater).let {
            binding = it
            it.root
        }

    override fun bindViews() {
        super.bindViews()
        with(binding.wordFinder) {
            onQueryTextChanged = { filter ->
                if (::contentAdapter.isInitialized) {
                    viewModel.applyFilter(filter)?.let { contentAdapter.updateItems(it) }
                }
            }
            onQueryTextSubmit = { filter ->
                if (::contentAdapter.isInitialized) {
                    viewModel.applyFilter(filter)?.let { contentAdapter.updateItems(it) }
                }
            }
            binding.addFileButton.setOnClickListener {
                showFileChooser()
            }
        }
        with(binding.contentRecyclerView) {
            layoutManager = PagerLayoutManager(
                context = this@DashboardActivity,
                onLastElementVisible = viewModel::fetchNextPage
            )
            adapter = DashboardContentAdapter().also { contentAdapter = it }
        }
    }

    override fun initObservers() {
        super.initObservers()
        observe(viewModel.errorLiveData) {
            Toast.makeText(this, "Error -> $it", Toast.LENGTH_LONG).show()
        }
        observe(viewModel.wordsLiveData) {
            if (::contentAdapter.isInitialized) contentAdapter.updateItems(it)
        }
        observe(viewModel.wordCountLiveData) {
            binding.wordCountTextView.text =
                getString(R.string.word_count_summary, it.first, it.second)
        }
    }

    private fun showFileChooser() {
        if (isReadPermissionGranted) {
            startActivityForResult(
                Intent().apply {
                    type = INTENT_FILE_TYPE
                    action = Intent.ACTION_GET_CONTENT
                },
                REQUEST_FILE_CHOOSER_CODE
            )
        } else {
            requestReadPermissions()
        }
    }

    private fun requestReadPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            REQUEST_READ_PERMISSION_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_READ_PERMISSION_CODE -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showFileChooser()
                } else {
                    TODO("Show feedback message")
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_FILE_CHOOSER_CODE &&
            resultCode == Activity.RESULT_OK &&
            data != null
        ) {
            viewModel.processFileData(data, contentResolver)
        } else {
            TODO("Show feedback message")
        }
    }
}