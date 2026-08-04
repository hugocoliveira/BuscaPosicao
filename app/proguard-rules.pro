# Regras ProGuard para BuscaPosicao
# Manter modelos de resposta da API (usados pelo Gson via reflexão)
-keep class br.com.lit.busca.posicao.data.remote.** { *; }

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**

# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }

# ML Kit
-keep class com.google.mlkit.** { *; }
