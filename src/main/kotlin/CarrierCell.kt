package org.example

import com.google.common.geometry.S2CellId

data class CarrierCell(
    val carrier: CarrierReference,
    val cell: S2CellWithDemandClass,
)

data class S2CellWithDemandClass(val s2CellId: S2CellId, val demandClass: DemandClass) : Comparable<S2CellWithDemandClass> {
    override fun compareTo(other: S2CellWithDemandClass): Int {
        return compareValuesBy(this, other, { it.s2CellId }, { it.demandClass })
    }
}

enum class DemandClass {
    LEG_L,
    LDR_L,
    HDR_NONE,
    HDR_L,
    HDR_S,
    HDR_LS;

    fun isHdr() = when (this) {
        HDR_LS, HDR_L, HDR_S, HDR_NONE -> true
        else -> false
    }
}