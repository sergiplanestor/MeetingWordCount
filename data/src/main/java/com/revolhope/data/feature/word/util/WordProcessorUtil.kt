package com.revolhope.data.feature.word.util

object WordProcessorUtil {

    private val regexClearWord = Regex("[^\\w]")

    fun filterWords(words: List<String>): Map<String, List<String>> {
        val results = mutableMapOf<String, List<String>>()
        words.forEach { word ->
            if (word.isNotBlank() && !results.keys.any { filterPredicate(it, word) }) {
                results[word] = words.filter { filterPredicate(it, word) }
            }
        }
        return results
    }

    private fun filterPredicate(word: String, anotherWord: String) =
        word.hashCode() != anotherWord.hashCode() && areSameWord(word, anotherWord)

    private fun areSameWord(word: String, anotherWord: String) =
        word.replace(
            regex = regexClearWord,
            replacement = ""
        ).equals(
            other = anotherWord.replace(
                regex = regexClearWord,
                replacement = ""
            ),
            ignoreCase = true
        )

}