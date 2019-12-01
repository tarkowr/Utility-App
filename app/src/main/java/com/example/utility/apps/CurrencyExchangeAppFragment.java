package com.example.utility.apps;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.example.utility.R;
import com.example.utility.helpers.JavaUtils;

import java.text.DecimalFormat;

public class CurrencyExchangeAppFragment extends Fragment {

    final String usd = "USD";
    final String gbp = "GBP";

    boolean usdToGbp = true;
    double conversionRate = 1.23;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_currency_exchange_app, container, false);

        Button quote = view.findViewById(R.id.quoteBtn);
        Switch currencySwitch = view.findViewById(R.id.currencySwitch);

        quote.setOnClickListener(getQuote);
        currencySwitch.setOnClickListener(switchCurrency);

        return view;
    }

    /*
     Quote Button onClick event - Performs input validation
     */
    View.OnClickListener getQuote = new View.OnClickListener() {
        public void onClick(View view)
        {
            view = getView();
            final EditText amount = view.findViewById(R.id.currencyAmountNum);
            final Double lowerLimit = 0.0;

            double amountDouble;

            String amountString = GetWidgetText(amount);

            if(CheckIfEmptyString(amountString)){
                JavaUtils.ShowToast(getActivity(), R.string.app_currency_exchange_invalid_amount);
                return;
            }

            amountDouble = JavaUtils.DoubleTryParse(amountString);

            if(amountDouble == 0){
                JavaUtils.ShowToast(getActivity(), R.string.app_currency_exchange_invalid_amount);
                return;
            }

            if(!ValidAmountLowerRange(amountDouble, lowerLimit)){
                JavaUtils.ShowToast(getActivity(), R.string.app_currency_exchange_invalid_amount);
                return;
            }

            DisplayResults(amountDouble, view);
        }
    };

    /*
    Switch onClick event - Toggles conversion direction
     */
    View.OnClickListener switchCurrency = new View.OnClickListener() {
        public void onClick(View view){
            view = getView();
            usdToGbp = !usdToGbp;

            SetUpperLowerCurrencyNames(view);
            String currentResult = ((TextView)view.findViewById(R.id.topConverted)).getText().toString();

            if(!CheckIfEmptyString(currentResult)){
                DisplayResults(JavaUtils.DoubleTryParse(currentResult), view);
            }
        }
    };

    /*
    Gets the calculated exchange value and displays the results in the UI
     */
    private void DisplayResults(double amount, View view){
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

        SetUpperLowerCurrencies(format.format(amount), format.format(result), view);
        ((TextView)view.findViewById(R.id.topConversionRate)).setText(topRate);
        ((TextView)view.findViewById(R.id.bottomConversionRate)).setText(bottomRate);
    }

    /*
    Sets a currency in the top and bottom slots in the UI
     */
    private void SetUpperLowerCurrencyNames(View view){
        final TextView topCurrencyName = view.findViewById(R.id.topCurrencyName);
        final TextView bottomCurrencyName = view.findViewById(R.id.bottomCurrencyName);

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
    private void SetUpperLowerCurrencies(String upper, String lower, View view){
        final TextView topResult = view.findViewById(R.id.topConverted);
        final TextView bottomResult = view.findViewById(R.id.bottomConverted);

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
