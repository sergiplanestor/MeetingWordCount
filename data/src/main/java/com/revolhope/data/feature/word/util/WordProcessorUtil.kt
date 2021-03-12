package com.revolhope.data.feature.word.util

object WordProcessorUtil {

    private val regexClearWord = Regex("[^\\w]")

    fun filterWords(words: List<String>): List<Pair<String, Int>> {
        val results = mutableListOf<Pair<String, Int>>()
        words.forEach { word ->
            if (word.isNotBlank() && !results.any { areSameWord(it.first, word) }) {
                results.add(clearWord(word) to words.count { areSameWord(it, word) })
            }
        }
        return results
    }

    private fun clearWord(word: String): String = word.replace(
        regex = regexClearWord,
        replacement = ""
    )

    private fun areSameWord(word: String, anotherWord: String) =
        clearWord(word).equals(other = clearWord(anotherWord), ignoreCase = true)

}