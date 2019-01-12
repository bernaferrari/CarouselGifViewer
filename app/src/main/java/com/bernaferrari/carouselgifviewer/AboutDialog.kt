package com.bernaferrari.carouselgifviewer

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.afollestad.materialdialogs.MaterialDialog
import com.bernaferrari.carouselgifviewer.BuildConfig.VERSION_NAME

// inspired from mnml
class AboutDialog : DialogFragment() {

    companion object {
        private const val TAG = "[ABOUT_DIALOG]"

        /** Shows the about dialog inside of [context]. */
        fun show(supportFragmentManager: FragmentManager) {
            val dialog = AboutDialog()
            dialog.show(supportFragmentManager, TAG)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = activity ?: throw IllegalStateException("Oh no!")
        return MaterialDialog(context)
            .title(text = getString(R.string.about_title, VERSION_NAME))
            .message(
                res = R.string.about_body,
                html = true,
                lineHeightMultiplier = 1.4f
            )
            .positiveButton(R.string.dismiss)
    }
}