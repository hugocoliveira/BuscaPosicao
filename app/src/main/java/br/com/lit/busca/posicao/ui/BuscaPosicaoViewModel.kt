package br.com.lit.busca.posicao.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.lit.busca.posicao.data.repository.PosicaoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ──────────────────────────────────────────────────────────────────────────────
// ViewModel da tela principal. Gerencia estado da busca e resultado da API.
// Toda lógica de negócio e I/O ficam aqui — os Composables só observam estado.
// ──────────────────────────────────────────────────────────────────────────────

/**
 * Estado da tela de busca. Sealed class garante que a UI trate
 * todos os casos possíveis em um único when().
 */
sealed class BuscaUiState {
    /** Tela inicial — aguardando entrada do usuário */
    data object Idle : BuscaUiState()

    /** Chamada à API em andamento */
    data object Loading : BuscaUiState()

    /**
     * Resposta recebida com sucesso.
     * @param campos mapa de chave → valor para exibição dinâmica nos cards
     */
    data class Success(val campos: Map<String, Any?>) : BuscaUiState()

    /**
     * Erro na chamada ou resultado vazio.
     * @param mensagem texto descritivo para exibir ao operador
     */
    data class Error(val mensagem: String) : BuscaUiState()
}

/**
 * ViewModel da tela BuscaPosicao.
 * Expõe [uiState] como StateFlow imutável para a UI e [query] com o
 * texto atual do campo de busca.
 */
class BuscaPosicaoViewModel : ViewModel() {

    /** Repositório de dados — fonte única de verdade */
    private val repositorio = PosicaoRepository()

    /** Estado interno mutável — somente o ViewModel escreve aqui */
    private val _uiState = MutableStateFlow<BuscaUiState>(BuscaUiState.Idle)

    /** Estado exposto à UI — somente leitura */
    val uiState: StateFlow<BuscaUiState> = _uiState.asStateFlow()

    /** Texto atual do campo de busca — sincronizado com o TextField */
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    /**
     * Atualiza o texto do campo de busca.
     * Chamado pelo Composable a cada caractere digitado.
     *
     * @param valor novo valor do campo
     */
    fun onQueryChange(valor: String) {
        _query.value = valor
        // Volta ao Idle quando o usuário apaga o campo, limpando resultado anterior
        if (valor.isBlank()) _uiState.value = BuscaUiState.Idle
    }

    /**
     * Dispara a busca com o valor atual de [query].
     * Valida o campo antes de chamar a API.
     * A chamada ocorre em Dispatchers.IO para não bloquear a Main thread.
     */
    fun buscar() {
        val lgpla = _query.value.trim()
        if (lgpla.isBlank()) {
            _uiState.value = BuscaUiState.Error("Digite ou escaneie um valor para buscar.")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = BuscaUiState.Loading

            repositorio.buscarPosicao(lgpla).fold(
                onSuccess = { campos ->
                    _uiState.value = if (campos.isEmpty()) {
                        BuscaUiState.Error("Nenhum resultado encontrado para \"$lgpla\".")
                    } else {
                        BuscaUiState.Success(campos)
                    }
                },
                onFailure = { erro ->
                    _uiState.value = BuscaUiState.Error(
                        "Erro ao buscar dados: ${erro.message ?: "verifique a conexão."}"
                    )
                }
            )
        }
    }

    /**
     * Chamado após leitura bem-sucedida da câmera.
     * Preenche o campo e dispara a busca automaticamente.
     *
     * @param valor código lido pelo scanner (QR ou barcode)
     */
    fun onCodigoEscaneado(valor: String) {
        _query.value = valor
        buscar()
    }

    /** Limpa o resultado e volta ao estado inicial */
    fun limpar() {
        _query.value = ""
        _uiState.value = BuscaUiState.Idle
    }
}
