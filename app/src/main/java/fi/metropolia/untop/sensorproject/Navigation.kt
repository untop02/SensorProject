package fi.metropolia.untop.sensorproject

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Destinations(val route: String) {
    data object Home : Destinations("home_route")
    data object History : Destinations("history_route")
    data object Settings : Destinations("profile_route")
    data object Graph : Destinations("graph_route")
}

data class BottomNavigationItem(
    val label: String = "",
    val icon: ImageVector = Icons.Filled.Home,
    val route: String = ""
) {
    fun bottomNavigationItems(): List<BottomNavigationItem> {
        return listOf(
            BottomNavigationItem(
                label = "Home",
                icon = Icons.Default.Home,
                route = Destinations.Home.route
            ),
            BottomNavigationItem(
                label = "History",
                icon = Icons.Filled.DateRange,
                route = Destinations.History.route
            ),
            BottomNavigationItem(
                label = "Settings",
                icon = Icons.Filled.Settings,
                route = Destinations.Settings.route
            ),
        )
    }
}