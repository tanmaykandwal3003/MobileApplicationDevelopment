package com.example.currencycalc.ui

import android.util.Log
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val CurrencyOptions = listOf("INR", "USD", "EUR", "JPY")

private object CurrencyConverterDimens {
    val ScreenPadding = 16.dp
    val SpacerSmall = 8.dp
    val SpacerMedium = 12.dp
    val SpacerLarge = 16.dp
    val ButtonCornerRadius = 8.dp
}

private object CurrencyConverterTypography {
    val TitleSize = 24.sp
    val TitleWeight = FontWeight.Bold
    val ResultSize = 18.sp
    val ResultWeight = FontWeight.Medium
}

private const val LogTag = "CurrencyConverterScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyConverterScreen(modifier: Modifier = Modifier) {
    var amountInput by remember { mutableStateOf("") }
    var fromCurrency by remember { mutableStateOf(CurrencyOptions.first()) }
    var toCurrency by remember { mutableStateOf(CurrencyOptions[1]) }
    var fromMenuExpanded by remember { mutableStateOf(false) }
    var toMenuExpanded by remember { mutableStateOf(false) }
    var resultValue by remember { mutableStateOf("--") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(CurrencyConverterDimens.ScreenPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Currency Converter",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = CurrencyConverterTypography.TitleSize,
                fontWeight = CurrencyConverterTypography.TitleWeight
            ),
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(CurrencyConverterDimens.SpacerLarge))

        OutlinedTextField(
            value = amountInput,
            onValueChange = { amountInput = it },
            label = { Text("Enter Amount") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(CurrencyConverterDimens.SpacerMedium))

        CurrencyDropdownField(
            label = "From Currency",
            selected = fromCurrency,
            options = CurrencyOptions,
            expanded = fromMenuExpanded,
            onExpandedChange = { fromMenuExpanded = it },
            onSelected = { fromCurrency = it },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(CurrencyConverterDimens.SpacerSmall))

        CurrencyDropdownField(
            label = "To Currency",
            selected = toCurrency,
            options = CurrencyOptions,
            expanded = toMenuExpanded,
            onExpandedChange = { toMenuExpanded = it },
            onSelected = { toCurrency = it },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(CurrencyConverterDimens.SpacerLarge))

        Button(
            onClick = { Log.d(LogTag, "Convert tapped") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(CurrencyConverterDimens.ButtonCornerRadius)
        ) {
            Text("Convert")
        }

        Spacer(modifier = Modifier.height(CurrencyConverterDimens.SpacerMedium))

        Text(
            text = "Result: $resultValue",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = CurrencyConverterTypography.ResultSize,
                fontWeight = CurrencyConverterTypography.ResultWeight
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
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

@Preview(showBackground = true)
@Composable
private fun CurrencyConverterScreenPreview() {
    MaterialTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            CurrencyConverterScreen()
        }
    }
}
