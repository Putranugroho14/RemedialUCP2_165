package com.example.ucp2.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ucp2.viewmodel.*
import com.example.ucp2.viewmodel.provider.PenyediaViewModel
import com.example.ucp2.view.route.*
import kotlinx.coroutines.launch
import androidx.compose.ui.res.stringResource
import com.example.ucp2.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KategoriHomeScreen(
    navigateBack: () -> Unit,
    navigateToEntry: () -> Unit,
    onDetailClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeKategoriViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val uiState by viewModel.kategoriList.collectAsState()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            PerpustakaanTopAppBar(
                title = stringResource(R.string.daftar_kategori),
                canNavigateBack = true,
                navigateUp = navigateBack,
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = navigateToEntry, shape = MaterialTheme.shapes.medium, modifier = Modifier.padding(18.dp)) {
                Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(R.string.tambah_kategori))
            }
        }
    ) { innerPadding ->
        if (uiState.isEmpty()) {
            Box(Modifier.padding(innerPadding).fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.no_kategori), style = MaterialTheme.typography.titleLarge)
            }
        } else {
            LazyColumn(Modifier.padding(innerPadding)) {
                items(uiState) { item ->
                    Card(
                        modifier = Modifier.padding(8.dp).fillMaxWidth().clickable { onDetailClick(item.id) },
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(item.nama, style = MaterialTheme.typography.titleLarge)
                            Text(stringResource(R.string.id_format, item.id), style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KategoriEntryScreen(
    navigateBack: () -> Unit,
    viewModel: EntryKategoriViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val uiState = viewModel.uiState
    val parentList by viewModel.parentList.collectAsState()
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        topBar = {
            PerpustakaanTopAppBar(title = stringResource(R.string.tambah_kategori), canNavigateBack = true, navigateUp = navigateBack, scrollBehavior = scrollBehavior)
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
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
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
                        DropdownMenuItem(text = { Text(parent.nama) }, onClick = {
                            viewModel.updateUiState(uiState.detailKategori.copy(parentId = parent.id))
                            expanded = false
                        })
                    }
                }
            }
            
            Button(
                onClick = { scope.launch { viewModel.saveKategori(); if(uiState.isSuccess) navigateBack() } },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.isEntryValid
            ) {
                Text(stringResource(R.string.simpan))
            }
            if (uiState.errorMessage != null) {
                Text(uiState.errorMessage!!, color = Color.Red)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KategoriDetailScreen(
    navigateBack: () -> Unit,
    onEdit: (Int) -> Unit,
    viewModel: DetailKategoriViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val books by viewModel.recursiveBookList.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val scope = rememberCoroutineScope()
    

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.dialog_delete_kategori_title)) },
            text = { Text(stringResource(R.string.dialog_delete_kategori_desc)) },
            confirmButton = {
                TextButton(onClick = { 
                    scope.launch { 
                        viewModel.deleteKategori(deleteBooks = false)
                        if (viewModel.deleteError == null) {
                            navigateBack()
                        } else {
                            showDeleteDialog = false
                            showErrorDialog = true
                        }
                    } 
                }) {
                    Text(stringResource(R.string.action_delete_recursive))
                }
            },
            dismissButton = {
                 TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.action_unlink))
                }
            }
        )
    }
    
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text("Gagal Menghapus") },
            text = { Text(viewModel.deleteError ?: "Terjadi kesalahan") },
            confirmButton = {
                TextButton(onClick = { showErrorDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            PerpustakaanTopAppBar(title = stringResource(R.string.detail_kategori), canNavigateBack = true, navigateUp = navigateBack, scrollBehavior = scrollBehavior)
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onEdit(uiState.detailKategori.id) }, Modifier.padding(18.dp)) {
                Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.edit_kategori))
            }
        }
    ) { innerPadding ->
        Column(Modifier.padding(innerPadding).padding(16.dp).fillMaxWidth()) {
            Text(stringResource(R.string.nama_format, uiState.detailKategori.nama), style = MaterialTheme.typography.titleLarge)
            Text(stringResource(R.string.deskripsi_format, uiState.detailKategori.deskripsi))
            Spacer(Modifier.height(16.dp))
            Button(onClick = { showDeleteDialog = true }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete))
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.hapus_kategori))
            }
            if (viewModel.deleteError != null) {
                 Text(viewModel.deleteError!!, color = Color.Red)
            }
            
            Spacer(Modifier.height(24.dp))
            Text(stringResource(R.string.buku_di_kategori), style = MaterialTheme.typography.titleMedium)
            LazyColumn {
                items(books) { book ->
                     Card(Modifier.padding(vertical = 4.dp).fillMaxWidth(), elevation = CardDefaults.cardElevation(1.dp)) {
                         Column(Modifier.padding(8.dp)) {
                             Text(book.judul, style = MaterialTheme.typography.bodyLarge)
                             Text(stringResource(R.string.status_format, book.status), style = MaterialTheme.typography.bodySmall)
                         }
                     }
                }
            }
        }
    }
}
