package com.example.domain

import kotlin.math.*

object CivilEngineeringFormulas {

    fun calculateConcreteMix(
        lengthM: Double,
        widthM: Double,
        thicknessM: Double,
        count: Int,
        grade: ConcreteGrade,
        wastagePercent: Double
    ): ConcreteMixResult? {
        if (lengthM <= 0 || widthM <= 0 || thicknessM <= 0 || count <= 0 || wastagePercent < 0) {
            return null
        }
        val wetVol = lengthM * widthM * thicknessM * count * (1.0 + wastagePercent / 100.0)
        val dryVol = wetVol * 1.54
        val totalParts = grade.cementPart + grade.sandPart + grade.gravelPart

        val cementVol = dryVol * (grade.cementPart / totalParts)
        val cementKg = cementVol * 1440.0
        val cementBags = cementKg / 50.0

        val sandM3 = dryVol * (grade.sandPart / totalParts)
        val sandKg = sandM3 * 1600.0

        val gravelM3 = dryVol * (grade.gravelPart / totalParts)
        val gravelKg = gravelM3 * 1550.0

        val waterLiters = cementKg * grade.waterCementRatio

        return ConcreteMixResult(
            wetVolumeM3 = wetVol,
            dryVolumeM3 = dryVol,
            cementKg = cementKg,
            cementBags50Kg = cementBags,
            sandM3 = sandM3,
            sandKg = sandKg,
            gravelM3 = gravelM3,
            gravelKg = gravelKg,
            waterLiters = waterLiters
        )
    }

    fun calculateRebar(
        diameterMm: Int,
        lengthPerBarM: Double,
        barCount: Int
    ): RebarResult? {
        if (diameterMm <= 0 || lengthPerBarM <= 0 || barCount <= 0) return null
        // Exact density of steel = 7850 kg/m³ -> (π * d² / 4) * 1e-6 * 7850 ≈ d² / 162.197
        val singleAreaMm2 = Math.PI * diameterMm * diameterMm / 4.0
        val singleAreaCm2 = singleAreaMm2 / 100.0
        val unitWeight = singleAreaMm2 * 1e-6 * 7850.0
        val totalLength = lengthPerBarM * barCount
        val totalWeightKg = totalLength * unitWeight
        val branches12m = ceil(totalLength / 12.0).toInt()

        return RebarResult(
            diameterMm = diameterMm,
            unitWeightKgPerM = unitWeight,
            singleAreaCm2 = singleAreaCm2,
            totalLengthM = totalLength,
            totalWeightKg = totalWeightKg,
            totalWeightTon = totalWeightKg / 1000.0,
            totalAreaCm2 = singleAreaCm2 * barCount,
            branches12m = branches12m
        )
    }

    fun calculateBeamAnalysis(
        spanM: Double,
        udlKnPerM: Double,
        pointLoadCenterKn: Double,
        elasticityGpa: Double,
        inertiaCm4: Double
    ): BeamAnalysisResult? {
        if (spanM <= 0 || elasticityGpa <= 0 || inertiaCm4 <= 0) return null
        if (udlKnPerM < 0 || pointLoadCenterKn < 0) return null
        if (udlKnPerM == 0.0 && pointLoadCenterKn == 0.0) return null

        val reaction = (udlKnPerM * spanM) / 2.0 + pointLoadCenterKn / 2.0
        val maxShear = reaction
        val maxMoment = (udlKnPerM * spanM * spanM) / 8.0 + (pointLoadCenterKn * spanM) / 4.0

        // Convert to consistent units: E in kN/m² = GPa * 1e6; I in m⁴ = cm⁴ * 1e-8
        val eKnPerM2 = elasticityGpa * 1e6
        val iM4 = inertiaCm4 * 1e-8
        val ei = eKnPerM2 * iM4

        val deflUdlM = (5.0 * udlKnPerM * spanM.pow(4)) / (384.0 * ei)
        val deflPointM = (pointLoadCenterKn * spanM.pow(3)) / (48.0 * ei)
        val totalDeflMm = (deflUdlM + deflPointM) * 1000.0

        val spanMm = spanM * 1000.0
        val ratio = if (totalDeflMm > 1e-9) spanMm / totalDeflMm else Double.POSITIVE_INFINITY
        val passes = totalDeflMm <= (spanMm / 360.0)

        val steps = 40
        val points = (0..steps).map { idx ->
            val ratioX = idx.toFloat() / steps.toFloat()
            val x = ratioX * spanM
            val vPoint = when {
                idx < steps / 2 -> pointLoadCenterKn / 2.0
                idx > steps / 2 -> -pointLoadCenterKn / 2.0
                else -> 0.0
            }
            val shear = reaction - udlKnPerM * x - (if (x > spanM / 2.0 + 1e-9) pointLoadCenterKn else if (abs(x - spanM / 2.0) <= 1e-9) pointLoadCenterKn / 2.0 else 0.0)
            val shearClean = if (abs(x - spanM / 2.0) <= 1e-9 && pointLoadCenterKn > 0.0) vPoint else shear

            val momentUdl = (udlKnPerM * x / 2.0) * (spanM - x)
            val momentPoint = if (x <= spanM / 2.0) {
                (pointLoadCenterKn / 2.0) * x
            } else {
                (pointLoadCenterKn / 2.0) * (spanM - x)
            }
            BeamDiagramPoint(
                xRatio = ratioX,
                shearKn = shearClean,
                momentKnm = momentUdl + momentPoint
            )
        }

        return BeamAnalysisResult(
            spanM = spanM,
            reactionLeftKn = reaction,
            reactionRightKn = reaction,
            maxShearKn = maxShear,
            maxMomentKnm = maxMoment,
            maxDeflectionMm = totalDeflMm,
            spanOverDeflection = ratio,
            passesL360 = passes,
            diagramPoints = points
        )
    }

