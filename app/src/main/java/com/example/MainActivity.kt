package com.example

import android.graphics.Color as AndroidColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Domain
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.ThemePreference
import com.example.domain.MainTab
import com.example.ui.CivilCalcViewModel
import com.example.ui.components.BlueprintBackgroundBox
import com.example.ui.screens.GeotechSurveyingScreen
import com.example.ui.screens.ScientificCalculatorScreen
import com.example.ui.screens.StructuralCalculatorsScreen
import com.example.ui.screens.UnitConverterScreen
import com.example.ui.theme.CivilCalcTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(AndroidColor.TRANSPARENT, AndroidColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(AndroidColor.TRANSPARENT, AndroidColor.TRANSPARENT)
        )
        setContent {
            val viewModel: CivilCalcViewModel = viewModel()
            val themePref by viewModel.themePreference.collectAsStateWithLifecycle()
            val isDark = when (themePref) {
                ThemePreference.SYSTEM -> isSystemInDarkTheme()
                ThemePreference.DARK -> true
                ThemePreference.LIGHT -> false
            }

            LaunchedEffect(isDark) {
                if (isDark) {
                    enableEdgeToEdge(
                        statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
                        navigationBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT)
                    )
                } else {
                    enableEdgeToEdge(
                        statusBarStyle = SystemBarStyle.light(AndroidColor.TRANSPARENT, AndroidColor.TRANSPARENT),
                        navigationBarStyle = SystemBarStyle.light(AndroidColor.TRANSPARENT, AndroidColor.TRANSPARENT)
                    )
                }
            }

            CivilCalcTheme(darkTheme = isDark) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    CivilCalcRoot(viewModel = viewModel, themePref = themePref)
                }
            }
        }
    }
}

