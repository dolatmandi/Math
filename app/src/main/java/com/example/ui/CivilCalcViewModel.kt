package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.PreferencesRepository
import com.example.data.ThemePreference
import com.example.domain.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Locale

data class ConcreteInputs(
    val lengthM: String = "5",
    val widthM: String = "4",
    val thicknessM: String = "0.25",
    val count: String = "1",
    val wastagePercent: String = "5",
    val grade: ConcreteGrade = ConcreteGrade.C25
)

data class RebarInputs(
    val diameterMm: Int = 16,
    val lengthPerBarM: String = "12",
    val barCount: String = "24"
)

data class BeamInputs(
    val spanM: String = "6",
    val udlKnPerM: String = "15",
    val pointLoadKn: String = "20",
    val elasticityGpa: String = "25",
    val inertiaCm4: String = "180000"
)

data class RcFlexureInputs(
    val widthMm: String = "350",
    val heightMm: String = "500",
    val coverMm: String = "40",
    val fcMpa: String = "25",
    val fyMpa: String = "400",
    val steelAreaCm2: String = "12.57"
)

data class SlopeInputs(
    val horizontalM: String = "25",
    val verticalM: String = "3.5"
)

data class ExcavationInputs(
    val lengthM: String = "14",
    val widthM: String = "10",
    val depthM: String = "3.2",
    val sideSlopeHV: String = "0.5",
    val swellPercent: String = "25",
    val truckCapacityM3: String = "12"
)

data class BearingInputs(
    val shape: FootingShape = FootingShape.SQUARE,
    val widthB: String = "2.2",
    val depthDf: String = "1.5",
    val unitWeightGamma: String = "18.5",
    val cohesionC: String = "15",
    val phiDegrees: String = "30",
    val safetyFactor: String = "3.0"
)

data class AzimuthInputs(
    val n1: String = "3952410.50",
    val e1: String = "536120.25",
    val n2: String = "3952485.80",
    val e2: String = "536210.60"
)

data class ConverterUiState(
    val category: UnitCategory = UnitCategory.STRESS,
    val inputText: String = "25",
    val fromIndex: Int = 0,
    val toIndex: Int = 1
) {
    val fromUnit: EngineeringUnit get() = category.units.getOrElse(fromIndex) { category.units.first() }
    val toUnit: EngineeringUnit get() = category.units.getOrElse(toIndex) { category.units.last() }
}

data class ScientificUiState(
    val expression: String = "",
    val previewResult: String = "0",
    val errorMessage: String? = null,
    val lastAnswer: Double = 0.0,
    val isDegreeMode: Boolean = true,
    val justEvaluated: Boolean = false,
    val history: List<CalcHistoryItem> = emptyList()
)

class CivilCalcViewModel(application: Application) : AndroidViewModel(application) {

    private val prefsRepo = PreferencesRepository(application.applicationContext)

