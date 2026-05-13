package com.example.pastillas.ui.components.texedits

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.innerShadow
import androidx.compose.ui.graphics.shadow.Shadow

@Composable
fun CajaCantidadPastillas(
    numPastillas: Int,
    cantidades: List<Int>,
    onCantidadSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val textColor = MaterialTheme.colorScheme.onSurface
    val placeholderColor = MaterialTheme.colorScheme.onSurfaceVariant
    val backgroundColor = MaterialTheme.colorScheme.surface
    var expandedCantidad by remember { mutableStateOf(false) }
    val shadowColor = Color(0x40000000)
    val displayText = if (numPastillas <= 0) "Cantidad de pastillas" else "$numPastillas pastillas"
    val displayColor = if (numPastillas <= 0) placeholderColor else textColor

    Box(
        modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(backgroundColor)
            .innerShadow(
                shape = RoundedCornerShape(10.dp),
                shadow = Shadow(
                    radius = 10.dp,
                    spread = 2.dp,
                    color = shadowColor,
                    offset = DpOffset(x = 6.dp, y = 7.dp)
                )
            )
            .clickable { expandedCantidad = true }
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = displayText,
                color = displayColor,
                fontSize = 18.sp
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = displayColor
            )
        }

        DropdownMenu(
            expanded = expandedCantidad,
            onDismissRequest = { expandedCantidad = false },
            modifier = Modifier.width(200.dp) //Desplegable
        ) {
            cantidades.forEach { cantidad ->
                DropdownMenuItem(
                    text = { Text("$cantidad", color = textColor) },
                    onClick = {
                        onCantidadSelected(cantidad)
                        expandedCantidad = false
                    }
                )
            }
        }
    }
}

