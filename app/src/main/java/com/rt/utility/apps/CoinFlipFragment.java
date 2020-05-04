package com.rt.utility.apps;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.rt.utility.R;
import com.rt.utility.helpers.JavaUtils;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.Random;

public class CoinFlipFragment extends Fragment {
    private ProgressBar progressBar;
    private EditText editNumOfFlips;
    private TextView txtHeads;
    private TextView txtTails;
    private TextView txtStatus;
    private Button flipBtn;

    private Random rand;
    private long totalHeads;
    private long totalTails;
    private long numOfFlips;

    private final long MAX_FLIPS = 100000000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        rand = new Random();
        totalHeads = 0;
        totalTails = 0;
        numOfFlips = 1;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coinflip_app, container, false);

        editNumOfFlips = view.findViewById(R.id.editTextNumFlips);

        progressBar = view.findViewById(R.id.progressBarFlip);
        progressBar.setVisibility(View.INVISIBLE);

        txtHeads = view.findViewById(R.id.txtHeads);
        txtTails = view.findViewById(R.id.txtTails);
        txtStatus = view.findViewById(R.id.txtStatus);

        flipBtn = view.findViewById(R.id.btnFlip);
        flipBtn.setOnClickListener(flip);

        return view;
    }

    /*
    Flip button onClick event - Handles input validation and flips
     */
    private View.OnClickListener flip = new View.OnClickListener() {
        public void onClick(View view) {
            setFlipResults(null, null);
            txtStatus.setText(null);

            numOfFlips = JavaUtils.LongTryParse(editNumOfFlips.getText().toString());

            if(numOfFlips > MAX_FLIPS){
                txtStatus.setText(R.string.app_coin_flip_error_max);
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            flipBtn.setEnabled(false);

            new FlipCoinsAsync(CoinFlipFragment.this).execute();
        }
    };

    /*
    Virtually "flips a coin" with Heads/Tails represented by a boolean
     */
    private boolean flipCoin(){
        return rand.nextBoolean();
    }

    /*
    Virtually "flips multiple coins" and tallies the results
     */
    private void flipCoins(){
        for(int i=0; i < numOfFlips; i++){
            if(flipCoin()){
                totalHeads++;
            }
            else{
                totalTails++;
            }
        }
    }

    /*
    Sets the heads/tails results in the UI
     */
    private void setFlipResults(String headStr, String tailStr){
        txtHeads.setText(headStr);
        txtTails.setText(tailStr);
    }

    /*
    onComplete event for flip async task - Updates UI with results
     */
    private void onFlipComplete(){
        if(getActivity() == null){
            return;
        }

        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                DecimalFormat formatter = new DecimalFormat("###,###");

                flipBtn.setEnabled(true);
                setFlipResults("Heads: " + formatter.format(totalHeads), "Tails: " + formatter.format(totalTails));
                progressBar.setVisibility(View.INVISIBLE);

                totalHeads = 0;
                totalTails = 0;
            }
        });
    }

    /*
    Flips the specified number of coins asynchronously
    Learned about async tasks from https://developer.android.com/reference/android/os/AsyncTask
     */
    private static class FlipCoinsAsync extends AsyncTask<String, String, String> {

        private WeakReference<CoinFlipFragment> ref;

        FlipCoinsAsync(CoinFlipFragment context){
            ref = new WeakReference<>(context);
        }

        @Override
        protected String doInBackground(String... args) {
            CoinFlipFragment activity = ref.get();

            if (activity == null) return null;

            activity.flipCoins();
            return null;
        }

        @Override
        protected void onProgressUpdate(String... progress) { }

        @Override
        protected void onPostExecute(String status) {
            CoinFlipFragment activity = ref.get();

            if (activity == null) return;

            activity.onFlipComplete();
        }
    }
}
