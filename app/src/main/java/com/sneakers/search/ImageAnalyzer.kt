package com.sneakers.search

import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler

class ImageAnalyzer : ImageAnalysis.Analyzer {
    private fun degreesToFirebaseRotation(degrees: Int): Int = when (degrees) {
        0 -> FirebaseVisionImageMetadata.ROTATION_0
        90 -> FirebaseVisionImageMetadata.ROTATION_90
        180 -> FirebaseVisionImageMetadata.ROTATION_180
        270 -> FirebaseVisionImageMetadata.ROTATION_270
        else -> throw Exception("Rotation must be 0, 90, 180, or 270.")
    }

    override fun analyze(image: ImageProxy?, rotationDegrees: Int) {
        val mediaImage = image?.image
        val imageRotation = degreesToFirebaseRotation(rotationDegrees)
        if (mediaImage != null) {
            val image = FirebaseVisionImage.fromMediaImage(mediaImage, imageRotation)
            val labeler = FirebaseVision.getInstance().onDeviceImageLabeler
            labeler.processImage(image)
                .addOnSuccessListener { labels ->
                    for (label in labels){
                        val text = label.text
                        val entityId = label.entityId
                        val confidence = label.confidence
                        Log.i("analyse image ", text)
                        Log.i("analyse image ", entityId)
                        Log.i("analyse image ", confidence.toString())
                    }
                }
                .addOnFailureListener{
                    println(it)
                }
        }
    }
}