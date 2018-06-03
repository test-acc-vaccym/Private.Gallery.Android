package com.privategallery.akscorp.privategalleryandroid

import android.app.Application
import android.content.ContextWrapper
import com.hawkcatcherkotlin.akscorp.hawkcatcherkotlin.HawkExceptionCatcher
import com.privategallery.akscorp.privategalleryandroid.Database.LocalDatabaseAPI
import com.privategallery.akscorp.privategalleryandroid.Utilities.SecurityController

/**
 * Created by AksCorp on 30.03.2018.
 * akscorp2014@gmail.com
 * web site aksenov-vladimir.herokuapp.com
 */


class Application : Application() {
    
    /**
     * Hawk catcher
     */
    private lateinit var exceptionCatcher: HawkExceptionCatcher
    lateinit var securityController:SecurityController


    override fun onCreate() {
        super.onCreate()
    
        exceptionCatcher = HawkExceptionCatcher(this, HAWK_TOKEN)
        securityController = SecurityController(this.baseContext)
        try
        {
            exceptionCatcher.start()
        } catch (e: Exception)
        {
            e.printStackTrace()
        }
    }
}