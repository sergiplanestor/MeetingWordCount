package com.revolhope.data.feature.word.repositoryimpl

import com.revolhope.data.common.base.BaseRepositoryImpl
import com.revolhope.data.feature.word.cache.WordCacheDataSource
import com.revolhope.data.feature.word.mapper.WordMapper
import com.revolhope.data.feature.word.util.WordProcessorUtil
import com.revolhope.domain.feature.common.model.State
import com.revolhope.domain.feature.word.model.WordModel
import com.revolhope.domain.feature.word.repository.WordRepository
import javax.inject.Inject

class WordRepositoryImpl @Inject constructor() : WordRepository, BaseRepositoryImpl() {

    override suspend fun storeWords(
        rawWords: List<String>,
        fileName: String?
    ): State<List<WordModel>> =
        statefulAction {
            WordCacheDataSource.storeValidWords(fileName, rawWords)
            processWords(WordCacheDataSource.fetchWords(50))
        }

    override suspend fun fetchWords(limit: Int): State<List<WordModel>> =
        statefulAction {
            processWords(WordCacheDataSource.fetchWords(limit))
        }

    private fun processWords(list: List<Pair<String, List<String>>>): List<WordModel> {
        val processedWords = mutableListOf<WordModel>()
        list.forEach {
            processedWords.addAll(
                WordProcessorUtil.filterWords(it.second).map { entry ->
                    WordMapper.fromProcessedWordToModel(entry, it.first)
                }
            )
        }
        return processedWords
    }
}