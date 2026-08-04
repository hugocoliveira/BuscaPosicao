package br.com.lit.busca.posicao.data.remote

import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// ──────────────────────────────────────────────────────────────────────────────
// Configuração da camada de rede.
// Cria um singleton de ApiService com Basic Auth fixo para o SAP.
// ponytail: credenciais hardcoded — mover para BuildConfig/prefs se multiusuário.
// ──────────────────────────────────────────────────────────────────────────────

/**
 * Fábrica de instâncias de rede.
 * Expõe um singleton [apiService] pronto para uso no repositório.
 */
object NetworkModule {

    /** URL base do servidor SAP OData */
    private const val BASE_URL =
        "http://vm77.4hub.cloud:57700/sap/opu/odata4/sap/zsb_hco_busca_material/srvd_a2x/sap/zsd_hco_busca_material/0001/"

    /** Credenciais Basic Auth fixas para o endpoint de posição */
    private const val USERNAME = "HOLIVEIRA"
    private const val PASSWORD = "*Hugo753951"

    /**
     * Cliente OkHttp configurado com:
     * - interceptor de autenticação Basic Auth em toda requisição
     * - interceptor de log (nível BODY) para debug
     * - timeouts generosos para rede corporativa SAP
     */
    private val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            // Adiciona o header Authorization: Basic <base64> em toda requisição
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("Authorization", Credentials.basic(USERNAME, PASSWORD))
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Instância Retrofit configurada com o cliente OkHttp e conversor Gson.
     * Usa GsonConverterFactory para desserializar a resposta em Map<String, Any?>.
     */
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Singleton de ApiService. Compartilhado por toda a aplicação.
     * Inicializado na primeira chamada (lazy).
     */
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
