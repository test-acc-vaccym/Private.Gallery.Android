package com.privategallery.akscorp.privategalleryandroid.Adapters

import android.content.Context
import android.content.ContextWrapper
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.privategallery.akscorp.privategalleryandroid.Essentials.Image
import com.privategallery.akscorp.privategalleryandroid.R
import com.privategallery.akscorp.privategalleryandroid.Utilities.GlideApp
import kotlinx.android.synthetic.main.local_storage_rv_item.view.*
import org.jetbrains.anko.find

/**
 * Created by AksCorp on 03.04.2018.
 * akscorp2014@gmail.com
 * web site aksenov-vladimir.herokuapp.com
 */
class UnlockPreviewGridAdapter(private val context: Context, val images: List<Image>) :
    RecyclerView.Adapter<UnlockPreviewGridAdapter.previewHolder>()
{
    var used: MutableSet<Image> = mutableSetOf()

    init
    {
        used = mutableSetOf()
    }

    override fun getItemCount(): Int
    {
        return images.size
    }

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): UnlockPreviewGridAdapter.previewHolder
    {
        val inflater = LayoutInflater.from(context)
        val photoView = inflater.inflate(R.layout.unlock_rv_item, null, false)
        return previewHolder(photoView)
    }

    /**
     * Load imageData by [GlideApp] library from local folder
     */
    override fun onBindViewHolder(holder: UnlockPreviewGridAdapter.previewHolder, position: Int)
    {
        val imageView = holder.preview

        GlideApp.with(context).load(getImagePath(images[position])).placeholder(R.color.placeholder)
            .error(R.drawable.placeholder_image_error)
            .transition(DrawableTransitionOptions.withCrossFade(500)).into(imageView)

        val image = images[position]

        if (used.contains(image))
        {
            holder.toggle.visibility = View.VISIBLE
        } else
        {
            holder.toggle.visibility = View.INVISIBLE
        }

        holder.itemView.setOnClickListener {
            if (used.contains(image))
            {
                used.remove(image)
                holder.toggle.visibility = View.INVISIBLE
            } else
            {
                used.add(image)
                holder.toggle.visibility = View.VISIBLE
            }
        }
    }

    private fun getImagePath(image: Image) =
        ContextWrapper(context).filesDir.path + "/Images/${image.id}.${image.extension}"

    inner class previewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val preview: ImageView = itemView.preview_iv as ImageView
        val toggle: ImageView = itemView.toggle as ImageView
    }
}