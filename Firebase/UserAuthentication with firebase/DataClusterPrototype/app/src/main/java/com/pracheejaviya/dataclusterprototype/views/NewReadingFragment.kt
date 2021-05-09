package com.pracheejaviya.dataclusterprototype.views

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.lifecycle.lifecycleScope

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.pracheejaviya.dataclusterprototype.R
import com.pracheejaviya.dataclusterprototype.extensions.getUri
import com.pracheejaviya.dataclusterprototype.extensions.logV
import com.pracheejaviya.dataclusterprototype.extensions.makeGone
import com.pracheejaviya.dataclusterprototype.extensions.makeVisible
import kotlinx.android.synthetic.main.fragment_new_reading.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File


class NewReadingFragment : Fragment() {
    private var imageCapture: ImageCapture? = null
    private var lensFacing = CameraSelector.DEFAULT_BACK_CAMERA
    private val uriArrayList: MutableList<Uri> = mutableListOf()

    private lateinit var preview: Preview
    private lateinit var camera: Camera


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_reading, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkRequirements()

    }

    private fun checkRequirements() {
        if (checkPermission())
            init()
        else {
            requirePermission()
        }
    }

    private fun requirePermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                REQUIRED_PERMISSIONS[0]
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                REQUIRED_PERMISSIONS[0]
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf<String>(REQUIRED_PERMISSIONS[0]), 1)
        }
    }

    private fun checkPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(requireContext(), REQUIRED_PERMISSIONS[0]) == PackageManager.PERMISSION_GRANTED
    }

    private fun init() {
        startCamera()
        setListeners()

    }

    private fun setListeners() {
        camera_capture_button.setOnClickListener { takePhoto() }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            // Preview
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .build()

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture)

            } catch(exc: Exception) {
                Log.e(ContentValues.TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePhoto() {

        val imageCapture = imageCapture ?: return

        cancelCapture.makeVisible()
        capturedImage.makeVisible()

        imageCapture.takePicture(ContextCompat.getMainExecutor(context),object : ImageCapture.OnImageCapturedCallback() {

            override fun onCaptureSuccess(image: ImageProxy) {
                capturedImage.setOnClickListener{
//                    val buffer = image.planes[0].buffer
//                    val bytes = ByteArray(buffer.capacity()).also { buffer.get(it) }
//                    lifecycleScope.launch {
//                        withContext(Dispatchers.IO) {
//
//                            val rotatedBitmap = rotateBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.size))
//                            val uri = rotatedBitmap?.let { getUri(requireContext(), it) }
//                            uri?.let { uriArrayList.add(it) }
//                            withContext(Dispatchers.Main) {
//                                logV("captured")
//                            }
////                            val file = File("uri")
////                            try {
////                                val stream = ByteArrayOutputStream()
////                                rotatedBitmap?.compress(Bitmap.CompressFormat.PNG, 100, stream)
////                                val byteArray: ByteArray = stream.toByteArray()
////                                rotatedBitmap?.recycle()
////                                file.writeBytes(byteArray)
////                            } catch (e: java.io.FileNotFoundException) {
////                                logV("File not found")
////                            }
//                        }
//                    }
//                    image.close()
                }
            }
            override fun onError(exception: ImageCaptureException) {
                val errorType = exception.imageCaptureError
                logV(exception.localizedMessage)
            }
        })

        cancelCapture.setOnClickListener{
            cancelCapture.makeGone()
            capturedImage.makeGone()
            startCamera()
        }
    }





    companion object {
        fun newInstance() = NewReadingFragment()
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}