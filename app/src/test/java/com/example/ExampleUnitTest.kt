package com.example

import com.example.domain.*
import org.junit.Assert.*
import org.junit.Test
import kotlin.math.abs

class ExampleUnitTest {

    @Test
    fun scientificEvaluator_handlesTrigAndImplicitMultiplication() {
        val res1 = MathExpressionEvaluator.evaluate("2sin(30)+√(16)", isDegreeMode = true)
        assertTrue(res1 is EvalResult.Success)
        assertEquals(5.0, (res1 as EvalResult.Success).value, 1e-6)

        val res2 = MathExpressionEvaluator.evaluate("200×15%", isDegreeMode = true)
        assertTrue(res2 is EvalResult.Success)
        assertEquals(30.0, (res2 as EvalResult.Success).value, 1e-6)

        val divZero = MathExpressionEvaluator.evaluate("10÷0", isDegreeMode = true)
        assertTrue(divZero is EvalResult.Error)
    }

    @Test
    fun beamAnalysis_computesAccurateMomentsAndDeflection() {
        // Simply supported beam: L = 6m, w = 10 kN/m, P = 0 -> Mmax = wL²/8 = 45 kN·m, Vmax = 30 kN
        val res = CivilEngineeringFormulas.calculateBeamAnalysis(
            spanM = 6.0,
            udlKnPerM = 10.0,
            pointLoadCenterKn = 0.0,
            elasticityGpa = 25.0,
            inertiaCm4 = 180000.0
        )
        assertNotNull(res)
        assertEquals(30.0, res!!.maxShearKn, 1e-4)
        assertEquals(45.0, res.maxMomentKnm, 1e-4)
        assertTrue(res.maxDeflectionMm > 0.0)
    }

    @Test
    fun rebarAndConcrete_computeExpectedQuantities() {
        val rebar = CivilEngineeringFormulas.calculateRebar(16, 12.0, 10)
        assertNotNull(rebar)
        assertEquals(120.0, rebar!!.totalLengthM, 1e-4)
        assertEquals(10, rebar.branches12m)
        assertTrue(abs(rebar.unitWeightKgPerM - 1.578) < 0.01)

        val concrete = CivilEngineeringFormulas.calculateConcreteMix(5.0, 4.0, 0.25, 1, ConcreteGrade.C25, 0.0)
        assertNotNull(concrete)
        assertEquals(5.0, concrete!!.wetVolumeM3, 1e-4)
        assertEquals(7.7, concrete.dryVolumeM3, 1e-4)
    }

    @Test
    fun rcFlexure_computesDesignMomentAndBehavior() {
        val rc = CivilEngineeringFormulas.calculateRcFlexure(
            widthMm = 350.0,
            heightMm = 500.0,
            coverMm = 40.0,
            fcMpa = 25.0,
            fyMpa = 400.0,
            steelAreaCm2 = 12.57
        )
        assertNotNull(rc)
        assertEquals(FlexuralBehavior.TENSION_CONTROLLED, rc!!.behavior)
        assertEquals(0.90, rc.phiFactor, 1e-4)
        assertTrue(rc.designMomentKnm in 170.0..200.0)
    }

    @Test
    fun geotechAndSurveying_computeAzimuthAndBearing() {
        val az = CivilEngineeringFormulas.calculateAzimuth(0.0, 0.0, 100.0, 100.0)
        assertNotNull(az)
        assertEquals(45.0, az!!.azimuthDecimalDeg, 1e-4)
        assertEquals(141.421, az.distanceM, 1e-2)

        val bearing = CivilEngineeringFormulas.calculateBearingCapacity(
            shape = FootingShape.SQUARE,
            widthB = 2.0,
            depthDf = 1.5,
            unitWeightGamma = 18.0,
            cohesionC = 10.0,
            phiDegrees = 30.0,
            safetyFactor = 3.0
        )
        assertNotNull(bearing)
        assertTrue(bearing!!.allowableCapacityKpa > 150.0)
    }

    @Test
    fun unitConverter_mpaToKgfCm2() {
        val mpa = UnitCategory.STRESS.units.first { it.symbol == "MPa" }
        val kgfCm2 = UnitCategory.STRESS.units.first { it.symbol == "kgf/cm²" }
        val converted = EngineeringUnitConverter.convert(25.0, mpa, kgfCm2)
        assertEquals(254.929, converted, 0.05)
    }
}
