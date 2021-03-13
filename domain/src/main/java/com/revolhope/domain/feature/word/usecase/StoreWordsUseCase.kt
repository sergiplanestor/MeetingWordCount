package com.revolhope.domain.feature.word.usecase

import com.revolhope.domain.feature.common.model.State
import com.revolhope.domain.feature.word.model.WordModel
import com.revolhope.domain.feature.word.repository.WordRepository
import javax.inject.Inject

class StoreWordsUseCase @Inject constructor(
    private val wordRepository: WordRepository
) {

    suspend operator fun invoke(req: Request): State<Pair<Int, List<WordModel>>> =
        wordRepository.storeWords(req.rawWords, req.fileName)

    data class Request(val rawWords: List<String>, val fileName: String?)
}