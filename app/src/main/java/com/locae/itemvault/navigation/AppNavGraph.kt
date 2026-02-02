package com.locae.itemvault.navigation

import android.content.Context
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.locae.itemvault.data.database.ItemDatabase
import com.locae.itemvault.data.repository.ItemRepository
import com.locae.itemvault.presentation.add_edit.AddEditScreen
import com.locae.itemvault.presentation.add_edit.AddEditViewModel
import com.locae.itemvault.presentation.home.HomeScreen
import com.locae.itemvault.presentation.home.HomeViewModel

@Composable
fun AppNavGraph(windowWidthSizeClass: WindowWidthSizeClass) {
    val navController = rememberNavController()
    val context = LocalContext.current

    // Create repository instance
    val repository = remember {
        val db = ItemDatabase.getInstance(context)
        ItemRepository(db.itemDao())
    }

    // Create HomeViewModel
    val homeViewModel = remember { HomeViewModel(repository) }

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                navController = navController,
                viewModel = homeViewModel,
                windowWidthSizeClass = windowWidthSizeClass
            )
        }
        composable(
            route = "add_edit/{itemId}",
            arguments = listOf(
                navArgument("itemId") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getInt("itemId") ?: -1
            // Create a new AddEditViewModel for each navigation
            val addEditViewModel = remember(itemId) {
                AddEditViewModel(repository, context)
            }
            AddEditScreen(
                navController = navController,
                itemId = itemId,
                viewModel = addEditViewModel,
                windowWidthSizeClass = windowWidthSizeClass
            )
        }
    }
}
