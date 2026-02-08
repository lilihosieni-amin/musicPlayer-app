package com.example.liliplayer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.liliplayer.playback.PlaybackController
import com.example.liliplayer.ui.components.MiniPlayer
import com.example.liliplayer.ui.navigation.NavGraph
import com.example.liliplayer.ui.navigation.Screen
import com.example.liliplayer.ui.theme.*
import kotlinx.coroutines.launch

data class DrawerItem(
    val screen: Screen,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(playbackController: PlaybackController) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val drawerItems = listOf(
        DrawerItem(Screen.Songs, "Songs", Icons.Filled.MusicNote, Icons.Outlined.MusicNote),
        DrawerItem(Screen.Albums, "Albums", Icons.Filled.Album, Icons.Outlined.Album),
        DrawerItem(Screen.Artists, "Artists", Icons.Filled.Person, Icons.Outlined.Person),
        DrawerItem(Screen.Playlists, "Playlists", Icons.Filled.QueueMusic, Icons.Outlined.QueueMusic),
        DrawerItem(Screen.Genres, "Genres", Icons.Filled.Category, Icons.Outlined.Category),
        DrawerItem(Screen.Tags, "Tags", Icons.Filled.Label, Icons.Outlined.Label),
        DrawerItem(Screen.Favorites, "Favorites", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder)
    )

    val mainRoutes = drawerItems.map { it.screen.route }
    val isMainScreen = mainRoutes.any { it == currentDestination?.route }
    val playbackState by playbackController.playbackState.collectAsState()

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Cycle colors for drawer items
    val itemColors = listOf(MintGreen, SoftPink, SkyBlue, Lavender, SunnyYellow, MintGreen, Secondary)

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = isMainScreen,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = BgCard,
                drawerShape = RoundedCornerShape(topEnd = 32.dp, bottomEnd = 32.dp)
            ) {
                // Drawer header with gradient
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Primary, Accent)
                            )
                        )
                        .padding(horizontal = 24.dp, vertical = 28.dp)
                ) {
                    Text(
                        "Lili Player",
                        style = MaterialTheme.typography.headlineMedium,
                        color = BgCard
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                drawerItems.forEachIndexed { index, item ->
                    val selected = currentDestination?.hierarchy?.any { it.route == item.screen.route } == true
                    val itemAccent = itemColors[index % itemColors.size]
                    NavigationDrawerItem(
                        icon = {
                            Icon(
                                if (selected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label
                            )
                        },
                        label = { Text(item.label) },
                        selected = selected,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(item.screen.route) {
                                popUpTo(Screen.Songs.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                        shape = RoundedCornerShape(20.dp),
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = itemAccent.copy(alpha = 0.25f),
                            selectedIconColor = Primary,
                            selectedTextColor = TextPrimary,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextPrimary
                        )
                    )
                }
            }
        }
    ) {
        Scaffold(
            containerColor = BgMain,
            topBar = {
                if (isMainScreen) {
                    TopAppBar(
                        title = {
                            Text(
                                "Lili Player",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Primary)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = BgMain),
                        actions = {
                            IconButton(onClick = { navController.navigate(Screen.Search.route) }) {
                                Icon(Icons.Default.Search, contentDescription = "Search", tint = Primary)
                            }
                            IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = TextSecondary)
                            }
                        }
                    )
                }
            },
            bottomBar = {
                if (isMainScreen && playbackState.currentSong != null) {
                    MiniPlayer(
                        playbackState = playbackState,
                        onPlayPause = { playbackController.playPause() },
                        onNext = { playbackController.next() },
                        onPrevious = { playbackController.previous() },
                        onClick = { navController.navigate(Screen.NowPlaying.route) }
                    )
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                NavGraph(
                    navController = navController,
                    playbackController = playbackController
                )
            }
        }
    }
}
