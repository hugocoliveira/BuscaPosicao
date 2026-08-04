package br.com.lit.busca.posicao.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import br.com.lit.busca.posicao.R

// ──────────────────────────────────────────────────────────────────────────────
// Campo de busca com botão de câmera integrado.
// Stateless: recebe valor e emite eventos — sem estado interno.
// ──────────────────────────────────────────────────────────────────────────────

/**
 * Campo de texto para digitação da posição com botão de scanner QR/barcode.
 *
 * @param valor          texto atual do campo
 * @param onValorChange  callback ao digitar
 * @param onBuscar       callback ao pressionar Enter ou ação do teclado
 * @param onAbrirCamera  callback ao clicar no ícone de câmera
 * @param modifier       modificador externo
 */
@Composable
fun ScannerField(
    valor: String,
    onValorChange: (String) -> Unit,
    onBuscar: () -> Unit,
    onAbrirCamera: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value         = valor,
        onValueChange = onValorChange,
        modifier      = modifier.fillMaxWidth(),
        label         = { Text(stringResource(R.string.hint_campo_busca)) },
        singleLine    = true,
        // Ícone de câmera como trailing — abre scanner ao clicar
        trailingIcon  = {
            IconButton(onClick = onAbrirCamera) {
                Icon(
                    imageVector        = Icons.Default.QrCodeScanner,
                    contentDescription = stringResource(R.string.botao_escanear),
                    tint               = MaterialTheme.colorScheme.primary
                )
            }
        },
        // Teclado com ação "Buscar" (lupa) — dispara onBuscar ao confirmar
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onBuscar() }),
        shape           = MaterialTheme.shapes.medium
    )
}
