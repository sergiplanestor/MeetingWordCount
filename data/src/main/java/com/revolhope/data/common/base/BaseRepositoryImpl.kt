package com.revolhope.data.common.base

import com.revolhope.domain.feature.common.model.State
import java.lang.Exception

abstract class BaseRepositoryImpl {

    protected suspend fun <T> statefulAction(action: suspend () -> T): State<T> =
        try {
            State.Success(action.invoke())
        } catch (e: Exception) {
            State.Error(e.message, e)
        }
}