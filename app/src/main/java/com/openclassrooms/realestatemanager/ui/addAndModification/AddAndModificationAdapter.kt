package com.openclassrooms.realestatemanager.ui.addAndModification

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.model.PhotoEntity
import com.openclassrooms.realestatemanager.databinding.ItemPhotoBinding

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class AddAndModificationAdapter(
    private val photoList: MutableList<PhotoEntity>,
    private val onDeletePhoto: (position: Int) -> Unit,
    private val onSetPrimaryPhoto: (Int) -> Unit
) : RecyclerView.Adapter<AddAndModificationAdapter.PhotoViewHolder>() {

    private val photos: MutableList<Pair<PhotoEntity, Drawable?>> = mutableListOf()
    private var selectedPrimaryPhotoPosition: Int = -1

    // Create new views (invoked by the layout manager) and inflates the item view layout from xml to a view holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPhotoBinding.inflate(inflater, parent, false)
        return PhotoViewHolder(binding)
    }

    // Returns the list of photos with the position of each item in the adapter
    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val (photoEntity, drawable) = photos[position]
        holder.bind(drawable, photoEntity.isPrimaryPhoto, position == selectedPrimaryPhotoPosition)
    }

    // Add a new item to the list of photos and notify the adapter that an item has been inserted
    fun addPhoto(photoEntity: PhotoEntity, drawable: Drawable?) {
        photoList.add(photoEntity)
        photos.add(photoEntity to drawable)
        notifyItemInserted(photos.size - 1)
    }

    // Update an item from the list of photos
    fun updatePhotos(drawableList: List<Drawable?>) {
        photos.clear()
        photos.addAll(photoList.zip(drawableList))
        notifyDataSetChanged()
    }

    // Update the primary photo icon of the item from the list of photos
    fun updatePrimaryPhotoIcons(selectedPosition: Int) {
        selectedPrimaryPhotoPosition = selectedPosition
        notifyDataSetChanged()
    }

    // Get number of items in the list of photos
    override fun getItemCount(): Int = photos.size

    // Init holder with binding and set click listeners on buttons needed
    inner class PhotoViewHolder(private val binding: ItemPhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.deleteButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onDeletePhoto(position) // Call onDeletePhoto callback
                }
            }
            binding.primaryPhotoButton.setOnClickListener {
                // Add to primary photo
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    if (selectedPrimaryPhotoPosition != position) {
                        // Set the clicked item as the primary photo
                        onSetPrimaryPhoto(position) // Call onSetPrimaryPhoto callback
                    }
                }
            }
        }

        fun bind(drawable: Drawable?, isPrimary: Boolean = false, isItemSelected: Boolean = false) {
            binding.imageView.setImageDrawable(drawable)
            val primaryPhotoIcon = when {
                isItemSelected -> {
                    R.drawable.ic_primary_photo_to_add
                }
                isPrimary -> R.drawable.ic_primary_photo_added_true
                else -> R.drawable.ic_primary_photo_added_false
            }
            binding.primaryPhotoButton.setImageResource(primaryPhotoIcon)
        }
    }

    // Get drawable from photo entity (from URI or from resources) and return it as a drawable object into the adapter
    fun getDrawableFromPhotoEntity(context: Context, photoEntity: PhotoEntity, isPrimary: Boolean): Drawable? {
        return if (photoEntity.photoURI?.startsWith("ic_") == true) {
            // Photo from resources and try to get the drawable from the resources else return null
            val resourceId = context.resources.getIdentifier(
                photoEntity.photoURI,
                "drawable",
                context.packageName
            )
            try {
                val drawable = ContextCompat.getDrawable(context, resourceId)
                drawable?.mutate()
            } catch (e: Exception) {
                null
            }
        } else {
            // Photo from URI and try to get the drawable from the URI else return null
            val uri = Uri.parse(photoEntity.photoURI)
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                inputStream?.use {
                    val drawable = Drawable.createFromStream(it, uri.toString())
                    drawable?.mutate()
                }
            } catch (e: Exception) {
                null
            }
        }
    }
}