package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.domain.*
import com.example.ui.*
import com.example.ui.components.*
import com.example.ui.theme.LocalCivilColors

@Composable
fun StructuralCalculatorsScreen(
    selected: StructuralCalculatorType?,
    onSelect: (StructuralCalculatorType?) -> Unit,
    concreteInputs: ConcreteInputs,
    concreteResult: ConcreteMixResult?,
    onUpdateConcrete: ((ConcreteInputs) -> ConcreteInputs) -> Unit,
    rebarInputs: RebarInputs,
    rebarResult: RebarResult?,
    onUpdateRebar: ((RebarInputs) -> RebarInputs) -> Unit,
    beamInputs: BeamInputs,
    beamResult: BeamAnalysisResult?,
    onUpdateBeam: ((BeamInputs) -> BeamInputs) -> Unit,
    rcInputs: RcFlexureInputs,
    rcResult: RcFlexureResult?,
    onUpdateRc: ((RcFlexureInputs) -> RcFlexureInputs) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isWide = maxWidth >= 760.dp

        if (isWide) {
            val activeType = selected ?: StructuralCalculatorType.CONCRETE_MIX
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StructuralHubList(
                    selected = activeType,
                    onSelect = onSelect,
                    showBanner = false,
                    modifier = Modifier.weight(0.85f)
                )
                Box(modifier = Modifier.weight(1.35f)) {
                    StructuralCalculatorDetail(
                        type = activeType,
                        onBack = null,
                        concreteInputs = concreteInputs,
                        concreteResult = concreteResult,
                        onUpdateConcrete = onUpdateConcrete,
                        rebarInputs = rebarInputs,
                        rebarResult = rebarResult,
                        onUpdateRebar = onUpdateRebar,
                        beamInputs = beamInputs,
                        beamResult = beamResult,
                        onUpdateBeam = onUpdateBeam,
                        rcInputs = rcInputs,
                        rcResult = rcResult,
                        onUpdateRc = onUpdateRc
                    )
                }
            }
        } else {
            if (selected == null) {
                StructuralHubList(
                    selected = null,
                    onSelect = onSelect,
                    showBanner = true,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                )
            } else {
                BackHandler { onSelect(null) }
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                    StructuralCalculatorDetail(
                        type = selected,
                        onBack = { onSelect(null) },
                        concreteInputs = concreteInputs,
                        concreteResult = concreteResult,
                        onUpdateConcrete = onUpdateConcrete,
                        rebarInputs = rebarInputs,
                        rebarResult = rebarResult,
                        onUpdateRebar = onUpdateRebar,
                        beamInputs = beamInputs,
                        beamResult = beamResult,
                        onUpdateBeam = onUpdateBeam,
                        rcInputs = rcInputs,
                        rcResult = rcResult,
                        onUpdateRc = onUpdateRc
                    )
                }
            }
        }
    }
}

@Composable
private fun StructuralHubList(
    selected: StructuralCalculatorType?,
    onSelect: (StructuralCalculatorType) -> Unit,
    showBanner: Boolean,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        if (showBanner) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(145.dp)
                        .clip(RoundedCornerShape(20.dp))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_blueprint_header),
                        contentDescription = stringResource(R.string.cd_blueprint_banner),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0x330B1622),
                                        Color(0xDD0B1622)
                                    )
                                )
                            )
                            .padding(16.dp),
                        contentAlignment = Alignment.BottomStart
                    ) {
                        Column {
                            Text(
                                text = "ماژول‌های محاسبات سازه و بتن",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White
                            )
                            Text(
                                text = "محاسبات اجرایی و طراحی بر اساس آیین‌نامه ACI و مبحث نهم",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFD5ECFF)
                            )
                        }
                    }
                }
            }
        }
        items(StructuralCalculatorType.entries) { type ->
            val icon = when (type) {
                StructuralCalculatorType.CONCRETE_MIX -> Icons.Outlined.Layers
                StructuralCalculatorType.REBAR_WEIGHT -> Icons.Outlined.Straighten
                StructuralCalculatorType.BEAM_ANALYSIS -> Icons.Outlined.Architecture
                StructuralCalculatorType.RC_FLEXURE -> Icons.Outlined.ViewColumn
            }
            ModuleMenuCard(
                title = type.title,
                subtitle = type.subtitle,
                codeBadge = type.codeBadge,
                icon = icon,
                selected = selected == type,
                testTag = "structural_card_${type.name.lowercase()}",
                onClick = { onSelect(type) }
            )
        }
    }
}

