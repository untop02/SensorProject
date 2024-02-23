package fi.metropolia.untop.sensorproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fi.metropolia.untop.sensorproject.ui.theme.SensorProjectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Main()
        }
    }
}

@Composable
fun Main() {
    val navController = rememberNavController()
    var navigationSelectedItem by remember { mutableIntStateOf(0) }
    SensorProjectTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
                bottomBar = {
                    BottomAppBar(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.primary,
                    ) {
                        BottomNavigationItem().bottomNavigationItems()
                            .forEachIndexed { index, navigationItem ->
                                NavigationBarItem(
                                    selected = index == navigationSelectedItem,
                                    label = {
                                        Text(navigationItem.label)
                                    },
                                    icon = {
                                        Icon(
                                            navigationItem.icon,
                                            contentDescription = navigationItem.label
                                        )
                                    },
                                    onClick = {
                                        navigationSelectedItem = index
                                        navController.navigate(navigationItem.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )
                            }
                    }
                },
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = Screens.Home.route,
                    modifier = Modifier.padding(paddingValues = innerPadding),
                    //MIKSI NÄMÄ EI POISTA TUOTA YHTÄ ANIMAATIOTA 💩
                    popEnterTransition = { EnterTransition.None},
                    popExitTransition = { ExitTransition.None},
                    enterTransition = { EnterTransition.None},
                    exitTransition = { ExitTransition.None}
                ) {
                    composable(Screens.Home.route) {
                        Home(modifier = Modifier)
                    }
                    composable(Screens.Search.route) {
                    }
                    composable(Screens.Settings.route) {
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SensorProjectTheme {
        Main()
    }
}