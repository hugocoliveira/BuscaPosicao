package br.com.lit.busca.posicao.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap

// ──────────────────────────────────────────────────────────────────────────────
// Interface Retrofit para o endpoint OData SAP de busca de posição.
// A URL base é configurada no NetworkModule.
// ──────────────────────────────────────────────────────────────────────────────

/**
 * Contrato HTTP da API de posição de armazém.
 * Endpoint: Posicao(P_Lgpla='{valor}')/Set
 *
 * Retorna um Map genérico para permitir exibição dinâmica de todos os
 * campos sem precisar mapear cada campo em uma data class.
 */
interface ApiService {

    /**
     * Busca os dados de uma posição pelo código informado.
     *
     * @param lgpla código da posição (lido da câmera ou digitado)
     * @param queryParams parâmetros adicionais de query string
     *                    (ex: sap-client=100, $format=json)
     * @return mapa com todos os campos retornados pelo OData
     */
    @GET("Posicao(P_Lgpla='{lgpla}')/Set")
    suspend fun buscarPosicao(
        @Path("lgpla", encoded = false) lgpla: String,
        @QueryMap queryParams: Map<String, String> = DEFAULT_QUERY
    ): Map<String, Any?>

    companion object {
        /** Parâmetros de query obrigatórios para o SAP OData */
        val DEFAULT_QUERY = mapOf(
            "sap-client" to "100",
            "\$format"   to "json"
        )
    }
}
