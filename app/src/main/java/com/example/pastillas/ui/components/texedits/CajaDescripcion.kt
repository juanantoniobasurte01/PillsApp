package com.example.pastillas.ui.components.texedits

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.AlertDialogDefaults.shape
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.draw.innerShadow
import androidx.compose.ui.graphics.shadow.Shadow

@Composable
fun CajaDescripcion(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "Descripción",
    modifier: Modifier = Modifier
) {
    val textColor = MaterialTheme.colorScheme.onSurface
    val backgroundColor = MaterialTheme.colorScheme.surface
    val shadowColor = Color(0x40000000)

    Box(
        modifier
            .background(
                color = Color.Red,
                shape = shape
            )
            .fillMaxWidth()
            .height(100.dp)
            .background(backgroundColor, RoundedCornerShape(10.dp))
            .innerShadow(
                shape = RoundedCornerShape(10.dp),
                shadow = Shadow(
                    radius = 10.dp,
                    spread = 2.dp,
                    color = shadowColor,
                    offset = DpOffset(x = 6.dp, y = 7.dp)
                )
            )
            .padding(16.dp)
    ) {
        BasicTextField(
            value = value,
            onValueChange = { if (it.length <= 200) onValueChange(it) },
            textStyle = TextStyle(
                color = textColor,
                fontSize = 16.sp
            ),
            maxLines = 4,
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        color = Color.Gray,
                        fontSize = 18.sp
                    )
                }
                innerTextField()
            },
            modifier = Modifier
                .fillMaxSize()

        )
    }
}
