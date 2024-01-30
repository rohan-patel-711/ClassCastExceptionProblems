package org.example

@JvmInline
value class BitRateKbps(private val doubleKbps: Double) : Comparable<BitRateKbps> {
    operator fun minus(other: BitRateKbps) = BitRateKbps(this.doubleKbps - other.doubleKbps)

    operator fun div(other: SpectralEfficiencyBpSpHz): FrequencyKhz {
        val efficiency = other.toSpectralEfficiencyBpSpHz()
        val bandwidth = if (efficiency > 0.0) this.doubleKbps / efficiency else 0.0
        return FrequencyKhz(bandwidth)
    }

    override operator fun compareTo(other: BitRateKbps) = this.doubleKbps.compareTo(other.doubleKbps)
}

val Double.kbps get() = BitRateKbps(this)
