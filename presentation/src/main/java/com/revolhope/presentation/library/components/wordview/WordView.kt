package com.revolhope.presentation.library.components.wordview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.revolhope.domain.feature.word.model.WordModel
import com.revolhope.presentation.R
import com.revolhope.presentation.databinding.ComponentWordViewBinding

class WordView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding = ComponentWordViewBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    fun bind(model: WordModel) {
        binding.wordTextView.text = model.word
        binding.wordOccurrencesNumberTextView.text =
            context.getString(R.string.word_occurrences_number, model.occurrences)
        binding.parentFileTextView.text =
            context.getString(R.string.word_originals_file, model.originalFileName)
    }
}