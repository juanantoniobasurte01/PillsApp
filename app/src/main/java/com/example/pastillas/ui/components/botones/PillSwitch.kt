package com.example.pastillas.ui.components.botones

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.pastillas.R



@Composable
fun PillSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (checked) 180f else 0f,
        animationSpec = tween(
            durationMillis = 400,
            easing = FastOutSlowInEasing
        ),
        label = "pillRotation"
    )

    Box(
        modifier = Modifier
            .width(64.dp)
            .height(36.dp)
            .clip(RoundedCornerShape(18.dp))
            .then(
                if (checked) {
                    Modifier.background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF004D5A),
                                Color(0xFF006688),
                                Color(0xFF3399AA)
                            )
                        )
                    )
                } else {
                    Modifier.background(Color(0xFFBDBDBD))
                }
            )
            .clickable { onCheckedChange(!checked) }
            .padding(4.dp),
        contentAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Image(
            painter = painterResource(R.drawable.pill),
            contentDescription = null,
            modifier = Modifier
                .size(26.dp)
                .graphicsLayer {
                    rotationZ = rotation
                }
        )
    }
}