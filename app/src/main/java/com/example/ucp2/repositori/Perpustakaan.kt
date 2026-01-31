package com.example.ucp2.repositori

import androidx.room.withTransaction
import com.example.ucp2.room.AuditDao
import com.example.ucp2.room.AuditLog
import com.example.ucp2.room.Buku
import com.example.ucp2.room.BukuDao
import com.example.ucp2.room.Kategori
import com.example.ucp2.room.KategoriDao
import com.example.ucp2.room.PerpustakaanDatabase
import kotlinx.coroutines.flow.Flow
import android.content.Context
import com.example.ucp2.R

interface RepositoryPerpustakaan {
    fun getAllKategori(): Flow<List<Kategori>>
    fun getAllBuku(): Flow<List<Buku>>
    fun getBukuStream(id: Int): Flow<Buku?>
    fun getKategoriStream(id: Int): Flow<Kategori?>
    

    fun getBukuByKategoriRecursive(categoryId: Int): Flow<List<Buku>>

    suspend fun insertKategori(kategori: Kategori)
    suspend fun updateKategori(kategori: Kategori)
    

    suspend fun deleteKategori(categoryId: Int, deleteBooks: Boolean)

    suspend fun insertBuku(buku: Buku)
    suspend fun updateBuku(buku: Buku)
    suspend fun deleteBuku(id: Int)
}

class RepositoryPerpustakaanOffline(
    private val bukuDao: BukuDao,
    private val kategoriDao: KategoriDao,
    private val auditDao: AuditDao,
    private val database: PerpustakaanDatabase,
    private val context: Context
) : RepositoryPerpustakaan {

    override fun getAllKategori(): Flow<List<Kategori>> = kategoriDao.getAllKategori()
    override fun getAllBuku(): Flow<List<Buku>> = bukuDao.getAllBuku()
    
    override fun getBukuStream(id: Int): Flow<Buku?> = bukuDao.getBuku(id)

    override fun getKategoriStream(id: Int): Flow<Kategori?> = kategoriDao.getKategori(id) 

    override fun getBukuByKategoriRecursive(categoryId: Int): Flow<List<Buku>> = 
        bukuDao.getBukuByKategoriRecursive(categoryId)

    override suspend fun insertKategori(kategori: Kategori) {
        database.withTransaction {
            val id = kategoriDao.insertKategori(kategori)
            auditDao.insertLog(AuditLog(action = "INSERT", tableName = "kategori", recordId = id.toInt()))
        }
    }

    override suspend fun updateKategori(kategori: Kategori) {

        if (kategori.parentId != null) {
            val count = kategoriDao.isAncestor(kategori.id, kategori.parentId)
            if (count > 0) {
                throw Exception(context.getString(R.string.cyclic_error))
            }
        }
        
        database.withTransaction {
           kategoriDao.updateKategori(kategori)
           auditDao.insertLog(AuditLog(action = "UPDATE", tableName = "kategori", recordId = kategori.id))
        }
    }

    override suspend fun deleteKategori(categoryId: Int, deleteBooks: Boolean) {
        database.withTransaction {

             val borrowedCount = bukuDao.countDipinjamByKategori(categoryId)
             if (borrowedCount > 0) {
                 throw Exception(context.getString(R.string.borrowed_error))
             }
             

             if (deleteBooks) {

                 bukuDao.softDeleteBukuByKategori(categoryId)
                 auditDao.insertLog(AuditLog(action = "SOFT_DELETE_CASCADE", tableName = "buku", recordId = categoryId))
             } else {

                 bukuDao.unlinkBukuByKategori(categoryId)
                 auditDao.insertLog(AuditLog(action = "UNLINK_BOOKS", tableName = "buku", recordId = categoryId))
             }
             

             kategoriDao.softDeleteKategori(categoryId)
             auditDao.insertLog(AuditLog(action = "SOFT_DELETE", tableName = "kategori", recordId = categoryId))
        }
    }

    override suspend fun insertBuku(buku: Buku) {
        database.withTransaction {
            val id = bukuDao.insertBuku(buku)
            auditDao.insertLog(AuditLog(action = "INSERT", tableName = "buku", recordId = id.toInt()))
        }
    }

    override suspend fun updateBuku(buku: Buku) {
        database.withTransaction {
             bukuDao.updateBuku(buku)
             auditDao.insertLog(AuditLog(action = "UPDATE", tableName = "buku", recordId = buku.id))
        }
    }

    override suspend fun deleteBuku(id: Int) {
         bukuDao.softDeleteBuku(id)
         auditDao.insertLog(AuditLog(action = "SOFT_DELETE", tableName = "buku", recordId = id))
    }
}
