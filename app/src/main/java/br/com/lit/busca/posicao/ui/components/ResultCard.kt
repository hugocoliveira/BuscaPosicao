package br.com.lit.busca.posicao.ui.components

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.google.gson.JsonObject

/**
 * Card de resultado de posição SAP.
 * Exibe os 16 campos da API em grid de 2 colunas com labels abreviados em negrito.
 */
@Composable
fun ResultCard(
    objeto: JsonObject,
    indice: Int,
    modifier: Modifier = Modifier
) {
    // Pares (label abreviado, valor) na ordem de exibição
    val campos = listOf(
        "Material"   to objeto.str("Material"),
        "Descrição"  to objeto.str("MaterialDescription"),
        "Armazém"    to objeto.str("Lgnum"),
        "Tp. Armaz." to objeto.str("StorageType"),
        "Área"       to objeto.str("StorageArea"),
        "Tp. Bin"    to objeto.str("StorageBinType"),
        "Nº UCs"     to objeto.str("NumberOfHUs"),
        "Peso"       to objeto.str("Weight"),
        "Un. Peso"   to objeto.str("WeightUnit"),
        "Volume"     to objeto.str("Volume"),
        "Un. Vol."   to objeto.str("VolumeUnit"),
        "Dt. Mov."   to formatarData(objeto.str("MovedAtDate")),
        "Hr. Mov."   to formatarHora(objeto.str("MovedAtTime")),
        "Dt. Cont."  to formatarData(objeto.str("CountDate")),
        "Hr. Cont."  to formatarHora(objeto.str("CountTime"))
    )

    Card(
        modifier  = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
        shape     = RoundedCornerShape(6.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {

            // Grid 3 colunas — 5 linhas para 15 campos, cabe sem scroll
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                campos.chunked(3).forEach { grupo ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        CampoCell(grupo[0].first, grupo[0].second, Modifier.weight(1f))
                        Spacer(Modifier.width(8.dp))
                        if (grupo.size > 1) CampoCell(grupo[1].first, grupo[1].second, Modifier.weight(1f))
                        else Spacer(Modifier.weight(1f))
                        Spacer(Modifier.width(8.dp))
                        if (grupo.size > 2) CampoCell(grupo[2].first, grupo[2].second, Modifier.weight(1f))
                        else Spacer(Modifier.weight(1f))
                    }
                }
            }

            Spacer(Modifier.height(4.dp))
        }
    }
}

@Composable
private fun CampoCell(label: String, valor: String?, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text           = label,
            style          = MaterialTheme.typography.bodySmall,
            color          = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight     = FontWeight.Bold,
            textDecoration = TextDecoration.Underline
        )
        Text(
            text  = valor ?: "—",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun JsonObject.str(key: String): String? {
    val el = this.get(key) ?: return null
    if (el.isJsonNull) return null
    return try {
        val s = el.asString
        if (s.isBlank() || s == "null") null else s
    } catch (e: Exception) {
        el.toString().takeIf { it.isNotBlank() }
    }
}

private fun formatarData(raw: String?): String {
    if (raw == null) return "—"
    val p = raw.split("-")
    return if (p.size == 3) "${p[2]}/${p[1]}/${p[0]}" else raw
}

private fun formatarHora(raw: String?): String {
    if (raw == null) return "—"
    val p = raw.split(":")
    return if (p.size >= 2) "${p[0]}:${p[1]}" else raw
}
