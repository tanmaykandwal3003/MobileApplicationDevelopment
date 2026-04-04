package com.example.currencycalc.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.currencycalc.R
import com.example.currencycalc.data.ExchangeRates
import com.example.currencycalc.ui.theme.CurrencyCalcTheme
import com.example.currencycalc.viewmodel.CurrencyConverterUiState
import com.example.currencycalc.viewmodel.CurrencyViewModel

private object ConverterDimens {
    val ScreenPadding = 16.dp
    val CardPadding = 16.dp
    val SectionSpacing = 14.dp
    val CardCornerRadius = 14.dp
    val CardElevation = 8.dp
    val ButtonCornerRadius = 8.dp
    val IconSpacing = 8.dp
    val ResultFontSize = 20.sp
    val DisclaimerSpacing = 6.dp
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyConverterScreen(
    viewModel: CurrencyViewModel,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var fromMenuExpanded by remember { mutableStateOf(false) }
    var toMenuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.currency_converter_title)) },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = stringResource(R.string.content_desc_settings)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(ConverterDimens.ScreenPadding)
        ) {
            CurrencyConverterCard(
                uiState = uiState,
                fromMenuExpanded = fromMenuExpanded,
                toMenuExpanded = toMenuExpanded,
                onFromMenuExpandedChange = { fromMenuExpanded = it },
                onToMenuExpandedChange = { toMenuExpanded = it },
                onAmountChange = viewModel::onAmountChange,
                onFromCurrencyChange = viewModel::onFromCurrencyChange,
                onToCurrencyChange = viewModel::onToCurrencyChange,
                onConvert = viewModel::convert
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CurrencyConverterCard(
    uiState: CurrencyConverterUiState,
    fromMenuExpanded: Boolean,
    toMenuExpanded: Boolean,
    onFromMenuExpandedChange: (Boolean) -> Unit,
    onToMenuExpandedChange: (Boolean) -> Unit,
    onAmountChange: (String) -> Unit,
    onFromCurrencyChange: (String) -> Unit,
    onToCurrencyChange: (String) -> Unit,
    onConvert: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(ConverterDimens.CardCornerRadius),
        elevation = CardDefaults.cardElevation(defaultElevation = ConverterDimens.CardElevation),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(ConverterDimens.CardPadding)
        ) {
            OutlinedTextField(
                value = uiState.amount,
                onValueChange = onAmountChange,
                label = { Text(stringResource(R.string.amount_label)) },
                placeholder = { Text(stringResource(R.string.amount_placeholder)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.AttachMoney,
                        contentDescription = stringResource(R.string.content_desc_amount_icon)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            Spacer(modifier = Modifier.height(ConverterDimens.SectionSpacing))

            CurrencyDropdownField(
                label = stringResource(R.string.from_currency_label),
                selected = uiState.fromCurrency,
                options = ExchangeRates.supportedCodes,
                expanded = fromMenuExpanded,
                onExpandedChange = onFromMenuExpandedChange,
                onSelected = onFromCurrencyChange,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(ConverterDimens.SectionSpacing))

            CurrencyDropdownField(
                label = stringResource(R.string.to_currency_label),
                selected = uiState.toCurrency,
                options = ExchangeRates.supportedCodes,
                expanded = toMenuExpanded,
                onExpandedChange = onToMenuExpandedChange,
                onSelected = onToCurrencyChange,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(ConverterDimens.SectionSpacing))

            Button(
                onClick = onConvert,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(ConverterDimens.ButtonCornerRadius)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.SwapHoriz,
                        contentDescription = stringResource(R.string.content_desc_convert)
                    )
                    Spacer(modifier = Modifier.width(ConverterDimens.IconSpacing))
                    Text(stringResource(R.string.convert_button))
                }
            }

            Spacer(modifier = Modifier.height(ConverterDimens.SectionSpacing))

            Text(
                text = stringResource(R.string.result_line, uiState.result),
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = ConverterDimens.ResultFontSize,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(ConverterDimens.DisclaimerSpacing))

            Text(
                text = stringResource(R.string.rates_disclaimer),
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CurrencyDropdownField(
    label: String,
    selected: String,
    options: List<String>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                .fillMaxWidth(),
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelected(option)
                        onExpandedChange(false)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun CurrencyConverterCardPreview() {
    CurrencyCalcTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(Modifier.padding(ConverterDimens.ScreenPadding)) {
                CurrencyConverterCard(
                    uiState = CurrencyConverterUiState(
                        amount = "100",
                        fromCurrency = ExchangeRates.CODE_USD,
                        toCurrency = ExchangeRates.CODE_EUR,
                        result = "92.22"
                    ),
                    fromMenuExpanded = false,
                    toMenuExpanded = false,
                    onFromMenuExpandedChange = {},
                    onToMenuExpandedChange = {},
                    onAmountChange = {},
                    onFromCurrencyChange = {},
                    onToCurrencyChange = {},
                    onConvert = {}
                )
            }
        }
    }
}
