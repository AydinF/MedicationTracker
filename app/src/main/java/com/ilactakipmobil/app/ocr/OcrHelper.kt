package com.ilactakipmobil.app.ocr

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

object OcrHelper {

    fun processImage(
        context: Context,
        imageUri: Uri,
        onSuccess: (IlacOcrResult) -> Unit,
        onError: (Exception) -> Unit
    ) {
        try {
            val image = InputImage.fromFilePath(context, imageUri)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val result = analyzeText(visionText.text)
                    onSuccess(result)
                }
                .addOnFailureListener { e ->
                    onError(e)
                }
        } catch (e: Exception) {
            onError(e)
        }
    }

    fun processBitmap(
        bitmap: Bitmap,
        onSuccess: (IlacOcrResult) -> Unit,
        onError: (Exception) -> Unit
    ) {
        try {
            val image = InputImage.fromBitmap(bitmap, 0)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val result = analyzeText(visionText.text)
                    onSuccess(result)
                }
                .addOnFailureListener { e ->
                    onError(e)
                }
        } catch (e: Exception) {
            onError(e)
        }
    }

    fun analyzeText(rawText: String): IlacOcrResult {
        val lines = rawText.split("\n").map { it.trim() }.filter { it.isNotEmpty() }

        var teshisEdilenAd = ""
        var teshisEdilenGuc = ""
        var teshisEdilenAdet = ""

        // 1. Doz/Ürün Gücü Ara (Örn: 500 mg, 1000 mg, 10mg)
        val gucRegex = Regex("""(?i)\b\d+(\.\d+)?\s*(mg|g|mcg|ml|iu)\b""")
        val gucMatch = gucRegex.find(rawText)
        if (gucMatch != null) {
            teshisEdilenGuc = gucMatch.value.uppercase()
        }

        // 2. Tablet/Kapsül Adedi Ara (Örn: 20 Tablet, 30 Kapsül, 10 Draje)
        val adetRegex = Regex("""(?i)\b\d+\s*(tablet|kapsül|kapsul|ampul|saşe|sase|flakon|draje|tb)\b""")
        val adetMatch = adetRegex.find(rawText)
        if (adetMatch != null) {
            teshisEdilenAdet = adetMatch.value
        }

        // 3. İlaç Adı Tespiti
        val gecerliSatirlar = lines.filter { satir ->
            val s = satir.uppercase()
            !s.contains("S.K.T") &&
            !s.contains("PARTI") &&
            !s.contains("BATCH") &&
            !s.contains("EXP") &&
            !s.contains("FİYATI") &&
            !s.contains("TL") &&
            !s.contains("REÇETE") &&
            !s.contains("SADECE") &&
            !s.contains("KULLANIM") &&
            satir.length >= 3
        }

        if (gecerliSatirlar.isNotEmpty()) {
            val adayAd = gecerliSatirlar.firstOrNull { satir ->
                !satir.contains("mg", ignoreCase = true) &&
                !satir.contains("tablet", ignoreCase = true) &&
                !satir.contains("kapsül", ignoreCase = true)
            } ?: gecerliSatirlar.first()

            teshisEdilenAd = adayAd.replace(Regex("""[^a-zA-ZğüşıöçĞÜŞİÖÇ\s]"""), "").trim()
        }

        val birlesikDozaj = buildString {
            if (teshisEdilenGuc.isNotEmpty()) append(teshisEdilenGuc)
            if (teshisEdilenAdet.isNotEmpty()) {
                if (isNotEmpty()) append(" - ")
                append(teshisEdilenAdet)
            }
            if (isEmpty()) append("1 Tablet")
        }

        return IlacOcrResult(
            ilacAdi = teshisEdilenAd,
            urunGucuDozaj = birlesikDozaj,
            tabletSayisi = teshisEdilenAdet,
            hamMetin = rawText
        )
    }
}
