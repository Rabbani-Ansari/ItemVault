package com.locae.itemvault.presentation.add_edit

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.locae.itemvault.presentation.components.FullScreenImageViewer
import com.locae.itemvault.presentation.components.ImagePickerBottomSheet
import com.locae.itemvault.presentation.components.StarRatingRow
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditScreen(
    navController: NavController,
    itemId: Int,
    viewModel: AddEditViewModel,
    windowWidthSizeClass: WindowWidthSizeClass = WindowWidthSizeClass.Compact
) {
    val context = LocalContext.current

    LaunchedEffect(itemId) {
        viewModel.loadItem(itemId)
    }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showImagePicker by remember { mutableStateOf(false) }
    var showFullScreenViewer by remember { mutableStateOf(false) }
    var fullScreenStartIndex by remember { mutableStateOf(0) }

    // Camera URI for taking photos
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.addImage(it) }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && photoUri != null) {
            viewModel.addImage(photoUri!!)
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val file = createImageFile(context)
            photoUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            cameraLauncher.launch(photoUri!!)
        }
    }

    fun takePhoto() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            val file = createImageFile(context)
            photoUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            cameraLauncher.launch(photoUri!!)
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Item") },
            text = { Text("Are you sure you want to delete this item? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteItem {
                            navController.popBackStack()
                        }
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Image picker bottom sheet
    if (showImagePicker) {
        ImagePickerBottomSheet(
            onDismiss = { showImagePicker = false },
            onTakePhoto = { takePhoto() },
            onChooseFromGallery = { galleryLauncher.launch("image/*") }
        )
    }

    // Full screen image viewer
    if (showFullScreenViewer && viewModel.imagePaths.isNotEmpty()) {
        FullScreenImageViewer(
            imagePaths = viewModel.imagePaths.toList(),
            startIndex = fullScreenStartIndex,
            onDismiss = { showFullScreenViewer = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (viewModel.isEditMode) "Edit Item" else "Add Item") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (viewModel.isEditMode) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                Icons.Filled.Delete,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        if (windowWidthSizeClass == WindowWidthSizeClass.Compact) {
            // Portrait layout: single column
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                FormContent(
                    viewModel = viewModel,
                    onAddPhotoClick = { showImagePicker = true },
                    onImageClick = { index ->
                        fullScreenStartIndex = index
                        showFullScreenViewer = true
                    },
                    onSaveClick = {
                        if (viewModel.saveItem()) {
                            navController.popBackStack()
                        }
                    }
                )
            }
        } else {
            // Landscape layout: two columns
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Left column: Images, Item Type, Quantity
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    ImageSection(
                        imagePaths = viewModel.imagePaths,
                        onRemoveImage = { viewModel.removeImage(it) },
                        onImageClick = { index ->
                            fullScreenStartIndex = index
                            showFullScreenViewer = true
                        },
                        onAddPhotoClick = { showImagePicker = true }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    ItemTypeField(
                        value = viewModel.itemType,
                        onValueChange = { if (it.length <= 15) viewModel.itemType = it }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    QuantityField(
                        value = viewModel.quantity,
                        onValueChange = { viewModel.quantity = it }
                    )
                }

                // Right column: Rating, Remarks, Save
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    RatingSection(
                        rating = viewModel.rating,
                        onRatingChanged = { viewModel.rating = it }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    RemarksField(
                        value = viewModel.remarks,
                        onValueChange = { if (it.length <= 200) viewModel.remarks = it }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    ValidationAndSave(
                        error = viewModel.validationError,
                        onSaveClick = {
                            if (viewModel.saveItem()) {
                                navController.popBackStack()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun FormContent(
    viewModel: AddEditViewModel,
    onAddPhotoClick: () -> Unit,
    onImageClick: (Int) -> Unit,
    onSaveClick: () -> Unit
) {
    ImageSection(
        imagePaths = viewModel.imagePaths,
        onRemoveImage = { viewModel.removeImage(it) },
        onImageClick = onImageClick,
        onAddPhotoClick = onAddPhotoClick
    )

    Spacer(modifier = Modifier.height(24.dp))

    ItemTypeField(
        value = viewModel.itemType,
        onValueChange = { if (it.length <= 15) viewModel.itemType = it }
    )

    Spacer(modifier = Modifier.height(16.dp))

    QuantityField(
        value = viewModel.quantity,
        onValueChange = { viewModel.quantity = it }
    )

    Spacer(modifier = Modifier.height(16.dp))

    RatingSection(
        rating = viewModel.rating,
        onRatingChanged = { viewModel.rating = it }
    )

    Spacer(modifier = Modifier.height(16.dp))

    RemarksField(
        value = viewModel.remarks,
        onValueChange = { if (it.length <= 200) viewModel.remarks = it }
    )

    Spacer(modifier = Modifier.height(24.dp))

    ValidationAndSave(
        error = viewModel.validationError,
        onSaveClick = onSaveClick
    )
}

@Composable
private fun ImageSection(
    imagePaths: List<String>,
    onRemoveImage: (Int) -> Unit,
    onImageClick: (Int) -> Unit,
    onAddPhotoClick: () -> Unit
) {
    Text(
        text = "Photos",
        style = MaterialTheme.typography.titleMedium
    )
    Spacer(modifier = Modifier.height(8.dp))
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        imagePaths.forEachIndexed { index, path ->
            Box(
                modifier = Modifier.size(90.dp)
            ) {
                AsyncImage(
                    model = File(path),
                    contentDescription = "Image ${index + 1}",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(
                            interactionSource = null,
                            indication = null,
                            onClick = { onImageClick(index) }
                        ),
                    contentScale = ContentScale.Crop
                )
                // Delete badge
                IconButton(
                    onClick = { onRemoveImage(index) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(24.dp)
                        .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                ) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = "Remove",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
        
        Card(
            modifier = Modifier
                .size(90.dp)
                .clickable(
                    interactionSource = null,
                    indication = null,
                    onClick = { onAddPhotoClick() }
                ),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "Add Photo",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Add",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun ItemTypeField(
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Item Type") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        supportingText = { Text("${value.length} / 15") }
    )
}

@Composable
private fun QuantityField(
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Quantity") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}

@Composable
private fun RatingSection(
    rating: Int,
    onRatingChanged: (Int) -> Unit
) {
    Text(
        text = "Rating",
        style = MaterialTheme.typography.titleMedium
    )
    Spacer(modifier = Modifier.height(8.dp))
    StarRatingRow(
        rating = rating,
        onRatingChanged = onRatingChanged
    )
}

@Composable
private fun RemarksField(
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Remarks (optional)") },
        modifier = Modifier.fillMaxWidth(),
        minLines = 3,
        maxLines = 4,
        supportingText = { Text("${value.length} / 200") }
    )
}

@Composable
private fun ValidationAndSave(
    error: String?,
    onSaveClick: () -> Unit
) {
    if (error != null) {
        Text(
            text = error,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
    }

    Button(
        onClick = onSaveClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Save")
    }
}

private fun createImageFile(context: android.content.Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) ?: context.filesDir
    return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
}
