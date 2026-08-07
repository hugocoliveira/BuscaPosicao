package br.com.lit.busca.posicao.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.lit.busca.posicao.data.repository.PosicaoRepository
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BuscaUiState(
    val campoBusca: String           = "",
    val carregando: Boolean          = false,
    val erro: String?                = null,
    val resultados: List<JsonObject> = emptyList(),
    val scannerAberto: Boolean       = false,
    val semResultados: Boolean       = false
)

class BuscaPosicaoViewModel : ViewModel() {

    private val repositorio = PosicaoRepository.instance

    private val _uiState = MutableStateFlow(BuscaUiState())
    val uiState: StateFlow<BuscaUiState> = _uiState.asStateFlow()

    fun onCampoAlterado(valor: String) {
        _uiState.update { it.copy(campoBusca = valor, erro = null) }
    }

    fun onBuscar() {
        val valor = _uiState.value.campoBusca.trim()
        if (valor.isBlank()) return
        buscar(valor)
    }

    fun onCodigoEscaneado(codigo: String) {
        _uiState.update { it.copy(campoBusca = codigo, scannerAberto = false) }
        buscar(codigo)
    }

    fun onAbrirScanner() {
        _uiState.update { it.copy(scannerAberto = true, erro = null) }
    }

    fun onFecharScanner() {
        _uiState.update { it.copy(scannerAberto = false) }
    }

    fun onLimpar() {
        _uiState.value = BuscaUiState()
    }

    private fun buscar(valor: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(carregando = true, erro = null, resultados = emptyList(), semResultados = false) }

            repositorio.buscarPosicao(valor).fold(
                onSuccess = { lista ->
                    _uiState.update {
                        it.copy(
                            carregando    = false,
                            resultados    = lista,
                            campoBusca    = "",
                            semResultados = lista.isEmpty()
                        )
                    }
                },
                onFailure = { excecao ->
                    _uiState.update {
                        it.copy(carregando = false, erro = excecao.message ?: "Erro desconhecido")
                    }
                }
            )
        }
    }
}
