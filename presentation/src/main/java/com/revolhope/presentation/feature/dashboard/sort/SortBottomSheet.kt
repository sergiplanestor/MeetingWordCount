package com.revolhope.presentation.feature.dashboard.sort

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.revolhope.presentation.databinding.BottomSheetSortBinding

class SortBottomSheet(
    private val currentSort: SortType?,
    private val onSortApplied: (SortType?) -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetSortBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetSortBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        currentSort?.let(::setSelectedOption)
        binding.doneButton.setOnClickListener {
            onSortApplied.invoke(getSelectedOption())
            this.dismiss()
        }
    }

    private fun setSelectedOption(type: SortType) {
        when (type) {
            SortType.POSITION -> binding.radioButtonGroup.check(binding.sortPositionRadioButton.id)
            SortType.POSITION_REVERSED -> binding.radioButtonGroup.check(binding.sortPositionInvertedRadioButton.id)
            SortType.ALPHABETIC_ASCENDING -> binding.radioButtonGroup.check(binding.sortAlphabeticAscRadioButton.id)
            SortType.ALPHABETIC_DESCENDING -> binding.radioButtonGroup.check(binding.sortAlphabeticDescRadioButton.id)
            SortType.OCCURRENCES_ASCENDING -> binding.radioButtonGroup.check(binding.sortOccurrencesAscRadioButton.id)
            SortType.OCCURRENCES_DESCENDING -> binding.radioButtonGroup.check(binding.sortOccurrencesDescRadioButton.id)
        }
    }

    private fun getSelectedOption(): SortType? =
        when (binding.radioButtonGroup.checkedRadioButtonId) {
            binding.sortPositionRadioButton.id -> {
                SortType.POSITION
            }
            binding.sortPositionInvertedRadioButton.id -> {
                SortType.POSITION_REVERSED
            }
            binding.sortAlphabeticAscRadioButton.id -> {
                SortType.ALPHABETIC_ASCENDING
            }
            binding.sortAlphabeticDescRadioButton.id -> {
                SortType.ALPHABETIC_DESCENDING
            }
            binding.sortOccurrencesAscRadioButton.id -> {
                SortType.OCCURRENCES_ASCENDING
            }
            binding.sortOccurrencesDescRadioButton.id -> {
                SortType.OCCURRENCES_DESCENDING
            }
            else -> null
        }

    fun show(fm: FragmentManager) {
        this.show(fm, javaClass.simpleName)
    }
}