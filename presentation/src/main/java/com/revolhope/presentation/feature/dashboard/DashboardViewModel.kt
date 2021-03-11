package com.revolhope.presentation.feature.dashboard

import android.content.ContentResolver
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.revolhope.presentation.library.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor() : BaseViewModel() {

    companion object {
        private val regexSplit = Regex("[\\s]")
        private val regexClearWord = Regex("[^\\w]")
    }

    val wordCount: LiveData<Int> get() = _wordCount
    private var _wordCount = MutableLiveData<Int>()

    // TODO: Check Warnings
    fun processFileData(data: Intent, resolver: ContentResolver) {
        launch {
            withContext(Dispatchers.IO) {
                data.data?.let {
                    resolver.openInputStream(it)?.run {
                        val bytes = readBytes()
                        close()
                        processWords(String(bytes))
                    }
                }
            }
        }
    }

    // TODO: REMOVE this fun
    fun reset() {
        _wordCount.value = 0
    }

    private fun processWords(newData: String) {
        val words = filterWords(newData.split(regexSplit))
        _wordCount.postValue((wordCount.value ?: 0) + words.count())
    }

    private fun filterWords(words: List<String>): List<String> {
        val results = mutableListOf<String>()
        words.forEach { word ->
            if (word.isNotBlank() && !results.any { areSameWord(it, word) }) {
                results.add(word)
            }
        }
        return results
    }

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