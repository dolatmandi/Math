package com.example.domain

enum class UnitCategory(val title: String, val units: List<EngineeringUnit>) {
    STRESS(
        title = "تنش و فشار",
        units = listOf(
            EngineeringUnit("MPa", "مگاپاسکال (N/mm²)", 1_000_000.0),
            EngineeringUnit("kgf/cm²", "کیلوگرم‌نیرو بر سانتی‌متر مربع", 98_066.5),
            EngineeringUnit("kPa", "کیلوپاسکال (kN/m²)", 1_000.0),
            EngineeringUnit("Pa", "پاسکال (N/m²)", 1.0),
            EngineeringUnit("GPa", "گیگاپاسکال", 1_000_000_000.0),
            EngineeringUnit("bar", "بار", 100_000.0),
            EngineeringUnit("psi", "پوند بر اینچ مربع", 6_894.75729),
            EngineeringUnit("ksi", "کیپ بر اینچ مربع", 6_894_757.29)
        )
    ),
    FORCE(
        title = "نیرو و بار",
        units = listOf(
            EngineeringUnit("kN", "کیلونیوتن", 1_000.0),
            EngineeringUnit("N", "نیوتن", 1.0),
            EngineeringUnit("kgf", "کیلوگرم‌نیرو", 9.80665),
            EngineeringUnit("tonf", "تن‌نیرو", 9_806.65),
            EngineeringUnit("lbf", "پوند‌نیرو", 4.4482216),
            EngineeringUnit("kip", "کیپ (۱۰۰۰ پوند)", 4_448.2216)
        )
    ),
    MOMENT(
        title = "لنگر و گشتاور",
        units = listOf(
            EngineeringUnit("kN·m", "کیلونیوتن‌متر", 1_000.0),
            EngineeringUnit("N·m", "نیوتن‌متر", 1.0),
            EngineeringUnit("kgf·m", "کیلوگرم‌نیرو‌متر", 9.80665),
            EngineeringUnit("tonf·m", "تن‌نیرو‌متر", 9_806.65),
            EngineeringUnit("kip·ft", "کیپ‌فوت", 1_355.8179)
        )
    ),
    LENGTH(
        title = "طول و دهانه",
        units = listOf(
            EngineeringUnit("m", "متر", 1.0),
            EngineeringUnit("cm", "سانتی‌متر", 0.01),
            EngineeringUnit("mm", "میلی‌متر", 0.001),
            EngineeringUnit("km", "کیلومتر", 1_000.0),
            EngineeringUnit("in", "اینچ", 0.0254),
            EngineeringUnit("ft", "فوت", 0.3048),
            EngineeringUnit("yd", "یارد", 0.9144)
        )
    ),
    AREA(
        title = "مساحت",
        units = listOf(
            EngineeringUnit("m²", "متر مربع", 1.0),
            EngineeringUnit("cm²", "سانتی‌متر مربع", 0.0001),
            EngineeringUnit("mm²", "میلی‌متر مربع", 0.000001),
            EngineeringUnit("ha", "هکتار", 10_000.0),
            EngineeringUnit("ft²", "فوت مربع", 0.09290304),
            EngineeringUnit("in²", "اینچ مربع", 0.00064516)
        )
    ),
    VOLUME(
        title = "حجم",
        units = listOf(
            EngineeringUnit("m³", "متر مکعب", 1.0),
            EngineeringUnit("L", "لیتر", 0.001),
            EngineeringUnit("cm³", "سانتی‌متر مکعب", 0.000001),
            EngineeringUnit("ft³", "فوت مکعب", 0.0283168466),
            EngineeringUnit("yd³", "یارد مکعب", 0.764554858)
        )
    ),
    DENSITY(
        title = "وزن مخصوص",
        units = listOf(
            EngineeringUnit("kN/m³", "کیلونیوتن بر متر مکعب", 101.971621),
            EngineeringUnit("kg/m³", "کیلوگرم بر متر مکعب", 1.0),
            EngineeringUnit("g/cm³", "گرم بر سانتی‌متر مکعب", 1_000.0),
            EngineeringUnit("ton/m³", "تن بر متر مکعب", 1_000.0),
            EngineeringUnit("lb/ft³", "پوند بر فوت مکعب", 16.018463)
        )
    )
}

data class EngineeringUnit(
    val symbol: String,
    val persianName: String,
    val factorToBase: Double
)

object EngineeringUnitConverter {
    fun convert(value: Double, from: EngineeringUnit, to: EngineeringUnit): Double {
        val baseValue = value * from.factorToBase
        return baseValue / to.factorToBase
    }

    fun convertAll(value: Double, from: EngineeringUnit, category: UnitCategory): List<Pair<EngineeringUnit, Double>> {
        val baseValue = value * from.factorToBase
        return category.units.map { target ->
            target to (baseValue / target.factorToBase)
        }
    }
}
