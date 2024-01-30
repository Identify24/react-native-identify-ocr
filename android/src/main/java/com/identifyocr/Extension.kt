package com.identifyocr

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.Image
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Size
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.CheckResult
import androidx.annotation.ColorInt
import androidx.camera.core.*
import androidx.camera.core.Camera
import androidx.camera.view.PreviewView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt


fun <T> Fragment.observe(data: LiveData<T>, block: (T) -> Unit) {
    data.observe(this, Observer(block))
}


fun Int.toDp() : Float{
    return this * Resources.getSystem().displayMetrics.density
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Image.convertYuv420888ImageToBitmap(): Bitmap? {
    require(this.format == ImageFormat.YUV_420_888) {
        "Unsupported image format $(image.format)"
    }

    this.planes.forEach {
        it?.let { plane->
            if(plane.buffer == null){
                return null
            }
        }
    }

    val planes = this.planes

    // Because of the variable row stride it's not possible to know in
    // advance the actual necessary dimensions of the yuv planes.
    lateinit var yuvBytes:List<ByteArray>
    val yRowStride:Int
    val uvRowStride:Int
    val uvPixelStride:Int
    try {
        yuvBytes = planes.map { plane ->
            val buffer = plane.buffer
            val yuvBytes = ByteArray(buffer.capacity())
            buffer[yuvBytes]
            buffer.rewind()  // Be kind…
            yuvBytes
        }

        yRowStride = planes[0].rowStride
        uvRowStride = planes[1].rowStride
        uvPixelStride = planes[1].pixelStride
    }
    catch (e:Exception){
        return null
    }

    val width = this.width
    val height = this.height
    @ColorInt val argb8888 = IntArray(width * height)
    var i = 0
    for (y in 0 until height) {
        val pY = yRowStride * y
        val uvRowStart = uvRowStride * (y shr 1)
        for (x in 0 until width) {
            val uvOffset = (x shr 1) * uvPixelStride
            argb8888[i++] =
                yuvToRgb(
                    yuvBytes[0][pY + x].toIntUnsigned(),
                    yuvBytes[1][uvRowStart + uvOffset].toIntUnsigned(),
                    yuvBytes[2][uvRowStart + uvOffset].toIntUnsigned()
                )
        }
    }
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    bitmap.setPixels(argb8888, 0, width, 0, 0, width, height)
    return bitmap
}

private fun Byte.toIntUnsigned(): Int {
    return toInt() and 0xFF
}

private val CHANNEL_RANGE = 0 until (1 shl 18)

@ColorInt
private fun yuvToRgb(nY: Int, nU: Int, nV: Int): Int {
    var nY = nY
    var nU = nU
    var nV = nV
    nY -= 16
    nU -= 128
    nV -= 128
    nY = nY.coerceAtLeast(0)

    // This is the floating point equivalent. We do the conversion in integer
    // because some Android devices do not have floating point in hardware.
    // nR = (int)(1.164 * nY + 2.018 * nU);
    // nG = (int)(1.164 * nY - 0.813 * nV - 0.391 * nU);
    // nB = (int)(1.164 * nY + 1.596 * nV);
    var nR = 1192 * nY + 1634 * nV
    var nG = 1192 * nY - 833 * nV - 400 * nU
    var nB = 1192 * nY + 2066 * nU

    // Clamp the values before normalizing them to 8 bits.
    nR = nR.coerceIn(CHANNEL_RANGE) shr 10 and 0xff
    nG = nG.coerceIn(CHANNEL_RANGE) shr 10 and 0xff
    nB = nB.coerceIn(CHANNEL_RANGE) shr 10 and 0xff
    return -0x1000000 or (nR shl 16) or (nG shl 8) or nB
}




fun EditText.changed(changed: () -> Unit){
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {

        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if (p0?.length == 1) {
                changed()
            }
        }

    })
}

fun Bitmap.rotateImage(angle: Float): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(angle)
    return Bitmap.createBitmap(this, 0, 0, this.width, this.height, matrix, true)
}

fun Bitmap.createFlippedBitmap(xFlip: Boolean, yFlip: Boolean): Bitmap? {
    val matrix = Matrix()
    matrix.postScale(
        if (xFlip) -1f else 1f,
        if (yFlip) -1f else 1f,
        this.width / 2f,
        this.height / 2f
    )
    return Bitmap.createBitmap(this, 0, 0, this.width, this.height, matrix, true)
}


fun Image.toBitmap(): Bitmap {
    val buffer = planes[0].buffer
    buffer.rewind()
    val bytes = ByteArray(buffer.capacity())
    buffer.get(bytes)
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}

fun Bitmap.cropImage(previewSize: Size, cardFinder: Rect): Bitmap {

    if (cardFinder.right > previewSize.width)  cardFinder.right = previewSize.width
    if (cardFinder.left < 0) cardFinder.left = 0
    if (cardFinder.bottom > previewSize.height) cardFinder.bottom = previewSize.height
    if (cardFinder.top < 0) cardFinder.top = 0

    // Scale the previewImage to match the fullImage
    val scaledPreviewImage = previewSize.scaleAndCenterWithin(this.size())
    val previewScale = scaledPreviewImage.width().toFloat() / previewSize.width

    // Scale the cardFinder to match the scaledPreviewImage
    val scaledCardFinder = Rect(
        (cardFinder.left * previewScale).roundToInt(),
        (cardFinder.top * previewScale).roundToInt(),
        (cardFinder.right * previewScale).roundToInt(),
        (cardFinder.bottom * previewScale).roundToInt()
    )

    // Position the scaledCardFinder on the fullImage
    val cropRect = Rect(
        max(0, scaledCardFinder.left + scaledPreviewImage.left),
        max(0, scaledCardFinder.top + scaledPreviewImage.top),
        min(this.width, scaledCardFinder.right + scaledPreviewImage.left),
        min(this.height, scaledCardFinder.bottom + scaledPreviewImage.top)
    )

    return this.crop(cropRect)
}

