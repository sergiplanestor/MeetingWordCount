package com.revolhope.presentation.library.components.wordfinder

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import com.revolhope.presentation.R
import com.revolhope.presentation.databinding.ComponentWordFinderBinding
import com.revolhope.presentation.library.extensions.drawable
import com.revolhope.presentation.library.extensions.inflater

class WordFinderView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attributeSet, defStyleAttr) {

    // Private fields
    private val binding: ComponentWordFinderBinding = ComponentWordFinderBinding.inflate(
        context.inflater,
        this,
        true
    )

    private val endIconDrawable: Drawable? by lazy { context.drawable(R.drawable.ic_clear) }

    // Public properties
    var onQueryTextChanged: ((query: String) -> Unit)? = null
    var onQueryTextSubmit: ((query: String) -> Unit)? = null

    init {
        setupListeners()
    }

    private fun setupListeners() {
        binding.finderInputLayout.setEndIconOnClickListener { binding.finderInputEditText.setText("") }
        binding.finderInputEditText.addTextChangedListener(
            onTextChanged = { text, _, _, _ ->
                binding.finderInputLayout.endIconDrawable =
                    if (text.isNullOrBlank()) null else endIconDrawable
                changeValidUI(isValid = true)
                onQueryTextChanged?.invoke(text.toString())
            }
        )
        binding.finderInputEditText.setOnEditorActionListener { _, actionId, event ->
            if (isValidImeOption(actionId, event)) {
                onQuerySubmit()
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun onQuerySubmit() {
        val query = binding.finderInputEditText.text?.toString()
        if (isValidQuery(query)) onQueryTextSubmit?.invoke(query!!)
    }

    private fun changeValidUI(isValid: Boolean) {
        binding.finderInputLayout.error = if (isValid) {
            null
        } else {
            context.getString(R.string.find_word_error)
        }
    }

    private fun isValidImeOption(actionId: Int, event: KeyEvent?) =
        actionId == EditorInfo.IME_ACTION_SEARCH || event?.action == KeyEvent.ACTION_DOWN &&
                event.keyCode == KeyEvent.KEYCODE_ENTER

    private fun isValidQuery(query: String?): Boolean =
        (!query.isNullOrBlank()).also(::changeValidUI)

}