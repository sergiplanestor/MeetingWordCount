package com.revolhope.data.injection

import com.revolhope.data.feature.word.repositoryimpl.WordRepositoryImpl
import com.revolhope.domain.feature.word.repository.WordRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindWordRepository(
        wordRepositoryImpl: WordRepositoryImpl
    ): WordRepository

}