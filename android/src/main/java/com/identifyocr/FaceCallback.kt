package com.identifyocr

import com.identifyocr.model.entities.FaceEntity

interface FaceCallback {
    fun getFace(faceEntity : FaceEntity)
    fun faceNotFoundError()
}