@Composable
private fun StructuralCalculatorDetail(
    type: StructuralCalculatorType,
    onBack: (() -> Unit)?,
    concreteInputs: ConcreteInputs,
    concreteResult: ConcreteMixResult?,
    onUpdateConcrete: ((ConcreteInputs) -> ConcreteInputs) -> Unit,
    rebarInputs: RebarInputs,
    rebarResult: RebarResult?,
    onUpdateRebar: ((RebarInputs) -> RebarInputs) -> Unit,
    beamInputs: BeamInputs,
    beamResult: BeamAnalysisResult?,
    onUpdateBeam: ((BeamInputs) -> BeamInputs) -> Unit,
    rcInputs: RcFlexureInputs,
    rcResult: RcFlexureResult?,
    onUpdateRc: ((RcFlexureInputs) -> RcFlexureInputs) -> Unit
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
            StructuralCalculatorType.CONCRETE_MIX -> ConcreteMixCalculatorView(
                inputs = concreteInputs,
                result = concreteResult,
                onUpdate = onUpdateConcrete
            )
            StructuralCalculatorType.REBAR_WEIGHT -> RebarCalculatorView(
                inputs = rebarInputs,
                result = rebarResult,
                onUpdate = onUpdateRebar
            )
            StructuralCalculatorType.BEAM_ANALYSIS -> BeamAnalysisCalculatorView(
                inputs = beamInputs,
                result = beamResult,
                onUpdate = onUpdateBeam
            )
            StructuralCalculatorType.RC_FLEXURE -> RcFlexureCalculatorView(
                inputs = rcInputs,
                result = rcResult,
                onUpdate = onUpdateRc
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ConcreteMixCalculatorView(
    inputs: ConcreteInputs,
    result: ConcreteMixResult?,
    onUpdate: ((ConcreteInputs) -> ConcreteInputs) -> Unit
) {
    Text("رده مقاومت بتن و نسبت اختلاط", style = MaterialTheme.typography.titleSmall)
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(ConcreteGrade.entries) { grade ->
            FilterChip(
                selected = inputs.grade == grade,
                onClick = { onUpdate { it.copy(grade = grade) } },
                label = { Text(grade.label, style = MaterialTheme.typography.labelMedium) },
                modifier = Modifier.testTag("concrete_grade_${grade.name.lowercase()}")
            )
        }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        EngineeringInputField(
            label = "طول (L)",
            value = inputs.lengthM,
            onValueChange = { v -> onUpdate { it.copy(lengthM = v) } },
            unit = "m",
            testTag = "concrete_length_input",
            modifier = Modifier.weight(1f)
        )
        EngineeringInputField(
            label = "عرض (W)",
            value = inputs.widthM,
            onValueChange = { v -> onUpdate { it.copy(widthM = v) } },
            unit = "m",
            testTag = "concrete_width_input",
            modifier = Modifier.weight(1f)
        )
    }

    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        EngineeringInputField(
            label = "ضخامت / ارتفاع (H)",
            value = inputs.thicknessM,
            onValueChange = { v -> onUpdate { it.copy(thicknessM = v) } },
            unit = "m",
            testTag = "concrete_thickness_input",
            modifier = Modifier.weight(1f)
        )
        EngineeringInputField(
            label = "تعداد المان",
            value = inputs.count,
            onValueChange = { v -> onUpdate { it.copy(count = v.filter(Char::isDigit)) } },
            unit = "عدد",
            testTag = "concrete_count_input",
            modifier = Modifier.weight(1f)
        )
    }

    EngineeringInputField(
        label = "ضریب پرت مصالح",
        value = inputs.wastagePercent,
        onValueChange = { v -> onUpdate { it.copy(wastagePercent = v) } },
        unit = "%",
        testTag = "concrete_wastage_input"
    )

    if (result != null) {
        ConcreteMixProportionBar(result = result)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ResultMetricCard(
                title = "حجم بتن تر",
                value = formatEngineeringValue(result.wetVolumeM3, 3),
                unit = "m³",
                modifier = Modifier.weight(1f)
            )
            ResultMetricCard(
                title = "سیمان (کیسه ۵۰ کیلویی)",
                value = formatEngineeringValue(result.cementBags50Kg, 1),
                unit = "کیسه",
                subtitle = "${formatEngineeringValue(result.cementKg, 0)} kg",
                accentColor = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ResultMetricCard(
                title = "ماسه مورد نیاز",
                value = formatEngineeringValue(result.sandM3, 2),
                unit = "m³",
                subtitle = "${formatEngineeringValue(result.sandKg, 0)} kg",
                modifier = Modifier.weight(1f)
            )
            ResultMetricCard(
                title = "شن مورد نیاز",
                value = formatEngineeringValue(result.gravelM3, 2),
                unit = "m³",
                subtitle = "${formatEngineeringValue(result.gravelKg, 0)} kg",
                modifier = Modifier.weight(1f)
            )
        }
        ResultMetricCard(
            title = "آب اختلاط (W/C = ${inputs.grade.waterCementRatio})",
            value = formatEngineeringValue(result.waterLiters, 0),
            unit = "لیتر",
            accentColor = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun RebarCalculatorView(
    inputs: RebarInputs,
    result: RebarResult?,
    onUpdate: ((RebarInputs) -> RebarInputs) -> Unit
) {
    val diameters = listOf(8, 10, 12, 14, 16, 18, 20, 22, 25, 28, 32)
    Text("انتخاب نمره میلگرد (قطر اسمی)", style = MaterialTheme.typography.titleSmall)
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(diameters) { d ->
            FilterChip(
                selected = inputs.diameterMm == d,
                onClick = { onUpdate { it.copy(diameterMm = d) } },
                label = {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        Text("Φ$d", style = MaterialTheme.typography.labelMedium)
                    }
                },
                modifier = Modifier.testTag("rebar_dia_$d")
            )
        }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        EngineeringInputField(
            label = "طول هر شاخه / آرماتور",
            value = inputs.lengthPerBarM,
            onValueChange = { v -> onUpdate { it.copy(lengthPerBarM = v) } },
            unit = "m",
            testTag = "rebar_length_input",
            modifier = Modifier.weight(1f)
        )
        EngineeringInputField(
            label = "تعداد میلگرد",
            value = inputs.barCount,
            onValueChange = { v -> onUpdate { it.copy(barCount = v.filter(Char::isDigit)) } },
            unit = "عدد",
            testTag = "rebar_count_input",
            modifier = Modifier.weight(1f)
        )
    }

    if (result != null) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ResultMetricCard(
                title = "وزن کل آرماتور",
                value = formatEngineeringValue(result.totalWeightKg, 2),
                unit = "kg",
                subtitle = "${formatEngineeringValue(result.totalWeightTon, 3)} تن",
                accentColor = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.weight(1f)
            )
            ResultMetricCard(
                title = "سطح مقطع کل (As)",
                value = formatEngineeringValue(result.totalAreaCm2, 2),
                unit = "cm²",
                subtitle = "تک میلگرد: ${formatEngineeringValue(result.singleAreaCm2, 2)} cm²",
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ResultMetricCard(
                title = "وزن هر متر طول",
                value = formatEngineeringValue(result.unitWeightKgPerM, 3),
                unit = "kg/m",
                modifier = Modifier.weight(1f)
            )
            ResultMetricCard(
                title = "تعداد شاخه ۱۲ متری",
                value = result.branches12m.toString(),
                unit = "شاخه",
                subtitle = "طول کل: ${formatEngineeringValue(result.totalLengthM, 1)} m",
                accentColor = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun BeamAnalysisCalculatorView(
    inputs: BeamInputs,
    result: BeamAnalysisResult?,
    onUpdate: ((BeamInputs) -> BeamInputs) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        EngineeringInputField(
            label = "طول دهانه تیر (L)",
            value = inputs.spanM,
            onValueChange = { v -> onUpdate { it.copy(spanM = v) } },
            unit = "m",
            testTag = "beam_span_input",
            modifier = Modifier.weight(1f)
        )
        EngineeringInputField(
            label = "بار گسترده یکنواخت (w)",
            value = inputs.udlKnPerM,
            onValueChange = { v -> onUpdate { it.copy(udlKnPerM = v) } },
            unit = "kN/m",
            testTag = "beam_udl_input",
            modifier = Modifier.weight(1f)
        )
    }

    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        EngineeringInputField(
            label = "بار متمرکز وسط دهانه (P)",
            value = inputs.pointLoadKn,
            onValueChange = { v -> onUpdate { it.copy(pointLoadKn = v) } },
            unit = "kN",
            testTag = "beam_point_input",
            modifier = Modifier.weight(1f)
        )
        EngineeringInputField(
            label = "مدول الاستیسیته (E)",
            value = inputs.elasticityGpa,
            onValueChange = { v -> onUpdate { it.copy(elasticityGpa = v) } },
            unit = "GPa",
            testTag = "beam_e_input",
            modifier = Modifier.weight(1f)
        )
    }

    EngineeringInputField(
        label = "ممان اینرسی مقطع (I)",
        value = inputs.inertiaCm4,
        onValueChange = { v -> onUpdate { it.copy(inertiaCm4 = v) } },
        unit = "cm⁴",
        testTag = "beam_inertia_input"
    )

    if (result != null) {
        BeamDiagramsCanvas(result = result)

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ResultMetricCard(
                title = "لنگـر ماکزیمـم (Mmax)",
                value = formatEngineeringValue(result.maxMomentKnm, 2),
                unit = "kN·m",
                accentColor = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.weight(1f)
            )
            ResultMetricCard(
                title = "برش و عکس‌العمل تکیه‌گاه",
                value = formatEngineeringValue(result.maxShearKn, 2),
                unit = "kN",
                subtitle = "Ra = Rb = ${formatEngineeringValue(result.reactionLeftKn, 2)} kN",
                modifier = Modifier.weight(1f)
            )
        }
        val deflectionColor = if (result.passesL360) {
            LocalCivilColors.current.successAccent
        } else {
            MaterialTheme.colorScheme.error
        }
        ResultMetricCard(
            title = "خیز ماکزیمم وسط دهانه (δmax)",
            value = formatEngineeringValue(result.maxDeflectionMm, 2),
            unit = "mm",
            subtitle = if (result.passesL360) {
                "نسبت L/${formatEngineeringValue(result.spanOverDeflection, 0)} — مجاز (کمتر از L/360)"
            } else {
                "نسبت L/${formatEngineeringValue(result.spanOverDeflection, 0)} — بیش از حد مجاز L/360"
            },
            accentColor = deflectionColor,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun RcFlexureCalculatorView(
    inputs: RcFlexureInputs,
    result: RcFlexureResult?,
    onUpdate: ((RcFlexureInputs) -> RcFlexureInputs) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        EngineeringInputField(
            label = "عرض تیر (b)",
            value = inputs.widthMm,
            onValueChange = { v -> onUpdate { it.copy(widthMm = v) } },
            unit = "mm",
            testTag = "rc_width_input",
            modifier = Modifier.weight(1f)
        )
        EngineeringInputField(
            label = "ارتفاع کل تیر (h)",
            value = inputs.heightMm,
            onValueChange = { v -> onUpdate { it.copy(heightMm = v) } },
            unit = "mm",
            testTag = "rc_height_input",
            modifier = Modifier.weight(1f)
        )
    }

    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        EngineeringInputField(
            label = "مقاومت بتن (fc')",
            value = inputs.fcMpa,
            onValueChange = { v -> onUpdate { it.copy(fcMpa = v) } },
            unit = "MPa",
            testTag = "rc_fc_input",
            modifier = Modifier.weight(1f)
        )
        EngineeringInputField(
            label = "تنش تسلیم فولاد (fy)",
            value = inputs.fyMpa,
            onValueChange = { v -> onUpdate { it.copy(fyMpa = v) } },
            unit = "MPa",
            testTag = "rc_fy_input",
            modifier = Modifier.weight(1f)
        )
    }

    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        EngineeringInputField(
            label = "پوشش بتنی (Cover)",
            value = inputs.coverMm,
            onValueChange = { v -> onUpdate { it.copy(coverMm = v) } },
            unit = "mm",
            testTag = "rc_cover_input",
            modifier = Modifier.weight(1f)
        )
        EngineeringInputField(
            label = "مساحت آرماتور کششی (As)",
            value = inputs.steelAreaCm2,
            onValueChange = { v -> onUpdate { it.copy(steelAreaCm2 = v) } },
            unit = "cm²",
            testTag = "rc_as_input",
            modifier = Modifier.weight(1f)
        )
    }

    // Quick preset bar configurations
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        val presets = listOf(
            "3Φ16 (6.03)" to "6.03",
            "4Φ18 (10.18)" to "10.18",
            "4Φ20 (12.57)" to "12.57",
            "5Φ22 (19.01)" to "19.01"
        )
        items(presets) { (label, areaVal) ->
            AssistChip(
                onClick = { onUpdate { it.copy(steelAreaCm2 = areaVal) } },
                label = {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        Text(label, style = MaterialTheme.typography.labelSmall)
                    }
                }
            )
        }
    }

    if (result != null) {
        val w = inputs.widthMm.toDoubleSafe() ?: 350.0
        val h = inputs.heightMm.toDoubleSafe() ?: 500.0
        val cov = inputs.coverMm.toDoubleSafe() ?: 40.0
        RcSectionCanvas(widthMm = w, heightMm = h, coverMm = cov, result = result)

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ResultMetricCard(
                title = "ظرفیت لنگر طراحی (φMn)",
                value = formatEngineeringValue(result.designMomentKnm, 2),
                unit = "kN·m",
                subtitle = "Mn = ${formatEngineeringValue(result.nominalMomentKnm, 2)} (φ=${formatEngineeringValue(result.phiFactor, 2)})",
                accentColor = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.weight(1f)
            )
            ResultMetricCard(
                title = "درصد آرماتور (ρ)",
                value = formatEngineeringValue(result.rhoPercent, 3),
                unit = "%",
                subtitle = "ρmin = ${formatEngineeringValue(result.rhoMinPercent, 3)}% (${if (result.meetsMinSteel) "تامین" else "کمتر از حداقل!"})",
                accentColor = if (result.meetsMinSteel) LocalCivilColors.current.successAccent else MaterialTheme.colorScheme.error,
                modifier = Modifier.weight(1f)
            )
        }
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = when (result.behavior) {
                FlexuralBehavior.TENSION_CONTROLLED -> MaterialTheme.colorScheme.tertiaryContainer
                FlexuralBehavior.TRANSITION -> MaterialTheme.colorScheme.secondaryContainer
                FlexuralBehavior.COMPRESSION_CONTROLLED -> MaterialTheme.colorScheme.error.copy(alpha = 0.14f)
            },
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "رفتار مقطع: ${result.behavior.label}",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(14.dp)
            )
        }
    }
}
