package com.example.ucp2.viewmodel.provider

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.ucp2.repositori.PerpustakaanApp
import com.example.ucp2.viewmodel.DetailBukuViewModel
import com.example.ucp2.viewmodel.DetailKategoriViewModel
import com.example.ucp2.viewmodel.EntryBukuViewModel
import com.example.ucp2.viewmodel.EntryKategoriViewModel
import com.example.ucp2.viewmodel.HomeBukuViewModel
import com.example.ucp2.viewmodel.HomeKategoriViewModel
import com.example.ucp2.viewmodel.UpdateBukuViewModel
import com.example.ucp2.viewmodel.UpdateKategoriViewModel

object PenyediaViewModel {
    val Factory = viewModelFactory {
        initializer {
            HomeKategoriViewModel(perpustakaanApp().container.repositoryPerpustakaan)
        }
        initializer {
            EntryKategoriViewModel(perpustakaanApp().container.repositoryPerpustakaan)
        }
        initializer {
            DetailKategoriViewModel(
                this.createSavedStateHandle(),
                perpustakaanApp().container.repositoryPerpustakaan
            )
        }
        initializer {
            UpdateKategoriViewModel(
                this.createSavedStateHandle(),
                perpustakaanApp().container.repositoryPerpustakaan
            )
        }


        initializer {
            HomeBukuViewModel(perpustakaanApp().container.repositoryPerpustakaan)
        }
        initializer {
            EntryBukuViewModel(perpustakaanApp().container.repositoryPerpustakaan)
        }
        initializer {
            DetailBukuViewModel(
                this.createSavedStateHandle(),
                perpustakaanApp().container.repositoryPerpustakaan
            )
        }
        initializer {
            UpdateBukuViewModel(
                this.createSavedStateHandle(),
                perpustakaanApp().container.repositoryPerpustakaan
            )
        }
    }
}

fun CreationExtras.perpustakaanApp(): PerpustakaanApp =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as PerpustakaanApp)
