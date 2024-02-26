package fi.metropolia.untop.sensorproject

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fi.metropolia.untop.sensorproject.ui.theme.Pink40

@Composable
fun Settings(modifier: Modifier) {
    val colors = listOf(Color.Green, Color.Cyan, Color.Red, Pink40)
    val currentFontSizePx = with(LocalDensity.current) { 50.dp.toPx() }
    val currentFontSizeDoublePx = currentFontSizePx * 2
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = currentFontSizeDoublePx,
        animationSpec = infiniteRepeatable(tween(1000, easing = LinearEasing)), label = ""
    )
    Column(modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)) {
        repeat(4) {
            var checked by rememberSaveable { mutableStateOf(false) }
            ElevatedCard(
                Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                )
            ) {
                Row(
                    Modifier.clip(shape = RoundedCornerShape(500.dp))
                ) {
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
                        Text(
                            text = "Setting",
                            fontWeight = FontWeight.Black,
                            style = if (checked) {
                                TextStyle(
                                    Brush.linearGradient(
                                        colors = colors,
                                        start = Offset(offset, offset),
                                        end = Offset(
                                            offset + currentFontSizePx,
                                            offset + currentFontSizePx
                                        ),
                                        tileMode = TileMode.Mirror
                                    )
                                )
                            } else {
                                TextStyle()
                            }
                        )
                        Text(
                            text = "Setting Description",
                            style = if (checked) {
                                TextStyle(
                                    Brush.linearGradient(
                                        colors = colors,
                                        start = Offset(offset, offset),
                                        end = Offset(
                                            offset + currentFontSizePx,
                                            offset + currentFontSizePx
                                        ),
                                        tileMode = TileMode.Mirror
                                    )
                                )
                            } else {
                                TextStyle()
                            }
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Switch(
                        checked = checked,
                        onCheckedChange = {
                            checked = it
                        },
                        modifier
                            .align(Alignment.CenterVertically)
                            .padding(end = 5.dp),
                        thumbContent = {
                            if (checked) {
                                Icon(
                                    imageVector = Icons.Outlined.Check,
                                    contentDescription = null
                                )
                            }
                        }
                    )
                }
            }
            Spacer(modifier.padding(5.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SettingsPrev() {
    Settings(modifier = Modifier)
}