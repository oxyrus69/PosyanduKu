package com.posyanduku.app.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.posyanduku.app.data.Sex
import com.posyanduku.app.data.StatusFlag
import com.posyanduku.app.data.TargetType
import java.time.LocalDate

@Entity(tableName = "families")
data class FamilyEntity(
    @PrimaryKey val id: String,
    val noKk: String,
    val kepala: String,
    val rt: String,
    val rw: String,
    val anggota: Int,
)

@Entity(tableName = "targets")
data class TargetEntity(
    @PrimaryKey val id: String,
    val familyId: String?,
    val nama: String,
    val tanggalLahir: LocalDate,
    val sex: Sex,
    val type: TargetType,
    val nik: String?,
    val namaIbu: String?,
    val hamilKe: Int?,
    val hpht: LocalDate?,             // untuk BUMIL; hpl = hpht+280
    val upSwipeDate: LocalDate?,      // dipakai Home/Kunjungan
)

@Entity(tableName = "visits")
data class VisitEntity(
    @PrimaryKey val id: String,
    val targetId: String,
    val tanggal: LocalDate,
    val jenis: String,                // "Posyandu" / "Kunjungan rumah"
    val bb: Double?, val tb: Double?,
    val imt: Double?,                 // remaja/lansia
    val lila: Double?,                // bumil
    val status: StatusFlag,
    val catatan: String,
    val zBbu: Double?, val zTbu: Double?, val zBbtb: Double?,
    val sdidtkSkor: Int?, val sdidtkStatus: StatusFlag?,
)

@Entity(tableName = "immunizations")
data class ImmunizationEntity(
    @PrimaryKey val id: String,
    val targetId: String,
    val vaksin: String,
    val dosis: String,
    val jadwal: LocalDate,
    val diberikan: LocalDate?,
)

@Entity(tableName = "sdidtk")
data class SdidtkEntity(
    @PrimaryKey val id: String,
    val targetId: String,
    val tanggal: LocalDate,
    val skor: Int,
    val detail: String,
    val status: StatusFlag,
)

@Entity(tableName = "outbox")
data class OutboxEntity(
    @PrimaryKey val clientRequestId: String,   // UUID v4 => idempotensi
    val kind: String,                          // "visit"/"imunisasi"/"target"
    val refId: String,
    val createdAt: Long,
)
