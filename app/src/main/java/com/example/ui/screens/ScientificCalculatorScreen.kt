package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Backspace
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.domain.CalcHistoryItem
import com.example.ui.ScientificUiState
import com.example.ui.components.copyTextToClipboard
import com.example.ui.theme.JetBrainsMonoFontFamily

private data class EngineeringConstant(
    val label: String,
    val token: String,
    val hint: String
)

private val CivilConstants = listOf(
    EngineeringConstant("g", "9.81", "شتاب ثقل"),
    EngineeringConstant("γc", "2400", "وزن مخصوص بتن"),
    EngineeringConstant("γs", "7850", "چگالی فولاد"),
    EngineeringConstant("π", "π", "عدد پی"),
    EngineeringConstant("e", "e", "عدد نپر"),
    EngineeringConstant("√2", "√(2)", "رادیکال ۲")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScientificCalculatorScreen(
    state: ScientificUiState,
    onToken: (String) -> Unit,
    onFunction: (String) -> Unit,
    onSquare: () -> Unit,
    onBackspace: () -> Unit,
    onClear: () -> Unit,
    onEquals: () -> Unit,
    onToggleDegree: () -> Unit,
    onLoadHistory: (CalcHistoryItem) -> Unit,
    onClearHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showHistorySheet by remember { mutableStateOf(false) }
    var showInverseTrig by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val copiedMsg = stringResource(R.string.copied_to_clipboard)

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isWide = maxWidth >= 700.dp

        if (isWide) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1.4f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DisplayAndConstants(
                        state = state,
                        onToggleDegree = onToggleDegree,
                        onShowHistory = { showHistorySheet = true },
                        showHistoryButton = false,
                        onCopy = { copyTextToClipboard(context, state.previewResult, copiedMsg) },
                        onToken = onToken
                    )
                    ScientificKeypad(
                        showInverseTrig = showInverseTrig,
                        onToggleInverse = { showInverseTrig = !showInverseTrig },
                        onToken = onToken,
                        onFunction = onFunction,
                        onSquare = onSquare,
                        onBackspace = onBackspace,
                        onClear = onClear,
                        onEquals = onEquals,
                        modifier = Modifier.weight(1f)
                    )
                }
                HistorySidePanel(
                    history = state.history,
                    onSelect = onLoadHistory,
                    onClear = onClearHistory,
                    modifier = Modifier
                        .weight(0.75f)
                        .fillMaxHeight()
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DisplayAndConstants(
                    state = state,
                    onToggleDegree = onToggleDegree,
                    onShowHistory = { showHistorySheet = true },
                    showHistoryButton = true,
                    onCopy = { copyTextToClipboard(context, state.previewResult, copiedMsg) },
                    onToken = onToken
                )
                ScientificKeypad(
                    showInverseTrig = showInverseTrig,
                    onToggleInverse = { showInverseTrig = !showInverseTrig },
                    onToken = onToken,
                    onFunction = onFunction,
                    onSquare = onSquare,
                    onBackspace = onBackspace,
                    onClear = onClear,
                    onEquals = onEquals,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    if (showHistorySheet) {
        ModalBottomSheet(
            onDismissRequest = { showHistorySheet = false }
        ) {
            HistorySheetContent(
                history = state.history,
                onSelect = {
                    onLoadHistory(it)
                    showHistorySheet = false
                },
                onClear = onClearHistory
            )
        }
    }
}

@Composable
private fun DisplayAndConstants(
    state: ScientificUiState,
    onToggleDegree: () -> Unit,
    onShowHistory: () -> Unit,
    showHistoryButton: Boolean,
    onCopy: () -> Unit,
    onToken: (String) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterChip(
                        selected = state.isDegreeMode,
                        onClick = onToggleDegree,
                        label = {
                            Text(
                                text = if (state.isDegreeMode) "DEG" else "RAD",
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        modifier = Modifier.testTag("deg_rad_toggle")
                    )
                    if (showHistoryButton) {
                        FilledTonalIconButton(
                            onClick = onShowHistory,
                            modifier = Modifier
                                .size(40.dp)
                                .testTag("calc_history_button")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.History,
                                contentDescription = stringResource(R.string.cd_history),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
                IconButton(
                    onClick = onCopy,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("calc_copy_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ContentCopy,
                        contentDescription = stringResource(R.string.cd_copy_result),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                val scrollState = rememberScrollState()
                LaunchedEffect(state.expression) {
                    scrollState.scrollTo(scrollState.maxValue)
                }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = state.expression.ifEmpty { "0" },
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        modifier = Modifier
                            .horizontalScroll(scrollState)
                            .testTag("calc_expression_display")
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    AnimatedVisibility(visible = state.errorMessage != null) {
                        Text(
                            text = state.errorMessage.orEmpty(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    if (state.errorMessage == null) {
                        Text(
                            text = "= ${state.previewResult}",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.secondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.testTag("calc_preview_display")
                        )
                    }
                }
            }
        }
    }

    // Engineering quick constants bar
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(CivilConstants) { item ->
            AssistChip(
                onClick = { onToken(item.token) },
                label = {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        Text(
                            text = "${item.label}=${item.token}",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                },
                modifier = Modifier.testTag("const_${item.label}")
            )
        }
    }
}

@Composable
private fun ScientificKeypad(
    showInverseTrig: Boolean,
    onToggleInverse: () -> Unit,
    onToken: (String) -> Unit,
    onFunction: (String) -> Unit,
    onSquare: () -> Unit,
    onBackspace: () -> Unit,
    onClear: () -> Unit,
    onEquals: () -> Unit,
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            // Scientific Function Row 1
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.82f),
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                SciKey(
                    label = if (showInverseTrig) "2nd*" else "2nd",
                    isActive = showInverseTrig,
                    onClick = onToggleInverse,
                    testTag = "key_2nd",
                    modifier = Modifier.weight(1f)
                )
                SciKey(
                    label = if (showInverseTrig) "asin" else "sin",
                    onClick = { onFunction(if (showInverseTrig) "asin(" else "sin(") },
                    testTag = "key_sin",
                    modifier = Modifier.weight(1f)
                )
                SciKey(
                    label = if (showInverseTrig) "acos" else "cos",
                    onClick = { onFunction(if (showInverseTrig) "acos(" else "cos(") },
                    testTag = "key_cos",
                    modifier = Modifier.weight(1f)
                )
                SciKey(
                    label = if (showInverseTrig) "atan" else "tan",
                    onClick = { onFunction(if (showInverseTrig) "atan(" else "tan(") },
                    testTag = "key_tan",
                    modifier = Modifier.weight(1f)
                )
                SciKey(
                    label = "x²",
                    onClick = onSquare,
                    testTag = "key_square",
                    modifier = Modifier.weight(1f)
                )
            }

            // Scientific Function Row 2
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.82f),
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                SciKey("(", { onToken("(") }, "key_lparen", Modifier.weight(1f))
                SciKey(")", { onToken(")") }, "key_rparen", Modifier.weight(1f))
                SciKey("√", { onFunction("√(") }, "key_sqrt", Modifier.weight(1f))
                SciKey("log", { onFunction("log(") }, "key_log", Modifier.weight(1f))
                SciKey("ln", { onFunction("ln(") }, "key_ln", Modifier.weight(1f))
            }

            // Row 3: C, ^, %, ÷
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                CalcKey(
                    label = "AC",
                    containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.14f),
                    contentColor = MaterialTheme.colorScheme.error,
                    onClick = onClear,
                    testTag = "key_clear",
                    modifier = Modifier.weight(1f)
                )
                CalcKey(
                    label = "^",
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    onClick = { onToken("^") },
                    testTag = "key_pow",
                    modifier = Modifier.weight(1f)
                )
                CalcKey(
                    label = "%",
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    onClick = { onToken("%") },
                    testTag = "key_percent",
                    modifier = Modifier.weight(1f)
                )
                CalcKey(
                    label = "÷",
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    onClick = { onToken("÷") },
                    testTag = "key_div",
                    modifier = Modifier.weight(1f)
                )
            }

            // Row 4: 7 8 9 ×
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                NumKey("7", onToken, Modifier.weight(1f))
                NumKey("8", onToken, Modifier.weight(1f))
                NumKey("9", onToken, Modifier.weight(1f))
                CalcKey(
                    label = "×",
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    onClick = { onToken("×") },
                    testTag = "key_mul",
                    modifier = Modifier.weight(1f)
                )
            }

            // Row 5: 4 5 6 -
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                NumKey("4", onToken, Modifier.weight(1f))
                NumKey("5", onToken, Modifier.weight(1f))
                NumKey("6", onToken, Modifier.weight(1f))
                CalcKey(
                    label = "-",
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    onClick = { onToken("-") },
                    testTag = "key_sub",
                    modifier = Modifier.weight(1f)
                )
            }

            // Row 6: 1 2 3 +
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                NumKey("1", onToken, Modifier.weight(1f))
                NumKey("2", onToken, Modifier.weight(1f))
                NumKey("3", onToken, Modifier.weight(1f))
                CalcKey(
                    label = "+",
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    onClick = { onToken("+") },
                    testTag = "key_add",
                    modifier = Modifier.weight(1f)
                )
            }

            // Row 7: ANS, 0, ., ⌫, =
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                CalcKey(
                    label = "ANS",
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    onClick = { onToken("ANS") },
                    testTag = "key_ans",
                    fontSizeSp = 14,
                    modifier = Modifier.weight(1f)
                )
                NumKey("0", onToken, Modifier.weight(1f))
                NumKey(".", onToken, Modifier.weight(1f))
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable(onClick = onBackspace)
                        .testTag("key_backspace")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.Backspace,
                            contentDescription = stringResource(R.string.cd_backspace),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                CalcKey(
                    label = "=",
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                    onClick = onEquals,
                    testTag = "key_equals",
                    modifier = Modifier.weight(1.1f)
                )
            }
        }
    }
}

