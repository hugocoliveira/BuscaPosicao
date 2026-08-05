package br.com.lit.busca.posicao.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.gson.JsonObject

@Composable
fun ResultCard(
    objeto: JsonObject,
    indice: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier  = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape     = RoundedCornerShape(6.dp),
        colors    = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text       = "Item ${indice + 1}",
                style      = MaterialTheme.typography.titleMedium,
                color      = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(4.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
            Spacer(modifier = Modifier.height(4.dp))

            objeto.entrySet().forEach { (chave, elemento) ->
                val valorTexto = when {
                    elemento == null || elemento.isJsonNull -> "—"
                    elemento.isJsonPrimitive               -> elemento.asString
                    else                                   -> elemento.toString()
                }
                CampoLinha(chave = formatarChave(chave), valor = valorTexto)
                Spacer(modifier = Modifier.height(2.dp))
            }
        }
    }
}

@Composable
private fun CampoLinha(chave: String, valor: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text       = "$chave:",
            style      = MaterialTheme.typography.bodySmall,
            color      = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium,
            modifier   = Modifier.width(120.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text     = valor,
            style    = MaterialTheme.typography.bodyMedium,
            color    = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
    }
}

private fun formatarChave(chave: String): String =
    chave
        .replace("_", " ")
        .replace(Regex("([a-z])([A-Z])")) { r -> "${r.groupValues[1]} ${r.groupValues[2]}" }
        .trim()
