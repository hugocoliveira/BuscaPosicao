package br.com.lit.busca.posicao.data.repository

import br.com.lit.busca.posicao.data.remote.NetworkModule

// ──────────────────────────────────────────────────────────────────────────────
// Repositório de posição — única fonte de verdade para a UI.
// Orquestra a chamada à API e devolve Result<T> para isolar erros de rede
// do ViewModel, evitando crashes silenciosos.
// ──────────────────────────────────────────────────────────────────────────────

/**
 * Repositório que abstrai a origem dos dados de posição de armazém.
 * Atualmente só tem fonte remota (SAP OData). Sem cache local — dados
 * de posição mudam com frequência e não justificam persistência.
 */
class PosicaoRepository {

    /** Serviço Retrofit injetado via NetworkModule singleton */
    private val api = NetworkModule.apiService

    /**
     * Busca os dados de uma posição no SAP.
     * Filtra campos internos do OData (prefixo "@") para exibir apenas
     * campos de negócio relevantes ao operador.
     *
     * @param lgpla código da posição (ex: "WH-001-02-03")
     * @return [Result.success] com mapa de campos visíveis, ou
     *         [Result.failure] com a exceção em caso de erro de rede/HTTP
     */
    suspend fun buscarPosicao(lgpla: String): Result<Map<String, Any?>> {
        return runCatching {
            val resposta = api.buscarPosicao(lgpla)

            // Filtra metadados OData (@odata.context, @odata.etag etc.)
            // para exibir apenas campos de negócio na UI
            resposta.filterKeys { chave -> !chave.startsWith("@") }
        }
    }
}
