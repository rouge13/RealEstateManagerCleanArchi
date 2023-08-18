package com.openclassrooms.realestatemanager.ui.property

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.openclassrooms.realestatemanager.data.model.PhotoEntity

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class PropertyInfoAdapter(fragment: Fragment, private val photoList: List<PhotoEntity>?, private val soldOut : Boolean) :
    FragmentStateAdapter(fragment) {

    private val defaultPhoto = PhotoEntity(
        id = -1,
        photoURI = "ic_default_property", // Replace with the resource name of your default photo
        description = "No photo!" // Replace with the default description
    )

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int = photoList?.size!!

    // Create the fragments to display for each item in the photo list. If the photo list is empty, display the default photo.
    override fun createFragment(position: Int): Fragment {
        return if (photoList.isNullOrEmpty() || position >= photoList.size) {
            PhotoFragment.newInstance(defaultPhoto, soldOut)
        } else {
            val photo = photoList[position]
            PhotoFragment.newInstance(photo, soldOut)
        }
    }
}
