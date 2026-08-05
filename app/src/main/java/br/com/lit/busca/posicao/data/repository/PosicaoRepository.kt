package br.com.lit.busca.posicao.data.repository

import br.com.lit.busca.posicao.data.remote.NetworkModule
import com.google.gson.JsonObject

class PosicaoRepository {

    private val api = NetworkModule.apiService

    suspend fun buscarPosicao(lgpla: String): Result<List<JsonObject>> {
        return runCatching {
            val resposta = api.buscarPosicao(lgpla)

            // Filtra metadados OData (@odata.context, @odata.etag etc.)
            val filtrado = JsonObject()
            resposta.entrySet()
                .filter { !it.key.startsWith("@") }
                .forEach { filtrado.add(it.key, it.value) }

            if (filtrado.size() == 0) emptyList() else listOf(filtrado)
        }
    }

    companion object {
        val instance = PosicaoRepository()
    }
}
