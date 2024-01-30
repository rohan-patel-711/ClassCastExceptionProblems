package org.example

data class DemandDeallocation(
    val demandToDeallocate: BitRateKbps,
    val extraBandwidth: FrequencyKhz,
)