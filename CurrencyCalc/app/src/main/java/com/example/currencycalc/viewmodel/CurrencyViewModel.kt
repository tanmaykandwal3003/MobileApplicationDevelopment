package com.example.currencycalc.viewmodel

import androidx.lifecycle.ViewModel
import com.example.currencycalc.data.ConversionMessages
import com.example.currencycalc.data.ExchangeRates
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

data class CurrencyConverterUiState(
    val amount: String = "",
    val fromCurrency: String = ExchangeRates.CODE_INR,
    val toCurrency: String = ExchangeRates.CODE_USD,
    val result: String = "--"
)

class CurrencyViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        CurrencyConverterUiState(
            fromCurrency = ExchangeRates.supportedCodes.first(),
            toCurrency = ExchangeRates.supportedCodes[1]
        )
    )
    val uiState: StateFlow<CurrencyConverterUiState> = _uiState.asStateFlow()

    private val resultFormatter = DecimalFormat("#,##0.00", DecimalFormatSymbols(Locale.US)).apply {
        roundingMode = RoundingMode.HALF_UP
        maximumFractionDigits = 2
        minimumFractionDigits = 2
    }

    fun onAmountChange(value: String) {
        _uiState.update { it.copy(amount = value) }
    }

    fun onFromCurrencyChange(code: String) {
        _uiState.update { it.copy(fromCurrency = code) }
    }

    fun onToCurrencyChange(code: String) {
        _uiState.update { it.copy(toCurrency = code) }
    }

    fun convert() {
        val raw = _uiState.value.amount.trim()
        if (raw.isEmpty()) {
            _uiState.update { it.copy(result = ConversionMessages.INVALID_INPUT) }
            return
        }

        val amount = raw.toDoubleOrNull()
        if (amount == null) {
            _uiState.update { it.copy(result = ConversionMessages.INVALID_INPUT) }
            return
        }

        val state = _uiState.value
        val rates = ExchangeRates.rates
        val fromRate = rates[state.fromCurrency]
        val toRate = rates[state.toCurrency]

        if (fromRate == null || toRate == null || toRate == 0.0) {
            _uiState.update { it.copy(result = ConversionMessages.INVALID_INPUT) }
            return
        }

        val amountInInr = amount * fromRate
        val converted = amountInInr / toRate
        _uiState.update { it.copy(result = resultFormatter.format(converted)) }
    }
}
