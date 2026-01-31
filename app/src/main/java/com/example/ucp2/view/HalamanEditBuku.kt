package com.example.ucp2.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ucp2.viewmodel.UpdateBukuViewModel
import com.example.ucp2.viewmodel.provider.PenyediaViewModel
import kotlinx.coroutines.launch
import androidx.compose.ui.res.stringResource
import com.example.ucp2.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BukuUpdateScreen(
    navigateBack: () -> Unit,
    viewModel: UpdateBukuViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val uiState = viewModel.uiState
    val kategoriList by viewModel.kategoriList.collectAsState()
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        topBar = {
            PerpustakaanTopAppBar(title = stringResource(R.string.edit_buku), canNavigateBack = true, navigateUp = navigateBack, scrollBehavior = scrollBehavior)
        }
    ) { innerPadding ->
        Column(
            Modifier.padding(innerPadding).padding(16.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = uiState.detailBuku.judul,
                onValueChange = { viewModel.updateUiState(uiState.detailBuku.copy(judul = it)) },
                label = { Text(stringResource(R.string.judul_buku)) },
                modifier = Modifier.fillMaxWidth()
            )
            

            var statusExpanded by remember { mutableStateOf(false) }
            val statusOptions = listOf(stringResource(R.string.tersedia), stringResource(R.string.dipinjam), stringResource(R.string.hilang))
            ExposedDropdownMenuBox(expanded = statusExpanded, onExpandedChange = { statusExpanded = !statusExpanded }) {
                OutlinedTextField(
                    value = uiState.detailBuku.status,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.status_buku)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = statusExpanded, onDismissRequest = { statusExpanded = false }) {
                    statusOptions.forEach { status ->
                        DropdownMenuItem(text = { Text(status) }, onClick = {
                            viewModel.updateUiState(uiState.detailBuku.copy(status = status))
                            statusExpanded = false
                        })
                    }
                }
            }
            

            var katExpanded by remember { mutableStateOf(false) }
            val selectedKategori = kategoriList.find { it.id == uiState.detailBuku.kategoriId }?.nama ?: stringResource(R.string.tanpa_kategori)
            
            ExposedDropdownMenuBox(expanded = katExpanded, onExpandedChange = { katExpanded = !katExpanded }) {
                OutlinedTextField(
                    value = selectedKategori,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.kategori_buku)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = katExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = katExpanded, onDismissRequest = { katExpanded = false }) {
                    DropdownMenuItem(text = { Text(stringResource(R.string.tanpa_kategori)) }, onClick = {
                        viewModel.updateUiState(uiState.detailBuku.copy(kategoriId = null))
                        katExpanded = false
                    })
                    kategoriList.forEach { kat ->
                        DropdownMenuItem(text = { Text(kat.nama) }, onClick = {
                            viewModel.updateUiState(uiState.detailBuku.copy(kategoriId = kat.id))
                            katExpanded = false
                        })
                    }
                }
            }
            
            Button(
                onClick = { scope.launch { viewModel.updateBuku(); if(uiState.isSuccess) navigateBack() } },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.isEntryValid
            ) {
                Text(stringResource(R.string.update))
            }
            if (uiState.errorMessage != null) {
                Text(uiState.errorMessage!!, color = Color.Red)
            }
        }
    }
}
