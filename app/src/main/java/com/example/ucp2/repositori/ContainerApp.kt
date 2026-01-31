package com.example.ucp2.repositori

import android.app.Application
import android.content.Context
import com.example.ucp2.room.PerpustakaanDatabase

interface ContainerApp {
    val repositoryPerpustakaan: RepositoryPerpustakaan
}

class ContainerDataApp(private val context: Context): ContainerApp {
    override val repositoryPerpustakaan: RepositoryPerpustakaan by lazy {
        val db = PerpustakaanDatabase.getDatabase(context)
        RepositoryPerpustakaanOffline(db.bukuDao(), db.kategoriDao(), db.auditDao(), db, context)
    }
}

class PerpustakaanApp : Application() {
    lateinit var container: ContainerApp

    override fun onCreate() {
        super.onCreate()
        container = ContainerDataApp(this)
    }
}
