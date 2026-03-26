package com.example.pastillas.ui.components.texedits

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.innerShadow
import androidx.compose.ui.graphics.shadow.Shadow

@Composable
fun CajaHorario(
    value: String,
    horarios: List<String>,
    onHorarioSelected: (String) -> Unit,
    expandedHorario: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = MaterialTheme.colorScheme.surface
    val isDark = isSystemInDarkTheme()
    val shadowColor = if (isDark) Color(0x40000000) else Color(0x40000000)

    val displayText = if (value.isBlank()) "Horario" else value
    val displayColor = if (value.isBlank()) Color.Gray else Color.Black

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
                    offset = DpOffset(6.dp, 7.dp)
                )
            )
            .clickable { onExpandedChange(true) }
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
                color = Color.Gray,
                fontSize = 18.sp
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = displayColor
            )
        }




        DropdownMenu(
            expanded = expandedHorario,
            onDismissRequest = { onExpandedChange(false) },
            modifier = Modifier.width(220.dp)
        ) {
            horarios.forEach { horario ->
                DropdownMenuItem(
                    text = { Text(horario, color = Color.Gray) },
                    onClick = {
                        onHorarioSelected(horario)
                        onExpandedChange(false)
                    }
                )
            }


        }
    }
}
