package com.revolhope.domain.feature.common.base

import com.revolhope.domain.feature.common.model.State

abstract class BaseUseCase<REQ, RES> {

    suspend operator fun invoke(req: REQ): State<RES> =
        try {
            execute(req)?.let {
                State.Success(data = it)
            } ?: State.Error(message = null, throwable = Exception())
        } catch (e: Exception) {
            State.Error(message = null, throwable = e)
        }

    abstract suspend fun execute(request: REQ): RES?
}