@Composable
private fun SciKey(
    label: String,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier,
    isActive: Boolean = false
) {
    val bg = if (isActive) {
        MaterialTheme.colorScheme.secondaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.72f)
    }
    val fg = if (isActive) {
        MaterialTheme.colorScheme.onSecondaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = bg,
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontFamily = JetBrainsMonoFontFamily,
                    fontWeight = FontWeight.SemiBold
                ),
                color = fg
            )
        }
    }
}

@Composable
private fun NumKey(
    digit: String,
    onToken: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    CalcKey(
        label = digit,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        onClick = { onToken(digit) },
        testTag = "key_$digit",
        modifier = modifier
    )
}

@Composable
private fun CalcKey(
    label: String,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier,
    fontSizeSp: Int = 20
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = containerColor,
        tonalElevation = 1.dp,
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = fontSizeSp.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = contentColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun HistorySidePanel(
    history: List<CalcHistoryItem>,
    onSelect: (CalcHistoryItem) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
        modifier = modifier
    ) {
        HistorySheetContent(history = history, onSelect = onSelect, onClear = onClear)
    }
}

@Composable
private fun HistorySheetContent(
    history: List<CalcHistoryItem>,
    onSelect: (CalcHistoryItem) -> Unit,
    onClear: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "تاریخچه محاسبات",
                style = MaterialTheme.typography.titleMedium
            )
            if (history.isNotEmpty()) {
                IconButton(
                    onClick = onClear,
                    modifier = Modifier.testTag("clear_history_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.DeleteSweep,
                        contentDescription = stringResource(R.string.cd_clear_history)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        if (history.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "هنوز محاسبه‌ای ثبت نشده است.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.heightIn(max = 360.dp)
            ) {
                items(history, key = { it.id }) { item ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onSelect(item) }
                    ) {
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(
                                    text = item.expression,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontFamily = JetBrainsMonoFontFamily),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "= ${item.result}",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontFamily = JetBrainsMonoFontFamily,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
