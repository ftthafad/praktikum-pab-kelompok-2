package com.travelwaka.app.di

import com.travelwaka.app.data.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindWisataRepository(
        wisataRepositoryImpl: WisataRepositoryImpl
    ): WisataRepository

    @Binds
    @Singleton
    abstract fun bindBookmarkRepository(
        bookmarkRepositoryImpl: BookmarkRepositoryImpl
    ): BookmarkRepository

    @Binds
    @Singleton
    abstract fun bindReviewRepository(
        reviewRepositoryImpl: ReviewRepositoryImpl
    ): ReviewRepository

    @Binds
    @Singleton
    abstract fun bindPengajuanRepository(
        pengajuanRepositoryImpl: PengajuanRepositoryImpl
    ): PengajuanRepository

    @Binds
    @Singleton
    abstract fun bindPengelolaRepository(
        pengelolaRepositoryImpl: PengelolaRepositoryImpl
    ): PengelolaRepository
}
