package com.example.islamicnames.fragments


import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.islamicnames.R
import com.example.islamicnames.adapter.NameAdapter
import com.example.islamicnames.data.NameRepository
import com.example.islamicnames.model.Gender
import com.example.islamicnames.viewmodel.NameViewModel
import com.example.islamicnames.viewmodel.NameViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.getValue

class GirlNamesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var nameAdapter: NameAdapter
    private lateinit var searchEditText: EditText
    private lateinit var boysButton: Button
    private lateinit var girlsButton: Button

    private val viewModel: NameViewModel by activityViewModels {
        NameViewModelFactory(NameRepository(this.requireActivity()))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_names, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recycler_view)
        searchEditText = view.findViewById(R.id.et_search)
        boysButton = view.findViewById(R.id.btn_boys)
        girlsButton = view.findViewById(R.id.btn_girls)

        // Set up RecyclerView
        nameAdapter = NameAdapter(
            onFavoriteClick = { name ->
                viewModel.toggleFavorite(name.id)
            }
        )
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = nameAdapter
        }

        // Set active gender to GIRL
        viewModel.setActiveGender(Gender.GIRL)

        // Set up gender toggle buttons
        girlsButton.isSelected = true
        boysButton.setOnClickListener {
            viewModel.setActiveGender(Gender.BOY)
            updateButtonSelection(true)
        }

        girlsButton.setOnClickListener {
            viewModel.setActiveGender(Gender.GIRL)
            updateButtonSelection(false)
        }

        // Set up search functionality
        /*searchEditText.addTextChangedListener {
            viewModel.search(it.toString())
        }*/

        // 4. Improve the TextWatcher implementation:
        searchEditText.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Directly trigger the search here, which might be more reliable
            }

            override fun afterTextChanged(s: Editable?) {
                // Not using this to avoid potential double triggers
                val query = s?.toString() ?: ""
                viewModel.search(query)
            }
        })

        // Observe search results

        viewModel.searchResults.observe(viewLifecycleOwner) { names ->
            // Clear any cached state in the adapter
            nameAdapter.submitList(null)

            // Then submit the new list
            nameAdapter.submitList(names)

            // Scroll the RecyclerView back to the top
            recyclerView.scrollToPosition(0)
        }


        // Observe active gender changes
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.activeGender.collect { gender ->
                when (gender) {
                    Gender.BOY -> updateButtonSelection(true)
                    Gender.GIRL -> updateButtonSelection(false)
                }
            }
        }
    }

    private fun updateButtonSelection(isBoySelected: Boolean) {
        boysButton.isSelected = isBoySelected
        girlsButton.isSelected = !isBoySelected

        // Update styles
        if (isBoySelected) {
            boysButton.setBackgroundResource(R.drawable.bg_selected_button)
            boysButton.setTextColor(resources.getColor(R.color.white, null))
            girlsButton.setBackgroundResource(R.drawable.bg_unselected_button)
            girlsButton.setTextColor(resources.getColor(R.color.primary, null))
        } else {
            boysButton.setBackgroundResource(R.drawable.bg_unselected_button)
            boysButton.setTextColor(resources.getColor(R.color.primary, null))
            girlsButton.setBackgroundResource(R.drawable.bg_selected_button)
            girlsButton.setTextColor(resources.getColor(R.color.white, null))
        }
    }
}