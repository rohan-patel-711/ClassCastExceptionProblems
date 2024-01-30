package org.example

@JvmInline
value class SpectralEfficiencyBpSpHz(private val spectralEfficiencyBpSpHz: Double) : Comparable<SpectralEfficiencyBpSpHz> {
    operator fun times(other: FrequencyKhz) = BitRateKbps(this.spectralEfficiencyBpSpHz * other.toDoubleKhz())

    override operator fun compareTo(other: SpectralEfficiencyBpSpHz) =
        this.spectralEfficiencyBpSpHz.compareTo(
            other.spectralEfficiencyBpSpHz,
        )

    fun toSpectralEfficiencyBpSpHz() = this.spectralEfficiencyBpSpHz
}

val Double.bpspHz get() = SpectralEfficiencyBpSpHz(this)
