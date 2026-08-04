package br.com.lit.busca.posicao.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import br.com.lit.busca.posicao.ui.theme.BuscaPosicaoTheme

// ──────────────────────────────────────────────────────────────────────────────
// Ponto de entrada do app. Responsabilidade mínima: configurar o tema
// e entregar o controle ao Composable raiz.
// ──────────────────────────────────────────────────────────────────────────────

/**
 * Activity única do BuscaPosicao.
 * Ativada via Intent explícita pelo MenuAutomatico — sem ícone de launcher.
 * O valor da posição a buscar pode ser recebido como extra da Intent
 * via key "TRANSACAO" (padrão do MenuAutomatico).
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Habilita layout edge-to-edge (conteúdo sob status/navigation bar)
        enableEdgeToEdge()

        setContent {
            BuscaPosicaoTheme {
                BuscaPosicaoScreen()
            }
        }
    }
}