    fun calculateRcFlexure(
        widthMm: Double,
        heightMm: Double,
        coverMm: Double,
        fcMpa: Double,
        fyMpa: Double,
        steelAreaCm2: Double
    ): RcFlexureResult? {
        if (widthMm <= 0 || heightMm <= 0 || coverMm < 0 || fcMpa <= 0 || fyMpa <= 0 || steelAreaCm2 <= 0) {
            return null
        }
        val d = heightMm - coverMm - 10.0 // assuming ~10mm stirrup + half main bar centroid offset
        if (d <= 0) return null

        val asMm2 = steelAreaCm2 * 100.0
        val beta1 = when {
            fcMpa <= 28.0 -> 0.85
            fcMpa >= 56.0 -> 0.65
            else -> 0.85 - 0.05 * ((fcMpa - 28.0) / 7.0)
        }

        val a = (asMm2 * fyMpa) / (0.85 * fcMpa * widthMm)
        val c = a / beta1
        if (c <= 0) return null

        val et = 0.003 * ((d - c) / c)
        val ey = fyMpa / 200000.0 // Es = 200,000 MPa

        val phi = when {
            et >= 0.005 -> 0.90
            et <= ey -> 0.65
            else -> 0.65 + 0.25 * ((et - ey) / (0.005 - ey))
        }

        val behavior = when {
            et >= 0.005 -> FlexuralBehavior.TENSION_CONTROLLED
            et > ey -> FlexuralBehavior.TRANSITION
            else -> FlexuralBehavior.COMPRESSION_CONTROLLED
        }

        // Mn in N·mm -> divide by 1e6 for kN·m
        val mnKnm = (asMm2 * fyMpa * (d - a / 2.0)) / 1e6
        val phiMnKnm = phi * mnKnm

        val rho = (asMm2 / (widthMm * d)) * 100.0
        val rhoMin = max(0.25 * sqrt(fcMpa) / fyMpa, 1.4 / fyMpa) * 100.0

        return RcFlexureResult(
            effectiveDepthMm = d,
            steelAreaCm2 = steelAreaCm2,
            compressionBlockAMm = a,
            neutralAxisCMm = c,
            tensileStrainEt = et,
            phiFactor = phi,
            nominalMomentKnm = mnKnm,
            designMomentKnm = phiMnKnm,
            rhoPercent = rho,
            rhoMinPercent = rhoMin,
            behavior = behavior,
            meetsMinSteel = rho >= rhoMin
        )
    }

    fun calculateSlope(horizontalM: Double, verticalM: Double): SlopeResult? {
        if (horizontalM <= 0) return null
        val absV = abs(verticalM)
        val slopePercent = (verticalM / horizontalM) * 100.0
        val angleDeg = Math.toDegrees(atan2(verticalM, horizontalM))
        val ratioOneInN = if (absV > 1e-9) horizontalM / absV else Double.POSITIVE_INFINITY
        val inclined = hypot(horizontalM, verticalM)

        return SlopeResult(
            horizontalM = horizontalM,
            verticalM = verticalM,
            slopePercent = slopePercent,
            angleDegrees = angleDeg,
            ratioOneInN = ratioOneInN,
            inclinedLengthM = inclined
        )
    }

