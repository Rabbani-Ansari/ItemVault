package com.locae.itemvault.data.repository

import com.locae.itemvault.data.database.ItemDao
import com.locae.itemvault.data.model.Item
import kotlinx.coroutines.flow.Flow

class ItemRepository(private val itemDao: ItemDao) {
    fun getAllItems(): Flow<List<Item>> = itemDao.getAllItems()

    suspend fun getItemById(id: Int): Item? = itemDao.getItemById(id)

    suspend fun saveItem(item: Item) {
        if (item.id == 0) itemDao.insertItem(item)
        else itemDao.updateItem(item)
    }

    suspend fun deleteItem(item: Item) = itemDao.deleteItem(item)

    suspend fun getUnsyncedItems(): List<Item> = itemDao.getUnsyncedItems()
}
