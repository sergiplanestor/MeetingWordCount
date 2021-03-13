package com.revolhope.presentation.library.base

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.revolhope.presentation.R
import com.revolhope.presentation.library.components.loader.LoaderView
import com.revolhope.presentation.library.components.snackbar.SnackBar
import com.revolhope.presentation.library.components.snackbar.model.SnackBarModel

abstract class BaseActivity : AppCompatActivity() {

    lateinit var root: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(inflateView().also { root = it })
        bindViews()
        initObservers()
    }

    abstract fun inflateView(): View

    open fun bindViews() {
        // Nothing to do here
    }

    open fun initObservers() {
        // Nothing to do here
    }

    protected fun onErrorReceived(error: String) {
        SnackBar.show(root, SnackBarModel.Error(error))
    }

    protected fun onLoaderVisibilityChanges(show: Boolean) {
        findViewById<LoaderView?>(R.id.loader_view)?.let {
            if (show) it.show() else it.hide()
        }
    }
}