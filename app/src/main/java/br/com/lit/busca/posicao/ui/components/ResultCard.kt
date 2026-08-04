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

// ──────────────────────────────────────────────────────────────────────────────
// Card genérico para exibir os campos retornados pela API de posição.
// Recebe um mapa de chave→valor e renderiza cada par em uma linha,
// sem precisar conhecer os campos específicos da resposta.
// ──────────────────────────────────────────────────────────────────────────────

/**
 * Card que exibe todos os campos de um resultado de posição SAP.
 * Renderização genérica: funciona com qualquer conjunto de campos
 * retornados pela API OData sem alterar código.
 *
 * @param campos mapa chave→valor dos campos de negócio (metadados OData já filtrados)
 * @param modifier modificador para tamanho e posicionamento externo
 */
@Composable
fun ResultCard(
    campos: Map<String, Any?>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(12.dp),
        colors   = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            campos.entries.forEachIndexed { indice, (chave, valor) ->
                CampoLinha(
                    chave = formatarChave(chave),
                    valor = formatarValor(valor)
                )

                // Divisor entre campos — exceto após o último
                if (indice < campos.size - 1) {
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(
                        color     = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                        thickness = 0.5.dp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

/**
 * Linha individual com rótulo (chave) e valor de um campo.
 *
 * @param chave nome do campo formatado para leitura humana
 * @param valor conteúdo do campo como String
 */
@Composable
private fun CampoLinha(chave: String, valor: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        // Rótulo do campo — ocupa 40% da largura
        Text(
            text     = chave,
            style    = MaterialTheme.typography.labelMedium,
            color    = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(0.4f)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Valor do campo — ocupa 60% restantes
        Text(
            text     = valor,
            style    = MaterialTheme.typography.bodyMedium,
            color    = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(0.6f)
        )
    }
}

/**
 * Converte o nome técnico do campo SAP para leitura humana.
 * Ex: "P_Lgpla" → "P Lgpla", "MatnrDesc" → "Matnr Desc"
 *
 * @param chave nome original do campo na resposta OData
 * @return nome formatado com espaços antes de letras maiúsculas
 */
private fun formatarChave(chave: String): String =
    chave
        .replace("_", " ")
        .replace(Regex("([a-z])([A-Z])")) { r -> "${r.groupValues[1]} ${r.groupValues[2]}" }
        .trim()

/**
 * Converte o valor do campo para String legível.
 * Trata nulos e tipos numéricos vindos do Gson (Double → sem casas decimais
 * quando for número inteiro).
 *
 * @param valor conteúdo bruto do campo (pode ser String, Double, Boolean, null)
 * @return representação textual do valor
 */
private fun formatarValor(valor: Any?): String = when (valor) {
    null           -> "—"
    is Double      -> if (valor == valor.toLong().toDouble()) valor.toLong().toString()
                      else valor.toString()
    else           -> valor.toString().ifBlank { "—" }
}
