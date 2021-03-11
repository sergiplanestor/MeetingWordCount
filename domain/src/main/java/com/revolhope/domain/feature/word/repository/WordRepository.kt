package com.revolhope.domain.feature.word.repository

import com.revolhope.domain.feature.common.model.State
import com.revolhope.domain.feature.word.model.WordModel

interface WordRepository {

    suspend fun getProcessedWords(rawWords: List<String>, fileName: String?): State<List<WordModel>>

}