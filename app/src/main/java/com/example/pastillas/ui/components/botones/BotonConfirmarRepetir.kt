package com.example.pastillas.ui.components.botones

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pastillas.data.model.Toma


@Composable
fun BotonConfirmarRepetir(
    dosisCorrecta: Boolean,
    toma: Toma,
    pastillasDetectadas: Int,
    indexToma: Int,
    navController: NavController,
    onGuardarHistorial: (Toma, Int, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val shadowColor = Color(0x40000000)

    var pressed by remember { mutableStateOf(false) }

    val animatedWidth by animateDpAsState(if (pressed) 270.dp else 285.dp)
    val animatedHeight by animateDpAsState(if (pressed) 90.dp else 100.dp)

    val outerShadowRadius by animateDpAsState(if (pressed) 0.dp else 10.dp)
    val outerShadowSpread by animateDpAsState(if (pressed) 0.dp else 6.dp)
    val outerShadowOffsetX by animateDpAsState(if (pressed) 0.dp else 4.dp)
    val outerShadowOffsetY by animateDpAsState(if (pressed) 0.dp else 4.dp)

    val innerShadowRadius by animateDpAsState(if (pressed) 10.dp else 0.dp)
    val innerShadowSpread by animateDpAsState(if (pressed) 2.dp else 0.dp)
    val innerShadowOffsetX by animateDpAsState(if (pressed) 6.dp else 0.dp)
    val innerShadowOffsetY by animateDpAsState(if (pressed) 7.dp else 0.dp)

    val cornerRadius = 60.dp

    val buttonText = if (dosisCorrecta) "CONFIRMAR" else "REPETIR"

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
                        onGuardarHistorial(toma, pastillasDetectadas, dosisCorrecta)
                        if (dosisCorrecta) {
                            navController.navigate("main") {
                                popUpTo("main") { inclusive = true }
                            }
                        } else {
                            navController.navigate("camara/$indexToma")
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = buttonText,
            fontSize = 35.sp,
            color = Color.White,
            fontWeight = FontWeight.SemiBold
        )
    }
}
