package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.domain.*
import com.example.ui.*
import com.example.ui.components.*

@Composable
fun GeotechSurveyingScreen(
    selected: GeotechCalculatorType?,
    onSelect: (GeotechCalculatorType?) -> Unit,
    slopeInputs: SlopeInputs,
    slopeResult: SlopeResult?,
    onUpdateSlope: ((SlopeInputs) -> SlopeInputs) -> Unit,
    excavationInputs: ExcavationInputs,
    excavationResult: ExcavationResult?,
    onUpdateExcavation: ((ExcavationInputs) -> ExcavationInputs) -> Unit,
    bearingInputs: BearingInputs,
    bearingResult: BearingCapacityResult?,
    onUpdateBearing: ((BearingInputs) -> BearingInputs) -> Unit,
    azimuthInputs: AzimuthInputs,
    azimuthResult: AzimuthResult?,
    onUpdateAzimuth: ((AzimuthInputs) -> AzimuthInputs) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isWide = maxWidth >= 760.dp

        if (isWide) {
            val activeType = selected ?: GeotechCalculatorType.SLOPE_GRADE
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                GeotechHubList(
                    selected = activeType,
                    onSelect = onSelect,
                    modifier = Modifier.weight(0.85f)
                )
                Box(modifier = Modifier.weight(1.35f)) {
                    GeotechCalculatorDetail(
                        type = activeType,
                        onBack = null,
                        slopeInputs = slopeInputs,
                        slopeResult = slopeResult,
                        onUpdateSlope = onUpdateSlope,
                        excavationInputs = excavationInputs,
                        excavationResult = excavationResult,
                        onUpdateExcavation = onUpdateExcavation,
                        bearingInputs = bearingInputs,
                        bearingResult = bearingResult,
                        onUpdateBearing = onUpdateBearing,
                        azimuthInputs = azimuthInputs,
                        azimuthResult = azimuthResult,
                        onUpdateAzimuth = onUpdateAzimuth
                    )
                }
            }
        } else {
            if (selected == null) {
                GeotechHubList(
                    selected = null,
                    onSelect = onSelect,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                )
            } else {
                BackHandler { onSelect(null) }
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                    GeotechCalculatorDetail(
                        type = selected,
                        onBack = { onSelect(null) },
                        slopeInputs = slopeInputs,
                        slopeResult = slopeResult,
                        onUpdateSlope = onUpdateSlope,
                        excavationInputs = excavationInputs,
                        excavationResult = excavationResult,
                        onUpdateExcavation = onUpdateExcavation,
                        bearingInputs = bearingInputs,
                        bearingResult = bearingResult,
                        onUpdateBearing = onUpdateBearing,
                        azimuthInputs = azimuthInputs,
                        azimuthResult = azimuthResult,
                        onUpdateAzimuth = onUpdateAzimuth
                    )
                }
            }
        }
    }
}

@Composable
private fun GeotechHubList(
    selected: GeotechCalculatorType?,
    onSelect: (GeotechCalculatorType) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(bottom = 4.dp)) {
                Text(
                    text = "ژئوتکنیک، خاکبرداری و نقشه‌برداری",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "محاسبات پی، گودبرداری، شیب‌بندی و مختصات UTM",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        items(GeotechCalculatorType.entries) { type ->
            val icon = when (type) {
                GeotechCalculatorType.SLOPE_GRADE -> Icons.Outlined.Terrain
                GeotechCalculatorType.EXCAVATION -> Icons.Outlined.Foundation
                GeotechCalculatorType.BEARING_CAPACITY -> Icons.Outlined.Compress
                GeotechCalculatorType.AZIMUTH_COORDS -> Icons.Outlined.Explore
            }
            ModuleMenuCard(
                title = type.title,
                subtitle = type.subtitle,
                codeBadge = type.codeBadge,
                icon = icon,
                selected = selected == type,
                testTag = "geotech_card_${type.name.lowercase()}",
                onClick = { onSelect(type) }
            )
        }
    }
}

