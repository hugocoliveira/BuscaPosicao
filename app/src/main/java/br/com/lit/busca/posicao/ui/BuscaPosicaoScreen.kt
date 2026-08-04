package br.com.lit.busca.posicao.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.lit.busca.posicao.R
import br.com.lit.busca.posicao.scanner.CameraScanner
import br.com.lit.busca.posicao.ui.components.ResultCard
import br.com.lit.busca.posicao.ui.components.ScannerField
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

// ──────────────────────────────────────────────────────────────────────────────
// Tela principal do app. Stateless em relação a dados — delega tudo ao ViewModel.
// Gerencia apenas estado de UI local: visibilidade do scanner.
// ──────────────────────────────────────────────────────────────────────────────

/**
 * Tela única do BuscaPosicao.
 * Exibe campo de busca, botão buscar, resultado em cards e modal de câmera.
 *
 * @param viewModel instância fornecida pelo Compose (escopo de Activity)
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun BuscaPosicaoScreen(
    viewModel: BuscaPosicaoViewModel = viewModel()
) {
    // Coleta estados do ViewModel — recompõe apenas quando mudam
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val query   by viewModel.query.collectAsStateWithLifecycle()

    // Estado local: controla se o modal de câmera está visível
    var mostrarScanner by remember { mutableStateOf(false) }

    // Permissão de câmera — solicitada ao clicar no ícone de scanner
    val permissaoCamera = rememberPermissionState(android.Manifest.permission.CAMERA)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text  = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.Top
        ) {
            // Campo de busca com botão de câmera
            ScannerField(
                valor         = query,
                onValorChange = viewModel::onQueryChange,
                onBuscar      = viewModel::buscar,
                onAbrirCamera = {
                    if (permissaoCamera.status.isGranted) {
                        mostrarScanner = true
                    } else {
                        permissaoCamera.launchPermissionRequest()
                    }
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Botão Buscar
            Button(
                onClick  = viewModel::buscar,
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text  = stringResource(R.string.botao_buscar),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Área de resultado — transição suave entre estados
            AnimatedContent(
                targetState = uiState,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "estado-busca"
            ) { estado ->
                when (estado) {
                    is BuscaUiState.Idle    -> { /* sem conteúdo na tela inicial */ }

                    is BuscaUiState.Loading -> {
                        // Indicador de loading centralizado
                        Box(
                            modifier        = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(48.dp),
                                color    = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    is BuscaUiState.Success -> {
                        // Lista de cards — um por resultado (LazyColumn para performance)
                        // ponytail: API retorna objeto único; LazyColumn já suporta N itens
                        //           caso o endpoint evolua para coleção.
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(
                                items = listOf(estado.campos),
                                key   = { it.hashCode() }
                            ) { campos ->
                                ResultCard(campos = campos)
                            }
                        }
                    }

                    is BuscaUiState.Error -> {
                        Text(
                            text  = estado.mensagem,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }

    // Modal fullscreen da câmera — exibido sobre a tela principal
    if (mostrarScanner) {
        Dialog(
            onDismissRequest = { mostrarScanner = false },
            properties       = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                if (permissaoCamera.status.isGranted) {
                    CameraScanner(
                        modifier       = Modifier.fillMaxSize(),
                        onCodigoLido   = { codigo ->
                            mostrarScanner = false
                            viewModel.onCodigoEscaneado(codigo)
                        }
                    )
                } else {
                    // Permissão negada — instrui o usuário
                    Box(
                        modifier        = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text  = stringResource(R.string.permissao_camera_negada),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}
