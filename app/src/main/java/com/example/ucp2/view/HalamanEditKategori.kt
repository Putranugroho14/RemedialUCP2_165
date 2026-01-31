package com.example.ucp2.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ucp2.viewmodel.UpdateKategoriViewModel
import com.example.ucp2.viewmodel.provider.PenyediaViewModel
import kotlinx.coroutines.launch
import androidx.compose.ui.res.stringResource
import com.example.ucp2.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KategoriUpdateScreen(
    navigateBack: () -> Unit,
    viewModel: UpdateKategoriViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val uiState = viewModel.uiState
    val parentList by viewModel.parentList.collectAsState()
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        topBar = {
            PerpustakaanTopAppBar(title = stringResource(R.string.edit_kategori), canNavigateBack = true, navigateUp = navigateBack, scrollBehavior = scrollBehavior)
        }
    ) { innerPadding ->
        Column(
            Modifier.padding(innerPadding).padding(16.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = uiState.detailKategori.nama,
                onValueChange = { viewModel.updateUiState(uiState.detailKategori.copy(nama = it)) },
                label = { Text(stringResource(R.string.nama_kategori)) },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = uiState.detailKategori.deskripsi,
                onValueChange = { viewModel.updateUiState(uiState.detailKategori.copy(deskripsi = it)) },
                label = { Text(stringResource(R.string.deskripsi_kategori)) },
                modifier = Modifier.fillMaxWidth()
            )
            

            var expanded by remember { mutableStateOf(false) }
            val selectedParent = parentList.find { it.id == uiState.detailKategori.parentId }?.nama ?: stringResource(R.string.none)
            
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                OutlinedTextField(
                    value = selectedParent,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.parent_kategori)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    DropdownMenuItem(text = { Text(stringResource(R.string.none)) }, onClick = {
                        viewModel.updateUiState(uiState.detailKategori.copy(parentId = null))
                        expanded = false
                    })
                    parentList.forEach { parent ->

                         if (parent.id != uiState.detailKategori.id) {
                            DropdownMenuItem(text = { Text(parent.nama) }, onClick = {
                                viewModel.updateUiState(uiState.detailKategori.copy(parentId = parent.id))
                                expanded = false
                            })
                         }
                    }
                }
            }
            
            Button(
                onClick = { scope.launch { viewModel.updateKategori(); if(uiState.isSuccess) navigateBack() } },
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
