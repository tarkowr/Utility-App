package com.example.utility.apps;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.utility.R;
import com.example.utility.helpers.JavaUtils;

import java.text.DecimalFormat;

public class CurrencyExchangeAppActivity extends AppCompatActivity {

    final String usd = "USD";
    final String gbp = "GBP";

    boolean usdToGbp = true;
    double conversionRate = 1.23;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_exchange_app);
    }

    /*
 Quote Button onClick event - Performs input validation
  */
    public void getQuote(View view){
        final EditText amount = findViewById(R.id.currencyAmountNum);
        final Double lowerLimit = 0.0;

        double amountDouble;

        String amountString = GetWidgetText(amount);

        if(CheckIfEmptyString(amountString)){
            JavaUtils.ShowToast(CurrencyExchangeAppActivity.this, R.string.app_currency_exchange_invalid_amount);
            return;
        }

        amountDouble = JavaUtils.DoubleTryParse(amountString);

        if(amountDouble == 0){
            JavaUtils.ShowToast(CurrencyExchangeAppActivity.this, R.string.app_currency_exchange_invalid_amount);
            return;
        }

        if(!ValidAmountLowerRange(amountDouble, lowerLimit)){
            JavaUtils.ShowToast(CurrencyExchangeAppActivity.this, R.string.app_currency_exchange_invalid_amount);
            return;
        }

        DisplayResults(amountDouble);
    }

    /*
    Switch onClick event - Toggles conversion direction
     */
    public void switchCurrency(View view){
        usdToGbp = !usdToGbp;

        SetUpperLowerCurrencyNames();
        String currentResult = ((TextView)findViewById(R.id.topConverted)).getText().toString();

        if(!CheckIfEmptyString(currentResult)){
            DisplayResults(JavaUtils.DoubleTryParse(currentResult));
        }
    }

    /*
    Gets the calculated exchange value and displays the results in the UI
     */
    private void DisplayResults(double amount){
        DecimalFormat format = new DecimalFormat("#.##");
        String topRate, bottomRate, a, b;
        Double result;

        a = ReadConversion(gbp, usd, conversionRate);
        b = ReadConversion(usd, gbp, Math.round(1/conversionRate * 100.0)/100.0);

        if(usdToGbp){
            topRate = b;
            bottomRate = a;
        }
        else{
            topRate = a;
            bottomRate = b;
        }

        result = Convert(usdToGbp, amount);

        SetUpperLowerCurrencies(format.format(amount), format.format(result));
        ((TextView)findViewById(R.id.topConversionRate)).setText(topRate);
        ((TextView)findViewById(R.id.bottomConversionRate)).setText(bottomRate);
    }

    /*
    Sets a currency in the top and bottom slots in the UI
     */
    private void SetUpperLowerCurrencyNames(){
        final TextView topCurrencyName = findViewById(R.id.topCurrencyName);
        final TextView bottomCurrencyName = findViewById(R.id.bottomCurrencyName);

        if(usdToGbp){
            topCurrencyName.setText(usd);
            bottomCurrencyName.setText(gbp);
        }
        else{
            topCurrencyName.setText(gbp);
            bottomCurrencyName.setText(usd);
        }
    }

    /*
    Sets the exchange values in the UI
     */
    private void SetUpperLowerCurrencies(String upper, String lower){
        final TextView topResult = findViewById(R.id.topConverted);
        final TextView bottomResult = findViewById(R.id.bottomConverted);

        topResult.setText(upper);
        bottomResult.setText(lower);
    }

    /*
    Determines whether a string is empty
     */
    private boolean CheckIfEmptyString(String str){
        return str == null || str == "";
    }

    /*
    Converts a currency to another with the conversion rate
     */
    private double Convert(boolean usdToGbp, double amount){
        if(usdToGbp){
            return amount/conversionRate;
        }
        else{
            return amount * conversionRate;
        }
    }

    /*
    Checks if the user's amount input is above a minimum value
     */
    private boolean ValidAmountLowerRange(double amount, double lowerRange){
        return amount > lowerRange;
    }

    /*
    Retrieves the text from a EditText widget
     */
    private String GetWidgetText(EditText widget){
        try{
            return widget.getText().toString();
        } catch (Exception e){
            return null;
        }
    }

    /*
    Format the conversion rate string
     */
    public static String ReadConversion(String a, String b, Double rate){
        return "1 " + a + " = " + rate + " " + b;
    }
}
