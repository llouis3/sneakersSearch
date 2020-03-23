package com.sneakers.search

import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.objects.FirebaseVisionObjectDetectorOptions

class ObjectDetectorAnalyzer : ImageAnalysis.Analyzer {
    companion object {
        val options = FirebaseVisionObjectDetectorOptions.Builder()
            .setDetectorMode(FirebaseVisionObjectDetectorOptions.SINGLE_IMAGE_MODE)
            .enableMultipleObjects()
            .enableClassification()
            .build()
        val objectDetector = FirebaseVision.getInstance().getOnDeviceObjectDetector(options)
    }

    private fun degreesToFirebaseRotation(degrees: Int): Int = when(degrees) {
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
            objectDetector.processImage(image)
                .addOnSuccessListener { detectedObjects ->
                    for (objects in detectedObjects) {
                        val id = objects.trackingId
                        val bounds = objects.boundingBox
                        val category = objects.classificationCategory
                        val confidence = objects.classificationConfidence
                        Log.i("Objects id ", "$id $bounds $category $confidence")
                    }
                }
                .addOnFailureListener {
                    Log.i("Error ", it.toString())
                }
        }
    }
}
