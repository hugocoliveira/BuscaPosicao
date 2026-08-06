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

/**
 * Card de resultado de posição SAP.
 * Exibe os campos do JSON com labels em português e datas formatadas.
 * Campos internos (Lgnum) são omitidos; Lgpla aparece no cabeçalho.
 */
@Composable
fun ResultCard(
    objeto: JsonObject,
    indice: Int,
    modifier: Modifier = Modifier
) {
    val lgpla = objeto.str("Lgpla") ?: "—"

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

            // Cabeçalho: posição encontrada
            Text(
                text       = "Posição: $lgpla",
                style      = MaterialTheme.typography.titleMedium,
                color      = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(4.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
            Spacer(modifier = Modifier.height(6.dp))

            // Campos principais
            CampoLinha("Tipo de depósito",   objeto.str("StorageType"))
            CampoLinha("Área armazmto.",      objeto.str("StorageArea"))
            CampoLinha("Tp.posição depósito", objeto.str("StorageBinType"))
            CampoLinha("Nº UCs",              objeto.str("NumberOfHUs"))
            CampoLinha("Peso",                objeto.str("Weight"))
            CampoLinha("Unidade do peso",     objeto.str("WeightUnit"))
            CampoLinha("Volume",              objeto.str("Volume"))
            CampoLinha("Unidade volume",      objeto.str("VolumeUnit"))

            Spacer(modifier = Modifier.height(6.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
            Spacer(modifier = Modifier.height(6.dp))

            // Último movimento — data e hora separados e formatados
            CampoLinha("Último movimento (data)", formatarData(objeto.str("MovedAtDate")))
            CampoLinha("Último movimento (hora)", formatarHora(objeto.str("MovedAtTime")))

            Spacer(modifier = Modifier.height(4.dp))

        }
    }
}

@Composable
private fun CampoLinha(label: String, valor: String?) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text       = "$label:",
            style      = MaterialTheme.typography.bodySmall,
            color      = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium,
            modifier   = Modifier.width(160.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text     = valor ?: "—",
            style    = MaterialTheme.typography.bodyMedium,
            color    = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
    }
    Spacer(modifier = Modifier.height(2.dp))
}

/** Extrai string de um campo JsonObject; retorna null se ausente, nulo ou vazio. */
private fun JsonObject.str(key: String): String? {
    val el = this.get(key) ?: return null
    if (el.isJsonNull) return null
    val s = el.asString
    return if (s.isBlank() || s == "null") null else s
}

/** "2022-11-17" → "17/11/2022". Null ou inválido → "—". */
private fun formatarData(raw: String?): String {
    if (raw == null) return "—"
    val p = raw.split("-")
    return if (p.size == 3) "${p[2]}/${p[1]}/${p[0]}" else raw
}

/** "09:07:40" → "09:07". Null → "—". */
private fun formatarHora(raw: String?): String {
    if (raw == null) return "—"
    val p = raw.split(":")
    return if (p.size >= 2) "${p[0]}:${p[1]}" else raw
}
