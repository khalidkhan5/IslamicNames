package com.example.islamicnames.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.islamicnames.data.NameRepository
import com.example.islamicnames.model.Gender
import com.example.islamicnames.model.Name
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NameViewModel(private val repository: NameRepository) : ViewModel() {

    private val _boyNames = MutableLiveData<List<Name>>()
    val boyNames: LiveData<List<Name>> = _boyNames

    private val _girlNames = MutableLiveData<List<Name>>()
    val girlNames: LiveData<List<Name>> = _girlNames

    private val _favorites = MutableLiveData<List<Name>>()
    val favorites: LiveData<List<Name>> = _favorites

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableLiveData<List<Name>>()
    val searchResults: LiveData<List<Name>> = _searchResults

    private val _activeGender = MutableStateFlow(Gender.GIRL)
    val activeGender: StateFlow<Gender> = _activeGender.asStateFlow()

    init {
        updateNames()
        viewModelScope.launch {
            repository.namesFlow.collect {
                updateNames()
            }
        }

        viewModelScope.launch {
            repository.favoritesFlow.collect { favoritesList ->
                _favorites.value = favoritesList
            }
        }
    }

    private fun updateNames() {
        _boyNames.value = repository.getBoyNames()
        _girlNames.value = repository.getGirlNames()
        _favorites.value = repository.getFavorites()
        search(_searchQuery.value)
    }

    fun toggleFavorite(nameId: Int) {
        repository.toggleFavorite(nameId)
    }

    fun search(query: String) {
        _searchQuery.value = query
        val results = repository.searchNames(query)

        // Filter by active gender if we're in the names view
        val filteredResults = when (activeGender.value) {
            Gender.BOY -> results.filter { it.gender == Gender.BOY }
            Gender.GIRL -> results.filter { it.gender == Gender.GIRL }
        }

        _searchResults.postValue(filteredResults)
    }

    fun setActiveGender(gender: Gender) {
        _activeGender.value = gender
        search(_searchQuery.value)
    }
}

class NameViewModelFactory(private val repository: NameRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NameViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NameViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}