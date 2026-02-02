package com.locae.itemvault.presentation.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.locae.itemvault.data.model.Item
import com.locae.itemvault.data.repository.ItemRepository
import com.locae.itemvault.data.repository.SyncManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: ItemRepository) : ViewModel() {
    
    val items: Flow<List<Item>> = repository.getAllItems()
    
    var isSyncing by mutableStateOf(false)
        private set
    
    var syncMessage by mutableStateOf<String?>(null)
        private set
    
    fun sync() {
        viewModelScope.launch {
            isSyncing = true
            syncMessage = "Syncing..."
            try {
                SyncManager.syncItems(repository)
                syncMessage = "Sync complete"
            } catch (e: Exception) {
                syncMessage = "Sync failed"
            } finally {
                isSyncing = false
            }
        }
    }
    
    fun clearSyncMessage() {
        syncMessage = null
    }
}
