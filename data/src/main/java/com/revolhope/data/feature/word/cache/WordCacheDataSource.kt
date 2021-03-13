package com.revolhope.data.feature.word.cache

object WordCacheDataSource {

    private val wordsCache: MutableMap<String, MutableList<String>> = mutableMapOf()

    fun storeValidWords(fileName: String?, words: List<String>) {
        if (!wordsCache.containsKey(fileName)) {
            fileName?.let { wordsCache[it] = mutableListOf() }
        }
        wordsCache[fileName]?.clear()
        wordsCache[fileName]?.addAll(words.filter { it.trim().isNotBlank() })
    }

    fun fetchWords(limit: Int): List<Pair<String, List<String>>> {
        val result = mutableListOf<Pair<String, List<String>>>()
        var resultCount = 0
        wordsCache.keys.forEach { key ->
            when {
                resultCount + wordsCache[key]!!.size < limit -> {
                    result.add(key to wordsCache[key]!!)
                    resultCount += wordsCache[key]!!.size
                }
                resultCount < limit -> {
                    val toIndex = limit - resultCount
                    result.add(key to wordsCache[key]!!.subList(0, toIndex))
                    resultCount += toIndex
                }
                resultCount >= limit -> return@forEach
            }
        }
        return result
    }

    fun clear() {
        wordsCache.clear()
    }
}