package com.example.islamicnames.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.islamicnames.R
import com.example.islamicnames.adapter.NameAdapter
import com.example.islamicnames.data.NameRepository
import com.example.islamicnames.viewmodel.NameViewModel
import com.example.islamicnames.viewmodel.NameViewModelFactory

class FavoritesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var nameAdapter: NameAdapter
    private lateinit var emptyTextView: TextView

    private val viewModel: NameViewModel by activityViewModels {
        NameViewModelFactory(NameRepository(this.requireActivity()))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recycler_view)
        emptyTextView = view.findViewById(R.id.tv_empty_favorites)

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

        // Observe favorites
        viewModel.favorites.observe(viewLifecycleOwner) { favorites ->
            nameAdapter.submitList(favorites)

            // Show empty state if no favorites
            if (favorites.isEmpty()) {
                emptyTextView.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                emptyTextView.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
        }
    }
}