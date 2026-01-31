package com.example.ucp2.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface KategoriDao {
    @Query("SELECT * FROM kategori WHERE isDeleted = 0 ORDER BY nama ASC")
    fun getAllKategori(): Flow<List<Kategori>>

    @Query("SELECT * FROM kategori WHERE id = :id")
    fun getKategori(id: Int): Flow<Kategori>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertKategori(kategori: Kategori): Long

    @Update
    suspend fun updateKategori(kategori: Kategori)


    @Query("""
        WITH RECURSIVE Ancestors AS (
            SELECT id, parentId FROM kategori WHERE id = :startNodeId
            UNION ALL
            SELECT k.id, k.parentId FROM kategori k
            JOIN Ancestors a ON k.id = a.parentId
        )
        SELECT COUNT(*) FROM Ancestors WHERE id = :checkNodeId
    """)
    suspend fun isAncestor(startNodeId: Int?, checkNodeId: Int): Int

    @Query("UPDATE kategori SET isDeleted = 1 WHERE id = :id")
    suspend fun softDeleteKategori(id: Int)
}

@Dao
interface BukuDao {
    @Query("SELECT * FROM buku WHERE isDeleted = 0 ORDER BY judul ASC")
    fun getAllBuku(): Flow<List<Buku>>

    @Query("SELECT * FROM buku WHERE id = :id")
    fun getBuku(id: Int): Flow<Buku>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBuku(buku: Buku): Long

    @Query("UPDATE buku SET kategoriId = NULL WHERE kategoriId = :categoryId AND status != 'Dipinjam'")
    suspend fun unlinkBukuByKategori(categoryId: Int)

    @Update
    suspend fun updateBuku(buku: Buku)

    @Query("UPDATE buku SET isDeleted = 1 WHERE id = :id")
    suspend fun softDeleteBuku(id: Int)


    @Transaction
    @Query("""
        WITH RECURSIVE CategoryTree(id) AS (
            SELECT id FROM kategori WHERE id = :categoryId AND isDeleted = 0
            UNION ALL
            SELECT k.id FROM kategori k
            JOIN CategoryTree ct ON k.parentId = ct.id
            WHERE k.isDeleted = 0
        )
        SELECT * FROM buku WHERE kategoriId IN (SELECT id FROM CategoryTree) AND isDeleted = 0
    """)
    fun getBukuByKategoriRecursive(categoryId: Int): Flow<List<Buku>>

    @Query("SELECT COUNT(*) FROM buku WHERE kategoriId = :categoryId AND status = 'Dipinjam' AND isDeleted = 0")
    suspend fun countDipinjamByKategori(categoryId: Int): Int


    @Query("UPDATE buku SET isDeleted = 1 WHERE kategoriId = :categoryId AND status != 'Dipinjam'")
    suspend fun softDeleteBukuByKategori(categoryId: Int)


}

@Dao
interface AuditDao {
    @Insert
    suspend fun insertLog(log: AuditLog)
}
