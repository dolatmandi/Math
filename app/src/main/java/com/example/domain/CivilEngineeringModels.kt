package com.example.domain

enum class MainTab(val titleRes: String) {
    SCIENTIFIC("ماشین‌حساب"),
    STRUCTURAL("سازه و بتن"),
    GEOTECH("خاک و نقشه"),
    CONVERTER("تبدیل واحد")
}

enum class StructuralCalculatorType(
    val title: String,
    val subtitle: String,
    val codeBadge: String
) {
    CONCRETE_MIX(
        title = "حجم و طرح اختلاط بتن",
        subtitle = "محاسبه حجم تر و خشک، کیسه سیمان، ماسه، شن و آب",
        codeBadge = "ACI / مبحث ۹"
    ),
    REBAR_WEIGHT(
        title = "وزن و سطح مقطع میلگرد",
        subtitle = "محاسبه وزن کل، شاخه ۱۲ متری و سطح مقطع آرماتور (As)",
        codeBadge = "ISIRI 3132"
    ),
    BEAM_ANALYSIS(
        title = "تحلیل تیر دوسر ساده",
        subtitle = "نمودار برش (SFD)، لنگر خمشی (BMD) و خیز ماکزیمم",
        codeBadge = "مقاومت مصالح"
    ),
    RC_FLEXURE(
        title = "ظرفیت خمشی تیر بتنی",
        subtitle = "محاسبه لنگر مقاوم طراحی (φMn)، کرنش کششی و درصد آرماتور",
        codeBadge = "ACI 318"
    )
}

enum class GeotechCalculatorType(
    val title: String,
    val subtitle: String,
    val codeBadge: String
) {
    SLOPE_GRADE(
        title = "شیب، زاویه و اختلاف تراز",
        subtitle = "محاسبه درصد شیب، نسبت 1:N، زاویه و طول شیب‌دار",
        codeBadge = "راه و نقشه"
    ),
    EXCAVATION(
        title = "گودبرداری و عملیات خاکی",
        subtitle = "محاسبه حجم گود شیب‌دار، ضریب تورم خاک و تعداد کامیون",
        codeBadge = "فهرست بها"
    ),
    BEARING_CAPACITY(
        title = "ظرفیت باربری پی سطحی",
        subtitle = "محاسبه qu و qall با ضرایب ترزاقی-وسیک و ضریب اطمینان",
        codeBadge = "مبحث ۷"
    ),
    AZIMUTH_COORDS(
        title = "مختصات و آزیموت نقشه‌برداری",
        subtitle = "محاسبه فاصله افقی و گرای دو نقطه به درجه-دقیقه-ثانیه",
        codeBadge = "ژئودزی"
    )
}

enum class ConcreteGrade(
    val label: String,
    val fckMpa: Int,
    val cementPart: Double,
    val sandPart: Double,
    val gravelPart: Double,
    val waterCementRatio: Double
) {
    C15("C15 (مگر / بتن سبک) 1:2:4", 15, 1.0, 2.0, 4.0, 0.55),
    C20("C20 (معمولی) 1:1.5:3", 20, 1.0, 1.5, 3.0, 0.50),
    C25("C25 (سازه‌ای رایج) 1:1:2", 25, 1.0, 1.0, 2.0, 0.45),
    C30("C30 (مقاومت بالا) 1:0.75:1.5", 30, 1.0, 0.75, 1.5, 0.42)
}

data class ConcreteMixResult(
    val wetVolumeM3: Double,
    val dryVolumeM3: Double,
    val cementKg: Double,
    val cementBags50Kg: Double,
    val sandM3: Double,
    val sandKg: Double,
    val gravelM3: Double,
    val gravelKg: Double,
    val waterLiters: Double
)

data class RebarResult(
    val diameterMm: Int,
    val unitWeightKgPerM: Double,
    val singleAreaCm2: Double,
    val totalLengthM: Double,
    val totalWeightKg: Double,
    val totalWeightTon: Double,
    val totalAreaCm2: Double,
    val branches12m: Int
)

data class BeamDiagramPoint(
    val xRatio: Float,
    val shearKn: Double,
    val momentKnm: Double
)

data class BeamAnalysisResult(
    val spanM: Double,
    val reactionLeftKn: Double,
    val reactionRightKn: Double,
    val maxShearKn: Double,
    val maxMomentKnm: Double,
    val maxDeflectionMm: Double,
    val spanOverDeflection: Double,
    val passesL360: Boolean,
    val diagramPoints: List<BeamDiagramPoint>
)

enum class FlexuralBehavior(val label: String) {
    TENSION_CONTROLLED("کشش‌کنترل (مطلوب، φ = 0.90)"),
    TRANSITION("ناحیه انتقالی (کاهش ضریب φ)"),
    COMPRESSION_CONTROLLED("فشار‌کنترل (ترد و غیرمجاز در تیر)")
}

data class RcFlexureResult(
    val effectiveDepthMm: Double,
    val steelAreaCm2: Double,
    val compressionBlockAMm: Double,
    val neutralAxisCMm: Double,
    val tensileStrainEt: Double,
    val phiFactor: Double,
    val nominalMomentKnm: Double,
    val designMomentKnm: Double,
    val rhoPercent: Double,
    val rhoMinPercent: Double,
    val behavior: FlexuralBehavior,
    val meetsMinSteel: Boolean
)

data class SlopeResult(
    val horizontalM: Double,
    val verticalM: Double,
    val slopePercent: Double,
    val angleDegrees: Double,
    val ratioOneInN: Double,
    val inclinedLengthM: Double
)

data class ExcavationResult(
    val bottomAreaM2: Double,
    val topAreaM2: Double,
    val bankVolumeM3: Double,
    val looseVolumeM3: Double,
    val truckTrips: Int
)

enum class FootingShape(val label: String) {
    STRIP("پی نواری"),
    SQUARE("پی مربعی"),
    CIRCULAR("پی دایره‌ای")
}

data class BearingCapacityResult(
    val nc: Double,
    val nq: Double,
    val nGamma: Double,
    val ultimateCapacityKpa: Double,
    val allowableCapacityKpa: Double,
    val allowableTotalLoadKnPerMOrTotal: Double
)

data class AzimuthResult(
    val deltaN: Double,
    val deltaE: Double,
    val distanceM: Double,
    val azimuthDecimalDeg: Double,
    val azimuthDms: String,
    val quadrantBearing: String
)

data class CalcHistoryItem(
    val id: Long,
    val expression: String,
    val result: String
)