    fun calculateExcavation(
        lengthM: Double,
        widthM: Double,
        depthM: Double,
        sideSlopeHV: Double,
        swellPercent: Double,
        truckCapacityM3: Double
    ): ExcavationResult? {
        if (lengthM <= 0 || widthM <= 0 || depthM <= 0 || sideSlopeHV < 0 || swellPercent < 0 || truckCapacityM3 <= 0) {
            return null
        }
        val aBottom = lengthM * widthM
        val lTop = lengthM + 2.0 * sideSlopeHV * depthM
        val wTop = widthM + 2.0 * sideSlopeHV * depthM
        val aTop = lTop * wTop
        val aMid = (lengthM + sideSlopeHV * depthM) * (widthM + sideSlopeHV * depthM)

        // Prismoidal volume formula (exact for frustum of rectangular pyramid)
        val bankVolume = (depthM / 6.0) * (aBottom + 4.0 * aMid + aTop)
        val looseVolume = bankVolume * (1.0 + swellPercent / 100.0)
        val trips = ceil(looseVolume / truckCapacityM3).toInt()

        return ExcavationResult(
            bottomAreaM2 = aBottom,
            topAreaM2 = aTop,
            bankVolumeM3 = bankVolume,
            looseVolumeM3 = looseVolume,
            truckTrips = trips
        )
    }

    fun calculateBearingCapacity(
        shape: FootingShape,
        widthB: Double,
        depthDf: Double,
        unitWeightGamma: Double,
        cohesionC: Double,
        phiDegrees: Double,
        safetyFactor: Double
    ): BearingCapacityResult? {
        if (widthB <= 0 || depthDf < 0 || unitWeightGamma <= 0 || cohesionC < 0 || phiDegrees < 0 || phiDegrees > 48 || safetyFactor <= 0) {
            return null
        }
        val phiRad = Math.toRadians(phiDegrees)
        val nq = exp(Math.PI * tan(phiRad)) * tan(Math.toRadians(45.0 + phiDegrees / 2.0)).pow(2)
        val nc = if (phiDegrees < 0.01) 5.14 else (nq - 1.0) / tan(phiRad)
        val nGamma = if (phiDegrees < 0.01) 0.0 else 2.0 * (nq + 1.0) * tan(phiRad)

        val (sc, sq, sGamma) = when (shape) {
            FootingShape.STRIP -> Triple(1.0, 1.0, 1.0)
            FootingShape.SQUARE -> Triple(1.0 + (nq / nc), 1.0 + tan(phiRad), 0.6)
            FootingShape.CIRCULAR -> Triple(1.0 + (nq / nc), 1.0 + tan(phiRad), 0.6)
        }

        val qOverburden = unitWeightGamma * depthDf
        val qu = cohesionC * nc * sc + qOverburden * nq * sq + 0.5 * unitWeightGamma * widthB * nGamma * sGamma
        val qAll = qu / safetyFactor

        val area = when (shape) {
            FootingShape.STRIP -> widthB // per meter length
            FootingShape.SQUARE -> widthB * widthB
            FootingShape.CIRCULAR -> Math.PI * widthB * widthB / 4.0
        }

        return BearingCapacityResult(
            nc = nc,
            nq = nq,
            nGamma = nGamma,
            ultimateCapacityKpa = qu,
            allowableCapacityKpa = qAll,
            allowableTotalLoadKnPerMOrTotal = qAll * area
        )
    }

    fun calculateAzimuth(
        n1: Double,
        e1: Double,
        n2: Double,
        e2: Double
    ): AzimuthResult? {
        val dN = n2 - n1
        val dE = e2 - e1
        val distance = hypot(dN, dE)
        if (distance < 1e-12) return null

        // Surveying azimuth: clockwise from North (+N axis)
        var azDeg = Math.toDegrees(atan2(dE, dN))
        if (azDeg < 0) azDeg += 360.0

        val deg = floor(azDeg).toInt()
        val remMin = (azDeg - deg) * 60.0
        val min = floor(remMin).toInt()
        val sec = (remMin - min) * 60.0
        val dms = String.format(java.util.Locale.US, "%03d° %02d′ %04.1f″", deg, min, sec)

        val quadrant = when {
            azDeg <= 90.0 -> String.format(java.util.Locale.US, "N %.2f° E", azDeg)
            azDeg <= 180.0 -> String.format(java.util.Locale.US, "S %.2f° E", 180.0 - azDeg)
            azDeg <= 270.0 -> String.format(java.util.Locale.US, "S %.2f° W", azDeg - 180.0)
            else -> String.format(java.util.Locale.US, "N %.2f° W", 360.0 - azDeg)
        }

        return AzimuthResult(
            deltaN = dN,
            deltaE = dE,
            distanceM = distance,
            azimuthDecimalDeg = azDeg,
            azimuthDms = dms,
            quadrantBearing = quadrant
        )
    }
}
