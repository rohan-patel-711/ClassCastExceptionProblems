package org.example

@JvmInline
value class FrequencyKhz(private val doubleKhz: Double) : Comparable<FrequencyKhz> {
    operator fun unaryMinus() = FrequencyKhz(-this.doubleKhz)

    operator fun plus(other: FrequencyKhz) = FrequencyKhz(this.doubleKhz + other.doubleKhz)

    operator fun minus(other: FrequencyKhz) = FrequencyKhz(this.doubleKhz - other.doubleKhz)

    override operator fun compareTo(other: FrequencyKhz) = this.doubleKhz.compareTo(other.doubleKhz)

    fun toDoubleKhz() = this.doubleKhz
}

val Double.kHz get() = FrequencyKhz(this)
