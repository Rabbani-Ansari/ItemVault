package com.locae.itemvault.data.repository

import com.locae.itemvault.api.RetrofitClient

object SyncManager {

    suspend fun syncItems(repository: ItemRepository) {
        try {
            // --- PULL: fetch all items from backend and save locally ---
            val remoteItems = RetrofitClient.apiService.getItems()
            remoteItems.forEach { item ->
                repository.saveItem(item.copy(isSynced = true))
            }

            // --- PUSH: upload all unsynced local items ---
            val unsyncedItems = repository.getUnsyncedItems()
            unsyncedItems.forEach { item ->
                val saved = RetrofitClient.apiService.postItem(item)
                // Mark as synced locally
                repository.saveItem(saved.copy(isSynced = true))
            }
        } catch (e: Exception) {
            // If network fails, do nothing — data stays in Room for next sync attempt
            e.printStackTrace()
            throw e
        }
    }
}
