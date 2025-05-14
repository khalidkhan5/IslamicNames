package com.example.islamicnames.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.islamicnames.R
import com.example.islamicnames.model.Name

class NameAdapter(
    private val onFavoriteClick: (Name) -> Unit,
    private val showFavoriteIcon: Boolean = true
) : ListAdapter<Name, NameAdapter.NameViewHolder>(NameDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NameViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_name, parent, false)
        return NameViewHolder(view)
    }

    override fun onBindViewHolder(holder: NameViewHolder, position: Int) {
        val name = getItem(position)
        holder.bind(name, onFavoriteClick, showFavoriteIcon)
    }

    class NameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tv_name)
        private val tvMeaning: TextView = itemView.findViewById(R.id.tv_meaning)
        private val tvArabicName: TextView = itemView.findViewById(R.id.tv_arabic_name)
        private val ivFavorite: ImageView = itemView.findViewById(R.id.iv_favorite)

        fun bind(name: Name, onFavoriteClick: (Name) -> Unit, showFavoriteIcon: Boolean) {
            tvName.text = name.name
            tvMeaning.text = name.meaning
            tvArabicName.text = name.arabicName

            if (showFavoriteIcon) {
                ivFavorite.visibility = View.VISIBLE
                ivFavorite.setImageResource(
                    if (name.isFavorite) R.drawable.ic_favorite_filled
                    else R.drawable.ic_favorite_outline
                )
                ivFavorite.setOnClickListener { onFavoriteClick(name) }
            } else {
                ivFavorite.visibility = View.GONE
            }
        }
    }
}

class NameDiffCallback : DiffUtil.ItemCallback<Name>() {
    override fun areItemsTheSame(oldItem: Name, newItem: Name): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Name, newItem: Name): Boolean {
        return oldItem == newItem
    }
}