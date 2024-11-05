package fhnw.emoba.thatsapp.ui


import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fhnw.emoba.thatsapp.model.ThatsAppModel
import kotlin.math.roundToInt


@Composable
fun AppUI(model: ThatsAppModel) {


    //starter code here can be removed completely

    var moved by remember { mutableStateOf(false) }
    val pxToMove = with(LocalDensity.current) {
        100.dp.toPx().roundToInt()
    }
    val offset by animateIntOffsetAsState(
        targetValue = if (moved) {
            IntOffset(pxToMove, pxToMove)
        } else {
            IntOffset(100, 100)
        },
        label = "offset"
    )


    with(model) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Blue)
        ) {
            Box(
                modifier = Modifier
                    .offset {
                        offset
                    }
                    .background(Color.White)
                    .border(
                        border = BorderStroke(
                            12.dp,
                            Brush.sweepGradient(
                                0.15f to Color.Magenta,
                                0.35f to Color.Red,
                                0.65f to Color.Yellow,
                                0.95f to Color.Red,
                                center = Offset(250.0f, 450.0f)
                            )
                        ), shape = RectangleShape
                    )
                    .size(250.dp)
                    .clickable {
                        moved = !moved
                    },
                contentAlignment = Alignment.Center,
            ) {
                Text(text = title, style = TextStyle(fontSize = 28.sp))
            }
        }

    }
}