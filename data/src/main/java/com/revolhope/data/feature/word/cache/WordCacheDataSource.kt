package com.revolhope.data.feature.word.cache

/**
 * Words cache data source. It will be alive while app is running and will be destroyed with it.
 */
object WordCacheDataSource {

    /**
     * Map of FileName - List<Words>. Words are in raw mode.
     */
    private val wordsCache: MutableMap<String, MutableList<String>> = mutableMapOf()

    /**
     * Filters blank words and stores it in cache.
     * @param fileName name of the file.
     * @param words List of strings containing all read words from given file.
     * @return Total file words found.
     */
    fun storeValidWords(fileName: String?, words: List<String>): Int {
        if (!wordsCache.containsKey(fileName)) {
            fileName?.let { wordsCache[it] = mutableListOf() }
        }
        wordsCache[fileName]?.clear()
        wordsCache[fileName]?.addAll(words.filter { it.trim().isNotBlank() })
        return wordsCache[fileName]!!.count()
    }

    /**
     * Fetch words from cache.
     * @param limit maximum number of words returned.
     * @return List containing [Pair] of FileName as String and List of words as Strings.
     */
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

    /**
     * Clears cache.
     */
    fun clear() {
        wordsCache.clear()
    }
}