package com.revolhope.domain.feature.common.model

/**
 * Model to handle states
 */
sealed class State<out T> {
    data class Success<out T>(val data: T) : State<T>()
    data class Error(
        val message: String? = null,
        val throwable: Throwable? = null
    ) : State<Nothing>()
}