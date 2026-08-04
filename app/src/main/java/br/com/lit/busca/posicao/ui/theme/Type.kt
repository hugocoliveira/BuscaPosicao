package br.com.lit.busca.posicao.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ──────────────────────────────────────────────────────────────────────────────
// Tipografia do app — usa a fonte padrão do sistema (sem fonte customizada).
// Escala otimizada para leitura rápida em dispositivos de armazém.
// ──────────────────────────────────────────────────────────────────────────────

val AppTypography = Typography(

    /** Título da tela / nome do menu atual */
    headlineMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize   = 24.sp,
        lineHeight = 32.sp
    ),

    /** Texto principal dos itens */
    titleMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize   = 16.sp,
        lineHeight = 24.sp
    ),

    /** Rótulos de campos e texto secundário */
    labelMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize   = 12.sp,
        lineHeight = 16.sp
    ),

    /** Valores e texto de suporte */
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize   = 14.sp,
        lineHeight = 20.sp
    ),

    /** Texto secundário menor */
    bodySmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize   = 12.sp,
        lineHeight = 16.sp
    )
)
