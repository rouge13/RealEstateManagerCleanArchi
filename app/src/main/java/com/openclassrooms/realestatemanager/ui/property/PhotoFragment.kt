package com.openclassrooms.realestatemanager.ui.property

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.model.PhotoEntity
import com.openclassrooms.realestatemanager.databinding.FragmentPhotoBinding

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class PhotoFragment : Fragment() {

    private var _binding: FragmentPhotoBinding? = null
    private val binding get() = _binding!!
    private lateinit var photoEntity: PhotoEntity
    private var soldOut: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment with the FragmentPhotoBinding class to access the views and return the binding root
        _binding = FragmentPhotoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Retrieve the arguments
        val args = requireArguments()
        photoEntity = args.getParcelable("photoEntity") ?: defaultPhoto
        soldOut = args.getBoolean("soldOut")

        // Set the photo and description in the CustomImageView if the photo is not null or empty
        val photoUrl = getPhotoUrl(photoEntity.photoURI)
        if (photoUrl != null) {
            binding.customImageView.loadImage(photoUrl, calculateImageAspectRatio())
        } else {
            binding.customImageView.setImageResource(DEFAULT_PHOTO_RESOURCE_ID)
        }

        // Set the description and soldOut separately in the CustomImageView
        binding.customImageView.setDescriptionValue(photoEntity.description ?: "")
        binding.customImageView.setSoldOut(soldOut)
    }

    private fun getPhotoUrl(photo: String?): String? {
        // Return the URI for the default photo if the photo is null or empty or the resource identifier if it is a drawable resource with constructing the URI with the resource identifier
        if (!photo.isNullOrEmpty()) {
            val context = requireContext()
            val resourceId = context.resources.getIdentifier(photo, "drawable", context.packageName)
            if (resourceId != 0) {
                // Return the resource identifier as a string
                return "android.resource://${context.packageName}/$resourceId"
            } else {
                // Return the URI for other photo types
                return photo
            }
        }
        return null
    }

    private fun calculateImageAspectRatio(): Float {
        // Calculate the aspect ratio based on a fixed aspect ratio or the image size
        return 1f // Default aspect ratio is 1:1
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        // Default photo resource identifier for the default photo if the photo is null or empty or the resource identifier if it is a drawable resource with constructing the URI with the resource identifier
        private const val DEFAULT_PHOTO_RESOURCE_ID = R.drawable.ic_default_property
        private val defaultPhoto = PhotoEntity(
            id = -1,
            photoURI = null,
            description = "No photo!"
        )

        fun newInstance(photoEntity: PhotoEntity?, soldOut: Boolean): PhotoFragment {
            // Create a new instance of PhotoFragment and set the arguments for the photoEntity and soldOut values and return the fragment instance with the arguments set
            val args = Bundle().apply {
                putParcelable("photoEntity", photoEntity)
                putBoolean("soldOut", soldOut)
            }
            val fragment = PhotoFragment()
            fragment.arguments = args
            return fragment
        }
    }
}



