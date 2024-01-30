import com.google.common.geometry.S2CellId
import org.example.CarrierCell
import org.example.CarrierReference
import org.example.DemandClass
import org.example.Direction
import org.example.S2
import org.example.S2CellWithDemandClass
import org.example.bpspHz
import org.example.calculateRemainingBandwidths
import org.example.kHz
import org.example.kbps
import org.example.unloadOverloadedCarriers
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class Test {
    @Test
    fun `calculate remaining bandwidth for each channel test - IC-PENALTY-FACTOR-06`() {
        val cell1 = cell(s2Cell = 11.S2)
        val cell2 = cell(s2Cell = 21.S2)
        val cell3 = cell(s2Cell = 31.S2)
        val carrier1 = carrier(beamId = 1)
        val carrier2 = carrier(beamId = 2)

        val bandwidths = mapOf(carrier1 to 100.0.kHz, carrier2 to 100.0.kHz)

        val allocatedDemands =
            mapOf(
                CarrierCell(carrier1, cell1) to 60.0.kbps,
                CarrierCell(carrier1, cell2) to 150.0.kbps,
                CarrierCell(carrier2, cell1) to 80.0.kbps,
                CarrierCell(carrier2, cell3) to 30.0.kbps,
            )

        val spectralEfficiencies =
            mapOf(
                CarrierCell(carrier1, cell1) to 1.0.bpspHz,
                CarrierCell(carrier1, cell2) to 3.0.bpspHz,
                CarrierCell(carrier2, cell1) to 2.0.bpspHz,
                CarrierCell(carrier2, cell3) to 1.0.bpspHz,
            )

        val expectedRemainingBandwidth = mapOf(carrier1 to (-10.0).kHz, carrier2 to 30.0.kHz)
        val calculatedRemainingBandwidth = calculateRemainingBandwidths(bandwidths, spectralEfficiencies, allocatedDemands)

        assertEquals(
            expectedRemainingBandwidth,
            calculatedRemainingBandwidth,
        )
    }

    @Test
    fun `when a channel supporting a non-HDR cell is overloaded, only the overloaded bandwidth for that channel is deallocated - IC-PENALTY-FACTOR-03-D`() {
        val cell = cell(demandClass = DemandClass.LEG_L)
        val carrier = carrier()

        val allocatedDemands = mutableMapOf(CarrierCell(carrier, cell) to 200.0.kbps)

        val spectralEfficiencies = mapOf(CarrierCell(carrier, cell) to 1.0.bpspHz)

        val totalBandwidth = mapOf(carrier to 100.0.kHz)
        val remainingBandwidths = mapOf(carrier to (-100.0).kHz)
        val cellsToPreempt = listOf(cell)

        unloadOverloadedCarriers(remainingBandwidths, spectralEfficiencies, allocatedDemands, cellsToPreempt)

        val expectedAllocatedDemands = mapOf(CarrierCell(carrier, cell) to 100.0.kbps)

        assertEquals(expectedAllocatedDemands, allocatedDemands)

        val newRemainingBandwidths = calculateRemainingBandwidths(totalBandwidth, spectralEfficiencies, allocatedDemands)
        val expectedRemainingBandwidths = mapOf(carrier to 0.0.kHz)

        assertEquals(expectedRemainingBandwidths, newRemainingBandwidths)
    }

    @Test
    fun `when a channel supporting an HDR cell is overloaded, the entire demand for that channel is deallocated - IC-PENALTY-FACTOR-03-E`() {
        val cell = cell(demandClass = DemandClass.HDR_S)
        val carrier = carrier()

        val allocatedDemands = mutableMapOf(CarrierCell(carrier, cell) to 200.0.kbps)

        val spectralEfficiencies = mapOf(CarrierCell(carrier, cell) to 1.0.bpspHz)

        val totalBandwidth = mapOf(carrier to 100.0.kHz)
        val remainingBandwidths = mapOf(carrier to (-100.0).kHz)
        val cellsToPreempt = listOf(cell)

        unloadOverloadedCarriers(remainingBandwidths, spectralEfficiencies, allocatedDemands, cellsToPreempt)

        val expectedAllocatedDemands = mapOf(CarrierCell(carrier, cell) to 0.0.kbps)

        assertEquals(expectedAllocatedDemands, allocatedDemands)

        val newRemainingBandwidths = calculateRemainingBandwidths(totalBandwidth, spectralEfficiencies, allocatedDemands)
        val expectedRemainingBandwidths = mapOf(carrier to 100.0.kHz)

        assertEquals(expectedRemainingBandwidths, newRemainingBandwidths)
    }

    @Test
    fun `unload overloaded channels test - IC-PENALTY-FACTOR-03-C`() {
        val cell1 = cell(s2Cell = 11.S2)
        val cell2 = cell(s2Cell = 21.S2)
        val carrier1 = carrier(beamId = 1)
        val carrier2 = carrier(beamId = 2)

        val allocatedDemands =
            mutableMapOf(
                CarrierCell(carrier1, cell1) to 200.0.kbps,
                CarrierCell(carrier2, cell1) to 100.0.kbps,
                CarrierCell(carrier2, cell2) to 40.0.kbps,
            )

        val spectralEfficiencies =
            mapOf(
                CarrierCell(carrier1, cell1) to 2.0.bpspHz,
                CarrierCell(carrier2, cell1) to 1.0.bpspHz,
                CarrierCell(carrier2, cell2) to 2.0.bpspHz,
            )

        val totalBandwidth = mapOf(carrier1 to 80.0.kHz, carrier2 to 70.0.kHz)
        val remainingBandwidths = mapOf(carrier1 to (-20.0).kHz, carrier2 to (-50.0).kHz)
        val cellsToPreempt = listOf(cell2, cell1)

        unloadOverloadedCarriers(remainingBandwidths, spectralEfficiencies, allocatedDemands, cellsToPreempt)

        val expectedAllocatedDemands =
            mapOf(
                CarrierCell(carrier1, cell1) to 160.0.kbps,
                CarrierCell(carrier2, cell1) to 70.0.kbps,
                CarrierCell(carrier2, cell2) to 0.0.kbps,
            )

        assertEquals(expectedAllocatedDemands, allocatedDemands)

        val newRemainingBandwidths = calculateRemainingBandwidths(totalBandwidth, spectralEfficiencies, allocatedDemands)
        val expectedRemainingBandwidths = mapOf(carrier1 to 0.0.kHz, carrier2 to 0.0.kHz)

        assertEquals(expectedRemainingBandwidths, newRemainingBandwidths)
    }

    private fun carrier(beamId: Int = 1): CarrierReference {
        return CarrierReference(beamId, Direction.TX, 1234L, -1)
    }

    private fun cell(
        s2Cell: S2CellId = 43.S2,
        demandClass: DemandClass = DemandClass.LDR_L,
    ) = S2CellWithDemandClass(s2Cell, demandClass)
}
