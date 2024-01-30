package org.example

import com.google.common.geometry.S2CellId

fun calculateRemainingBandwidths(
    totalBandwidths: Map<CarrierReference, FrequencyKhz>,
    spectralEfficiencies: Map<CarrierCell, SpectralEfficiencyBpSpHz>,
    allocatedDemands: Map<CarrierCell, BitRateKbps>,
): Map<CarrierReference, FrequencyKhz> {
    val remainingBandwidths = HashMap(totalBandwidths)
    for ((carrierCell, demand) in allocatedDemands) {
        val efficiency = spectralEfficiencies.getOrDefault(carrierCell, 0.0.bpspHz)
        remainingBandwidths.merge(carrierCell.carrier, demand / efficiency, FrequencyKhz::minus)
    }
    return remainingBandwidths
}

fun unloadOverloadedCarriers(
    remainingBandwidths: Map<CarrierReference, FrequencyKhz>,
    spectralEfficiencies: Map<CarrierCell, SpectralEfficiencyBpSpHz>,
    allocatedDemands: MutableMap<CarrierCell, BitRateKbps>,
    cellsToPreempt: List<S2CellWithDemandClass>,
) {
    for ((carrier, remainingBandwidth) in remainingBandwidths) {
        var remainingBandwidthVar = remainingBandwidth
        for (cell in cellsToPreempt) {
            val carrierCell = CarrierCell(carrier, cell)
            val allocatedDemand = allocatedDemands.getOrDefault(carrierCell, 0.0.kbps)
            if (allocatedDemand > 0.0.kbps && remainingBandwidthVar < 0.0.kHz) {
                val spectralEfficiency = spectralEfficiencies.getOrDefault(carrierCell, 0.0.bpspHz)
                val update =
                    calculateCarrierDeallocationUpdate(
                        cell.demandClass,
                        -remainingBandwidthVar, // The remaining bandwidth is negative, so make it positive
                        allocatedDemand,
                        spectralEfficiency,
                    )
                allocatedDemands.merge(carrierCell, update.demandToDeallocate, BitRateKbps::minus)
                remainingBandwidthVar += update.extraBandwidth
            }
        }
    }
}

fun calculateCarrierDeallocationUpdate(
    demandClass: DemandClass,
    overloadedBandwidth: FrequencyKhz,
    demandAllocatedForCell: BitRateKbps,
    spectralEfficiency: SpectralEfficiencyBpSpHz,
): DemandDeallocation {
    return if (demandClass.isHdr()) {
        calculateCarrierDeallocationUpdateHdr(demandAllocatedForCell, spectralEfficiency)
    } else {
        calculateCarrierDeallocationUpdateNonHdr(overloadedBandwidth, demandAllocatedForCell, spectralEfficiency)
    }
}

fun calculateCarrierDeallocationUpdateHdr(
    demandAllocatedForCell: BitRateKbps,
    spectralEfficiency: SpectralEfficiencyBpSpHz,
): DemandDeallocation {
    return DemandDeallocation(demandAllocatedForCell, demandAllocatedForCell / spectralEfficiency)
}

fun calculateCarrierDeallocationUpdateNonHdr(
    overloadedBandwidth: FrequencyKhz,
    demandAllocatedForCell: BitRateKbps,
    spectralEfficiency: SpectralEfficiencyBpSpHz,
): DemandDeallocation {
    val overloadedDemandOnCarrier = spectralEfficiency * overloadedBandwidth
    return if (overloadedDemandOnCarrier < demandAllocatedForCell) {
        DemandDeallocation(overloadedDemandOnCarrier, overloadedBandwidth)
    } else if (overloadedDemandOnCarrier > demandAllocatedForCell) {
        DemandDeallocation(demandAllocatedForCell, demandAllocatedForCell / spectralEfficiency)
    } else {
        DemandDeallocation(demandAllocatedForCell, overloadedBandwidth)
    }
}

val Int.S2 get() =
    S2CellId(this.toLong())
        .also { s2Cell -> s2Cell.validate() }

fun S2CellId.validate() {
    if (!this.isValid) {
        throw Exception("Invalid S2CellId ${this.id()}")
    }
}
