package com.locae.itemvault.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class Item(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String,           // Item name, max 15 chars
    val quantity: Int,
    val rating: Int,            // 1 to 5
    val remarks: String = "",   // Optional, max 200 chars
    val imagePaths: String = "", // Comma-separated list of saved image file paths
    val isSynced: Boolean = false // false = not yet pushed to backend
)
