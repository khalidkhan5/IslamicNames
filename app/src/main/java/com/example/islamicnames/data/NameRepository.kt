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


class NameRepository(context: Context) {
    private lateinit var allNames: List<Name>
    private val _namesFlow = MutableStateFlow<List<Name>>(emptyList())
    val namesFlow: StateFlow<List<Name>> = _namesFlow.asStateFlow()

    init {
        loadNames(context)
    }

    private fun loadNames(context: Context) {
        val inputStream = context.resources.openRawResource(R.raw.names_data)
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val type = object : TypeToken<List<Name>>() {}.type
        val namesList: List<Name> = Gson().fromJson(jsonString, type)
        allNames = namesList
        _namesFlow.value = namesList
    }

    fun toggleFavorite(nameId: Int) {
        _namesFlow.update { names ->
            names.map { name ->
                if (name.id == nameId) {
                    name.copy(isFavorite = !name.isFavorite)
                } else {
                    name
                }
            }
        }
    }


    fun searchNames(query: String): List<Name> {
        return if (query.isNullOrEmpty()) {
            allNames
        } else {
            allNames.filter {
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