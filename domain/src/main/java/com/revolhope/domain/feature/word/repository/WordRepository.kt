package com.revolhope.domain.feature.word.repository

import com.revolhope.domain.feature.common.model.State
import com.revolhope.domain.feature.word.model.WordModel

interface WordRepository {

    suspend fun storeWords(rawWords: List<String>, fileName: String?): State<Pair<Int, List<WordModel>>>

    suspend fun fetchWords(limit: Int): State<List<WordModel>>

    suspend fun clearWords(): State<Boolean>
}