fun Bitmap.crop(crop: Rect): Bitmap {
    if (crop.right > this.width)  crop.right = this.width
    if (crop.left < 0 ) crop.left = 0
    if (crop.bottom > this.height) crop.bottom = this.height
    if ( crop.top < 0 ) crop.top = 0
    return Bitmap.createBitmap(this, crop.left, crop.top, crop.width(), crop.height())
}


fun Bitmap?.convertBitmapToBase64(width: Int, height: Int, quality: Int) : String{
    this?.let {
        val newBitmap = Bitmap.createScaledBitmap(it, width, height, false)
        val byteArrayOutputStream = ByteArrayOutputStream()
        newBitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)
        val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }
    return ""
}

/**
 * Get the size of a bitmap.
 */
fun Bitmap.size(): Size = Size(this.width, this.height)

@CheckResult
fun Size.scaleAndCenterWithin(containingSize: Size): Rect {
    val aspectRatio = width.toFloat() / height

    // Since the preview image may be at a different resolution than the full image, scale the
    // preview image to be circumscribed by the fullImage.
    val scaledSize = maxAspectRatioInSize(containingSize, aspectRatio)
    val left = (containingSize.width - scaledSize.width) / 2
    val top = (containingSize.height - scaledSize.height) / 2
    return Rect(
        /* left */ left,
        /* top */ top,
        /* right */ left + scaledSize.width,
        /* bottom */ top + scaledSize.height
    )
}

@CheckResult
fun maxAspectRatioInSize(area: Size, aspectRatio: Float): Size {
    var width = area.width
    var height = (width / aspectRatio).roundToInt()

    return if (height <= area.height) {
        Size(area.width, height)
    } else {
        height = area.height
        width = (height * aspectRatio).roundToInt()
        Size(min(width, area.width), height)
    }
}


fun  ImageProxy.calculateLuminance() : Double{
    val buffer = this.planes[0].buffer
    // Extract image data from callback object
    val data = buffer.toByteArray()
    // Convert the data into an array of pixel values
    val pixels = data.map { it.toInt() and 0xFF }
    // Compute average luminance for the image
    return pixels.average()
}

fun ByteBuffer.toByteArray(): ByteArray {
    rewind() // Rewind the buffer to zero
    val data = ByteArray(remaining())
    get(data) // Copy the buffer into a byte array
    return data // Return the byte array
}



fun PreviewView?.focusCameraFromTouchListener(camera: Camera?){
    this?.let { previewView->
        previewView.setOnTouchListener(View.OnTouchListener { view: View, motionEvent: MotionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> true
                MotionEvent.ACTION_UP -> {
                    // Get the MeteringPointFactory from PreviewView
                    val factory = previewView.getMeteringPointFactory()

                    // Create a MeteringPoint from the tap coordinates
                    val point = factory.createPoint(motionEvent.x, motionEvent.y)

                    // Create a MeteringAction from the MeteringPoint, you can configure it to specify the metering mode
                    val action = FocusMeteringAction.Builder(point).build()

                    // Trigger the focus and metering. The method returns a ListenableFuture since the operation
                    // is asynchronous. You can use it get notified when the focus is successful or if it fails.
                    camera?.cameraControl?.startFocusAndMetering(action)

                    true
                }
                else ->  false
            }
        })
    }
}

fun Fragment.getLightListener(calculatedPercent :(value : Int) -> Unit) : SensorEventListener{
    return object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            if(event?.sensor?.type == Sensor.TYPE_LIGHT){
                val percent = event.values[0].toInt()
                calculatedPercent(if (percent/5 >= 100)  100 else (percent/5))

            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        }
    }
}

fun Int.toPx() : Int = (this * Resources.getSystem().displayMetrics.density).toInt()

fun Context.getSensorManager(): SensorManager? = this.getSystemService(Context.SENSOR_SERVICE) as SensorManager?

fun Fragment.registerSensor(sensorEventListener: SensorEventListener,noLightSensor : () -> Unit,yesLightSensor : () -> Unit){
    requireContext().getSensorManager()?.let { sensorM->
        val lightSensor = sensorM.getDefaultSensor(Sensor.TYPE_LIGHT) as Sensor?
        lightSensor?.let { sensor->
            sensorM.registerListener(
                sensorEventListener,
                sensor,
                SensorManager.SENSOR_DELAY_NORMAL)
            yesLightSensor()
        } ?: run{
            noLightSensor()
        }
    }
}

fun Fragment.unRegisterSensor(sensorEventListener: SensorEventListener){
    requireContext().getSensorManager()?.let { sensorM->
        sensorM.unregisterListener(sensorEventListener)
    }
}




