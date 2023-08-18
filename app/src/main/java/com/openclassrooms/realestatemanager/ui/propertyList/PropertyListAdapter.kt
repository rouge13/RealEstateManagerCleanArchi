package com.openclassrooms.realestatemanager.ui.propertyList

import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.data.gathering.PropertyWithDetails
import com.openclassrooms.realestatemanager.data.model.PropertyEntity
import com.openclassrooms.realestatemanager.databinding.ItemPropertyBinding
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedPropertyViewModel
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedUtilsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class PropertyListAdapter(private val lifecycleOwner: LifecycleOwner, private val sharedPropertyViewModel: SharedPropertyViewModel, private val sharedUtilsViewModel: SharedUtilsViewModel, diffCallback: DiffUtil.ItemCallback<PropertyWithDetails>, private val onPropertyClick: (PropertyWithDetails) -> Unit)
    : ListAdapter<PropertyWithDetails, PropertyListAdapter.PropertyViewHolder>(diffCallback) {

    inner class PropertyViewHolder(private val binding: ItemPropertyBinding) : RecyclerView.ViewHolder(binding.root) {
        // Bind the view to the data and set the onClickListener to open the property details
        fun bind(get: PropertyWithDetails) {
            // Set the data to the view
            binding.propertyType.text = get.property?.typeOfHouse
            binding.propertySector.text = get.address?.boroughs?.takeIf { it.isNotBlank() }
            // Use coroutine scope to collect the value of getMoneyRateSelected
            CoroutineScope(Dispatchers.Main).launch {
                sharedUtilsViewModel.getMoneyRateSelected.observe(lifecycleOwner) { isEuroSelected ->
                    // Convert the price to the selected currency and set it to the view
                    val convertedPrice = get.property?.price?.let { sharedPropertyViewModel.convertPropertyPrice(it, isEuroSelected) }
                    binding.propertyValue.text = when (convertedPrice) {
                        is Int -> { if (isEuroSelected) { "$convertedPrice€" } else { "$${get.property.price}" } }
                        // When price or conversion is null
                        else -> "$${get.property?.price}"
                    }
                }
            }
            // Set the image to the view
            get.property?.let { setImageInRecyclerView(it) }
            // Set the sold text and alpha to the view if the property is sold or not
            val propertyLayout = binding.propertyLayout
            if (get.property?.isSold == true) {
                propertyLayout.alpha = 0.3f
                binding.soldText.visibility = android.view.View.VISIBLE
            } else {
                propertyLayout.alpha = 1f
                binding.soldText.visibility = android.view.View.GONE
            }

            // Set the onClickListener to the view to open the property details
            itemView.setOnClickListener {
                val position = this.bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val propertyWithDetails = getItem(position)
                    Log.d("PropertyListAdapter", "Item clicked: ${propertyWithDetails.property?.id}")
                    onPropertyClick(propertyWithDetails)
                }
            }
        }

        private fun setImageInRecyclerView(get: PropertyEntity) {
            val context = binding.root.context
            if (get.primaryPhoto == null) {
                val defaultImageId = context.resources.getIdentifier("ic_default_property", "drawable", context.packageName)
                binding.propertyImage.setImageResource(defaultImageId)
            } else {
                val resourceId = context.resources.getIdentifier(get.primaryPhoto, "drawable", context.packageName)
                if (resourceId != 0) {
                    // The primary photo is a drawable resource
                    binding.propertyImage.setImageResource(resourceId)
                } else {
                    // The primary photo is a URI
                    try {
                        val uri = Uri.parse(get.primaryPhoto)
                        val inputStream = context.contentResolver.openInputStream(uri)
                        if (inputStream != null) {
                            // Load the image from URI and set it to the view if it's not null
                            val bitmap = BitmapFactory.decodeStream(inputStream)
                            binding.propertyImage.setImageBitmap(bitmap)
                        } else {
                            // Failed to load the image from URI, use default image
                            val defaultImageId = context.resources.getIdentifier("ic_default_property", "drawable", context.packageName)
                            binding.propertyImage.setImageResource(defaultImageId)
                        }
                    } catch (e: Exception) {
                        // Error loading the image from URI, use default image
                        val defaultImageId = context.resources.getIdentifier("ic_default_property", "drawable", context.packageName)
                        binding.propertyImage.setImageResource(defaultImageId)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyViewHolder {
        // Inflate the view binding and return the view holder with the binding view as parameter
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPropertyBinding.inflate(inflater, parent, false)
        return PropertyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PropertyViewHolder, position: Int) {
        // Bind the view holder with the data at the position
        val propertyWithDetails = getItem(position)
        holder.bind(propertyWithDetails)
    }
}
