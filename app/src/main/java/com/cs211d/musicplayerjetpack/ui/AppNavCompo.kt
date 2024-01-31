package com.cs211d.musicplayerjetpack.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

enum class MusicPlayerAppScreen(var title:String){
    PlayerScreen("Player"),
    SongListScreen("List"),
    FolderScreen("Folder")
}

// screens navigation composable setup
@Composable
fun MusicPlayerAppNav(
    appViewModel: AppViewModel = viewModel(factory = AppViewModel.Factory),
    navController: NavHostController = rememberNavController()
){
    NavHost(
        navController = navController,
        startDestination = MusicPlayerAppScreen.PlayerScreen.name,
        modifier = Modifier
    ){
        // player screen composable with buttons navigate to other screens
        composable(route = MusicPlayerAppScreen.PlayerScreen.name){
            PlayerScreen(
                appViewModel,
                onClickPlayer = {},
                onClickList = {
                    navController.navigate(MusicPlayerAppScreen.SongListScreen.name){
                        popUpTo(MusicPlayerAppScreen.PlayerScreen.name){
                            inclusive = true
                        }
                    }
                },
                onClickFolder = {
                    navController.navigate(MusicPlayerAppScreen.FolderScreen.name){
                        popUpTo(MusicPlayerAppScreen.PlayerScreen.name){
                            inclusive = true
                        }
                    }
                }
            )
        }

        // song list screen composable with buttons navigate to other screens
        composable(route = MusicPlayerAppScreen.SongListScreen.name){
            SongListScreen(
                appViewModel,
                onClickPlayer = {
                    navController.navigate(MusicPlayerAppScreen.PlayerScreen.name){
                        popUpTo(MusicPlayerAppScreen.SongListScreen.name){
                            inclusive = true
                        }
                    }
                },
                onClickList = {},
                onClickFolder = {
                    navController.navigate(MusicPlayerAppScreen.FolderScreen.name){
                        popUpTo(MusicPlayerAppScreen.SongListScreen.name){
                            inclusive = true
                        }
                    }
                }
            )
        }

        // folder screen composable with buttons navigate to other screens
        composable(route = MusicPlayerAppScreen.FolderScreen.name){
            FolderScreen(
                appViewModel,
                onClickPlayer = {
                    navController.navigate(MusicPlayerAppScreen.PlayerScreen.name){
                        popUpTo(MusicPlayerAppScreen.FolderScreen.name){
                            inclusive = true
                        }
                    }
                },
                onClickList = {
                    navController.navigate(MusicPlayerAppScreen.SongListScreen.name){
                        popUpTo(MusicPlayerAppScreen.FolderScreen.name){
                            inclusive = true
                        }
                    }
                },
                onClickFolder = {}
            )
        }
    }
}
