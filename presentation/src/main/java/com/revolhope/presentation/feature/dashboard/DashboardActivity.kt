package com.revolhope.presentation.feature.dashboard

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import com.revolhope.domain.feature.word.model.WordModel
import com.revolhope.presentation.R
import com.revolhope.presentation.databinding.ActivityDashboardBinding
import com.revolhope.presentation.feature.dashboard.adapter.DashboardContentAdapter
import com.revolhope.presentation.feature.dashboard.adapter.PagerLayoutManager
import com.revolhope.presentation.feature.dashboard.sort.SortBottomSheet
import com.revolhope.presentation.library.base.BaseActivity
import com.revolhope.presentation.library.common.confirmbottomsheet.ConfirmationBottomSheet
import com.revolhope.presentation.library.common.confirmbottomsheet.ConfirmationModel
import com.revolhope.presentation.library.extensions.applyTint
import com.revolhope.presentation.library.extensions.changeMenuIcon
import com.revolhope.presentation.library.extensions.color
import com.revolhope.presentation.library.extensions.drawable
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

        fun start(baseActivity: BaseActivity) {
            baseActivity.startActivity(
                Intent(
                    baseActivity,
                    DashboardActivity::class.java
                ).apply {
                    putExtras(
                        bundleOf(
                            EXTRA_NAVIGATION_TRANSITION to NavTransition.LATERAL
                        )
                    )
                }
            )
        }
    }

    private val viewModel: DashboardViewModel by viewModels()
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var contentAdapter: DashboardContentAdapter
    private var menu: Menu? = null
    private val displayingWords: List<WordModel>
        get() = if (::contentAdapter.isInitialized) {
            contentAdapter.items
        } else {
            emptyList()
        }


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
        bindFinder()
        bindAdapter()
        onWordsReceived(emptyList())
    }

    override fun initObservers() {
        super.initObservers()
        observe(viewModel.loaderLiveData, ::onLoaderVisibilityChanges)
        observe(viewModel.errorLiveData, ::onErrorReceived)
        observe(viewModel.wordsLiveData, ::onWordsReceived)
        observe(viewModel.wordCountLiveData, ::onWordCountChanges)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        this.menu = menu
        menuInflater.inflate(R.menu.dashboard_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.menu_find -> {
                onFindMenuClick()
                true
            }
            R.id.menu_sort -> {
                onSortMenuClick()
                true
            }
            R.id.menu_clear -> {
                onClearMenuClick()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    private fun bindFinder() {
        with(binding.wordFinder) {
            onQueryTextChanged = { filter ->
                if (::contentAdapter.isInitialized) {
                    viewModel.currentFilter = filter
                    viewModel.notifyWords()
                }
            }
            onQueryTextSubmit = { filter ->
                if (::contentAdapter.isInitialized) {
                    viewModel.currentFilter = filter
                    viewModel.notifyWords()
                }
            }
            binding.addFileButton.setOnClickListener {
                showFileChooser()
            }
        }
    }

    private fun bindAdapter() {
        with(binding.contentRecyclerView) {
            layoutManager = PagerLayoutManager(
                context = this@DashboardActivity,
                onLastElementVisible = viewModel::fetchNextPage
            )
            adapter = DashboardContentAdapter().also { contentAdapter = it }
        }
    }

    private fun onWordsReceived(words: List<WordModel>) {
        binding.contentGroup.isVisible = words.isNotEmpty()
        binding.emptyStateView.isVisible = words.isEmpty()
        if (::contentAdapter.isInitialized && words.isNotEmpty()) {
            contentAdapter.updateItems(words)
        }
    }

    private fun onWordCountChanges(wordCount: Pair<Int, Int>) {
        binding.wordCountTextView.text =
            getString(R.string.word_count_summary, wordCount.first)
        binding.uniqueWordCountTextView.text =
            getString(R.string.unique_word_count_summary, wordCount.second)
    }

    private fun onFindMenuClick() {
        if (binding.wordFinder.isVisible) {
            binding.wordFinder.isVisible = false
            menu?.changeMenuIcon(R.id.menu_find, drawable(R.drawable.ic_find).apply {
                applyTint(color(R.color.white))
            })
        } else {
            binding.wordFinder.isVisible = true
            menu?.changeMenuIcon(R.id.menu_find, drawable(R.drawable.ic_find_off).apply {
                applyTint(color(R.color.white))
            })
        }
    }

    private fun onSortMenuClick() {
        SortBottomSheet(
            currentSort = viewModel.currentSortType,
            onSortApplied = {
                viewModel.currentSortType = it
                viewModel.notifyWords(displayingWords)
            }
        ).show(supportFragmentManager)
    }

    private fun onClearMenuClick() {
        ConfirmationBottomSheet(
            model = ConfirmationModel(
                description = getString(R.string.clear_data_description),
                onConfirm = viewModel::clearWords
            )
        ).show(supportFragmentManager)
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
                    onErrorReceived(getString(R.string.permissions_required))
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
            viewModel.processFileData(context = this, data = data, resolver = contentResolver)
        }
    }
}