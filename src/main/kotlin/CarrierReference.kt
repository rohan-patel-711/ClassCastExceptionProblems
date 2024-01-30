package org.example

data class CarrierReference(
    val beamId: Int,
    val direction: Direction,
    val mobileCenterFrequency: Long,
    val carrierType: Int,
) : Comparable<CarrierReference> {
    override fun compareTo(other: CarrierReference): Int {
        return compareValuesBy(this, other, { it.beamId }, { it.direction }, { it.mobileCenterFrequency }, { it.carrierType })
    }
}
enum class Direction {
    TX,
    RX,
}