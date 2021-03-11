package com.revolhope.presentation.library.components.wordfinder

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.revolhope.presentation.R
import com.revolhope.presentation.databinding.ComponentWordFinderBinding

class WordFinderView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attributeSet, defStyleAttr) {

    // Private fields
    private val binding: ComponentWordFinderBinding = ComponentWordFinderBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    private val endIconDrawable: Drawable? by lazy {
        ContextCompat.getDrawable(
            context,
            R.drawable.ic_find_24
        )
    }

    private val endIconDrawableError: Drawable? by lazy {
        ContextCompat.getDrawable(
            context,
            R.drawable.ic_find_off_24
        )
    }

    // Public properties
    var onQueryTextChanged: ((query: String) -> Unit)? = null
    var onQueryTextSubmit: ((query: String) -> Unit)? = null

    init {
        binding.finderInputLayout.endIconDrawable = endIconDrawable
        setupListeners()
    }

    private fun setupListeners() {
        binding.finderInputLayout.setEndIconOnClickListener { onQuerySubmit() }
        binding.finderInputEditText.addTextChangedListener(
            onTextChanged = { text, _, _, _ ->
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
        binding.finderInputLayout.endIconDrawable = if (isValid) {
            endIconDrawable
        } else {
            endIconDrawableError
        }
        binding.finderInputLayout.error = if (isValid) {
            null
        } else {
            context.getString(R.string.find_word_error)
        }
    }

    private fun isValidImeOption(actionId: Int, event: KeyEvent?) =
        actionId == EditorInfo.IME_ACTION_SEARCH || event?.action == KeyEvent.ACTION_DOWN &&
                event.keyCode == KeyEvent.KEYCODE_ENTER

    private fun isValidQuery(query: String?): Boolean = (!query.isNullOrBlank()).also(::changeValidUI)

}