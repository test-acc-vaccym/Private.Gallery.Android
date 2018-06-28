package com.privategallery.akscorp.privategalleryandroid.Adapters

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.support.transition.*
import android.support.v4.view.ViewCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.privategallery.akscorp.privategalleryandroid.Essentials.Image
import com.privategallery.akscorp.privategalleryandroid.Utilities.GlideApp
import kotlinx.android.synthetic.main.preview_rv_item.view.*
import com.bumptech.glide.request.RequestOptions
import com.privategallery.akscorp.privategalleryandroid.*
import com.privategallery.akscorp.privategalleryandroid.Activities.MainActivity
import com.privategallery.akscorp.privategalleryandroid.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.detail_fragment.view.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import android.support.v4.util.LruCache
import android.util.Log
import com.github.piasy.biv.loader.ImageLoader
import com.privategallery.akscorp.privategalleryandroid.Fragments.*
import kotlinx.coroutines.experimental.CommonPool
import java.io.File


/**
 * Created by AksCorp on 03.04.2018.
 * akscorp2014@gmail.com
 * web site aksenov-vladimir.herokuapp.com
 */

var lastImage: Bitmap? = null

lateinit var previews: LruCache<String, Bitmap?>
var lastSelectedImagePosition = -1
var used = mutableSetOf<String>()

class PreviewGridAdapter(val context: Context, val images: List<Image>) :
    RecyclerView.Adapter<PreviewGridAdapter.previewHolder>()
{

    val coroutinePool = mutableSetOf<String>()

    init
    {
        used = mutableSetOf<String>()
        val maxMemory = Runtime.getRuntime().maxMemory().toInt()
        val cacheSize = maxMemory / 4

        if (!::previews.isInitialized)
        {
            previews = object : LruCache<String, Bitmap?>(cacheSize)
            {
                override fun sizeOf(key: String?, bitmap: Bitmap?): Int
                {
                    return bitmap!!.byteCount
                }
            }
        }
    }

    override fun getItemCount(): Int
    {
        return images.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            : PreviewGridAdapter.previewHolder
    {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val photoView = inflater.inflate(R.layout.preview_rv_item, parent, false)
        return previewHolder(photoView)
    }

    override fun onBindViewHolder(holder: PreviewGridAdapter.previewHolder, position: Int)
    {

        holder.setIsRecyclable(false)

        val imageView = holder.preview

        val image = images[position]
        val imageName = "image_" + image.albumId.toString() + "_" + image.id.toString()
        ViewCompat.setTransitionName(imageView, imageName)

        if (previews[imageName] == null)
        {
            imageView.setImageResource(R.color.placeholder)
            loadImageIntoImageView(image, holder, imageName, {
                imageView.scaleType = ImageView.ScaleType.CENTER_CROP
                imageView.setImageBitmap(previews[imageName])
            })
        }
        else
        {
            if (position == lastSelectedImagePosition)
                imageView.setImageBitmap(lastImage)
            else
            {
                imageView.setImageBitmap(previews[imageName])
            }

            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        }

        imageView.setOnClickListener {
            if (previews[imageName] == null)
                return@setOnClickListener

            lastSelectedImagePosition = position
            lastImage = previews[imageName]

            showDetailDialog(imageView, images[position], imageName, position)
        }

        if (!used.contains(imageName))
        {
            used.add(imageName)
            tr.addSharedElement(imageView, imageView.transitionName)
        }

    }

    private fun loadImageIntoImageView(image: Image, previewHolder: previewHolder,
                                       imageName: String,
                                       action: () -> Unit)
    {

        launch(CommonPool) {
            val bmOptions = BitmapFactory.Options()
            if (previewHolder.itemView.measuredHeight == 0)
                bmOptions.inSampleSize = 6
            else
                bmOptions.inSampleSize = ((Math.max(image.width!!,
                    image.height!!) / (previewHolder.itemView.measuredHeight))).toInt()
            val bitmap = BitmapFactory.decodeFile(getImagePath(image), bmOptions)
            previews.put(imageName, bitmap)
            launch(UI) {
                action()
            }
        }
    }

    private fun showDetailDialog(imageView: ImageView, image: Image, imageName: String,
                                 position: Int)
    {
        val fragmentManager = (context as MainActivity).supportFragmentManager

        val detailFragment = DetailViewPagerFragment(this, position)

        val bundle = Bundle()
        bundle.putString("imageName", imageName)
        bundle.putSerializable("image", image)
        detailFragment.arguments = bundle

        val enterTransition = DetailsTransition()
        enterTransition.addListener(object : Transition.TransitionListener
        {
            override fun onTransitionEnd(transition: Transition)
            {
                try
                {

                    if (image.extension!!.toUpperCase() == "GIF")

                        GlideApp.with(context)
                            .load(getImagePath(image))
                            .placeholder(BitmapDrawable(context.resources, previews[imageName]))
                            .skipMemoryCache(true)
                            .error(R.drawable.placeholder_image_error)
                            .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                            .into(detailFragment.view!!.image2)
                    else
                        detailFragment.view!!.image.showImage(Uri.parse("file://" + getImagePath(
                            image)))


                    return
                } catch (e: Exception)
                {
                }
            }

            override fun onTransitionResume(transition: Transition)
            {
            }

            override fun onTransitionPause(transition: Transition)
            {
            }

            override fun onTransitionCancel(transition: Transition)
            {
            }

            override fun onTransitionStart(transition: Transition)
            {
                hideAppBar(context.appbar)
                hideFab(context.fab)
            }
        })
        detailFragment.sharedElementEnterTransition = enterTransition


        val parentFragment = fragmentManager.findFragmentByTag(PREVIEW_LIST_FRAGMENT_TAG)
        parentFragment.exitTransition = Fade()


        val returnTransition = DetailsTransition()
        detailFragment.sharedElementReturnTransition = returnTransition

        try
        {

            tr.replace(R.id.main_activity_constraint_layout_album, detailFragment,
                DETAIL_VIEW_PAGER_FRAGMENT_TAG).commit()
        } catch (e: Exception)
        {

        }
    }

    val tr =
        (context as MainActivity).supportFragmentManager.beginTransaction().addToBackStack(null)

    private fun getImagePath(image: Image) =
        ContextWrapper(context).filesDir.path + "/Images/${image.id}.${image.extension}"

    inner class previewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val preview: ImageView = itemView.preview_iv as ImageView
    }

}

