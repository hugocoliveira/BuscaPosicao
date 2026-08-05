package br.com.lit.busca.posicao.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import br.com.lit.busca.posicao.R

@Composable
fun ScannerField(
    valor: String,
    onValorChange: (String) -> Unit,
    onBuscar: () -> Unit,
    onAbrirScanner: () -> Unit,
    onLimpar: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value         = valor,
        onValueChange = onValorChange,
        modifier      = modifier,
        label         = { Text(stringResource(R.string.label_campo_busca)) },
        placeholder   = { Text(stringResource(R.string.placeholder_campo_busca)) },
        singleLine    = true,
        shape         = RoundedCornerShape(12.dp),
        trailingIcon  = {
            if (valor.isNotEmpty()) {
                IconButton(onClick = onLimpar) {
                    Icon(
                        imageVector        = Icons.Default.Clear,
                        contentDescription = stringResource(R.string.botao_limpar),
                        tint               = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                IconButton(onClick = onAbrirScanner) {
                    Icon(
                        imageVector        = Icons.Default.QrCodeScanner,
                        contentDescription = stringResource(R.string.descricao_icone_scanner),
                        tint               = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction    = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = { onBuscar() }
        )
    )
}
