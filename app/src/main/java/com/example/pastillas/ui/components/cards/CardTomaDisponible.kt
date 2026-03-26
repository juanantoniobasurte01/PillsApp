package com.example.pastillas.ui.components.cards


import com.example.pastillas.R
import androidx.compose.foundation.Image
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import com.example.pastillas.data.model.Toma


@Composable
fun CardTomaDisponible(
    toma: Toma,
    onClick: () -> Unit,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {

    val shadowColor = if (isDark) Color(0x40000000) else Color(0x40000000)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(10.dp), // ← así funciona
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {


            // Nombre descripción yhorario
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = toma.nombre,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontStyle = if (toma.nombre == "Toma sin título") FontStyle.Italic else FontStyle.Normal
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = toma.descripcion,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = toma.horario,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.Top)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            //   Número de pastillas, icono y notificación
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${toma.numeroPastillas}",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    val pastillaDrawable = if (isDark) R.drawable.ic_pastillanight else R.drawable.ic_pastilla
                    Image(
                        painter = painterResource(id = pastillaDrawable),
                        contentDescription = "Pastilla",
                        modifier = Modifier.size(20.dp)
                    )
                }

                //Icc de campana según modo dark/light
                val campanaDrawable = when {
                    toma.notificacionActiva && isDark -> R.drawable.ic_campananight
                    toma.notificacionActiva && !isDark -> R.drawable.ic_campana
                    !toma.notificacionActiva && isDark -> R.drawable.ic_campana_silenciadanight
                    else -> R.drawable.ic_campana_silenciada
                }

                Image(
                    painter = painterResource(id = campanaDrawable),
                    contentDescription = "Notificación",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}


