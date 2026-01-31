package com.example.ucp2.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ucp2.repositori.RepositoryPerpustakaan
import com.example.ucp2.room.Kategori
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


data class KategoriUiState(
    val detailKategori: DetailKategori = DetailKategori(),
    val isEntryValid: Boolean = false,
    val parentList: List<Kategori> = emptyList(),
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

data class DetailKategori(
    val id: Int = 0,
    val nama: String = "",
    val deskripsi: String = "",
    val parentId: Int? = null,
    val parentName: String? = null
)

fun DetailKategori.toEntity(): Kategori = Kategori(
    id = id,
    nama = nama,
    deskripsi = deskripsi,
    parentId = parentId
)

fun Kategori.toDetail(): DetailKategori = DetailKategori(
    id = id,
    nama = nama,
    deskripsi = deskripsi,
    parentId = parentId
)



class HomeKategoriViewModel(private val repository: RepositoryPerpustakaan) : ViewModel() {
    val kategoriList: StateFlow<List<Kategori>> = repository.getAllKategori()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}

class EntryKategoriViewModel(private val repository: RepositoryPerpustakaan) : ViewModel() {
    var uiState by mutableStateOf(KategoriUiState())
        private set
        
    val parentList: StateFlow<List<Kategori>> = repository.getAllKategori()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateUiState(detail: DetailKategori) {
        uiState = uiState.copy(
            detailKategori = detail,
            isEntryValid = detail.nama.isNotBlank() && detail.deskripsi.isNotBlank()
        )
    }

    suspend fun saveKategori() {
        if (uiState.isEntryValid) {
            try {
                repository.insertKategori(uiState.detailKategori.toEntity())
                uiState = uiState.copy(isSuccess = true, errorMessage = null)
            } catch (e: Exception) {
                uiState = uiState.copy(errorMessage = e.message)
            }
        }
    }
    
    fun resetState() {
        uiState = KategoriUiState()
    }
}

class DetailKategoriViewModel(
    savedStateHandle: androidx.lifecycle.SavedStateHandle,
    private val repository: RepositoryPerpustakaan
) : ViewModel() {
    val kategoriId: Int = checkNotNull(savedStateHandle["kategoriId"])

    val uiState: StateFlow<KategoriUiState> = repository.getKategoriStream(kategoriId)
        .filterNotNull()
        .map { 
             KategoriUiState(detailKategori = it.toDetail())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = KategoriUiState()
        )
        
    val recursiveBookList: StateFlow<List<com.example.ucp2.room.Buku>> = repository.getBukuByKategoriRecursive(kategoriId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        

    var deleteError by mutableStateOf<String?>(null)
        private set
    

    
    suspend fun deleteKategori(deleteBooks: Boolean) {
        try {
            repository.deleteKategori(kategoriId, deleteBooks)
        } catch (e: Exception) {
            deleteError = e.message
        }
    }
}

class UpdateKategoriViewModel(
    savedStateHandle: androidx.lifecycle.SavedStateHandle,
    private val repository: RepositoryPerpustakaan
) : ViewModel() {
    val kategoriId: Int = checkNotNull(savedStateHandle["kategoriId"])

    var uiState by mutableStateOf(KategoriUiState())
        private set
        
    val parentList: StateFlow<List<Kategori>> = repository.getAllKategori()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.getKategoriStream(kategoriId)
                .filterNotNull()
                .collect { 
                    uiState = uiState.copy(detailKategori = it.toDetail(), isEntryValid = true)
                }
        }
    }

    fun updateUiState(detail: DetailKategori) {
        uiState = uiState.copy(
            detailKategori = detail,
            isEntryValid = detail.nama.isNotBlank()
        )
    }

    suspend fun updateKategori() {
        if (uiState.isEntryValid) {
            try {
                repository.updateKategori(uiState.detailKategori.toEntity())
                uiState = uiState.copy(isSuccess = true)
            } catch (e: Exception) {

                uiState = uiState.copy(errorMessage = e.message)
            }
        }
    }
}
