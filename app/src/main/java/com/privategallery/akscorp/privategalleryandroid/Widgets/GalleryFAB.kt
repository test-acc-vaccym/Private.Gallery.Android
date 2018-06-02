package com.privategallery.akscorp.privategalleryandroid.Widgets

import android.animation.Animator
import android.content.Context
import android.graphics.Point
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityCompat
import android.support.v7.widget.Toolbar
import android.util.AttributeSet
import android.view.View
import android.view.animation.RotateAnimation
import com.privategallery.akscorp.privategalleryandroid.Activities.MainActivity
import com.privategallery.akscorp.privategalleryandroid.Fragments.LOCAL_STORAGE_FRAGMENT_TAG
import com.privategallery.akscorp.privategalleryandroid.Fragments.LocalStorageFragment
import com.privategallery.akscorp.privategalleryandroid.R
import com.privategallery.akscorp.privategalleryandroid.Utilities.CircularFragReveal
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import android.animation.Animator.AnimatorListener
import android.animation.AnimatorSet
import android.view.ViewAnimationUtils
import com.privategallery.akscorp.privategalleryandroid.R.id.fab
import android.support.v4.content.res.ResourcesCompat
import android.content.res.ColorStateList
import android.support.v4.view.ViewCompat.setBackgroundTintList
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.constraint.ConstraintLayout
import android.support.design.widget.CoordinatorLayout
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.RelativeLayout





/**
 * Created by AksCorp on 08.04.2018.
 * akscorp2014@gmail.com
 * web site aksenov-vladimir.herokuapp.com
 */
class GalleryFAB : FloatingActionButton, View.OnClickListener {
    private var isButtonShowGallery = false
    private var isAnimationRunning = false

    private var currentFragment: LocalStorageFragment? = null

    init {
        setOnClickListener(this)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onClick(v: View?) {
        if (isAnimationRunning)
            return

        isAnimationRunning = true


        try {
            //runGalleryAction()
            viewMenu()
            showAnimation()
        } catch (e: Exception) {
            isAnimationRunning = false
            isButtonShowGallery = false
            return
        }

        isButtonShowGallery = !isButtonShowGallery
    }

    private fun showAnimation() {
        var from = 0f
        var to = 45f
        if (isButtonShowGallery)
            from = to.also { to = from }

        val animation1 = RotateAnimation(from, to, width / 2f, height / 2f)
        animation1.duration = 700
        animation1.fillAfter = true
        startAnimation(animation1)
    }

    lateinit var toolbar: Toolbar

    private var layoutMain: CoordinatorLayout? = null
    private var layoutButtons: FrameLayout? = null
    private var layoutContent: ConstraintLayout? = null

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun viewMenu() {
        val fabCenter = Point(fab.x.toInt() + fab.width / 2, fab.y.toInt() + fab.height / 2)

        layoutMain = (context as MainActivity).main_activity_coordinator_layout
        layoutContent = (context as MainActivity).main_activity_constraint_layout
        layoutButtons = (context as MainActivity).reveal
        if (!isButtonShowGallery) {
            layoutButtons!!.visibility = View.VISIBLE

            (context as MainActivity).toolbar.setState(LOCK_FILES)
            currentFragment =  establishFragment()

            val startRadius = 0
            val endRadius = Math.hypot(layoutMain!!.getWidth().toDouble(),
                layoutMain!!.getHeight().toDouble()
            ).toInt()

            val anim = ViewAnimationUtils.createCircularReveal(
                layoutButtons,
                fabCenter.x,
                fabCenter.y,
                startRadius.toFloat(),
                endRadius.toFloat()
            )
            anim.duration = 1100

            anim.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animator: Animator) {

                }

                override fun onAnimationEnd(animator: Animator) {
                    isAnimationRunning = false
                }

                override fun onAnimationCancel(animator: Animator) {

                }

                override fun onAnimationRepeat(animator: Animator) {

                }
            })

            anim.start()
            //anim.start()

        } else {

            val startRadius = Math.hypot(layoutMain!!.getWidth().toDouble(),
                layoutMain!!.getHeight().toDouble()
            ).toInt()
            val endRadius = 0

            val anim = ViewAnimationUtils.createCircularReveal(
                layoutButtons,
                fabCenter.x,
                fabCenter.y,
                startRadius.toFloat(),
                endRadius.toFloat()
            )
            anim.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animator: Animator) {

                }

                override fun onAnimationEnd(animator: Animator) {
                    (context as MainActivity).toolbar.setState(COMMON)

                    (context as MainActivity).supportFragmentManager.beginTransaction()
                        .remove(currentFragment).commit()
                    layoutButtons!!.visibility = View.INVISIBLE

                    isAnimationRunning = false
                }

                override fun onAnimationCancel(animator: Animator) {

                }

                override fun onAnimationRepeat(animator: Animator) {

                }
            })
            anim.duration = 1100
            anim.start()
        }
    }

    private fun runGalleryAction() {
        //currentFragment = establishFragment()

        val mainActivity = context as MainActivity

        val fabCenter = Point(fab.x.toInt() + fab.width / 2, fab.y.toInt() + fab.height / 2)

        if (!isButtonShowGallery) {
            (context as MainActivity).reveal.visibility = View.VISIBLE

            //currentFragment = establishFragment()

            val builder = CircularFragReveal.Builder(mainActivity.main_activity_constraint_layout)
            builder.setRevealTime(1500)
            builder.setRevealColor(ActivityCompat.getColor(context, R.color.white))
            val circularReveal = builder.build()
            toolbar = (context as MainActivity).toolbar
            circularReveal.startReveal(fabCenter.x, fabCenter.y,
                object :
                    CircularFragReveal.OnCircularReveal {
                    override fun onFragCircRevealStart() {
                        (context as MainActivity).toolbar.setState(LOCK_FILES)
                    }

                    override fun onFragCircRevealEnded() {

                        isAnimationRunning = false
                    }

                    override fun onFragCircUnRevealStart() {
                    }

                    override fun onFragCircUnRevealEnded() {
                    }
                })
        } else {
            val builder =
                CircularFragReveal.Builder(currentFragment?.view)
            builder.setUnrevealTime(1000)
            builder.setRevealColor(ActivityCompat.getColor(context, R.color.white))
            val circularReveal = builder.build()

            circularReveal.startUnreveal(fabCenter.x, fabCenter.y,
                object :
                    CircularFragReveal.OnCircularReveal {
                    override fun onFragCircRevealStart() {
                    }

                    override fun onFragCircRevealEnded() {
                    }

                    override fun onFragCircUnRevealStart() {
                    }

                    override fun onFragCircUnRevealEnded() {
                        (context as MainActivity).toolbar.setState(COMMON)

                        (context as MainActivity).supportFragmentManager.beginTransaction()
                            .remove(currentFragment).commit()
                        isAnimationRunning = false
                    }
                })
        }
    }

    private fun establishFragment(): LocalStorageFragment {
        val fragment = LocalStorageFragment()

        val fragmentManager = (context as MainActivity).supportFragmentManager

        val fragmentTransaction = fragmentManager.beginTransaction()
            .replace(R.id.reveal, fragment, LOCAL_STORAGE_FRAGMENT_TAG)

        fragmentTransaction.commitNow()

        return fragment
    }

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )
}