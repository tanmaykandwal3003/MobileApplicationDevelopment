package com.example.currencycalc.data

/**
 * Fixed rates: INR value of one unit of each currency (INR is base). March 2026 approximate values.
 */
object ExchangeRates {

    const val CODE_INR = "INR"
    const val CODE_USD = "USD"
    const val CODE_EUR = "EUR"
    const val CODE_JPY = "JPY"

    val rates: Map<String, Double> = mapOf(
        CODE_INR to 1.0,
        CODE_USD to 92.4,
        CODE_EUR to 107.5,
        CODE_JPY to 0.58
    )

    val supportedCodes: List<String> = listOf(CODE_INR, CODE_USD, CODE_EUR, CODE_JPY)

    fun inrPerUnit(currencyCode: String): Double? = rates[currencyCode]
}
