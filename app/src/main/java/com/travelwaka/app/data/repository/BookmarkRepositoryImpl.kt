package com.travelwaka.app.data.repository

import com.travelwaka.app.network.ApiService
import com.travelwaka.app.network.model.BookmarkListResponse
import com.travelwaka.app.network.model.BookmarkStatusResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookmarkRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : BookmarkRepository {
    override suspend fun getBookmarks(): BookmarkListResponse = apiService.getBookmarks()
    override suspend fun toggleBookmark(wisataId: Int): BookmarkStatusResponse = apiService.toggleBookmark(wisataId)
    override suspend fun checkBookmark(wisataId: Int): BookmarkStatusResponse = apiService.checkBookmark(wisataId)
}
