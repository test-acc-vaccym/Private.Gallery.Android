package com.privategallery.akscorp.privategalleryandroid.Dialogs

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v4.app.DialogFragment;
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.progress_dialog.*
import android.os.Handler
import android.os.Message
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AlertDialog
import com.privategallery.akscorp.privategalleryandroid.R
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.alert
import org.jetbrains.anko.runOnUiThread


val CONFIRM_DIALOG_TAG = "LOAD_DIALOG_TAG"

class ConfirmDialog(val context: Context)
{
    fun showDialog(message: String, callback:()->Unit)
    {
        context.alert{
            this.message = message
            positiveButton(context.getString(R.string.yes)) {
                callback()
                it.cancel()
            }
            negativeButton(context.getString(R.string.no)) {
                it.cancel()
            }
        }.show()
    }
}