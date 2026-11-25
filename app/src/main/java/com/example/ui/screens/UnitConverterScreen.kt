package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.domain.EngineeringUnitConverter
import com.example.domain.UnitCategory
import com.example.ui.ConverterUiState
import com.example.ui.components.EngineeringInputField
import com.example.ui.components.ResultMetricCard
import com.example.ui.components.copyTextToClipboard
import com.example.ui.formatEngineeringValue
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.toDoubleSafe

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitConverterScreen(
    state: ConverterUiState,
    onSelectCategory: (UnitCategory) -> Unit,
    onInputChange: (String) -> Unit,
    onSelectFromUnit: (Int) -> Unit,
    onSelectToUnit: (Int) -> Unit,
    onSwapUnits: () -> Unit,
    modifier: Modifier = Modifier
) {
    val inputValue = state.inputText.toDoubleSafe() ?: 0.0
    val convertedMain = EngineeringUnitConverter.convert(inputValue, state.fromUnit, state.toUnit)
    val equivalents = EngineeringUnitConverter.convertAll(inputValue, state.fromUnit, state.category)
    val context = LocalContext.current
    val copiedMsg = stringResource(R.string.copied_to_clipboard)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "تبدیل واحد تخصصی مهندسی عمران",
            style = MaterialTheme.typography.titleLarge
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(UnitCategory.entries) { cat ->
                FilterChip(
                    selected = cat == state.category,
                    onClick = { onSelectCategory(cat) },
                    label = { Text(cat.title) },
                    modifier = Modifier.testTag("unit_cat_${cat.name.lowercase()}")
                )
            }
        }

        Surface(
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                EngineeringInputField(
                    label = "مقدار ورودی",
                    value = state.inputText,
                    onValueChange = onInputChange,
                    unit = state.fromUnit.symbol,
                    testTag = "converter_input_field",
                    allowNegative = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    UnitDropdownSelector(
                        label = "از واحد",
                        units = state.category.units.map { "${it.symbol} — ${it.persianName}" },
                        selectedIndex = state.fromIndex,
                        onSelect = onSelectFromUnit,
                        testTag = "converter_from_dropdown",
                        modifier = Modifier.weight(1f)
                    )
                    FilledTonalIconButton(
                        onClick = onSwapUnits,
                        modifier = Modifier
                            .padding(top = 6.dp)
                            .minimumInteractiveComponentSize()
                            .testTag("converter_swap_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.SwapHoriz,
                            contentDescription = stringResource(R.string.cd_swap_units)
                        )
                    }
                    UnitDropdownSelector(
                        label = "به واحد",
                        units = state.category.units.map { "${it.symbol} — ${it.persianName}" },
                        selectedIndex = state.toIndex,
                        onSelect = onSelectToUnit,
                        testTag = "converter_to_dropdown",
                        modifier = Modifier.weight(1f)
                    )
                }

                ResultMetricCard(
                    title = "نتیجه تبدیل به ${state.toUnit.persianName}",
                    value = formatEngineeringValue(convertedMain, 5),
                    unit = state.toUnit.symbol,
                    accentColor = MaterialTheme.colorScheme.secondary
                )
            }
        }

        Text(
            text = "معادل در سایر واحدها",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            itemsIndexed(equivalents) { idx, (unit, value) ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (idx == state.toIndex) {
                        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
                    } else {
                        MaterialTheme.colorScheme.surface
                    },
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable {
                            copyTextToClipboard(
                                context,
                                "${formatEngineeringValue(value, 5)} ${unit.symbol}",
                                copiedMsg
                            )
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 11.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = unit.persianName,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = unit.symbol,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                            Text(
                                text = formatEngineeringValue(value, 5),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UnitDropdownSelector(
    label: String,
    units: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = units.getOrElse(selectedIndex) { "" }.substringBefore(" —"),
            onValueChange = {},
            readOnly = true,
            label = { Text(label, style = MaterialTheme.typography.labelSmall) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .testTag(testTag)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            units.forEachIndexed { idx, labelText ->
                DropdownMenuItem(
                    text = { Text(labelText, style = MaterialTheme.typography.bodySmall) },
                    onClick = {
                        onSelect(idx)
                        expanded = false
                    }
                )
            }
        }
    }
}
