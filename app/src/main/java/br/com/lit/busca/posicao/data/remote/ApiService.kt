package br.com.lit.busca.posicao.data.remote

import com.google.gson.JsonObject
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface ApiService {

    @GET("Posicao(P_Lgpla='{lgpla}')/Set")
    suspend fun buscarPosicao(
        @Path("lgpla", encoded = false) lgpla: String,
        @QueryMap queryParams: Map<String, String> = DEFAULT_QUERY
    ): JsonObject

    companion object {
        val DEFAULT_QUERY = mapOf(
            "sap-client" to "100",
            "\$format"   to "json"
        )
    }
}
