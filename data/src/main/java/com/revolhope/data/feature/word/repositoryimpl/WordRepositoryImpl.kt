package com.revolhope.data.feature.word.repositoryimpl

import com.revolhope.data.common.base.BaseRepositoryImpl
import com.revolhope.data.feature.word.cache.WordCacheDataSource
import com.revolhope.data.feature.word.mapper.WordMapper
import com.revolhope.data.feature.word.util.WordProcessorUtil
import com.revolhope.domain.feature.common.model.State
import com.revolhope.domain.feature.word.model.WordModel
import com.revolhope.domain.feature.word.repository.WordRepository
import javax.inject.Inject

/**
 * Word repository implementation.
 */
class WordRepositoryImpl @Inject constructor() : WordRepository, BaseRepositoryImpl() {

    /**
     * Stores words read from file.
     * @param rawWords list of words read.
     * @param fileName unparsed file name.
     * @return [Pair] of total number words in file as first param and [List] of [WordModel] as second.
     * This model is wrapped into [State]
     */
    override suspend fun storeWords(
        rawWords: List<String>,
        fileName: String?
    ): State<Pair<Int, List<WordModel>>> =
        statefulAction {
            val totalWords = WordCacheDataSource.storeValidWords(fileName, rawWords)
            totalWords to processWords(WordCacheDataSource.fetchWords(50))
        }

    /**
     * Fetch words from cache.
     * @param limit max. number of words to be fetched.
     * @return [List] of [WordModel] wrapped into [State].
     */
    override suspend fun fetchWords(limit: Int): State<List<WordModel>> =
        statefulAction {
            processWords(WordCacheDataSource.fetchWords(limit))
        }

    /**
     * Clears data from cache.
     */
    override suspend fun clearWords(): State<Boolean> =
        statefulAction {
            WordCacheDataSource.clear()
            true
        }

    /**
     * Util method to process words. @see [WordProcessorUtil] for further info.
     */
    private fun processWords(list: List<Pair<String, List<String>>>): List<WordModel> {
        val processedWords = mutableListOf<WordModel>()
        list.forEach {
            processedWords.addAll(
                WordProcessorUtil.filterWords(it.second).map { pair ->
                    WordMapper.fromProcessedWordToModel(pair, it.first)
                }
            )
        }
        return processedWords
    }
}