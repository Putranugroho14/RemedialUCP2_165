package com.example.ucp2.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Buku::class, Kategori::class, Penulis::class, BukuPenulisCrossRef::class, AuditLog::class],
    version = 1,
    exportSchema = false
)
abstract class PerpustakaanDatabase : RoomDatabase() {

    abstract fun bukuDao(): BukuDao
    abstract fun kategoriDao(): KategoriDao
    abstract fun auditDao(): AuditDao

    companion object {
        @Volatile
        private var INSTANCE: PerpustakaanDatabase? = null

        fun getDatabase(context: Context): PerpustakaanDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    PerpustakaanDatabase::class.java,
                    "library_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
