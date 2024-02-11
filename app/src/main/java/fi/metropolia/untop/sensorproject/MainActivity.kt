package fi.metropolia.untop.sensorproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import fi.metropolia.untop.sensorproject.ui.theme.SensorProjectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
                MyApp()
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}
@Composable
fun MyApp() {
    val navController = rememberNavController()

    Surface() {
        Column {
            Spacer(modifier = Modifier.weight(1f))
            NavigationHost(navController)
            BottomNavigationBar(navController)
        }

    }
}
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SensorProjectTheme {
        Greeting("Android")
    }
}