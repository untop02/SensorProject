package fi.metropolia.untop.sensorproject

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fi.metropolia.untop.sensorproject.ui.theme.Pink40

@Composable
fun History(modifier: Modifier) {
    val colors = listOf(Color.Green, Color.Cyan, Color.Red, Pink40)
    val currentFontSizePx = with(LocalDensity.current) { 70.dp.toPx() }
    val currentFontSizeDoublePx = currentFontSizePx * 2

    val infiniteTransition = rememberInfiniteTransition(label = "")
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = currentFontSizeDoublePx,
        animationSpec = infiniteRepeatable(tween(1000, easing = LinearEasing)), label = ""
    )
    val list = arrayListOf(
        "Test 1",
        "Test 2",
        "Test 3",
        "Test 4",
        "Test 5",
        "Test 6",
        "Test 7",
        "Test 8",
        "Test 9",
        "Test 10",
        "Test 11",
        "Test 12",
        "Test 13",
        "Test 14",
        "Test 15",
        "Test 16",
        "Test 17",
        "Test 18",
        "Test 19",
        "Test 20"
    )
    Box(
        modifier = modifier
            .fillMaxSize(),
    ) {
        Column(
            modifier = modifier.align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "History",
                modifier.padding(16.dp),
                fontFamily = FontFamily.Cursive,
                fontSize = 70.sp,
                style = TextStyle(
                    Brush.linearGradient(
                        colors = colors,
                        start = Offset(offset, offset),
                        end = Offset(offset + currentFontSizePx, offset + currentFontSizePx),
                        tileMode = TileMode.Mirror
                    )
                )
            )
            LazyColumn(
                modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                items(list) { item ->
                    ListItem(modifier, item = item)
                }
            }
        }
    }
}

@Composable
fun ListItem(modifier: Modifier, item: String) {
    Card(modifier.fillMaxWidth()) {
        Row {
            Image(
                painter = painterResource(id = R.drawable.cage),
                contentDescription = "David Lynch",
                contentScale = ContentScale.Crop,
                modifier = modifier
                    .padding(8.dp)
                    .width(50.dp)
                    .height(50.dp)
                    .clip(RoundedCornerShape(50.dp))
            )
            Column(
                modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 5.dp)
            ) {
                Text(text = item)
                Text(text = "Subtext")
            }
            Spacer(modifier = Modifier.weight(1f))
            Image(
                painter = painterResource(id = R.drawable.lynch),
                contentDescription = "David Lynch",
                contentScale = ContentScale.Crop,
                modifier = modifier
                    .width(55.dp)
                    .height(65.dp)
            )
        }
    }
}

@Preview
@Composable
fun ListItemPrev() {
    ListItem(modifier = Modifier, item = "Test")
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ListPrev() {
    History(modifier = Modifier)
}