    val themePreference: StateFlow<ThemePreference> = prefsRepo.themePreference
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ThemePreference.LIGHT)

    private val _currentTab = MutableStateFlow(MainTab.SCIENTIFIC)
    val currentTab: StateFlow<MainTab> = _currentTab.asStateFlow()

    private val _scientificState = MutableStateFlow(ScientificUiState())
    val scientificState: StateFlow<ScientificUiState> = _scientificState.asStateFlow()

    private val _selectedStructural = MutableStateFlow<StructuralCalculatorType?>(null)
    val selectedStructural: StateFlow<StructuralCalculatorType?> = _selectedStructural.asStateFlow()

    private val _selectedGeotech = MutableStateFlow<GeotechCalculatorType?>(null)
    val selectedGeotech: StateFlow<GeotechCalculatorType?> = _selectedGeotech.asStateFlow()

    // Structural inputs & live results
    private val _concreteInputs = MutableStateFlow(ConcreteInputs())
    val concreteInputs: StateFlow<ConcreteInputs> = _concreteInputs.asStateFlow()
    val concreteResult: StateFlow<ConcreteMixResult?> = _concreteInputs.map { inp ->
        CivilEngineeringFormulas.calculateConcreteMix(
            lengthM = inp.lengthM.toDoubleSafe() ?: -1.0,
            widthM = inp.widthM.toDoubleSafe() ?: -1.0,
            thicknessM = inp.thicknessM.toDoubleSafe() ?: -1.0,
            count = inp.count.toIntOrNull() ?: -1,
            grade = inp.grade,
            wastagePercent = inp.wastagePercent.toDoubleSafe() ?: -1.0
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val _rebarInputs = MutableStateFlow(RebarInputs())
    val rebarInputs: StateFlow<RebarInputs> = _rebarInputs.asStateFlow()
    val rebarResult: StateFlow<RebarResult?> = _rebarInputs.map { inp ->
        CivilEngineeringFormulas.calculateRebar(
            diameterMm = inp.diameterMm,
            lengthPerBarM = inp.lengthPerBarM.toDoubleSafe() ?: -1.0,
            barCount = inp.barCount.toIntOrNull() ?: -1
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val _beamInputs = MutableStateFlow(BeamInputs())
    val beamInputs: StateFlow<BeamInputs> = _beamInputs.asStateFlow()
    val beamResult: StateFlow<BeamAnalysisResult?> = _beamInputs.map { inp ->
        CivilEngineeringFormulas.calculateBeamAnalysis(
            spanM = inp.spanM.toDoubleSafe() ?: -1.0,
            udlKnPerM = inp.udlKnPerM.toDoubleSafe() ?: 0.0,
            pointLoadCenterKn = inp.pointLoadKn.toDoubleSafe() ?: 0.0,
            elasticityGpa = inp.elasticityGpa.toDoubleSafe() ?: -1.0,
            inertiaCm4 = inp.inertiaCm4.toDoubleSafe() ?: -1.0
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val _rcFlexureInputs = MutableStateFlow(RcFlexureInputs())
    val rcFlexureInputs: StateFlow<RcFlexureInputs> = _rcFlexureInputs.asStateFlow()
    val rcFlexureResult: StateFlow<RcFlexureResult?> = _rcFlexureInputs.map { inp ->
        CivilEngineeringFormulas.calculateRcFlexure(
            widthMm = inp.widthMm.toDoubleSafe() ?: -1.0,
            heightMm = inp.heightMm.toDoubleSafe() ?: -1.0,
            coverMm = inp.coverMm.toDoubleSafe() ?: -1.0,
            fcMpa = inp.fcMpa.toDoubleSafe() ?: -1.0,
            fyMpa = inp.fyMpa.toDoubleSafe() ?: -1.0,
            steelAreaCm2 = inp.steelAreaCm2.toDoubleSafe() ?: -1.0
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    // Geotech & Surveying inputs & live results
    private val _slopeInputs = MutableStateFlow(SlopeInputs())
    val slopeInputs: StateFlow<SlopeInputs> = _slopeInputs.asStateFlow()
    val slopeResult: StateFlow<SlopeResult?> = _slopeInputs.map { inp ->
        CivilEngineeringFormulas.calculateSlope(
            horizontalM = inp.horizontalM.toDoubleSafe() ?: -1.0,
            verticalM = inp.verticalM.toDoubleSafe() ?: 0.0
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val _excavationInputs = MutableStateFlow(ExcavationInputs())
    val excavationInputs: StateFlow<ExcavationInputs> = _excavationInputs.asStateFlow()
    val excavationResult: StateFlow<ExcavationResult?> = _excavationInputs.map { inp ->
        CivilEngineeringFormulas.calculateExcavation(
            lengthM = inp.lengthM.toDoubleSafe() ?: -1.0,
            widthM = inp.widthM.toDoubleSafe() ?: -1.0,
            depthM = inp.depthM.toDoubleSafe() ?: -1.0,
            sideSlopeHV = inp.sideSlopeHV.toDoubleSafe() ?: -1.0,
            swellPercent = inp.swellPercent.toDoubleSafe() ?: -1.0,
            truckCapacityM3 = inp.truckCapacityM3.toDoubleSafe() ?: -1.0
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val _bearingInputs = MutableStateFlow(BearingInputs())
    val bearingInputs: StateFlow<BearingInputs> = _bearingInputs.asStateFlow()
    val bearingResult: StateFlow<BearingCapacityResult?> = _bearingInputs.map { inp ->
        CivilEngineeringFormulas.calculateBearingCapacity(
            shape = inp.shape,
            widthB = inp.widthB.toDoubleSafe() ?: -1.0,
            depthDf = inp.depthDf.toDoubleSafe() ?: -1.0,
            unitWeightGamma = inp.unitWeightGamma.toDoubleSafe() ?: -1.0,
            cohesionC = inp.cohesionC.toDoubleSafe() ?: -1.0,
            phiDegrees = inp.phiDegrees.toDoubleSafe() ?: -1.0,
            safetyFactor = inp.safetyFactor.toDoubleSafe() ?: -1.0
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val _azimuthInputs = MutableStateFlow(AzimuthInputs())
    val azimuthInputs: StateFlow<AzimuthInputs> = _azimuthInputs.asStateFlow()
    val azimuthResult: StateFlow<AzimuthResult?> = _azimuthInputs.map { inp ->
        val n1 = inp.n1.toDoubleSafe() ?: return@map null
        val e1 = inp.e1.toDoubleSafe() ?: return@map null
        val n2 = inp.n2.toDoubleSafe() ?: return@map null
        val e2 = inp.e2.toDoubleSafe() ?: return@map null
        CivilEngineeringFormulas.calculateAzimuth(n1, e1, n2, e2)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    // Unit Converter
    private val _converterState = MutableStateFlow(ConverterUiState())
    val converterState: StateFlow<ConverterUiState> = _converterState.asStateFlow()

    init {
        viewModelScope.launch {
            prefsRepo.isDegreeMode.collect { degMode ->
                _scientificState.update { state ->
                    val updated = state.copy(isDegreeMode = degMode)
                    reEvaluatePreview(updated)
                }
            }
        }
    }

    fun selectTab(tab: MainTab) {
        _currentTab.value = tab
    }

    fun cycleThemePreference() {
        viewModelScope.launch {
            val next = when (themePreference.value) {
                ThemePreference.LIGHT -> ThemePreference.DARK
                ThemePreference.DARK -> ThemePreference.SYSTEM
                ThemePreference.SYSTEM -> ThemePreference.LIGHT
            }
            prefsRepo.setThemePreference(next)
        }
    }

    // Scientific Calculator Actions
    fun onCalcToken(token: String) {
        _scientificState.update { state ->
            val isOperator = token in listOf("+", "-", "×", "÷", "^", "%")
            val nextExpr = if (state.justEvaluated) {
                if (isOperator) {
                    "ANS$token"
                } else {
                    token
                }
            } else {
                state.expression + token
            }
            reEvaluatePreview(state.copy(expression = nextExpr, justEvaluated = false, errorMessage = null))
        }
    }

    fun onCalcFunction(fnPrefix: String) {
        _scientificState.update { state ->
            val nextExpr = if (state.justEvaluated) {
                "$fnPrefix${state.expression})"
            } else {
                state.expression + fnPrefix
            }
            reEvaluatePreview(state.copy(expression = nextExpr, justEvaluated = false, errorMessage = null))
        }
    }

    fun onCalcSquare() {
        _scientificState.update { state ->
            val base = if (state.expression.isEmpty()) "ANS" else state.expression
            val nextExpr = if (state.justEvaluated) "ANS^2" else "$base^2"
            reEvaluatePreview(state.copy(expression = nextExpr, justEvaluated = false, errorMessage = null))
        }
    }

    fun onCalcBackspace() {
        _scientificState.update { state ->
            if (state.expression.isEmpty()) return@update state
            val nextExpr = when {
                state.expression.endsWith("asin(") -> state.expression.dropLast(5)
                state.expression.endsWith("acos(") -> state.expression.dropLast(5)
                state.expression.endsWith("atan(") -> state.expression.dropLast(5)
                state.expression.endsWith("sin(") -> state.expression.dropLast(4)
                state.expression.endsWith("cos(") -> state.expression.dropLast(4)
                state.expression.endsWith("tan(") -> state.expression.dropLast(4)
                state.expression.endsWith("log(") -> state.expression.dropLast(4)
                state.expression.endsWith("ln(") -> state.expression.dropLast(3)
                state.expression.endsWith("ANS") -> state.expression.dropLast(3)
                else -> state.expression.dropLast(1)
            }
            reEvaluatePreview(state.copy(expression = nextExpr, justEvaluated = false, errorMessage = null))
        }
    }

    fun onCalcClear() {
        _scientificState.update { state ->
            state.copy(expression = "", previewResult = "0", errorMessage = null, justEvaluated = false)
        }
    }

    fun onCalcEquals() {
        _scientificState.update { state ->
            if (state.expression.isBlank()) return@update state
            when (val res = MathExpressionEvaluator.evaluate(state.expression, state.isDegreeMode, state.lastAnswer)) {
                is EvalResult.Success -> {
                    val historyItem = CalcHistoryItem(
                        id = System.nanoTime(),
                        expression = state.expression,
                        result = res.formatted
                    )
                    state.copy(
                        expression = res.formatted,
                        previewResult = res.formatted,
                        lastAnswer = res.value,
                        errorMessage = null,
                        justEvaluated = true,
                        history = (listOf(historyItem) + state.history).take(30)
                    )
                }
                is EvalResult.Error -> {
                    state.copy(errorMessage = res.message, justEvaluated = false)
                }
            }
        }
    }

    fun toggleDegreeMode() {
        val next = !_scientificState.value.isDegreeMode
        viewModelScope.launch { prefsRepo.setDegreeMode(next) }
    }

    fun loadHistoryItem(item: CalcHistoryItem) {
        _scientificState.update { state ->
            reEvaluatePreview(state.copy(expression = item.expression, justEvaluated = false, errorMessage = null))
        }
    }

    fun clearHistory() {
        _scientificState.update { it.copy(history = emptyList()) }
    }

    private fun reEvaluatePreview(state: ScientificUiState): ScientificUiState {
        if (state.expression.isBlank()) {
            return state.copy(previewResult = "0", errorMessage = null)
        }
        return when (val res = MathExpressionEvaluator.evaluate(state.expression, state.isDegreeMode, state.lastAnswer)) {
            is EvalResult.Success -> state.copy(previewResult = res.formatted, errorMessage = null)
            is EvalResult.Error -> state.copy(previewResult = state.previewResult)
        }
    }

    // Sub-screen selection
    fun selectStructuralCalculator(type: StructuralCalculatorType?) {
        _selectedStructural.value = type
    }

    fun selectGeotechCalculator(type: GeotechCalculatorType?) {
        _selectedGeotech.value = type
    }

    // Input Updaters
    fun updateConcreteInputs(transform: (ConcreteInputs) -> ConcreteInputs) {
        _concreteInputs.update(transform)
    }

    fun updateRebarInputs(transform: (RebarInputs) -> RebarInputs) {
        _rebarInputs.update(transform)
    }

    fun updateBeamInputs(transform: (BeamInputs) -> BeamInputs) {
        _beamInputs.update(transform)
    }

    fun updateRcFlexureInputs(transform: (RcFlexureInputs) -> RcFlexureInputs) {
        _rcFlexureInputs.update(transform)
    }

    fun updateSlopeInputs(transform: (SlopeInputs) -> SlopeInputs) {
        _slopeInputs.update(transform)
    }

    fun updateExcavationInputs(transform: (ExcavationInputs) -> ExcavationInputs) {
        _excavationInputs.update(transform)
    }

    fun updateBearingInputs(transform: (BearingInputs) -> BearingInputs) {
        _bearingInputs.update(transform)
    }

    fun updateAzimuthInputs(transform: (AzimuthInputs) -> AzimuthInputs) {
        _azimuthInputs.update(transform)
    }

    // Converter Actions
    fun selectUnitCategory(category: UnitCategory) {
        _converterState.update {
            ConverterUiState(
                category = category,
                inputText = "1",
                fromIndex = 0,
                toIndex = if (category.units.size > 1) 1 else 0
            )
        }
    }

    fun updateConverterInput(text: String) {
        _converterState.update { it.copy(inputText = text) }
    }

    fun selectFromUnit(index: Int) {
        _converterState.update { it.copy(fromIndex = index) }
    }

    fun selectToUnit(index: Int) {
        _converterState.update { it.copy(toIndex = index) }
    }

    fun swapConverterUnits() {
        _converterState.update { state ->
            state.copy(fromIndex = state.toIndex, toIndex = state.fromIndex)
        }
    }
}

fun String.toDoubleSafe(): Double? {
    val normalized = this.trim()
        .replace('٫', '.')
        .replace(',', '.')
        .replace('۰', '0').replace('۱', '1').replace('۲', '2')
        .replace('۳', '3').replace('۴', '4').replace('۵', '5')
        .replace('۶', '6').replace('۷', '7').replace('۸', '8')
        .replace('۹', '9')
    return normalized.toDoubleOrNull()
}

fun formatEngineeringValue(value: Double, decimals: Int = 2): String {
    if (value.isNaN() || value.isInfinite()) return "—"
    val absVal = kotlin.math.abs(value)
    return when {
        absVal >= 1e7 -> String.format(Locale.US, "%.3e", value)
        absVal > 0 && absVal < 0.001 -> String.format(Locale.US, "%.5f", value).trimEnd('0').trimEnd('.')
        else -> {
            val str = String.format(Locale.US, "%.${decimals}f", value)
            if (str.contains('.')) str.trimEnd('0').trimEnd('.') else str
        }
    }
}
