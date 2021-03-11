package com.revolhope.presentation.feature.dashboard

import android.content.ContentResolver
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.revolhope.domain.feature.word.model.WordModel
import com.revolhope.domain.feature.word.usecase.GetProcessedWordsUseCase
import com.revolhope.presentation.library.base.BaseViewModel
import com.revolhope.presentation.library.extensions.readAsync
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getProcessedWordsUseCase: GetProcessedWordsUseCase
) : BaseViewModel() {

    companion object {
        private val regexSplit = Regex("[\\s]")
    }

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
                        getProcessedWordsUseCase.invoke(
                            GetProcessedWordsUseCase.Request(
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
}