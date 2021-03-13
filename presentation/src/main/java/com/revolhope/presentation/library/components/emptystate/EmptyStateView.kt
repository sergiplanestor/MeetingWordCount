package com.revolhope.presentation.library.components.emptystate

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.revolhope.presentation.databinding.ComponentEmptyStateBinding
import com.revolhope.presentation.library.extensions.inflater

class EmptyStateView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attributeSet, defStyleAttr) {

    private val binding = ComponentEmptyStateBinding.inflate(
        context.inflater,
        this,
        true
    )

    fun setImage(drawable: Drawable) {
        binding.emptyStateImageView.setImageDrawable(drawable)
    }

    fun setTitle(title: String) {
        binding.titleTextView.text = title
    }

    fun setSubtitle(subtitle: String) {
        binding.subtitleTextView.text = subtitle
    }
}