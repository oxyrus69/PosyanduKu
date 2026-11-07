package com.posyanduku.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        FamilyEntity::class,
        TargetEntity::class,
        VisitEntity::class,
        ImmunizationEntity::class,
        SdidtkEntity::class,
        OutboxEntity::class,
    ],
    version = 1,
    // Lihat catatan di build.gradle: folder project memuat spasi, jadi
    // exportSchema diminta mati; skema versi 1 disimpan manual.
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class PosyanduDatabase : RoomDatabase() {
    abstract fun familyDao(): FamilyDao
    abstract fun targetDao(): TargetDao
    abstract fun visitDao(): VisitDao
    abstract fun immunizationDao(): ImmunizationDao
    abstract fun outboxDao(): OutboxDao

    companion object {
        @Volatile private var instance: PosyanduDatabase? = null
        fun get(context: Context): PosyanduDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    PosyanduDatabase::class.java,
                    "posyanduku.db",
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { instance = it }
            }
    }
}
