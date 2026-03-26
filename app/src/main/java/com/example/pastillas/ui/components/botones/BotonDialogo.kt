package com.example.pastillas.ui.components.botones

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.innerShadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.sp


@Composable
fun BotonDialogo(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    width: Dp = 250.dp,
    height: Dp = 60.dp
) {
    val shadowColor = Color(0x40000000)
    var pressed by remember { mutableStateOf(false) }

    val animatedWidth by animateDpAsState(if (pressed) width - 10.dp else width)
    val animatedHeight by animateDpAsState(if (pressed) height - 10.dp else height)
    val outerShadowRadius by animateDpAsState(if (pressed) 0.dp else 10.dp)
    val outerShadowSpread by animateDpAsState(if (pressed) 0.dp else 6.dp)
    val outerShadowOffsetX by animateDpAsState(if (pressed) 0.dp else 4.dp)
    val outerShadowOffsetY by animateDpAsState(if (pressed) 0.dp else 4.dp)

    val innerShadowRadius by animateDpAsState(if (pressed) 10.dp else 0.dp)
    val innerShadowSpread by animateDpAsState(if (pressed) 2.dp else 0.dp)
    val innerShadowOffsetX by animateDpAsState(if (pressed) 6.dp else 0.dp)
    val innerShadowOffsetY by animateDpAsState(if (pressed) 7.dp else 0.dp)

    val cornerRadius = 30.dp

    Box(

        modifier
            .width(animatedWidth)
            .height(animatedHeight)
            .dropShadow(
                shape = RoundedCornerShape(cornerRadius),
                shadow = Shadow(
                    radius = outerShadowRadius,
                    spread = outerShadowSpread,
                    color = shadowColor,
                    offset = DpOffset(outerShadowOffsetX, outerShadowOffsetY)
                )
            )

            .clip(RoundedCornerShape(cornerRadius))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF004D5A),
                        Color(0xFF004D5A),
                        Color(0xFF006688)
                    )
                )
            )
            .innerShadow(
                shape = RoundedCornerShape(cornerRadius),
                shadow = Shadow(
                    radius = innerShadowRadius,
                    spread = innerShadowSpread,
                    color = shadowColor,
                    offset = DpOffset(innerShadowOffsetX, innerShadowOffsetY)
                )
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        pressed = true
                        tryAwaitRelease()
                        pressed = false
                        onClick()
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = text,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
    }
}