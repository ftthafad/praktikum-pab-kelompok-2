package com.travelwaka.app.data

import com.travelwaka.app.data.repository.BookmarkRepository
import com.travelwaka.app.datastore.TokenDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookmarkManager @Inject constructor(
    private val bookmarkRepository: BookmarkRepository,
    private val tokenDataStore: TokenDataStore
) {
    private val _bookmarkedIds = MutableStateFlow<Set<Int>>(emptySet())
    val bookmarkedIds: StateFlow<Set<Int>> = _bookmarkedIds.asStateFlow()

    private val externalScope = CoroutineScope(Dispatchers.IO)

    init {
        // Automatically sync bookmarks when user token changes (login/logout)
        externalScope.launch {
            tokenDataStore.token.collect { token ->
                if (token.isNullOrEmpty()) {
                    _bookmarkedIds.value = emptySet()
                } else {
                    loadBookmarks()
                }
            }
        }
    }

    suspend fun loadBookmarks() {
        try {
            val token = tokenDataStore.token.first()
            if (!token.isNullOrEmpty()) {
                val response = bookmarkRepository.getBookmarks()
                if (response.status) {
                    val ids = response.data.map { it.wisata.id }.toSet()
                    _bookmarkedIds.value = ids
                }
            } else {
                _bookmarkedIds.value = emptySet()
            }
        } catch (e: Exception) {
            // Fail silently or handle error
        }
    }

    suspend fun toggleBookmark(wisataId: Int): Result<Boolean> {
        return try {
            val token = tokenDataStore.token.first()
            if (token.isNullOrEmpty()) {
                return Result.failure(Exception("User belum login"))
            }
            val response = bookmarkRepository.toggleBookmark(wisataId)
            if (response.status) {
                val currentIds = _bookmarkedIds.value.toMutableSet()
                if (response.isBookmarked) {
                    currentIds.add(wisataId)
                } else {
                    currentIds.remove(wisataId)
                }
                _bookmarkedIds.value = currentIds
                Result.success(response.isBookmarked)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun isBookmarked(wisataId: Int): Boolean {
        return _bookmarkedIds.value.contains(wisataId)
    }
}
