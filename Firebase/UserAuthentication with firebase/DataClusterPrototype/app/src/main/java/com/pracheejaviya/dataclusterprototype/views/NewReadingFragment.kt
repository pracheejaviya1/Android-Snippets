package com.pracheejaviya.dataclusterprototype.views

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
import java.io.FileNotFoundException

@RequiresApi(Build.VERSION_CODES.N)

class NewReadingFragment : Fragment() {
    private var imageCapture: ImageCapture? = null
    private lateinit var preview: Preview
    private lateinit var camera: Camera
    private val uriArrayList: MutableList<Uri> = mutableListOf()
    private var lensFacing = CameraSelector.DEFAULT_BACK_CAMERA


    private val db = FirebaseFirestore.getInstance()
    var currentuserUID = FirebaseAuth.getInstance().currentUser!!.uid
    val taskID1 = "PRRDYA984C"


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
                arrayOf(REQUIRED_PERMISSIONS[0]), 101
            )
        }
    }

    private fun checkPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(),
            REQUIRED_PERMISSIONS[0]
        ) == PackageManager.PERMISSION_GRANTED
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
                    this, cameraSelector, preview, imageCapture
                )

            } catch (exc: Exception) {
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
                val buffer = image.planes[0].buffer
                val bytes = ByteArray(buffer.capacity()).also { buffer.get(it) }
                val rotatedBitmap = rotateBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.size))

                imagePreview.makeVisible()
                imagePreview.apply {
                        setImageBitmap(rotatedBitmap)
                        logV("IMAGE SET")

                }
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        val uri = rotatedBitmap?.let { getUri(context, it) }
                        uri?.let { uriArrayList.add(it) }
                    }
                }
                image.close()
            }
                override fun onError(exception: ImageCaptureException) {
                    val errorType = exception.imageCaptureError
                    logV(exception.localizedMessage)
                }
            })

        cancelCapture.setOnClickListener {
            imagePreview.makeGone()
            cancelCapture.makeGone()
            capturedImage.makeGone()
            startCamera()
        }
    }

    private fun rotateBitmap(decodeByteArray: Bitmap?): Bitmap? {
        val width = decodeByteArray?.width
        val height = decodeByteArray?.height

        val matrix = Matrix()
        if (lensFacing == CameraSelector.DEFAULT_FRONT_CAMERA) {
            matrix.postRotate(90f)
            matrix.preScale(-1f,1f)
        }
        else {
            matrix.postRotate(90f)
        }
        val rotatedImage = Bitmap.createBitmap(decodeByteArray!!, 0, 0, width!!, height!!, matrix, true)
        decodeByteArray.recycle()
        return rotatedImage
    }
    companion object {
        fun newInstance() = NewReadingFragment()
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}