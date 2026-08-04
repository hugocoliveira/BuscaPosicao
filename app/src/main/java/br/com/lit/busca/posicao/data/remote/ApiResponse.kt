package br.com.lit.busca.posicao.data.remote

import com.google.gson.annotations.SerializedName

// ──────────────────────────────────────────────────────────────────────────────
// Modelos de resposta da API OData SAP.
// A API pode retornar um objeto único (navegação por chave) ou uma coleção
// envolta em "value": [...]. Ambos os formatos são tratados aqui.
// ──────────────────────────────────────────────────────────────────────────────

/**
 * Envelope OData para coleção de resultados.
 * Formato: { "value": [ {...}, {...} ] }
 *
 * @param value lista de itens retornados pela API
 */
data class ODataCollectionResponse(
    @SerializedName("value") val value: List<Map<String, Any?>> = emptyList()
)

/**
 * Envelope OData para objeto único (navegação por chave primária).
 * Usado quando a API retorna diretamente os campos sem envelope "value".
 * Representado como mapa genérico para exibição dinâmica dos campos.
 */
typealias ODataSingleResponse = Map<String, Any?>
