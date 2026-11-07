package com.posyanduku.app.data

enum class TargetType(val label: String, val urutan: Int) {
    BUMIL("Ibu Hamil", 1),
    BALITA("Balita", 2),
    REMAJA_LANSIA("Remaja/Lansia", 3);
}

enum class Sex { L, P }

enum class ZFactor { BBU, TBU, BBTB }

/** Status semantik bersama lintas modul. */
enum class StatusFlag(val label: String) {
    OKE("Baik"),
    PERHATIAN("Perlu Perhatian"),
    RUJUK("Rujuk");
}
