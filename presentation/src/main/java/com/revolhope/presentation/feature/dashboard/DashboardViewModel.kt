package com.revolhope.presentation.feature.dashboard

import android.content.ContentResolver
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.revolhope.domain.feature.word.model.WordModel
import com.revolhope.domain.feature.word.usecase.FetchWordsUseCase
import com.revolhope.domain.feature.word.usecase.StoreWordsUseCase
import com.revolhope.presentation.library.base.BaseViewModel
import com.revolhope.presentation.library.extensions.readAsync
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val storeWordsUseCase: StoreWordsUseCase,
    private val fetchWordsUseCase: FetchWordsUseCase
) : BaseViewModel() {

    companion object {
        private val regexSplit = Regex("[\\s]")
        private const val ITEMS_PER_PAGE = 50
    }

    private var currentPage: Int = 1
    private val limit get() = currentPage * ITEMS_PER_PAGE

    override val errorLiveData get() = _errorLiveData
    private val _errorLiveData = MutableLiveData<String>()

    val wordsLiveData: LiveData<List<WordModel>> get() = _wordsLiveData
    private val _wordsLiveData = MutableLiveData<List<WordModel>>()

    fun processFileData(data: Intent, resolver: ContentResolver) {
        if (data.data != null) {
            val uri = data.data
            launch {
                val content = resolver.readAsync(uri)
                if (content != null) {
                    withContext(Dispatchers.IO) {
                        storeWordsUseCase.invoke(
                            StoreWordsUseCase.Request(
                                rawWords = content.split(regexSplit),
                                fileName = uri?.lastPathSegment
                            )
                        )
                    }.also {
                        handleState(
                            state = it,
                            onSuccess = { words -> _wordsLiveData.value = words }
                        )
                    }
                } else {
                    // TODO: Change literal
                    _errorLiveData.value = "T_Generic error"
                }
            }
        } else {
            // TODO: Change literal
            _errorLiveData.value = "T_Generic error"
        }
    }

    fun fetchNextPage() {
        currentPage++
        launch {
            withContext(Dispatchers.IO) {
                fetchWordsUseCase.invoke(
                    FetchWordsUseCase.Request(limit)
                )
            }.also {
                handleState(
                    state = it,
                    onSuccess = { words -> _wordsLiveData.value = words }
                )
            }
        }
    }

    fun applyFilter(query: String): List<WordModel>? =
        if (query.isBlank()) {
            _wordsLiveData.value
        } else {
            _wordsLiveData.value?.filter { it.word.contains(query, ignoreCase = true) }
        }
}