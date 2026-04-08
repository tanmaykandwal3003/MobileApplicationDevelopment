package com.example.currencyexchanger;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText amountInput;
    private Spinner fromCurrencySpinner;
    private Spinner toCurrencySpinner;
    private TextView resultText;
    private static final String[] CURRENCIES = {"INR", "USD", "JPY", "EUR"};
    private static final Map<String, Double> INR_BASE_RATES = new HashMap<>();

    static {
        INR_BASE_RATES.put("INR", 1.0);
        INR_BASE_RATES.put("USD", 83.50);
        INR_BASE_RATES.put("JPY", 0.56);
        INR_BASE_RATES.put("EUR", 90.75);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applySavedTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        amountInput = findViewById(R.id.etAmount);
        fromCurrencySpinner = findViewById(R.id.spinnerFromCurrency);
        toCurrencySpinner = findViewById(R.id.spinnerToCurrency);
        Button convertButton = findViewById(R.id.btnConvert);
        ImageButton settingsButton = findViewById(R.id.btnSettings);
        resultText = findViewById(R.id.tvResultValue);

        setupCurrencySpinners();
        fromCurrencySpinner.setSelection(0);
        toCurrencySpinner.setSelection(1);

        convertButton.setOnClickListener(v -> performConversion());
        settingsButton.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
    }

    private void setupCurrencySpinners() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item,
                CURRENCIES
        );
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        fromCurrencySpinner.setAdapter(adapter);
        toCurrencySpinner.setAdapter(adapter);
    }

    private void performConversion() {
        String amountString = amountInput.getText().toString().trim();
        if (amountString.isEmpty()) {
            resultText.setText(getString(R.string.result_default));
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountString);
        } catch (NumberFormatException e) {
            resultText.setText(getString(R.string.result_default));
            return;
        }

        String fromCurrency = fromCurrencySpinner.getSelectedItem().toString();
        String toCurrency = toCurrencySpinner.getSelectedItem().toString();
        double convertedAmount = convert(amount, fromCurrency, toCurrency);
        resultText.setText(getString(R.string.result_format, formatResult(convertedAmount)));
    }

    private double convert(double amount, String fromCurrency, String toCurrency) {
        double fromRate = INR_BASE_RATES.get(fromCurrency);
        double toRate = INR_BASE_RATES.get(toCurrency);
        return amount * (fromRate / toRate);
    }

    private String formatResult(double value) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat formatter = new DecimalFormat("#,##0.00", symbols);
        return formatter.format(value);
    }
}