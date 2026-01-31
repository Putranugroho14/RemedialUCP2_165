package com.example.ucp2.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ucp2.viewmodel.*
import com.example.ucp2.viewmodel.provider.PenyediaViewModel
import kotlinx.coroutines.launch
import androidx.compose.ui.res.stringResource
import com.example.ucp2.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BukuHomeScreen(
    navigateBack: () -> Unit,
    navigateToEntry: () -> Unit,
    onDetailClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeBukuViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val uiState by viewModel.bukuList.collectAsState()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { PerpustakaanTopAppBar(title = stringResource(R.string.daftar_buku), canNavigateBack = true, navigateUp = navigateBack, scrollBehavior = scrollBehavior) },
        floatingActionButton = {
            FloatingActionButton(onClick = navigateToEntry, modifier = Modifier.padding(18.dp)) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_buku))
            }
        }
    ) { innerPadding ->
        if (uiState.isEmpty()) {
            Box(Modifier.padding(innerPadding).fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.no_buku), style = MaterialTheme.typography.titleLarge)
            }
        } else {
            LazyColumn(Modifier.padding(innerPadding)) {
                items(uiState) { item ->
                    Card(Modifier.padding(8.dp).fillMaxWidth().clickable { onDetailClick(item.id) }, elevation = CardDefaults.cardElevation(2.dp)) {
                        Column(Modifier.padding(16.dp)) {
                            Text(item.judul, style = MaterialTheme.typography.titleLarge)
                            Text(stringResource(R.string.status_format, item.status), style = MaterialTheme.typography.bodyMedium)
                            Text(stringResource(R.string.kategori_id, item.kategoriId?.toString() ?: stringResource(R.string.tanpa_kategori)), style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BukuEntryScreen(
    navigateBack: () -> Unit,
    viewModel: EntryBukuViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val uiState = viewModel.uiState
    val kategoriList by viewModel.kategoriList.collectAsState()
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(topBar = { PerpustakaanTopAppBar(stringResource(R.string.tambah_buku), true, Modifier, scrollBehavior, navigateBack) }) { innerPadding ->
        Column(Modifier.padding(innerPadding).padding(16.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
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
            
            Button(onClick = { scope.launch { viewModel.saveBuku(); if(uiState.isSuccess) navigateBack() } }, Modifier.fillMaxWidth(), enabled = uiState.isEntryValid) {
                Text(stringResource(R.string.simpan))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BukuDetailScreen(
    navigateBack: () -> Unit,
    onEdit: (Int) -> Unit,
    viewModel: DetailBukuViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    
    Scaffold(
        topBar = { PerpustakaanTopAppBar(stringResource(R.string.detail_buku), true, Modifier, scrollBehavior, navigateBack) },
        floatingActionButton = {
            FloatingActionButton(onClick = { onEdit(uiState.detailBuku.id) }, Modifier.padding(18.dp)) {
                Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.edit_buku))
            }
        }
    ) { innerPadding ->
        Column(Modifier.padding(innerPadding).padding(16.dp)) {
            Text(stringResource(R.string.judul_format, uiState.detailBuku.judul), style = MaterialTheme.typography.titleLarge)
            Text(stringResource(R.string.status_format, uiState.detailBuku.status))
            Text(stringResource(R.string.kategori_id, uiState.detailBuku.kategoriId?.toString() ?: stringResource(R.string.none)))
            Spacer(Modifier.height(16.dp))
            Button(onClick = { scope.launch { viewModel.deleteBuku(); navigateBack() } }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                Text(stringResource(R.string.hapus_buku))
            }
        }
    }
}
