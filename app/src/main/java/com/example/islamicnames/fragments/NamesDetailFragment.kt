package com.example.islamicnames.fragments

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.islamicnames.R
import com.example.islamicnames.adapter.NameDiffCallback
import com.example.islamicnames.data.NameRepository
import com.example.islamicnames.model.Gender
import com.example.islamicnames.model.Name
import com.example.islamicnames.viewmodel.NameViewModel
import com.example.islamicnames.viewmodel.NameViewModelFactory
import kotlinx.coroutines.launch

// 1. Create NameDetailsFragment
class NameDetailsFragment : Fragment() {

    private lateinit var tvName: TextView
    private lateinit var tvMeaning: TextView
    private lateinit var tvArabicName: TextView
    private lateinit var tvGender: TextView
    private lateinit var btnBack: Button
    private lateinit var ivFavorite: ImageView

    private val viewModel: NameViewModel by activityViewModels {
        NameViewModelFactory(NameRepository(this.requireActivity()))
    }

    private var nameId: Int = -1
    private lateinit var currentName: Name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nameId = arguments?.getInt("name_id", -1) ?: -1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_name_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvName = view.findViewById(R.id.tv_detail_name)
        tvMeaning = view.findViewById(R.id.tv_detail_meaning)
        tvArabicName = view.findViewById(R.id.tv_detail_arabic_name)
        tvGender = view.findViewById(R.id.tv_detail_gender)
        btnBack = view.findViewById(R.id.btn_back)
        ivFavorite = view.findViewById(R.id.iv_detail_favorite)

        btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        // Observe names to find the current name
        viewModel.searchResults.observe(viewLifecycleOwner) { names ->
            val name = names.find { it.id == nameId }
            if (name != null) {
                currentName = name
                displayNameDetails(name)
            }
        }

        // Also check boy names and girl names in case search results don't have it
        viewModel.boyNames.observe(viewLifecycleOwner) { names ->
            val name = names.find { it.id == nameId }
            if (name != null) {
                currentName = name
                displayNameDetails(name)
            }
        }

        viewModel.girlNames.observe(viewLifecycleOwner) { names ->
            val name = names.find { it.id == nameId }
            if (name != null) {
                currentName = name
                displayNameDetails(name)
            }
        }

        ivFavorite.setOnClickListener {
            if (::currentName.isInitialized) {
                viewModel.toggleFavorite(currentName.id)
            }
        }
    }

    private fun displayNameDetails(name: Name) {
        tvName.text = name.name
        tvMeaning.text = name.meaning
        tvArabicName.text = name.arabicName
        tvGender.text = if (name.gender == Gender.BOY) "Boy" else "Girl"

        ivFavorite.setImageResource(
            if (name.isFavorite) R.drawable.ic_favorite_filled
            else R.drawable.ic_favorite_outline
        )
    }

    companion object {
        fun newInstance(nameId: Int): NameDetailsFragment {
            val fragment = NameDetailsFragment()
            val args = Bundle()
            args.putInt("name_id", nameId)
            fragment.arguments = args
            return fragment
        }
    }
}
