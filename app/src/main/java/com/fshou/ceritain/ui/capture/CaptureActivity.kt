package com.fshou.ceritain.ui.capture

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import coil.load
import coil.transform.RoundedCornersTransformation
import com.fshou.ceritain.R
import com.fshou.ceritain.createCustomTempFile
import com.fshou.ceritain.databinding.ActivityCaptureBinding
import com.fshou.ceritain.ui.post.PostActivity


class CaptureActivity : AppCompatActivity(), ImageCapture.OnImageSavedCallback {

    private lateinit var binding: ActivityCaptureBinding
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var imageCapture: ImageCapture? = null
    private val viewModel: CaptureViewModel by viewModels()

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    private val requestCameraPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        handlePermissionGranted(isGranted)
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            viewModel.setCurrentImageUri(uri)
        } else {
            showToast(getString(R.string.no_media_selected))
        }
    }

    private fun handlePermissionGranted(isGranted: Boolean) {
        if (isGranted) {
            startCamera()
        } else {
            showToast(getString(R.string.permission_request_denied))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCaptureBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        WindowCompat.getInsetsController(window,binding.root).isAppearanceLightStatusBars = true



        with(binding) {
            topAppBar.setNavigationOnClickListener { finishAfterTransition() }
            gallery.setOnClickListener {
                launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        }

        if (!allPermissionsGranted()) {
            requestCameraPermission.launch(REQUIRED_PERMISSION)
            return
        }


        with(binding) {
            switchCamera.setOnClickListener {
                cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)
                    CameraSelector.DEFAULT_FRONT_CAMERA
                else CameraSelector.DEFAULT_BACK_CAMERA

                startCamera()
            }
            capture.setOnClickListener {
                takePhoto()
            }

            chooseImg.setOnClickListener {
                val intent = Intent(this@CaptureActivity, PostActivity::class.java)
                intent.putExtra(EXTRA_IMG_URI, viewModel.currentImageUri.value.toString())
                startActivity(intent)
                finish()
            }

            clearImage.setOnClickListener {
                viewModel.setCurrentImageUri(null)
                showCameraAction()
                startCamera()
            }

        }
        startCamera()
        viewModel.currentImageUri.observe(this) {
            if (it != null) {
                showPreviewImage(it)
                showConfirmationAction()
            }
        }
    }

    private fun showConfirmationAction() {
        with(binding) {
            gallery.visibility = View.GONE
            capture.visibility = View.GONE
            switchCamera.visibility = View.GONE
            clearImage.visibility = View.VISIBLE
            chooseImg.visibility = View.VISIBLE
        }
    }

    private fun showCameraAction() {
        with(binding) {
            gallery.visibility = View.VISIBLE
            capture.visibility = View.VISIBLE
            switchCamera.visibility = View.VISIBLE
            clearImage.visibility = View.GONE
            chooseImg.visibility = View.GONE
        }
    }

    private fun showPreviewImage(uri: Uri) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(application)
        val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
        cameraProvider.unbindAll()
        binding.imgPreview.load(uri) {
            crossfade(true)
            transformations(RoundedCornersTransformation(30f))
        }
        binding.container.visibility = View.GONE
    }

    private fun startCamera() {
        binding.container.visibility = View.VISIBLE
        val cameraProviderFuture = ProcessCameraProvider.getInstance(application)
        val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
        cameraProviderFuture.addListener({
            val preview = Preview.Builder().build()
            imageCapture = ImageCapture.Builder().build()
            preview.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this@CaptureActivity,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (exc: Exception) {
                showToast(getString(R.string.failed_to_show_camera))
                Log.e(TAG, "startCamera: ${exc.message}")
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        val photoFile = createCustomTempFile(this)
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            this@CaptureActivity
        )
    }


    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
        outputFileResults.savedUri?.let { viewModel.setCurrentImageUri(it) }
    }

    override fun onError(exception: ImageCaptureException) {
        showToast(getString(R.string.failed_to_capture))
    }

    private fun showToast(msg: String) =
        Toast.makeText(this@CaptureActivity, msg, Toast.LENGTH_LONG).show()


    override fun onDestroy() {
        super.onDestroy()
        val cameraProviderFuture = ProcessCameraProvider.getInstance(application)
        val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
        cameraProvider.unbindAll()
        imageCapture = null
    }

    companion object {
        const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
        const val TAG = "CaptureActivity"
        const val EXTRA_IMG_URI = "img_uri"
    }

}