private data class NavItemSpec(
    val tab: MainTab,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CivilCalcRoot(
    viewModel: CivilCalcViewModel,
    themePref: ThemePreference
) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val scientificState by viewModel.scientificState.collectAsStateWithLifecycle()
    val selectedStructural by viewModel.selectedStructural.collectAsStateWithLifecycle()
    val selectedGeotech by viewModel.selectedGeotech.collectAsStateWithLifecycle()

    val concreteInputs by viewModel.concreteInputs.collectAsStateWithLifecycle()
    val concreteResult by viewModel.concreteResult.collectAsStateWithLifecycle()
    val rebarInputs by viewModel.rebarInputs.collectAsStateWithLifecycle()
    val rebarResult by viewModel.rebarResult.collectAsStateWithLifecycle()
    val beamInputs by viewModel.beamInputs.collectAsStateWithLifecycle()
    val beamResult by viewModel.beamResult.collectAsStateWithLifecycle()
    val rcInputs by viewModel.rcFlexureInputs.collectAsStateWithLifecycle()
    val rcResult by viewModel.rcFlexureResult.collectAsStateWithLifecycle()

    val slopeInputs by viewModel.slopeInputs.collectAsStateWithLifecycle()
    val slopeResult by viewModel.slopeResult.collectAsStateWithLifecycle()
    val excavationInputs by viewModel.excavationInputs.collectAsStateWithLifecycle()
    val excavationResult by viewModel.excavationResult.collectAsStateWithLifecycle()
    val bearingInputs by viewModel.bearingInputs.collectAsStateWithLifecycle()
    val bearingResult by viewModel.bearingResult.collectAsStateWithLifecycle()
    val azimuthInputs by viewModel.azimuthInputs.collectAsStateWithLifecycle()
    val azimuthResult by viewModel.azimuthResult.collectAsStateWithLifecycle()

    val converterState by viewModel.converterState.collectAsStateWithLifecycle()

    val navItems = listOf(
        NavItemSpec(MainTab.SCIENTIFIC, stringResource(R.string.tab_scientific), Icons.Filled.Calculate, Icons.Outlined.Calculate),
        NavItemSpec(MainTab.STRUCTURAL, stringResource(R.string.tab_structural), Icons.Filled.Domain, Icons.Outlined.Domain),
        NavItemSpec(MainTab.GEOTECH, stringResource(R.string.tab_geotech), Icons.Filled.Terrain, Icons.Outlined.Terrain),
        NavItemSpec(MainTab.CONVERTER, stringResource(R.string.tab_converter), Icons.Filled.SwapHoriz, Icons.Outlined.SwapHoriz)
    )

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useRail = maxWidth >= 700.dp

        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.app_name),
                                style = MaterialTheme.typography.titleLarge,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Surface(
                                shape = MaterialTheme.shapes.small,
                                color = MaterialTheme.colorScheme.secondaryContainer
                            ) {
                                Text(
                                    text = "مهندسی عمران",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { viewModel.cycleThemePreference() },
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("theme_toggle_button")
                        ) {
                            val icon = when (themePref) {
                                ThemePreference.DARK -> Icons.Outlined.LightMode
                                ThemePreference.LIGHT -> Icons.Outlined.DarkMode
                                ThemePreference.SYSTEM -> Icons.Outlined.Contrast
                            }
                            Icon(
                                imageVector = icon,
                                contentDescription = stringResource(R.string.cd_toggle_theme)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f)
                    )
                )
            },
            bottomBar = {
                if (!useRail) {
                    NavigationBar(
                        windowInsets = WindowInsets.navigationBars,
                        containerColor = MaterialTheme.colorScheme.surface
                    ) {
                        navItems.forEach { item ->
                            val selected = currentTab == item.tab
                            NavigationBarItem(
                                selected = selected,
                                onClick = { viewModel.selectTab(item.tab) },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = item.title
                                    )
                                },
                                label = {
                                    Text(
                                        text = item.title,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                modifier = Modifier.testTag("nav_${item.tab.name.lowercase()}")
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            BlueprintBackgroundBox(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Row(modifier = Modifier.fillMaxSize()) {
                    if (useRail) {
                        NavigationRail(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f)
                        ) {
                            Spacer(modifier = Modifier.height(12.dp))
                            navItems.forEach { item ->
                                val selected = currentTab == item.tab
                                NavigationRailItem(
                                    selected = selected,
                                    onClick = { viewModel.selectTab(item.tab) },
                                    icon = {
                                        Icon(
                                            imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                            contentDescription = item.title
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = item.title,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    },
                                    modifier = Modifier.testTag("rail_${item.tab.name.lowercase()}")
                                )
                            }
                        }
                    }

                    Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                        when (currentTab) {
                            MainTab.SCIENTIFIC -> ScientificCalculatorScreen(
                                state = scientificState,
                                onToken = viewModel::onCalcToken,
                                onFunction = viewModel::onCalcFunction,
                                onSquare = viewModel::onCalcSquare,
                                onBackspace = viewModel::onCalcBackspace,
                                onClear = viewModel::onCalcClear,
                                onEquals = viewModel::onCalcEquals,
                                onToggleDegree = viewModel::toggleDegreeMode,
                                onLoadHistory = viewModel::loadHistoryItem,
                                onClearHistory = viewModel::clearHistory
                            )
                            MainTab.STRUCTURAL -> StructuralCalculatorsScreen(
                                selected = selectedStructural,
                                onSelect = viewModel::selectStructuralCalculator,
                                concreteInputs = concreteInputs,
                                concreteResult = concreteResult,
                                onUpdateConcrete = viewModel::updateConcreteInputs,
                                rebarInputs = rebarInputs,
                                rebarResult = rebarResult,
                                onUpdateRebar = viewModel::updateRebarInputs,
                                beamInputs = beamInputs,
                                beamResult = beamResult,
                                onUpdateBeam = viewModel::updateBeamInputs,
                                rcInputs = rcInputs,
                                rcResult = rcResult,
                                onUpdateRc = viewModel::updateRcFlexureInputs
                            )
                            MainTab.GEOTECH -> GeotechSurveyingScreen(
                                selected = selectedGeotech,
                                onSelect = viewModel::selectGeotechCalculator,
                                slopeInputs = slopeInputs,
                                slopeResult = slopeResult,
                                onUpdateSlope = viewModel::updateSlopeInputs,
                                excavationInputs = excavationInputs,
                                excavationResult = excavationResult,
                                onUpdateExcavation = viewModel::updateExcavationInputs,
                                bearingInputs = bearingInputs,
                                bearingResult = bearingResult,
                                onUpdateBearing = viewModel::updateBearingInputs,
                                azimuthInputs = azimuthInputs,
                                azimuthResult = azimuthResult,
                                onUpdateAzimuth = viewModel::updateAzimuthInputs
                            )
                            MainTab.CONVERTER -> UnitConverterScreen(
                                state = converterState,
                                onSelectCategory = viewModel::selectUnitCategory,
                                onInputChange = viewModel::updateConverterInput,
                                onSelectFromUnit = viewModel::selectFromUnit,
                                onSelectToUnit = viewModel::selectToUnit,
                                onSwapUnits = viewModel::swapConverterUnits
                            )
                        }
                    }
                }
            }
        }
    }
}
