package com.customer.offerswindow.utils.userimagecapture

import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.Group
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.customer.offerswindow.R
import com.customer.offerswindow.data.constant.Constants
import com.customer.offerswindow.databinding.DialoguePickActionBinding
import com.customer.offerswindow.utils.showToast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.*

class ActionBottomSheet : BottomSheetDialogFragment() {

    var _binding: DialoguePickActionBinding? = null
    val binding get() = _binding!!
    private lateinit var mBehavior: BottomSheetBehavior<*>
    private var image_uri: Uri? = null
    private var filePickMenuValue = 99

    private lateinit var resultLauncherFromCamera: ActivityResultLauncher<Uri>
    private lateinit var resultLauncherFromGallery: ActivityResultLauncher<Intent>
    private lateinit var requestMultiplePermissions: ActivityResultLauncher<Array<String>>
    private lateinit var resultLauncherFromVideo: ActivityResultLauncher<Uri>

    private var currentPhotoPath: String? = null
    private var currentVideoPath: String? = null

    private var tempImageUri: Uri? = null
    private var tempVideoUri: Uri? = null
    var selectedtype: String = ""
    var tempFile: File? = null

    private val readImagePermission =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) READ_MEDIA_IMAGES
        else READ_EXTERNAL_STORAGE

    companion object {
        const val TAG = "DialogChooseImageBottomSheet"
        var isFromReview = true
        var isLivecamera = false
        var onFileSelectedListener: OnFileSelectedListener? = null

        fun newInstance(
            isReview: Boolean = true,
            misLivecamera: Boolean = true,
            listener: OnFileSelectedListener
        ): ActionBottomSheet {
            isFromReview = isReview
            onFileSelectedListener = listener
            isLivecamera = misLivecamera
            return ActionBottomSheet()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        _binding = DialoguePickActionBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)
        mBehavior = BottomSheetBehavior.from(binding.root.parent as View)
//        mBehavior = BottomSheetBehavior.from(view.parent as View)
        //mBehavior.isDraggable = false
        mBehavior.skipCollapsed = true

        //mBehavior.isDraggable = false
        mBehavior.skipCollapsed = true
        if (isLivecamera) {
            binding?.groupVideo?.visibility = View.GONE
            binding.viewImage.visibility = View.GONE
            binding.viewVideo.visibility = View.GONE
            binding.viewGallery.visibility = View.GONE
            binding?.groupGallery?.visibility = View.GONE
            binding?.groupMyFiles?.visibility = View.GONE
        }
        initialize()

        return dialog
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onStart() {
        super.onStart()
        mBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        if (isFromReview) {
            binding.groupVideo.visibility = View.VISIBLE
            binding.viewImage.visibility = View.VISIBLE
        } else {
            binding.groupVideo.visibility = View.GONE
            binding.viewImage.visibility = View.GONE
        }
        setClickListener()
    }


    private fun setClickListener() {
        binding.groupImage.setAllOnClickListener {
            if (checkPermission()) openCamera()
            else requestPermission(101)
        }

        binding.groupVideo.setAllOnClickListener {
            if (checkPermission()) openVideo()
            else requestPermission(104)
        }

        binding.groupGallery.setAllOnClickListener {
            if (checkPermission()) openGallery()
            else requestPermission(102)
        }

        binding.groupMyFiles.setAllOnClickListener {
            if (checkPermission()) openMyFiles()
            else requestPermission(103)
        }
    }

    private fun checkPermission(): Boolean {
        return (ContextCompat.checkSelfPermission(
            this.requireContext(), CAMERA
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            this.requireContext(), READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestPermission(requestCode: Int) {
        filePickMenuValue = requestCode
        val arrayOfPermissions: Array<String> =
            if (requestCode == 101) arrayOf(CAMERA, READ_EXTERNAL_STORAGE)
            else arrayOf(READ_EXTERNAL_STORAGE)

        requestMultiplePermissions.launch(arrayOfPermissions)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        if (grantResults.isNotEmpty()) {
            when (requestCode) {
                101 -> {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) openCamera()
                }

                102 -> {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) openGallery()
                }

                103 -> {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) openMyFiles()
                }

                104 -> {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) openVideo()
                }
            }
        } else {
            showToast(requireActivity().getString(R.string.permission_denied))
        }
    }

    /*private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "VehiclePic")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Frm_Camera")
        image_uri =
            activity?.contentResolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        resultLauncherFromCamera.launch(image_uri)
    }*/

    private fun openCamera() {
        selectedtype = "Camera"
        tempImageUri = initTempUri()
        resultLauncherFromCamera.launch(tempImageUri!!)
    }

    private fun openVideo() {
        selectedtype = "Camera"
        tempVideoUri = initVideoTempUri()
        tempVideoUri?.let { resultLauncherFromVideo.launch(it) }

        /*val videoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        resultLauncherFromGallery.launch(videoIntent)*/
    }

    private fun openGallery() {
        selectedtype = "Gallery"
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        if (!isFromReview) intent.type = "image/*"
//        intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "video/*"))
        resultLauncherFromGallery.launch(intent)
    }

    private fun openMyFiles() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        if (isFromReview) {
            intent.type = "image/*|video/*"
            intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "video/*"))
        } else {
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*"))
//            intent.type = "image/*|application/*"
//            intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "application/*"))
        }
        resultLauncherFromGallery.launch(intent)
    }

    interface OnFileSelectedListener {
        fun onFileSelected(uri: Uri?, value: String, flag: String, file: File?)
    }

    private fun initialize() {
        requestMultiplePermissions =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                if (permissions.entries.isNotEmpty()) {
                    when (filePickMenuValue) {
                        101 -> {
                            if (permissions[CAMERA] == true && permissions[READ_EXTERNAL_STORAGE] == true) openCamera()
                            else {
                                if (permissions[CAMERA] == false) {
                                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                                            requireActivity(), CAMERA
                                        )
                                    ) {
                                        showDialog(permissions)
                                    } else {
                                        openCamera()
                                    }
                                } else if (permissions[READ_EXTERNAL_STORAGE] == false) {
                                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                                            requireActivity(), READ_EXTERNAL_STORAGE
                                        )
                                    ) {
                                        showDialog(permissions)
                                    } else {
                                        openCamera()
                                    }
                                }
                            }
                        }

                        102 -> {
                            if (permissions[READ_EXTERNAL_STORAGE] == true) openGallery()
                            else {
                                if (ActivityCompat.shouldShowRequestPermissionRationale(
                                        requireActivity(), READ_EXTERNAL_STORAGE
                                    )
                                ) {
                                    showDialog(permissions)
                                } else {
                                    openGallery()
                                }
                            }
                        }

                        103 -> {
                            if (permissions[READ_EXTERNAL_STORAGE] == true) openMyFiles()
                            if (ActivityCompat.shouldShowRequestPermissionRationale(
                                    requireActivity(), READ_EXTERNAL_STORAGE
                                )
                            ) {
                                showDialog(permissions)
                            } else {
                                openMyFiles()
                            }
                        }

                        104 -> {
                            if (permissions[READ_EXTERNAL_STORAGE] == true) openVideo()
                        }
                    }
                } else {
                    showToast(requireActivity().getString(R.string.permission_denied))
                }

            }

        resultLauncherFromCamera =
            registerForActivityResult(ActivityResultContracts.TakePicture()) {
                if (it) {
                    Log.d("imageUri--", tempImageUri.toString())
                    onFileSelectedListener?.onFileSelected(
                        tempImageUri, Constants.IMAGE, "Camera", tempFile
                    )
                } else {
                    showToast("Please capture image")
                }
                dismiss()
            }

        resultLauncherFromVideo =
            registerForActivityResult(ActivityResultContracts.CaptureVideo()) {
                if (it) {
                    Log.d("videoUri--", tempVideoUri.toString())
                    onFileSelectedListener?.onFileSelected(
                        tempVideoUri, Constants.VIDEO, selectedtype, tempFile
                    )
                } else {
                    showToast("Please record video")
                }
                dismiss()
            }

        resultLauncherFromGallery =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                    // There are no request codes
                    try {
                        val uri = result.data!!.data

                        Log.d("uri", uri.toString())
                        Log.d("path", uri?.path.toString())

                        val mediaTypeRaw = uri?.let { getMediaType(it) }
                        Log.d("receivedType", mediaTypeRaw.toString())

                        /*val contentResolver = requireContext().contentResolver
                        val stringMimeType = contentResolver.getType(uri!!)
                        val stringFileType = MimeTypeMap.getSingleton().getExtensionFromMimeType(stringMimeType)
                        Log.d("mime", stringFileType!!)*/

                        if (mediaTypeRaw == MediaType.MediaTypeImage) {
                            //handle image
                            Log.d("mime", "image")
                            onFileSelectedListener?.onFileSelected(
                                uri, Constants.IMAGE, selectedtype, tempFile
                            )
                        } else if (mediaTypeRaw == MediaType.MediaTypeVideo) {
                            //handle video
                            Log.d("mime", "video")
                            onFileSelectedListener?.onFileSelected(
                                uri, Constants.VIDEO, selectedtype, tempFile
                            )
                        }
                        dismiss()

                    } catch (e: Exception) {
                        showToast("something went wrong")
                    }

                } else showToast("Please select any file")
            }
    }

    private fun initTempUri(): Uri {
        //gets the temp_images dir
        val tempImagesDir = File(
            requireContext().filesDir, //this function gets the external cache dir
            getString(R.string.temp_images_dir)
        ) //gets the directory for the temporary images dir

        tempImagesDir.mkdir() //Create the temp_images dir

        //Creates the temp_image.jpg file
        val tempImage = File(
            tempImagesDir, //prefix the new abstract path with the temporary images dir path
            getString(R.string.temp_image)
        ) //gets the abstract temp_image file name

        currentPhotoPath = tempImage.absolutePath
        tempFile = tempImage
        //Returns the Uri object to be used with ActivityResultLauncher
        return FileProvider.getUriForFile(
            requireContext(), getString(R.string.authorities), tempImage
        )
    }

    private fun initVideoTempUri(): Uri? {
        try {
            val tempVideoDir = File(
                requireContext().filesDir, getString(R.string.temp_images_dir)
            )

            if (!tempVideoDir.exists()) {
                tempVideoDir.mkdir()
            }

            val videoName = "vid_" + System.currentTimeMillis() + ".mp4"
            val sdImageMainDirectory = File(tempVideoDir, videoName)

            return FileProvider.getUriForFile(
                requireContext(), getString(R.string.authorities), sdImageMainDirectory
            )

        } catch (ex: Exception) {
            Log.d("videoError", "startRecording ${ex.localizedMessage}")
            return null
        }
    }

    enum class MediaType {
        MediaTypeImage, MediaTypeVideo, Unknown
    }

    private fun getMediaType(source: Uri): MediaType {
        val mediaTypeRaw = requireActivity().contentResolver.getType(source)
        Log.d("mediaType", mediaTypeRaw.toString())
        if (mediaTypeRaw?.startsWith(Constants.IMAGE) == true) return MediaType.MediaTypeImage
        if (mediaTypeRaw?.startsWith(Constants.VIDEO) == true) return MediaType.MediaTypeVideo
        return MediaType.Unknown
    }

    private fun showDialog(permissions: Map<String, @JvmSuppressWildcards Boolean>) {
        MaterialAlertDialogBuilder(requireContext()).setTitle("Permission Required")
            .setMessage("Please Grant ${if (permissions[READ_EXTERNAL_STORAGE] == false) "Storage permission" else "Camera permission"}")
            .setNegativeButton("No") { d, _ ->
                d.dismiss()
            }.setPositiveButton("Ok") { d, _ ->
                requestPermission(filePickMenuValue)
                d.dismiss()
            }.show()
    }


    private fun openSettings() {
        val intent = Intent()
        intent.data = Uri.fromParts("package", requireActivity().packageName, null)
        intent.action = Uri.decode(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        startActivity(intent)
    }

    private fun both() {
        tempImageUri = initTempUri()

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempImageUri)
        takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        val takeVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        val chooserIntent = Intent.createChooser(takePictureIntent, "Capture Image or Video")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(takeVideoIntent))
        resultLauncherFromGallery.launch(chooserIntent)
    }

    private fun showCameraDialog() {
        MaterialAlertDialogBuilder(requireContext()).setTitle("Select Any Option")
            .setNegativeButton("Take Picture") { d, _ ->
                openCamera()
                d.dismiss()
            }.setPositiveButton("Capture Video") { d, _ ->
                openVideo()
                d.dismiss()
            }.show()
    }

    /*
    * Method to click group of items at single shot.
    * */
    private fun Group.setAllOnClickListener(listener: View.OnClickListener?) {
        referencedIds.forEach { id ->
            rootView.findViewById<View>(id).setOnClickListener(listener)
        }
    }


    fun getBytes(inputStream: InputStream): ByteArray {
        val byteBuffer = ByteArrayOutputStream()
        val bufferSize = 1024
        val buffer = ByteArray(bufferSize)
        var len: Int
        while (inputStream.read(buffer).also { len = it } != -1) {
            byteBuffer.write(buffer, 0, len)
        }
        return byteBuffer.toByteArray()
    }


}

