package com.example.utility.apps;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.utility.R;
import com.example.utility.helpers.HttpHelper;
import com.example.utility.helpers.JavaUtils;
import com.example.utility.models.ExchangeRate;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

public class CurrencyExchangeAppFragment extends Fragment {
    private ProgressBar progressBar;
    private TextView txtStatus;
    private EditText editAmount;
    private Button quoteBtn;

    private ExchangeRate exchangeRate;
    private double amount;
    private double conversionRate;
    private String currencyTop = "USD";
    private String currencyBottom = "GBP";
    private Timer timer;
    private Boolean useCachedResults;

    private final String API_URL = "https://api.exchangeratesapi.io/latest?base=";
    private final Integer MAX_DELAY = 1000;
    private final Integer REFRESH_DELAY = 10000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        amount = 0;
        conversionRate = 0;
        useCachedResults = false;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_currency_exchange_app, container, false);

        editAmount = view.findViewById(R.id.currencyAmountNum);
        txtStatus = view.findViewById(R.id.txtStatus);

        quoteBtn = view.findViewById(R.id.quoteBtn);
        quoteBtn.setOnClickListener(getQuote);

        progressBar = view.findViewById(R.id.progressBarCurrency);
        progressBar.setVisibility(View.INVISIBLE);

        SetUpperLowerCurrencyNames(view);

        return view;
    }

    /*
     Quote Button onClick event - Performs input validation
     */
    View.OnClickListener getQuote = new View.OnClickListener() {
        public void onClick(View view) // use getView() if view is needed in this event
        {
            final Double LOWER_LIMIT = 0.0;
            final Double UPPER_LIMIT = 1000000000.0;

            String amountString = JavaUtils.GetWidgetText(editAmount);
            txtStatus.setText(null);

            if(JavaUtils.CheckIfEmptyString(amountString)){
                txtStatus.setText(R.string.app_currency_exchange_invalid_amount);
                return;
            }

            amount = JavaUtils.DoubleTryParse(amountString);

            if(!JavaUtils.ValidRange(amount, LOWER_LIMIT, UPPER_LIMIT)){
                txtStatus.setText(R.string.app_currency_exchange_invalid_amount);
                return;
            }

            quoteBtn.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);

            new GetExchangeRateAsync().execute();
        }
    };

    /*
    Sets a currency in the top and bottom slots in the UI
     */
    private void SetUpperLowerCurrencyNames(View view){
        final TextView topCurrencyName = view.findViewById(R.id.topCurrencyName);
        final TextView bottomCurrencyName = view.findViewById(R.id.bottomCurrencyName);

        topCurrencyName.setText(currencyTop);
        bottomCurrencyName.setText(currencyBottom);
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
    Sets the conversation rate values in the UI
     */
    private void SetUpperLowerConversion(String topConversion, String bottomConversion, View view){
        TextView topConversionRate = view.findViewById(R.id.topConversionRate);
        TextView bottomConversionRate = view.findViewById(R.id.bottomConversionRate);

        topConversionRate.setText(topConversion);
        bottomConversionRate.setText(bottomConversion);
    }

    /*
    Converts a currency to another with the conversion rate
     */
    private double Convert(double amount){
        return amount * conversionRate;
    }

    /*
    Format the conversion rate string
     */
    private String ReadConversion(String a, String b, Double rate){
        return "1 " + a + " = " + rate + " " + b;
    }

    /*
    Concatenates the base currency to the API URL as a query parameter
     */
    private String formatUrl(String base){
        return API_URL + base.toUpperCase();
    }

    /*
    Using the https://exchangeratesapi.io/ API
     */
    private StringBuffer getExchangeRate(String base){
        StringBuffer httpResults;

        try{
            HttpHelper httpHelper = new HttpHelper(formatUrl(base));
            httpResults = httpHelper.Get();
        }
        catch (IOException exception){
            Log.d("ERROR", exception.getMessage());
            httpResults = null;
        }

        JavaUtils.pauseUI(JavaUtils.returnRandomInt(MAX_DELAY));

        return httpResults;
    }

    /*
    Using Jackson https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-core to parse the json String into a Java Object
    Referenced https://www.codexpedia.com/java/jackson-parser-example-in-android/ to learn how to convert json to an object
     */
    private ExchangeRate parseResults(String json){
        ObjectMapper mapper = new ObjectMapper();

        try{
            exchangeRate = mapper.readValue(json, ExchangeRate.class);
        }
        catch (Exception ex){
            Log.d("ERROR", ex.getMessage());
            exchangeRate = null;
        }

        return exchangeRate;
    }

    /*
    Gets the calculated exchange value and displays the results in the UI
     */
    private void DisplayResults(){
        final View view = getView();
        final DecimalFormat format = new DecimalFormat("#.##");

        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                String topRate, bottomRate;
                Double result;

                conversionRate = exchangeRate.getRateBySymbol(currencyBottom);
                conversionRate = Math.round(conversionRate * 100.0) / 100.0;

                topRate = ReadConversion(currencyTop, currencyBottom, conversionRate);
                bottomRate = ReadConversion(currencyBottom, currencyTop, Math.round(1/conversionRate * 100.0)/100.0);

                result = Convert(amount);

                SetUpperLowerCurrencies(format.format(amount), format.format(result), view);
                SetUpperLowerConversion(topRate, bottomRate, view);

                quoteBtn.setEnabled(true);
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void DisplayApiError(){
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                txtStatus.setText(R.string.app_currency_exchange_api_error);
            }
        });
    }

    /*
    Learned how to run asynchronous tasks from https://developer.android.com/reference/android/os/AsyncTask
     */
    private class GetExchangeRateAsync extends AsyncTask<String, String, ExchangeRate> {

        @Override
        protected ExchangeRate doInBackground(String... args) {
            if(!useCachedResults || exchangeRate.rates.isEmpty()){
                StringBuffer json = getExchangeRate(currencyTop);

                if(json != null){
                    exchangeRate = parseResults(json.toString());

                    useCachedResults = true;
                    timer = new Timer();
                    timer.schedule(new CustomTimerTask(), REFRESH_DELAY);

                    return exchangeRate;
                }
                else{
                    DisplayApiError();
                    return null;
                }
            }
            else{
                return exchangeRate;
            }
        }

        @Override
        protected void onProgressUpdate(String... progress) { }

        @Override
        protected void onPostExecute(ExchangeRate _exchangeRate) {
            if(_exchangeRate != null){
                DisplayResults();
            }
            else{
                DisplayApiError();
            }
        }
    }

    private class CustomTimerTask extends TimerTask {

        @Override
        public void run() {
            useCachedResults = false;
            cancel();
            timer.cancel();
            timer.purge();
        }
    }
}
