package com.revolhope.data.common.base

import com.revolhope.domain.feature.common.model.State
import java.lang.Exception

/**
 * Base repository implementation class.
 */
abstract class BaseRepositoryImpl {

    /**
     * Given an action that returns a type it will be wrapped into [State]. This way ensure
     * that action will be run safely
     */
    protected suspend fun <T> statefulAction(action: suspend () -> T): State<T> =
        try {
            State.Success(action.invoke())
        } catch (e: Exception) {
            State.Error(e.message, e)
        }
}