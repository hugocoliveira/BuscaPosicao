package br.com.lit.busca.posicao.scanner

import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

// ──────────────────────────────────────────────────────────────────────────────
// Integração CameraX + ML Kit para leitura de QR Code e código de barras.
// Exibe um preview em tempo real e chama [onCodigoLido] com o primeiro
// código detectado, parando a análise em seguida para evitar leituras duplicadas.
// ──────────────────────────────────────────────────────────────────────────────

private const val TAG = "CameraScanner"

/**
 * Composable que exibe o preview da câmera traseira e analisa frames
 * em busca de QR codes ou códigos de barras via ML Kit.
 *
 * Deve ser exibido apenas com permissão de câmera concedida.
 *
 * @param modifier modificador Compose para tamanho e posicionamento
 * @param onCodigoLido callback chamado com o valor raw do código detectado
 */
@Composable
fun CameraScanner(
    modifier: Modifier = Modifier,
    onCodigoLido: (String) -> Unit
) {
    val context      = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Executor dedicado para análise de imagem — não bloqueia a Main thread
    val analisadorExecutor = remember { Executors.newSingleThreadExecutor() }

    AndroidView(
        modifier = modifier,
        factory  = { ctx ->
            val previewView = PreviewView(ctx)

            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                // Use case de preview — exibe o feed da câmera no PreviewView
                val preview = Preview.Builder().build().also {
                    it.surfaceProvider = previewView.surfaceProvider
                }

                // Use case de análise de imagem — processa frames com ML Kit
                val analise = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also { imageAnalysis ->
                        imageAnalysis.setAnalyzer(analisadorExecutor) { imageProxy ->
                            processarFrame(imageProxy, imageAnalysis, onCodigoLido)
                        }
                    }

                runCatching {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        analise
                    )
                }.onFailure { e ->
                    Log.e(TAG, "Erro ao iniciar câmera", e)
                }
            }, ContextCompat.getMainExecutor(ctx))

            previewView
        }
    )
}

/**
 * Processa um frame da câmera com o scanner de código de barras do ML Kit.
 * Ao detectar um código válido, para a análise e notifica via [onCodigoLido].
 *
 * @param imageProxy frame capturado pela CameraX
 * @param imageAnalysis referência ao use case — usado para parar após leitura
 * @param onCodigoLido callback com o valor raw do código detectado
 */
@androidx.annotation.OptIn(ExperimentalGetImage::class)
private fun processarFrame(
    imageProxy: androidx.camera.core.ImageProxy,
    imageAnalysis: ImageAnalysis,
    onCodigoLido: (String) -> Unit
) {
    val mediaImage = imageProxy.image
    if (mediaImage == null) {
        imageProxy.close()
        return
    }

    val imagem = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
    val scanner = BarcodeScanning.getClient()

    scanner.process(imagem)
        .addOnSuccessListener { codigos ->
            // Pega o primeiro código válido detectado no frame
            val codigo = codigos
                .firstOrNull { it.valueType != Barcode.TYPE_UNKNOWN }
                ?.rawValue

            if (codigo != null) {
                // Para a análise para evitar leituras duplicadas do mesmo código
                imageAnalysis.clearAnalyzer()
                onCodigoLido(codigo)
            }
        }
        .addOnFailureListener { e ->
            Log.w(TAG, "Falha na análise do frame", e)
        }
        .addOnCompleteListener {
            // Sempre fechar o proxy para liberar o buffer da câmera
            imageProxy.close()
        }
}
