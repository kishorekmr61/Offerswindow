package com.customer.offerswindow.ui.manageprofile

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.Group
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.canhub.cropper.CropImageView
import com.customer.offerswindow.R
import com.customer.offerswindow.data.constant.Constants
import com.customer.offerswindow.data.helpers.AppPreference
import com.customer.offerswindow.databinding.FragmentManageProfileBinding
import com.customer.offerswindow.helper.NetworkResult
import com.customer.offerswindow.model.CustomerData
import com.customer.offerswindow.ui.customerprofile.CustomerProfileViewModel
import com.customer.offerswindow.utils.ShowFullToast
import com.customer.offerswindow.utils.convertDate
import com.customer.offerswindow.utils.getFilePathFromUri
import com.customer.offerswindow.utils.setWhiteToolBar
import com.customer.offerswindow.utils.showToast
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.default
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.ByteArrayOutputStream
import java.io.File


@AndroidEntryPoint
class ManageProfileFragment : Fragment(), CropImageView.OnCropImageCompleteListener {
    private var _binding: FragmentManageProfileBinding? = null
    private val binding get() = _binding!!
    private val manageProfileViewModel: ManageProfileViewModel by viewModels()
    private val viewModel: CustomerProfileViewModel by viewModels()
    private var customerData: CustomerData? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedState: Bundle?
    ): View {
        _binding = FragmentManageProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = manageProfileViewModel
        binding.executePendingBindings()
        init()
        setObserver()
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.cropImageView.setOnCropImageCompleteListener(this)
    }

    private fun init() {
        activity?.setWhiteToolBar("")
        manageProfileViewModel.isImgCaptured.set(false)
        val profileURL = AppPreference.read(Constants.PROFILE_IMAGE_URL, "").toString()
        customerData =
            Gson().fromJson(arguments?.getString(Constants.Customertype), CustomerData::class.java)
        if (profileURL.isNullOrEmpty()) {
            arguments?.let {
                binding.cropImageView.setImageUriAsync(
                    Uri.parse(
                        it.getString(Constants.BUNDLE_IMG_PATH).toString()
                    )
                )
            }
        } else {
            AppPreference.read(Constants.PROFILE_IMAGE_URL, "")?.isNullOrEmpty().let {
                it?.not().let {
                    binding.llLoader.visibility = View.VISIBLE
                    Glide.with(this)
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .load(
                            AppPreference.read(Constants.PROFILE_IMAGE_URL, "")
                                .toString()
                        ).error(R.drawable.ic_profile)
                        .into(object : CustomTarget<Bitmap>() {
                            override fun onResourceReady(
                                resource: Bitmap,
                                transition: Transition<in Bitmap>?
                            ) {
                                binding.llLoader.visibility = View.GONE
                                binding.cropImageView.setImageBitmap(resource)
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {
                                binding.llLoader.visibility = View.GONE

                            }
                        })
                }
            }

        }
        binding.btnDone.setOnClickListener {
            binding.cropImageView.getCroppedImage()
        }
    }


    /*
    * Method to click group of items at single shot.
    * */
    private fun Group.setAllOnClickListener(listener: View.OnClickListener?) {
        referencedIds.forEach { id ->
            rootView.findViewById<View>(id).setOnClickListener(listener)
        }
    }


    override fun onCropImageComplete(view: CropImageView, result: CropImageView.CropResult) {
        if (result != null) {
            handleCropResult(result)
        } else {
            showToast("error")
        }
    }

    private fun handleCropResult(result: CropImageView.CropResult) {
        var photoFile: File? = null
        if (result.error == null) {
            if (result.uriContent != null) {
                context?.contentResolver?.let {
                    getFilePathFromUri(it, result.uriContent as Uri)?.let {
                        photoFile = File(it)
                    }
                }
                photoFile?.let { compressAndPostImage(it) }
            } else {
                val uriinfo = result.bitmap?.let { getImageUriFromBitmap(it) }
                if (uriinfo != null) {
                    getFilePathFromUri(context?.contentResolver!!, uriinfo)?.let {
                        photoFile = File(it)
                    }
                }
                photoFile?.let { compressAndPostImage(it) }
//                updateProfileImage(result.bitmap.rowBytes.toString())
            }
        } else {
            activity?.showToast("Image crop failed:" + result.error?.message)
        }
    }

    private fun updateProfileImage(photoFile: File?) {
        photoFile?.let {
            val photoRequestBody = it.asRequestBody("image/*".toMediaTypeOrNull())
            val photoPart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "ImageFile",
                it.name,
                photoRequestBody
            )
            binding.llLoader.visibility = View.VISIBLE
            postData(true, photoPart)
        }
    }

    private fun postData(isPhotoAvailable: Boolean, photoPart: MultipartBody.Part) {


    }


    private fun getMaritalStatusType(maritalstatus: String): String? {
        return if (maritalstatus == "Married") {
            "M"
        } else if (maritalstatus == "Single")
            "S"
        else if (maritalstatus == "Divorced")
            "D"
        else
            maritalstatus
    }

    private fun getGenderType(gender: String): String {
        return if (gender == "Female") {
            "F"
        } else if (gender == "Male")
            "M"
        else
            ""

    }


    fun getImageUriFromBitmap(bitmap: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            requireActivity().contentResolver,
            bitmap,
            "Title",
            null
        )
        return Uri.parse(path.toString())
    }

    fun setObserver() {

    }

    private fun compressAndPostImage(photoFile: File) {
        var photoPart: MultipartBody.Part? = null
        lifecycleScope.launch {
            try {
                val compressedImageFile =
                    Compressor.compress(requireActivity(), photoFile) {
                        default(width = 200, format = Bitmap.CompressFormat.PNG)
                    }
                compressedImageFile.let {
                    val photoRequestBody =
                        compressedImageFile.asRequestBody("image/*".toMediaTypeOrNull())
                    photoPart = MultipartBody.Part.createFormData(
                        "AttachmentFilePath",
                        compressedImageFile.name,
                        photoRequestBody
                    )
                    updateProfileImage(photoFile)
                }
            } catch (e: Exception) {
                showToast(getString(R.string.something_went_wrong))
            }
        }
    }


}