package com.example.ucp2.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ucp2.repositori.RepositoryPerpustakaan
import com.example.ucp2.room.Buku
import com.example.ucp2.room.Kategori
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


data class BukuUiState(
    val detailBuku: DetailBuku = DetailBuku(),
    val isEntryValid: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

data class DetailBuku(
    val id: Int = 0,
    val judul: String = "",
    val status: String = "Tersedia",
    val kategoriId: Int? = null,
    val kategoriInfo: String = ""
)

fun DetailBuku.toEntity(): Buku = Buku(
    id = id,
    judul = judul,
    status = status,
    kategoriId = kategoriId
)

fun Buku.toDetail(): DetailBuku = DetailBuku(
    id = id,
    judul = judul,
    status = status,
    kategoriId = kategoriId
)



class HomeBukuViewModel(private val repository: RepositoryPerpustakaan) : ViewModel() {
    val bukuList: StateFlow<List<Buku>> = repository.getAllBuku()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}

class EntryBukuViewModel(private val repository: RepositoryPerpustakaan) : ViewModel() {
    var uiState by mutableStateOf(BukuUiState())
        private set
        
    val kategoriList: StateFlow<List<Kategori>> = repository.getAllKategori()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateUiState(detail: DetailBuku) {
        uiState = uiState.copy(
            detailBuku = detail,
            isEntryValid = detail.judul.isNotBlank() && detail.status.isNotBlank()
        )
    }

    suspend fun saveBuku() {
        if (uiState.isEntryValid) {
            try {
                repository.insertBuku(uiState.detailBuku.toEntity())
                uiState = uiState.copy(isSuccess = true, errorMessage = null)
            } catch (e: Exception) {
                uiState = uiState.copy(errorMessage = e.message)
            }
        }
    }
    
    fun resetState() {
        uiState = BukuUiState()
    }
}

class DetailBukuViewModel(
    savedStateHandle: androidx.lifecycle.SavedStateHandle,
    private val repository: RepositoryPerpustakaan
) : ViewModel() {
    val bukuId: Int = checkNotNull(savedStateHandle["bukuId"])

    val uiState: StateFlow<BukuUiState> = repository.getBukuStream(bukuId)
        .filterNotNull()
        .map { 
             BukuUiState(detailBuku = it.toDetail())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = BukuUiState()
        )
        
    suspend fun deleteBuku() {
        repository.deleteBuku(bukuId)
    }
}

class UpdateBukuViewModel(
    savedStateHandle: androidx.lifecycle.SavedStateHandle,
    private val repository: RepositoryPerpustakaan
) : ViewModel() {
    val bukuId: Int = checkNotNull(savedStateHandle["bukuId"])

    var uiState by mutableStateOf(BukuUiState())
        private set
        
    val kategoriList: StateFlow<List<Kategori>> = repository.getAllKategori()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.getBukuStream(bukuId)
                .filterNotNull()
                .collect { 
                    uiState = uiState.copy(detailBuku = it.toDetail(), isEntryValid = true)
                }
        }
    }

    fun updateUiState(detail: DetailBuku) {
        uiState = uiState.copy(
            detailBuku = detail,
            isEntryValid = detail.judul.isNotBlank()
        )
    }

    suspend fun updateBuku() {
        if (uiState.isEntryValid) {
             try {
                repository.updateBuku(uiState.detailBuku.toEntity())
                uiState = uiState.copy(isSuccess = true)
             } catch (e: Exception) {
                uiState = uiState.copy(errorMessage = e.message)
             }
        }
    }
}
