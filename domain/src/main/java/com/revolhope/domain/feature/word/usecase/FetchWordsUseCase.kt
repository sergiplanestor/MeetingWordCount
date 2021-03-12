package com.revolhope.domain.feature.word.usecase

import com.revolhope.domain.feature.common.model.State
import com.revolhope.domain.feature.word.model.WordModel
import com.revolhope.domain.feature.word.repository.WordRepository
import javax.inject.Inject

class FetchWordsUseCase @Inject constructor(
    private val wordRepository: WordRepository
) {
    suspend operator fun invoke(req: Request): State<List<WordModel>> =
        wordRepository.fetchWords(req.limit)

    data class Request(val limit: Int)
}