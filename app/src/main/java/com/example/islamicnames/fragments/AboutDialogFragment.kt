package com.example.islamicnames.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.islamicnames.R

class AboutDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle("About Islamic Names")
            .setMessage(
                "Islamic Names is an app designed to help Muslim parents find " +
                        "beautiful and meaningful names for their children. Each name " +
                        "includes its meaning and Arabic script.\n\n" +
                        "Version 1.0"
            )
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .create()
    }
}