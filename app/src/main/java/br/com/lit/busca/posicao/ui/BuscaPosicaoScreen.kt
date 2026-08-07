package br.com.lit.busca.posicao.ui

import android.Manifest
import android.media.MediaPlayer
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.lit.busca.posicao.R
import br.com.lit.busca.posicao.scanner.CameraScanner
import br.com.lit.busca.posicao.ui.components.ResultCard
import br.com.lit.busca.posicao.ui.components.ScannerField
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun BuscaPosicaoScreen(
    viewModel: BuscaPosicaoViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val permissaoCamera = rememberPermissionState(Manifest.permission.CAMERA)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text  = stringResource(R.string.titulo_tela),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ConteudoPrincipal(
                uiState         = uiState,
                onCampoAlterado = viewModel::onCampoAlterado,
                onBuscar        = viewModel::onBuscar,
                onLimpar        = viewModel::onLimpar,
                onRetentar      = viewModel::onBuscar,
                onAbrirScanner  = {
                    if (permissaoCamera.status.isGranted) {
                        viewModel.onAbrirScanner()
                    } else {
                        permissaoCamera.launchPermissionRequest()
                    }
                }
            )

            AnimatedVisibility(
                visible = uiState.scannerAberto,
                enter   = fadeIn(),
                exit    = fadeOut()
            ) {
                ScannerOverlay(
                    onCodigoLido = viewModel::onCodigoEscaneado,
                    onFechar     = viewModel::onFecharScanner
                )
            }
        }
    }
}

@Composable
private fun ConteudoPrincipal(
    uiState: BuscaUiState,
    onCampoAlterado: (String) -> Unit,
    onBuscar: () -> Unit,
    onLimpar: () -> Unit,
    onRetentar: () -> Unit,
    onAbrirScanner: () -> Unit
) {
    val context = LocalContext.current
    val focusRequester = remember { FocusRequester() }

    // Toca som e retorna foco quando busca não encontra resultados
    LaunchedEffect(uiState.semResultados) {
        if (uiState.semResultados) {
            val mp = MediaPlayer.create(context, R.raw.error)
            mp?.start()
            mp?.setOnCompletionListener { it.release() }
            runCatching { focusRequester.requestFocus() }
        }
    }

    // Retorna foco ao campo após exibir resultados para leitura imediata do próximo código
    LaunchedEffect(uiState.resultados) {
        if (uiState.resultados.isNotEmpty()) runCatching { focusRequester.requestFocus() }
    }

    LazyColumn(
        modifier            = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                ScannerField(
                    valor          = uiState.campoBusca,
                    onValorChange  = onCampoAlterado,
                    onBuscar       = onBuscar,
                    onAbrirScanner = onAbrirScanner,
                    onLimpar       = onLimpar,
                    modifier       = Modifier.fillMaxWidth().focusRequester(focusRequester)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Button(
                    onClick  = onBuscar,
                    modifier = Modifier.fillMaxWidth(),
                    enabled  = uiState.campoBusca.isNotBlank() && !uiState.carregando,
                    shape    = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text  = if (uiState.carregando)
                                    stringResource(R.string.buscando)
                                else
                                    stringResource(R.string.botao_buscar),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }

        // Erro inline com botão "Tentar novamente"
        if (uiState.erro != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    shape  = RoundedCornerShape(6.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier          = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text     = uiState.erro,
                            style    = MaterialTheme.typography.bodyMedium,
                            color    = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(onClick = onRetentar) {
                            Text(
                                text  = stringResource(R.string.tentar_novamente),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }

        // Loading
        if (uiState.carregando) {
            item {
                Box(
                    modifier         = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        // Nenhum resultado — som emitido via LaunchedEffect acima
        if (uiState.semResultados) {
            item {
                Box(
                    modifier         = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text  = stringResource(R.string.nenhum_resultado),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        itemsIndexed(
            items = uiState.resultados,
            key   = { indice, _ -> indice }
        ) { indice, objeto ->
            ResultCard(objeto = objeto, indice = indice)
        }

        item { Spacer(modifier = Modifier.height(8.dp)) }
    }
}

@Composable
private fun ScannerOverlay(
    onCodigoLido: (String) -> Unit,
    onFechar: () -> Unit
) {
    val jaLeu = remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        CameraScanner(
            modifier     = Modifier.fillMaxSize(),
            onCodigoLido = { codigo ->
                if (!jaLeu.value) {
                    jaLeu.value = true
                    onCodigoLido(codigo)
                }
            }
        )

        IconButton(
            onClick  = onFechar,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector        = Icons.Default.Close,
                contentDescription = stringResource(R.string.fechar_scanner),
                tint               = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}
