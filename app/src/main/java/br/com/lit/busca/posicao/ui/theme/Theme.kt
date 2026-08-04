package br.com.lit.busca.posicao.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// ──────────────────────────────────────────────────────────────────────────────
// MaterialTheme customizado para o BuscaPosicao.
// Usa sempre o tema claro — sem suporte a dark mode por ora,
// pois o app roda em dispositivos de armazém com configuração fixa.
// ──────────────────────────────────────────────────────────────────────────────

/** Esquema de cores claro mapeando os tokens da paleta LIT */
private val LitColorScheme = lightColorScheme(
    primary          = Primary,
    onPrimary        = OnPrimary,
    primaryContainer = PrimaryContainer,
    surface          = Surface,
    onSurface        = OnSurface,
    surfaceVariant   = SurfaceVariant,
    onSurfaceVariant = OnSurfaceVariant,
    error            = ErrorColor,
    outline          = Outline
)

/**
 * Tema principal do app. Envolve toda a hierarquia de Composables.
 *
 * @param content conteúdo Composable a ser estilizado pelo tema
 */
@Composable
fun BuscaPosicaoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LitColorScheme,
        typography  = AppTypography,
        content     = content
    )
}
