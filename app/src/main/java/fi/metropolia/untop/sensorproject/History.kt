package fi.metropolia.untop.sensorproject

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun History(modifier: Modifier) {
    val list = arrayListOf(
        "1",
        "2",
        "3",
        "4",
        "5",
        "6",
        "7",
        "8",
        "9",
        "10",
        "11",
        "12",
        "13",
        "14",
        "15",
        "16",
        "17",
        "18",
        "19",
        "20"
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
                fontSize = 50.sp
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
                painter = painterResource(id = R.drawable.lynch),
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
