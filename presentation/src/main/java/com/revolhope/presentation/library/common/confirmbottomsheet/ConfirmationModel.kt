package com.revolhope.presentation.library.common.confirmbottomsheet

data class ConfirmationModel(
    val description: String,
    val onConfirm: () -> Unit,
    val onCancel: (() -> Unit)? = null,
    val onDismiss: (() -> Unit)? = null
)