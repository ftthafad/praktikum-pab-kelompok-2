package com.travelwaka.app.data.repository

import com.travelwaka.app.network.model.BookmarkListResponse
import com.travelwaka.app.network.model.BookmarkStatusResponse

interface BookmarkRepository {
    suspend fun getBookmarks(): BookmarkListResponse
    suspend fun toggleBookmark(wisataId: Int): BookmarkStatusResponse
    suspend fun checkBookmark(wisataId: Int): BookmarkStatusResponse
}