@Composable
private fun GeotechCalculatorDetail(
    type: GeotechCalculatorType,
    onBack: (() -> Unit)?,
    slopeInputs: SlopeInputs,
    slopeResult: SlopeResult?,
    onUpdateSlope: ((SlopeInputs) -> SlopeInputs) -> Unit,
    excavationInputs: ExcavationInputs,
    excavationResult: ExcavationResult?,
    onUpdateExcavation: ((ExcavationInputs) -> ExcavationInputs) -> Unit,
    bearingInputs: BearingInputs,
    bearingResult: BearingCapacityResult?,
    onUpdateBearing: ((BearingInputs) -> BearingInputs) -> Unit,
    azimuthInputs: AzimuthInputs,
    azimuthResult: AzimuthResult?,
    onUpdateAzimuth: ((AzimuthInputs) -> AzimuthInputs) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CalculatorDetailHeader(
            title = type.title,
            codeBadge = type.codeBadge,
            onBack = onBack
        )

        when (type) {
            GeotechCalculatorType.SLOPE_GRADE -> SlopeCalculatorView(slopeInputs, slopeResult, onUpdateSlope)
            GeotechCalculatorType.EXCAVATION -> ExcavationCalculatorView(excavationInputs, excavationResult, onUpdateExcavation)
            GeotechCalculatorType.BEARING_CAPACITY -> BearingCapacityView(bearingInputs, bearingResult, onUpdateBearing)
            GeotechCalculatorType.AZIMUTH_COORDS -> AzimuthCalculatorView(azimuthInputs, azimuthResult, onUpdateAzimuth)
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SlopeCalculatorView(
    inputs: SlopeInputs,
    result: SlopeResult?,
    onUpdate: ((SlopeInputs) -> SlopeInputs) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        EngineeringInputField(
            label = "فاصله افقی (ΔX)",
            value = inputs.horizontalM,
            onValueChange = { v -> onUpdate { it.copy(horizontalM = v) } },
            unit = "m",
            testTag = "slope_dx_input",
            modifier = Modifier.weight(1f)
        )
        EngineeringInputField(
            label = "اختلاف ارتفاع (ΔH)",
            value = inputs.verticalM,
            onValueChange = { v -> onUpdate { it.copy(verticalM = v) } },
            unit = "m",
            testTag = "slope_dy_input",
            allowNegative = true,
            modifier = Modifier.weight(1f)
        )
    }

    if (result != null) {
        SlopeTriangleCanvas(result = result)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ResultMetricCard(
                title = "درصد شیب",
                value = formatEngineeringValue(result.slopePercent, 2),
                unit = "%",
                accentColor = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.weight(1f)
            )
            ResultMetricCard(
                title = "زاویه شیب",
                value = formatEngineeringValue(result.angleDegrees, 2),
                unit = "°",
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ResultMetricCard(
                title = "نسبت شیب (1 : N)",
                value = if (result.ratioOneInN.isInfinite()) "∞" else "1 : ${formatEngineeringValue(result.ratioOneInN, 2)}",
                unit = "",
                modifier = Modifier.weight(1f)
            )
            ResultMetricCard(
                title = "طول شیب‌دار (وتر)",
                value = formatEngineeringValue(result.inclinedLengthM, 3),
                unit = "m",
                accentColor = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ExcavationCalculatorView(
    inputs: ExcavationInputs,
    result: ExcavationResult?,
    onUpdate: ((ExcavationInputs) -> ExcavationInputs) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        EngineeringInputField(
            label = "طول کف گود (L)",
            value = inputs.lengthM,
            onValueChange = { v -> onUpdate { it.copy(lengthM = v) } },
            unit = "m",
            testTag = "excav_length_input",
            modifier = Modifier.weight(1f)
        )
        EngineeringInputField(
            label = "عرض کف گود (W)",
            value = inputs.widthM,
            onValueChange = { v -> onUpdate { it.copy(widthM = v) } },
            unit = "m",
            testTag = "excav_width_input",
            modifier = Modifier.weight(1f)
        )
    }

    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        EngineeringInputField(
            label = "عمق گودبرداری (H)",
            value = inputs.depthM,
            onValueChange = { v -> onUpdate { it.copy(depthM = v) } },
            unit = "m",
            testTag = "excav_depth_input",
            modifier = Modifier.weight(1f)
        )
        EngineeringInputField(
            label = "شیب دیواره (H:V)",
            value = inputs.sideSlopeHV,
            onValueChange = { v -> onUpdate { it.copy(sideSlopeHV = v) } },
            unit = "نسبت",
            testTag = "excav_slope_input",
            modifier = Modifier.weight(1f)
        )
    }

    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        EngineeringInputField(
            label = "درصد تورم خاک",
            value = inputs.swellPercent,
            onValueChange = { v -> onUpdate { it.copy(swellPercent = v) } },
            unit = "%",
            testTag = "excav_swell_input",
            modifier = Modifier.weight(1f)
        )
        EngineeringInputField(
            label = "ظرفیت کامیون",
            value = inputs.truckCapacityM3,
            onValueChange = { v -> onUpdate { it.copy(truckCapacityM3 = v) } },
            unit = "m³",
            testTag = "excav_truck_input",
            modifier = Modifier.weight(1f)
        )
    }

    if (result != null) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ResultMetricCard(
                title = "حجم گودبرداری درجا",
                value = formatEngineeringValue(result.bankVolumeM3, 2),
                unit = "m³",
                subtitle = "مساحت کف: ${formatEngineeringValue(result.bottomAreaM2, 1)} m²",
                modifier = Modifier.weight(1f)
            )
            ResultMetricCard(
                title = "حجم خاک ریخته (متورم)",
                value = formatEngineeringValue(result.looseVolumeM3, 2),
                unit = "m³",
                subtitle = "مساحت دهانه: ${formatEngineeringValue(result.topAreaM2, 1)} m²",
                accentColor = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.weight(1f)
            )
        }
        ResultMetricCard(
            title = "تعداد سرویس کامیون حمل خاک",
            value = result.truckTrips.toString(),
            unit = "سرویس",
            accentColor = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun BearingCapacityView(
    inputs: BearingInputs,
    result: BearingCapacityResult?,
    onUpdate: ((BearingInputs) -> BearingInputs) -> Unit
) {
    Text("شکل هندسی پی سطحی", style = MaterialTheme.typography.titleSmall)
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(FootingShape.entries) { shape ->
            FilterChip(
                selected = inputs.shape == shape,
                onClick = { onUpdate { it.copy(shape = shape) } },
                label = { Text(shape.label) },
                modifier = Modifier.testTag("footing_shape_${shape.name.lowercase()}")
            )
        }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        EngineeringInputField(
            label = if (inputs.shape == FootingShape.CIRCULAR) "قطر پی (B)" else "عرض پی (B)",
            value = inputs.widthB,
            onValueChange = { v -> onUpdate { it.copy(widthB = v) } },
            unit = "m",
            testTag = "bearing_b_input",
            modifier = Modifier.weight(1f)
        )
        EngineeringInputField(
            label = "عمق استقرار پی (Df)",
            value = inputs.depthDf,
            onValueChange = { v -> onUpdate { it.copy(depthDf = v) } },
            unit = "m",
            testTag = "bearing_df_input",
            modifier = Modifier.weight(1f)
        )
    }

    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        EngineeringInputField(
            label = "وزن مخصوص خاک (γ)",
            value = inputs.unitWeightGamma,
            onValueChange = { v -> onUpdate { it.copy(unitWeightGamma = v) } },
            unit = "kN/m³",
            testTag = "bearing_gamma_input",
            modifier = Modifier.weight(1f)
        )
        EngineeringInputField(
            label = "چسبندگی خاک (c)",
            value = inputs.cohesionC,
            onValueChange = { v -> onUpdate { it.copy(cohesionC = v) } },
            unit = "kPa",
            testTag = "bearing_c_input",
            modifier = Modifier.weight(1f)
        )
    }

    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        EngineeringInputField(
            label = "زاویه اصطکاک داخلی (φ)",
            value = inputs.phiDegrees,
            onValueChange = { v -> onUpdate { it.copy(phiDegrees = v) } },
            unit = "°",
            testTag = "bearing_phi_input",
            modifier = Modifier.weight(1f)
        )
        EngineeringInputField(
            label = "ضریب اطمینان (FS)",
            value = inputs.safetyFactor,
            onValueChange = { v -> onUpdate { it.copy(safetyFactor = v) } },
            unit = "",
            testTag = "bearing_fs_input",
            modifier = Modifier.weight(1f)
        )
    }

    if (result != null) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ResultMetricCard(
                title = "ظرفیت باربری مجاز (qall)",
                value = formatEngineeringValue(result.allowableCapacityKpa, 1),
                unit = "kPa",
                subtitle = "≈ ${formatEngineeringValue(result.allowableCapacityKpa * 0.010197, 2)} kgf/cm²",
                accentColor = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.weight(1f)
            )
            ResultMetricCard(
                title = "ظرفیت باربری نهایی (qu)",
                value = formatEngineeringValue(result.ultimateCapacityKpa, 1),
                unit = "kPa",
                modifier = Modifier.weight(1f)
            )
        }
        ResultMetricCard(
            title = if (inputs.shape == FootingShape.STRIP) "بار مجاز بر واحد طول پی" else "حداکثر بار ستون مجاز روی پی",
            value = formatEngineeringValue(result.allowableTotalLoadKnPerMOrTotal, 1),
            unit = if (inputs.shape == FootingShape.STRIP) "kN/m" else "kN",
            subtitle = "ضرایب: Nc=${formatEngineeringValue(result.nc, 2)} | Nq=${formatEngineeringValue(result.nq, 2)} | Nγ=${formatEngineeringValue(result.nGamma, 2)}",
            accentColor = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun AzimuthCalculatorView(
    inputs: AzimuthInputs,
    result: AzimuthResult?,
    onUpdate: ((AzimuthInputs) -> AzimuthInputs) -> Unit
) {
    Text("مختصات نقطه مبدا (A)", style = MaterialTheme.typography.titleSmall)
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        EngineeringInputField(
            label = "شمال نقطه ۱ (N₁)",
            value = inputs.n1,
            onValueChange = { v -> onUpdate { it.copy(n1 = v) } },
            unit = "m",
            testTag = "az_n1_input",
            allowNegative = true,
            modifier = Modifier.weight(1f)
        )
        EngineeringInputField(
            label = "شرق نقطه ۱ (E₁)",
            value = inputs.e1,
            onValueChange = { v -> onUpdate { it.copy(e1 = v) } },
            unit = "m",
            testTag = "az_e1_input",
            allowNegative = true,
            modifier = Modifier.weight(1f)
        )
    }

    Text("مختصات نقطه مقصد (B)", style = MaterialTheme.typography.titleSmall)
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        EngineeringInputField(
            label = "شمال نقطه ۲ (N₂)",
            value = inputs.n2,
            onValueChange = { v -> onUpdate { it.copy(n2 = v) } },
            unit = "m",
            testTag = "az_n2_input",
            allowNegative = true,
            modifier = Modifier.weight(1f)
        )
        EngineeringInputField(
            label = "شرق نقطه ۲ (E₂)",
            value = inputs.e2,
            onValueChange = { v -> onUpdate { it.copy(e2 = v) } },
            unit = "m",
            testTag = "az_e2_input",
            allowNegative = true,
            modifier = Modifier.weight(1f)
        )
    }

    if (result != null) {
        AzimuthCompassCanvas(azimuthDeg = result.azimuthDecimalDeg)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ResultMetricCard(
                title = "آزیموت (گرای امتداد)",
                value = result.azimuthDms,
                unit = "",
                subtitle = result.quadrantBearing,
                accentColor = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.weight(1f)
            )
            ResultMetricCard(
                title = "فاصله افقی (D)",
                value = formatEngineeringValue(result.distanceM, 3),
                unit = "m",
                modifier = Modifier.weight(1f)
            )
        }
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ResultMetricCard(
                    title = "ΔN",
                    value = formatEngineeringValue(result.deltaN, 3),
                    unit = "m",
                    modifier = Modifier.weight(1f)
                )
                ResultMetricCard(
                    title = "ΔE",
                    value = formatEngineeringValue(result.deltaE, 3),
                    unit = "m",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
