package com.revolhope.presentation.library.common.confirmbottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.revolhope.presentation.databinding.BottomSheetConfirmationBinding

class ConfirmationBottomSheet(
    val model: ConfirmationModel
) : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetConfirmationBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetConfirmationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.descriptionTextView.text = model.description
        binding.confirmButton.setOnClickListener {
            model.onConfirm.invoke()
            super.dismiss()
        }
        binding.cancelButton.setOnClickListener {
            model.onCancel?.invoke()
            super.dismiss()
        }
    }

    override fun dismiss() {
        model.onDismiss?.invoke()
        super.dismiss()
    }

    fun show(fm: FragmentManager) {
        this.show(fm, javaClass.simpleName)
    }
}