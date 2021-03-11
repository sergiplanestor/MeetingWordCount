package com.revolhope.data.feature.word.repositoryimpl

import com.revolhope.data.common.base.BaseRepositoryImpl
import com.revolhope.data.feature.word.mapper.WordMapper
import com.revolhope.data.feature.word.util.WordProcessorUtil
import com.revolhope.domain.feature.common.model.State
import com.revolhope.domain.feature.word.model.WordModel
import com.revolhope.domain.feature.word.repository.WordRepository
import javax.inject.Inject

class WordRepositoryImpl @Inject constructor() : WordRepository, BaseRepositoryImpl() {

    override suspend fun getProcessedWords(
        rawWords: List<String>,
        fileName: String?
    ): State<List<WordModel>> =
        statefulAction {
            val processedWords = WordProcessorUtil.filterWords(rawWords)
            processedWords.map { WordMapper.fromProcessedWordToModel(it, fileName) }
        }
}