package com.example.islamicnames.fragments


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.islamicnames.R

class MenuFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up menu options click listeners
        view.findViewById<TextView>(R.id.tv_about).setOnClickListener {
            // Show About Dialog
            AboutDialogFragment().show(parentFragmentManager, "about_dialog")
        }

        view.findViewById<TextView>(R.id.tv_settings).setOnClickListener {
            // Open settings activity - just a placeholder
            // startActivity(Intent(requireContext(), SettingsActivity::class.java))
        }

        view.findViewById<TextView>(R.id.tv_share).setOnClickListener {
            // Share app intent
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT,
                    "Check out the Islamic Names app - find beautiful names with meanings!")
                type = "text/plain"
            }
            startActivity(Intent.createChooser(shareIntent, "Share via"))
        }

        view.findViewById<TextView>(R.id.tv_rate).setOnClickListener {
            // Open Play Store for rating
            try {
                startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=${requireContext().packageName}")))
            } catch (e: Exception) {
                startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=${requireContext().packageName}")))
            }
        }
    }
}