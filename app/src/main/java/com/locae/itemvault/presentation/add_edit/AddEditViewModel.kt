package com.locae.itemvault.presentation.add_edit

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.locae.itemvault.data.model.Item
import com.locae.itemvault.data.repository.ItemRepository
import com.locae.itemvault.utils.ImageHelper
import kotlinx.coroutines.launch
import java.io.File

class AddEditViewModel(
    private val repository: ItemRepository,
    private val context: Context
) : ViewModel() {

    var itemType by mutableStateOf("")
    var quantity by mutableStateOf("")
    var rating by mutableStateOf(0)
    var remarks by mutableStateOf("")
    var imagePaths = mutableStateListOf<String>()

    var isEditMode by mutableStateOf(false)
        private set
    var currentItemId = 0
        private set

    var validationError: String? by mutableStateOf(null)
        private set

    fun loadItem(itemId: Int) {
        if (itemId == -1) {
            isEditMode = false
            return
        }
        isEditMode = true
        currentItemId = itemId
        viewModelScope.launch {
            val item = repository.getItemById(itemId) ?: return@launch
            itemType = item.type
            quantity = item.quantity.toString()
            rating = item.rating
            remarks = item.remarks
            imagePaths.clear()
            imagePaths.addAll(ImageHelper.pathsToList(item.imagePaths))
        }
    }

    fun addImage(uri: Uri) {
        val path = ImageHelper.saveImageToInternal(context, uri)
        imagePaths.add(path)
    }

    fun removeImage(index: Int) {
        if (index in imagePaths.indices) {
            File(imagePaths[index]).delete()
            imagePaths.removeAt(index)
        }
    }

    fun validate(): String? {
        if (itemType.isBlank()) return "Item Type is required."
        if (itemType.length > 15) return "Item Type must be 15 characters or less."
        if (quantity.isBlank()) return "Quantity is required."
        if (quantity.toIntOrNull() == null || quantity.toInt() <= 0) return "Enter a valid quantity."
        if (rating == 0) return "Please provide a star rating."
        if (imagePaths.isEmpty()) return "At least one photo is required."
        if (remarks.length > 200) return "Remarks must be 200 characters or less."
        return null
    }

    fun saveItem(): Boolean {
        val error = validate()
        if (error != null) {
            validationError = error
            return false
        }
        validationError = null
        viewModelScope.launch {
            val item = Item(
                id = if (isEditMode) currentItemId else 0,
                type = itemType.trim(),
                quantity = quantity.trim().toInt(),
                rating = rating,
                remarks = remarks.trim(),
                imagePaths = ImageHelper.listToPaths(imagePaths.toList()),
                isSynced = false
            )
            repository.saveItem(item)
        }
        return true
    }

    fun deleteItem(onDeleted: () -> Unit) {
        if (!isEditMode) return
        viewModelScope.launch {
            val item = repository.getItemById(currentItemId) ?: return@launch
            ImageHelper.pathsToList(item.imagePaths).forEach { File(it).delete() }
            repository.deleteItem(item)
            onDeleted()
        }
    }

    fun clearValidationError() {
        validationError = null
    }
}
