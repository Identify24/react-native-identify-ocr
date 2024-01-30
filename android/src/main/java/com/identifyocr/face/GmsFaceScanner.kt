package com.identifyocr.face

import android.graphics.Bitmap
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

class GmsFaceScanner  {

    var faceListener : GmsFaceProcessorListener?= null

    private val faceDetectorOptions by lazy {
        FaceDetectorOptions.Builder()
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setMinFaceSize(0.9f)
            .build()
    }

     private val detector by lazy {
        FaceDetection.getClient(faceDetectorOptions)
    }



    fun detectFace(image: ImageProxy?, bitmap : Bitmap?) {
        bitmap?.let {
            val inputImage = InputImage.fromBitmap(bitmap,0)
            detector.process(inputImage)
                .addOnSuccessListener { faces ->
                    if (faces.isNotEmpty() && faces.size == 1) {
                        faceListener?.success(face = faces.first())
                    }else{
                        faceListener?.error()
                    }
                }
                .addOnFailureListener {
                    faceListener?.error()
                }
                .addOnCompleteListener {
                    image?.close()
                }
        }
    }


    fun killGmsFaceService(){
        faceListener = null
        detector.close()
    }
}
