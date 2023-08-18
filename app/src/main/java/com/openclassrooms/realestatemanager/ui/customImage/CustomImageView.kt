package com.openclassrooms.realestatemanager.ui.customImage

import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.openclassrooms.realestatemanager.R


class CustomImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    private var description: String = ""
    private var soldOut: Boolean = false
    private var descriptionTextView: TextView
    private var soldOutTextView: TextView
    private var imageView: ImageView

    init {
        // Obtain the custom attributes
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomImageView)
        description = typedArray.getString(R.styleable.CustomImageView_descriptionValue) ?: ""
        soldOut = typedArray.getBoolean(R.styleable.CustomImageView_soldValue, false)
        typedArray.recycle()

        // Create the description TextView dynamically
        descriptionTextView = TextView(context).apply {
            paramOfDescriptionTextView(context)
        }

        // Create the soldOut TextView dynamically
        soldOutTextView = TextView(context).apply {
            paramOfSoldTextView(context)
        }

        // Create the ImageView for displaying the image
        imageView = ImageView(context).apply {
            paramOfImageView()
        }

        // Add the views to the CustomImageView
        addView(imageView)
        addView(descriptionTextView)
        addView(soldOutTextView)

        // Apply the initial attribute value for description and soldOut
        setDescription(description)
        setSoldOut(soldOut)
    }

    private fun ImageView.paramOfImageView() {
        // Set the layout params of the ImageView to match the parent width and wrap the content height and center it in the parent view
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
            gravity = Gravity.CENTER
        }
        adjustViewBounds = true
        scaleType = ImageView.ScaleType.CENTER_CROP
    }

    private fun TextView.paramOfDescriptionTextView(context: Context) {
        // Set the layout params of the TextView of the description to wrap the content width and wrap the content height and center to the bottom in the parent view with setting color, background color and text size
        layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
            gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            setMargins(0, 0, 0, 16)
        }
        setTextColor(ContextCompat.getColor(context, R.color.black))
        setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        visibility = View.GONE
    }

    private fun TextView.paramOfSoldTextView(context: Context) {
        // Set the layout params of the TextView of the soldText to match the parent width and wrap the content height and center it in the parent view with setting color, background color and text size
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
            gravity = Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL
            textAlignment = View.TEXT_ALIGNMENT_CENTER
        }
        setTextColor(ContextCompat.getColor(context, R.color.yellow))
        setBackgroundColor(ContextCompat.getColor(context, R.color.red))
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
        text = context.getString(R.string.sold_property)
        visibility = View.GONE
    }

    private fun setDescription(description: String) {
        // Set the description value and set the visibility of the description TextView to visible if the description is not empty
        this.description = description
        descriptionTextView.text = description
        descriptionTextView.visibility = if (description.isNotEmpty()) View.VISIBLE else View.GONE
        invalidate()
    }

    fun setDescriptionValue(value: String) {
        // Set the description value
        setDescription(value)
    }

    fun setSoldOut(soldOut: Boolean) {
        // Set the soldOut value and set the visibility of the soldOut TextView to visible if the soldOut is true with setting the alpha of the CustomImageView to 0.3f if the soldOut is true
        this.soldOut = soldOut
        soldOutTextView.visibility = if (soldOut) View.VISIBLE else View.GONE
        alpha = if (soldOut) 0.3f else 1f
        invalidate()
    }

    fun setImageResource(resourceId: Int) {
        // Set the image resource of the ImageView
        imageView.setImageResource(resourceId)
    }

    // DisCacheStrategy.ALL is used to cache the original image and the resized image based of the aspect ratio of the image with a specific width and height based of width of the screen
    fun loadImage(url: String, aspectRatio: Float = 1f) {
        Glide.with(context)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .apply(RequestOptions().override(calculateImageWidth(aspectRatio), calculateImageHeight(aspectRatio)))
            .into(imageView)
    }

    private fun calculateImageWidth(aspectRatio: Float): Int {
        // Calculate the width of the image based of the aspect ratio of the image
        return Resources.getSystem().displayMetrics.widthPixels
    }

    private fun calculateImageHeight(aspectRatio: Float): Int {
        // Calculate the height of the image based of the aspect ratio of the image
        val screenWidth = Resources.getSystem().displayMetrics.widthPixels
        return (screenWidth / aspectRatio).toInt()
    }
}





