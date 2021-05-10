package com.rt.utility.apps;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.rt.utility.R;
import com.rt.utility.helpers.HttpHelper;
import com.rt.utility.helpers.JavaUtils;
import com.rt.utility.models.ExchangeRate;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class CurrencyExchangeAppFragment extends Fragment {
    private ProgressBar progressBar;
    private EditText editAmount;
    private Spinner topCurrencySpinner;
    private Spinner bottomCurrencySpinner;

    private ExchangeRate exchangeRate;
    private String[] availableCurrencies;
    private double amount;
    private double conversionRate;
    private String currencyTop; // Base Currency
    private String currencyBottom;

    private final String APP_TAG = "CURRENCY_EXCHANGE_APP";
    private final String ACCESS_KEY = "b4e1bde979cfa85f8468317e5143ac65";
    private final String CURRENCIES_API = "http://api.exchangeratesapi.io/v1/latest?access_key=" + ACCESS_KEY;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        amount = 0;
        conversionRate = 0;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_currency_exchange_app, container, false);

        editAmount = view.findViewById(R.id.currencyAmountNum);
        editAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0) getQuote(s.toString());
            }
        });

        progressBar = view.findViewById(R.id.progressBarCurrency);

        topCurrencySpinner = view.findViewById(R.id.topCurrencySpinner);
        bottomCurrencySpinner = view.findViewById(R.id.bottomCurrencySpinner);

        progressBar.setVisibility(View.VISIBLE);

        new GetCurrenciesAsync(CurrencyExchangeAppFragment.this).execute();

        return view;
    }

    private void getQuote(String value) {
        final double LOWER_LIMIT = 0.0;
        final double UPPER_LIMIT = 1000000000.0;

        if(availableCurrencies == null || availableCurrencies.length == 0){
            DisplayApiError();
            return;
        }

        currencyTop = topCurrencySpinner.getSelectedItem().toString();
        currencyBottom = bottomCurrencySpinner.getSelectedItem().toString();

        if(currencyTop.isEmpty() || currencyBottom.isEmpty()){
            ShowErrorSnackbar(getString(R.string.app_currency_exchange_invalid_currency));
            return;
        }

        if(currencyTop.equals(currencyBottom)){
            ShowErrorSnackbar(getString(R.string.app_currency_exchange_same_currency));
            return;
        }

        if(JavaUtils.CheckIfEmptyString(value)){
            ShowErrorSnackbar(getString(R.string.app_currency_exchange_invalid_amount));
            return;
        }

        amount = JavaUtils.DoubleTryParse(value);

        if(!JavaUtils.ValidRange(amount, LOWER_LIMIT, UPPER_LIMIT)){
            ShowErrorSnackbar(getString(R.string.app_currency_exchange_invalid_amount));
            return;
        }

        DisplayResults();
    }

    private void ShowErrorSnackbar(String msg) {
        FragmentActivity activity = getActivity();

        if (activity == null) return;

        final View rootView = activity.findViewById(android.R.id.content);
        final int sbFontSize = 20;

        JavaUtils.ShowSnackbar(rootView, msg, sbFontSize);
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
    Extracts the abbreviation of each currency and appends to string array
     */
    private ArrayList<String> extractCurrencyNames(ExchangeRate _exchangeRate){
        int index = 0;

        ArrayList<String> currencies = new ArrayList<>();

        try{
            for(String key : _exchangeRate.rates.keySet()){
                if (key != null) {
                    currencies.add(index, key);
                }
                index++;
            }
        }
        catch (Exception ex){
            Log.d(APP_TAG, Objects.requireNonNull(ex.getMessage()));
        }

        if(!currencies.isEmpty()){
            try {
                Arrays.sort(currencies.toArray());
            }
            catch (Exception ex) {
                Log.d(APP_TAG,Objects.requireNonNull(ex.getMessage()));
            }
        }

        return currencies;
    }

    /*
    Fetch live currency data from https://exchangeratesapi.io/ API
     */
    private StringBuffer getExchangeRate(String url) {
        StringBuffer httpResults;

        try{
            HttpHelper httpHelper = new HttpHelper(url);
            httpResults = httpHelper.Get();
        }
        catch (IOException ex){
            Log.d(APP_TAG, Objects.requireNonNull(ex.getMessage()));
            httpResults = null;
        }

        return httpResults;
    }

    /*
    Using Jackson https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-core to parse the json String into a Java Object
    Learned how to convert json to an object from https://www.codexpedia.com/java/jackson-parser-example-in-android/
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
    Populates the currency spinner widgets with the currencies returned from the API
    */
    private void DisplayCurrencyOptions(){
        final FragmentActivity activity = getActivity();

        if(activity == null){
            return;
        }

        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(activity,
                        android.R.layout.simple_spinner_dropdown_item, availableCurrencies);

                topCurrencySpinner.setAdapter(adapter);
                bottomCurrencySpinner.setAdapter(adapter);

                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    /*
    Gets the calculated exchange value and displays the results in the UI
     */
    private void DisplayResults(){
        final View view = getView();

        if (view == null) return;

        final DecimalFormat format = new DecimalFormat("#,###.##");

        String topRate, bottomRate;
        Double result, conversionRateRounded;

        conversionRate = exchangeRate.getRateBySymbol(currencyTop, currencyBottom);
        conversionRateRounded = Math.round(conversionRate * 100.0) / 100.0;

        topRate = ReadConversion(currencyTop, currencyBottom, conversionRateRounded);
        bottomRate = ReadConversion(currencyBottom, currencyTop, Math.round(1/conversionRate * 100.0)/100.0);

        result = Convert(amount);

        SetUpperLowerCurrencies(format.format(amount), format.format(result), view);
        SetUpperLowerConversion(topRate, bottomRate, view);
    }

    /*
    Display an error if an API called return null or timed out
     */
    private void DisplayApiError(){
        final FragmentActivity activity = getActivity();

        if(activity == null){
            return;
        }

        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                progressBar.setVisibility(View.INVISIBLE);
                ShowErrorSnackbar(getString(R.string.app_currency_exchange_api_error));
            }
        });
    }

    /*
    Learned how to run asynchronous tasks from https://developer.android.com/reference/android/os/AsyncTask
     */
    private static class GetCurrenciesAsync extends AsyncTask<String, String, String[]> {

        private WeakReference<CurrencyExchangeAppFragment> ref;

        GetCurrenciesAsync(CurrencyExchangeAppFragment context){
            ref = new WeakReference<>(context);
        }

        @Override
        protected String[] doInBackground(String... args) {
            CurrencyExchangeAppFragment activity = ref.get();

            if (activity == null) return null;

            StringBuffer json = activity.getExchangeRate(activity.CURRENCIES_API);

            if(json != null){
                ExchangeRate rate = activity.parseResults(json.toString());
                if (rate == null) {
                    return null;
                }

                activity.exchangeRate = rate;
                activity.availableCurrencies = activity.extractCurrencyNames(rate).toArray(new String[0]);

                return activity.availableCurrencies;
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... progress) { }

        @Override
        protected void onPostExecute(String[] currencies) {
            CurrencyExchangeAppFragment activity = ref.get();

            if (activity == null) return;

            if(currencies != null){
                activity.DisplayCurrencyOptions();
            }
            else{
                activity.DisplayApiError();
            }
        }
    }
}
