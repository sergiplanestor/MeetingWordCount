package com.revolhope.presentation.library.base

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {

    private var isFirstOnResume: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(inflateView())
        bindViews()
        initObservers()
    }

    override fun onResume() {
        super.onResume()
        if (isFirstOnResume) {
            loadData()
            isFirstOnResume = false
        } else {
            reloadData()
        }
    }

    abstract fun inflateView(): View

    open fun bindViews() {
        // Nothing to do here
    }

    open fun initObservers() {
        // Nothing to do here
    }

    open fun loadData() {
        // Nothing to do here
    }

    open fun reloadData() {
        // Nothing to do here
    }
}