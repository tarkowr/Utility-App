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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.utility.R;
import com.example.utility.helpers.HttpHelper;
import com.example.utility.helpers.JavaUtils;
import com.example.utility.models.ExchangeRate;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class CurrencyExchangeAppFragment extends Fragment {
    private ProgressBar progressBar;
    private TextView txtStatus;
    private EditText editAmount;
    private Button quoteBtn;
    private Spinner topCurrencySpinner;
    private Spinner bottomCurrencySpinner;

    private ExchangeRate exchangeRate;
    private String[] availableCurrencies;
    private double amount;
    private double conversionRate;
    private String currencyTop; // Base Currency
    private String currencyBottom;
    private Timer timer;
    private Map<String, Boolean> useCachedResults;
    private Map<String, ExchangeRate> exchangeRates;

    private final String APP_TAG = "CURRENCY_EXCHANGE_APP";
    private final String API_URL_RATES = "https://api.exchangeratesapi.io/latest?base=";
    private final String API_URL_CURRENCIES = "https://api.exchangeratesapi.io/latest";
    private final Integer MAX_DELAY = 1000;
    private final Integer REFRESH_DELAY = 60000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        amount = 0;
        conversionRate = 0;
        timer = new Timer();
        useCachedResults = new HashMap<>();
        exchangeRates = new HashMap<>();
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

        InitializeSpinners(view);

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

            currencyTop = topCurrencySpinner.getSelectedItem().toString();
            currencyBottom = bottomCurrencySpinner.getSelectedItem().toString();

            String amountString = JavaUtils.GetWidgetText(editAmount);
            txtStatus.setText(null);

            if(currencyTop.isEmpty() || currencyBottom.isEmpty()){
                txtStatus.setText(R.string.app_currency_exchange_invalid_currency);
                return;
            }

            if(currencyTop.equals(currencyBottom)){
                txtStatus.setText(R.string.app_currency_exchange_same_currency);
                return;
            }

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

            if(!useCachedResults.get(currencyTop)){
                progressBar.setVisibility(View.VISIBLE);
            }

            new GetExchangeRateAsync().execute();
        }
    };

    private void InitializeSpinners(View view){
        topCurrencySpinner = view.findViewById(R.id.topCurrencySpinner);
        bottomCurrencySpinner = view.findViewById(R.id.bottomCurrencySpinner);

        quoteBtn.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        new GetCurrenciesAsync().execute();
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
    private String getRateUrl(String base){
        return API_URL_RATES + base.toUpperCase();
    }

    private String getCurrencyUrl(){
        return API_URL_CURRENCIES;
    }

    private String[] extractCurrencies(ExchangeRate _exchangeRate){
        int length = _exchangeRate.rates.size() + 1;
        int index = 0;

        String baseCurr = _exchangeRate.base;
        String[] currencies = new String[length];

        try{
            for(String key : _exchangeRate.rates.keySet()){
                currencies[index] = key;
                useCachedResults.put(key, false);
                index++;
            }

            currencies[index] = baseCurr;
            useCachedResults.put(baseCurr, false);
        }
        catch (Exception ex){
            Log.d(APP_TAG, Objects.requireNonNull(ex.getMessage()));
        }

        Arrays.sort(currencies);
        return currencies;
    }

    /*
    Using the https://exchangeratesapi.io/ API
     */
    private StringBuffer getExchangeRate(String url){
        StringBuffer httpResults;

        try{
            HttpHelper httpHelper = new HttpHelper(url);
            httpResults = httpHelper.Get();
        }
        catch (IOException ex){
            Log.d(APP_TAG, Objects.requireNonNull(ex.getMessage()));
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
        ExchangeRate _exchangeRate;

        try{
            _exchangeRate = mapper.readValue(json, ExchangeRate.class);
        }
        catch (Exception ex){
            Log.d(APP_TAG, Objects.requireNonNull(ex.getMessage()));
            _exchangeRate = null;
        }

        return _exchangeRate;
    }

    /*
    Gets the calculated exchange value and displays the results in the UI
     */
    private void DisplayResults(){
        final View view = getView();
        final DecimalFormat format = new DecimalFormat("#,###.##");

        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                String topRate, bottomRate;
                Double result, conversionRateRounded;

                conversionRate = exchangeRate.getRateBySymbol(currencyBottom);
                conversionRateRounded = Math.round(conversionRate * 100.0) / 100.0;

                topRate = ReadConversion(currencyTop, currencyBottom, conversionRateRounded);
                bottomRate = ReadConversion(currencyBottom, currencyTop, Math.round(1/conversionRate * 100.0)/100.0);

                result = Convert(amount);

                SetUpperLowerCurrencies(format.format(amount), format.format(result), view);
                SetUpperLowerConversion(topRate, bottomRate, view);

                quoteBtn.setEnabled(true);
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void DisplayCurrencyOptions(){
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, availableCurrencies);

                topCurrencySpinner.setAdapter(adapter);
                bottomCurrencySpinner.setAdapter(adapter);

                quoteBtn.setEnabled(true);
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void DisplayApiError(){
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                quoteBtn.setEnabled(true);
                progressBar.setVisibility(View.INVISIBLE);
                txtStatus.setText(R.string.app_currency_exchange_api_error);
            }
        });
    }

    /*
Learned how to run asynchronous tasks from https://developer.android.com/reference/android/os/AsyncTask
 */
    private class GetCurrenciesAsync extends AsyncTask<String, String, String[]> {

        @Override
        protected String[] doInBackground(String... args) {
            String url = getCurrencyUrl();
            StringBuffer json = getExchangeRate(url);

            if(json != null){
                ExchangeRate temp = parseResults(json.toString());
                availableCurrencies = extractCurrencies(temp);

                return availableCurrencies;
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... progress) { }

        @Override
        protected void onPostExecute(String[] currencies) {
            if(currencies != null){
                DisplayCurrencyOptions();
            }
            else{
                DisplayApiError();
            }
        }
    }

    /*
    Learned how to run asynchronous tasks from https://developer.android.com/reference/android/os/AsyncTask
     */
    private class GetExchangeRateAsync extends AsyncTask<String, String, ExchangeRate> {

        @Override
        protected ExchangeRate doInBackground(String... args) {
            String baseCurr = currencyTop;

            if(!useCachedResults.get(baseCurr) || exchangeRates.isEmpty()){
                String url = getRateUrl(currencyTop);
                StringBuffer json = getExchangeRate(url);

                if(json != null){
                    exchangeRate = parseResults(json.toString());

                    exchangeRates.put(baseCurr, exchangeRate);
                    useCachedResults.put(baseCurr, true);

                    timer.schedule(new CustomTimerTask(baseCurr), REFRESH_DELAY);
                }
                else{
                    return null;
                }
            }
            else{
                exchangeRate = exchangeRates.get(baseCurr);
            }

            return exchangeRate;
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

        private String key;

        public CustomTimerTask(String _key){
            super();
            key = _key;
        }

        @Override
        public void run() {
            useCachedResults.put(key, false);
            exchangeRates.remove(key);
            cancel();
        }
    }
}
