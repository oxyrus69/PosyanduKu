package com.posyanduku.app.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FamilyDao {
    @Query("SELECT * FROM families ORDER BY kepala") fun observeAll(): Flow<List<FamilyEntity>>
    @Query("SELECT COUNT(*) FROM families") fun observeCount(): Flow<Int>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsertAll(list: List<FamilyEntity>)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsert(item: FamilyEntity)
    @Query("SELECT COUNT(*) FROM families") suspend fun countSync(): Int
}

@Dao
interface TargetDao {
    @Query("SELECT * FROM targets ORDER BY nama") fun observeAll(): Flow<List<TargetEntity>>
    @Query("SELECT * FROM targets WHERE id = :id") fun observeById(id: String): Flow<TargetEntity?>
    @Query("SELECT * FROM targets ORDER BY nama") suspend fun allSync(): List<TargetEntity>
    @Query("SELECT COUNT(*) FROM targets") suspend fun countSync(): Int
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsertAll(list: List<TargetEntity>)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsert(item: TargetEntity)
}

@Dao
interface VisitDao {
    @Query("SELECT * FROM visits ORDER BY tanggal DESC") fun observeAll(): Flow<List<VisitEntity>>
    @Query("SELECT * FROM visits WHERE targetId = :targetId ORDER BY tanggal DESC") fun observeByTarget(targetId: String): Flow<List<VisitEntity>>
    @Query("SELECT COUNT(*) FROM visits") suspend fun countSync(): Int
    @Query("SELECT * FROM visits WHERE id = :id") suspend fun get(id: String): VisitEntity?
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsertAll(list: List<VisitEntity>)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsert(item: VisitEntity)
}

@Dao
interface ImmunizationDao {
    @Query("SELECT * FROM immunizations ORDER BY jadwal") fun observeAll(): Flow<List<ImmunizationEntity>>
    @Query("SELECT * FROM immunizations WHERE targetId = :targetId ORDER BY jadwal") fun observeByTarget(targetId: String): Flow<List<ImmunizationEntity>>
    @Query("SELECT COUNT(*) FROM immunizations") suspend fun countSync(): Int
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsertAll(list: List<ImmunizationEntity>)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsert(item: ImmunizationEntity)
}

@Dao
interface OutboxDao {
    @Query("SELECT * FROM outbox ORDER BY createdAt") fun observeAll(): Flow<List<OutboxEntity>>
    @Query("SELECT COUNT(*) FROM outbox") fun observeCount(): Flow<Int>
    @Insert(onConflict = OnConflictStrategy.IGNORE) suspend fun insert(item: OutboxEntity)
    @Query("DELETE FROM outbox WHERE clientRequestId = :id") suspend fun delete(id: String)
}
