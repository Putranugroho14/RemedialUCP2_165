package com.example.ucp2.view.route

import com.example.ucp2.R

interface DestinasiNavigasi {
    val route: String
    val titleRes: Int
}

object DestinasiHomeKategori : DestinasiNavigasi {
    override val route = "home_kategori"
    override val titleRes = R.string.app_name
}

object DestinasiEntryKategori : DestinasiNavigasi {
    override val route = "entry_kategori"
    override val titleRes = R.string.app_name
}

object DestinasiDetailKategori : DestinasiNavigasi {
    override val route = "detail_kategori"
    override val titleRes = R.string.app_name
    const val kategoriId = "kategoriId"
    val routeWithArgs = "$route/{$kategoriId}"
}

object DestinasiUpdateKategori : DestinasiNavigasi {
    override val route = "update_kategori"
    override val titleRes = R.string.app_name
    const val kategoriId = "kategoriId"
    val routeWithArgs = "$route/{$kategoriId}"
}

object DestinasiHomeBuku : DestinasiNavigasi {
    override val route = "home_buku"
    override val titleRes = R.string.app_name
}

object DestinasiEntryBuku : DestinasiNavigasi {
    override val route = "entry_buku"
    override val titleRes = R.string.app_name
}

object DestinasiDetailBuku : DestinasiNavigasi {
    override val route = "detail_buku"
    override val titleRes = R.string.app_name
    const val bukuId = "bukuId"
    val routeWithArgs = "$route/{$bukuId}"
}

object DestinasiUpdateBuku : DestinasiNavigasi {
    override val route = "update_buku"
    override val titleRes = R.string.app_name
    const val bukuId = "bukuId"
    val routeWithArgs = "$route/{$bukuId}"
}
