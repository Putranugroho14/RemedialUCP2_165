package com.example.ucp2.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "kategori",
    indices = [
        Index(value = ["nama"], unique = true)
    ]
)
data class Kategori(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nama: String,
    val deskripsi: String,
    val parentId: Int? = null,
    val isDeleted: Boolean = false
)

@Entity(
    tableName = "buku",
    foreignKeys = [
        ForeignKey(
            entity = Kategori::class,
            parentColumns = ["id"],
            childColumns = ["kategoriId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index(value = ["kategoriId"])]
)
data class Buku(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val judul: String,
    val status: String,
    val kategoriId: Int?,
    val isDeleted: Boolean = false
)

@Entity(tableName = "penulis")
data class Penulis(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nama: String,
    val isDeleted: Boolean = false
)

@Entity(
    tableName = "buku_penulis_cross_ref",
    primaryKeys = ["bukuId", "penulisId"],
    foreignKeys = [
        ForeignKey(entity = Buku::class, parentColumns = ["id"], childColumns = ["bukuId"]),
        ForeignKey(entity = Penulis::class, parentColumns = ["id"], childColumns = ["penulisId"])
    ],
    indices = [Index(value = ["penulisId"]), Index(value = ["bukuId"])]
)
data class BukuPenulisCrossRef(
    val bukuId: Int,
    val penulisId: Int
)

@Entity(tableName = "audit_log")
data class AuditLog(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val action: String,
    val tableName: String,
    val recordId: Int,
    val userId: String = "System",
    val notes: String = ""
)
