package com.openclassrooms.realestatemanager.ui.alertDialog

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.lifecycleScope
import com.openclassrooms.realestatemanager.data.model.PhotoEntity
import com.openclassrooms.realestatemanager.databinding.FragmentAddAndModifyPropertyBinding
import com.openclassrooms.realestatemanager.ui.addAndModification.AddAndModificationAdapter
import com.openclassrooms.realestatemanager.ui.addAndModification.AddAndModificationFragment
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedPropertyViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class PhotoOptionsAndSaveAlertDialog(
    private val context: Context,
    private val fragment: AddAndModificationFragment,
    private val sharedPropertyViewModel: SharedPropertyViewModel,
    private val binding: FragmentAddAndModifyPropertyBinding,
    private val adapter: AddAndModificationAdapter
) {
    private var currentDescription: String? = null

    fun showPhotoOptionsDialog() {
        val descriptionEditText = EditText(context)
        val alertDialogBuilder = AlertDialog.Builder(context)
            .setTitle("Add Photo description")
            .setView(descriptionEditText)
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
        val descriptionDialog = alertDialogBuilder.create()
        descriptionDialog.setOnShowListener {
            val positiveButton = descriptionDialog.getButton(DialogInterface.BUTTON_POSITIVE)
            positiveButton.isEnabled = false
            // Enable positive button once user starts to type something in the EditText field (description) and disable it if the field is empty
            descriptionEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                }

                override fun afterTextChanged(s: Editable?) {
                    positiveButton.isEnabled = !s.isNullOrBlank()
                }
            })
        }
        // Save photo with description once user clicks on positive button of the dialog and dismiss the dialog
        descriptionDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Save") { dialog, _ ->
            val description = descriptionEditText.text.toString()
            if (description.isNotBlank()) {
                savePhotoWithDescription(description)
            }
            dialog.dismiss()
        }
        // Show dialog
        descriptionDialog.show()
    }

    private fun savePhotoWithDescription(description: String) {
        // Show dialog to let user choose between taking a photo with camera or picking one from gallery
        val options = arrayOf("Take Photo", "Choose from Gallery")
        currentDescription = description
        AlertDialog.Builder(context)
            .setTitle("Add Photo")
            .setItems(options) { _, which ->
                when (which) {
                    // Take photo with camera
                    0 -> takePhotoFromCamera()
                    // Pick photo from gallery
                    1 -> choosePhotoFromGallery()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun takePhotoFromCamera() {
        // Start camera intent to take photo and save it to gallery once user clicks on save button of the camera app
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        fragment.startActivityForResult(
            takePictureIntent,
            AddAndModificationFragment.REQUEST_IMAGE_CAPTURE
        )
    }

    private fun choosePhotoFromGallery() {
        val galleryIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        fragment.startActivityForResult(
            galleryIntent,
            AddAndModificationFragment.REQUEST_IMAGE_PICK
        )
    }


    fun initImageCapture(data: Intent?) {
        // Get description and photo from camera intent and save it to gallery and database and save the URI of the photo to database by taking picture with camera
        val description = currentDescription ?: ""
        if (description.isNotBlank()) {
            val photoBitmap = data?.extras?.get("data") as? Bitmap
            photoBitmap?.let { bitmap ->
                val uriString = saveImageToGallery(bitmap.toDrawable(context.resources))
                uriString?.let { saveUriToDatabase(it, description) }
            }
            currentDescription = null
        }
    }

    fun initImagePick(data: Intent?) {
        // Get description and photo from gallery intent and save it to gallery and database and save the URI of the photo to database by picking picture from gallery
        val description = currentDescription ?: ""
        if (description.isNotBlank()) {
            val uri = data?.data
            uri?.let { it ->
                val drawable = Drawable.createFromStream(
                    context.contentResolver.openInputStream(it),
                    it.toString()
                )
                val uriString = drawable?.let { it1 -> saveImageToGallery(it1) }
                uriString?.let { saveUriToDatabase(it, description) }
            }
            currentDescription = null
        }
    }

    private fun saveImageToGallery(drawable: Drawable): String? {
        // Save photo to gallery and return the URI of the photo and apply a name and a jpeg type to the photo
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "image_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }
        // Use resolver to insert photo to gallery and return the URI of the photo
        val resolver = context.contentResolver
        var imageUri: String? = null

        try {
            // Use resolver to insert photo to gallery and return the URI of the photo try to catch exception if something goes wrong else save the URI of the photo
            resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                ?.let { uri ->
                    resolver.openOutputStream(uri)?.use { outputStream ->
                        drawable.toBitmap()
                            .compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                        imageUri = uri.toString()
                    }
                }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        // Return the URI of the photo
        return imageUri
    }

    private fun saveUriToDatabase(uri: String, description: String) {
        fragment.lifecycleScope.launch {
            val drawable = getDrawableFromUri(uri)
            val photoEntity = PhotoEntity(photoURI = uri, description = description)
            val insertedId = insertPhoto(photoEntity)?.toInt()
            if (insertedId != null) {
                photoEntity.id = insertedId
                // Update the adapter with the new photo entity and drawable
                drawable?.let {
                    adapter.addPhoto(photoEntity, it)
                }
                // Scroll to the newly added photo
                binding.fragmentPropertySelectedPhotosRecyclerView.smoothScrollToPosition(
                    adapter.itemCount - 1
                )
            } else {
                // Handle the case where photo insertion fails
                Toast.makeText(context, "Failed to insert photo", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private suspend fun getDrawableFromUri(uri: String): Drawable? {
        // Get drawable from URI and return it
        return withContext(Dispatchers.Main) {
            val context = context
            val inputStream = context.contentResolver.openInputStream(Uri.parse(uri))
            inputStream?.use {
                val bitmap = BitmapFactory.decodeStream(it)
                BitmapDrawable(context.resources, bitmap)
            }
        }
    }

    private suspend fun insertPhoto(photoEntity: PhotoEntity): Long? {
        return sharedPropertyViewModel.insertPhoto(photoEntity)
    }

}