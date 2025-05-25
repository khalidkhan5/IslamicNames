package com.example.islamicnames.data



import android.content.Context
import com.example.islamicnames.R
import com.example.islamicnames.model.Gender
import com.example.islamicnames.model.Name
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.File

private const val FAVORITES_FILE_NAME = "favorites.json"

class NameRepository(private val context: Context) {
    private lateinit var allNames: List<Name>
    private val _namesFlow = MutableStateFlow<List<Name>>(emptyList())
    val namesFlow: StateFlow<List<Name>> = _namesFlow.asStateFlow()

    // Create a dedicated flow for favorites
    private val _favoritesFlow = MutableStateFlow<List<Name>>(emptyList())
    val favoritesFlow: StateFlow<List<Name>> = _favoritesFlow.asStateFlow()
    private var favorites: MutableList<Name> = mutableListOf()

    init {
        loadNames(context)
        loadFavorites(context)
    }

    private fun loadNames(context: Context) {
        val inputStream = context.resources.openRawResource(R.raw.names_data)
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val type = object : TypeToken<List<Name>>() {}.type
        val namesList: List<Name> = Gson().fromJson(jsonString, type)
        allNames = namesList
        _namesFlow.value = namesList
    }

    private fun loadFavorites(context: Context) {
        val file = File(context.filesDir, FAVORITES_FILE_NAME)
        if (file.exists()) {
            try {
                val jsonString = file.readText()
                val type = object : TypeToken<List<Name>>() {}.type
                favorites = Gson().fromJson(jsonString, type) ?: mutableListOf()
            } catch (e: Exception) {
                // Handle file reading or parsing errors
                favorites = mutableListOf()
            }
        } else {
            favorites = mutableListOf()
        }
        syncFavoritesToNamesFlow()
        // Update favoritesFlow with the current favorites
        updateFavoritesFlow()
    }

    private fun saveFavorites() {
        try {
            val file = File(context.filesDir, FAVORITES_FILE_NAME)
            val jsonString = Gson().toJson(favorites)
            file.writeText(jsonString)
        } catch (e: Exception) {
            // Handle file writing errors
        }
    }

    fun toggleFavorite(nameId: Int) {
        // First, find the name in the current list
        val nameToToggle = _namesFlow.value.find { it.id == nameId } ?: return

        // Update the names in the flow first
        val updatedNames = _namesFlow.value.map { name ->
            if (name.id == nameId) {
                name.copy(isFavorite = !name.isFavorite)
            } else {
                name
            }
        }

        // Update the namesFlow immediately
        _namesFlow.value = updatedNames

        // Find the updated name
        val updatedName = updatedNames.find { it.id == nameId }!!

        // Update the favorites list based on the new state
        if (updatedName.isFavorite) {
            // If it's now favorite, add it to favorites list
            favorites.removeAll { it.id == nameId } // Remove if exists to avoid duplicates
            favorites.add(updatedName)
        } else {
            // If it's no longer favorite, remove it from favorites list
            favorites.removeAll { it.id == nameId }
        }

        // Save the updated favorites to storage
        saveFavorites()

        // Update the favorites flow with only the favorites from the updated list
        updateFavoritesFlow()
    }

    private fun updateFavoritesFlow() {
        _favoritesFlow.value = _namesFlow.value.filter { it.isFavorite }
    }

    private fun syncFavoritesToNamesFlow() {
        // Create a set of favorite IDs for faster lookup
        val favoriteIds = favorites.map { it.id }.toSet()

        _namesFlow.update { names ->
            names.map { name ->
                if (favoriteIds.contains(name.id)) {
                    name.copy(isFavorite = true)
                } else {
                    name.copy(isFavorite = false)
                }
            }
        }
    }

    fun searchNames(query: String): List<Name> {
        return if (query.isBlank()) {
            _namesFlow.value  // Changed from allNames to _namesFlow.value to include favorite states
        } else {
            _namesFlow.value.filter {  // Changed from allNames to _namesFlow.value
                it.name.startsWith(query, ignoreCase = true)
            }
        }
    }

    fun getBoyNames(): List<Name> {
        return _namesFlow.value.filter { it.gender == Gender.BOY }
    }

    fun getGirlNames(): List<Name> {
        return _namesFlow.value.filter { it.gender == Gender.GIRL }
    }

    fun getFavorites(): List<Name> {
        return _namesFlow.value.filter { it.isFavorite }
    }
}