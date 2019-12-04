package com.example.utility.apps;

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

import com.example.utility.R;
import com.example.utility.helpers.JavaUtils;

import java.util.Random;

public class CoinFlipFragment extends Fragment {
    private Random rand;
    private long totalHeads;
    private long totalTails;
    private ProgressBar progressBar;
    private EditText editNumOfFlips;
    private TextView txtHeads;
    private TextView txtTails;
    private long numOfFlips;
    private final String DEFAULT_FLIPS = "1";

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
        editNumOfFlips.setText(DEFAULT_FLIPS);

        progressBar = view.findViewById(R.id.progressBarFlip);
        progressBar.setVisibility(View.INVISIBLE);

        txtHeads = view.findViewById(R.id.txtHeads);
        txtTails = view.findViewById(R.id.txtTails);

        Button submit = view.findViewById(R.id.btnFlip);
        submit.setOnClickListener(flip);

        return view;
    }

    View.OnClickListener flip = new View.OnClickListener() {
        public void onClick(View view) {
            view = getView();

            txtHeads.setText(null);
            txtTails.setText(null);

            progressBar.setVisibility(View.VISIBLE);
            view.findViewById(R.id.btnFlip).setEnabled(false);
            numOfFlips = JavaUtils.LongTryParse(editNumOfFlips.getText().toString());

            new FlipCoinsAsync().execute();
        }
    };

    private CoinState flipCoin(){
        return rand.nextBoolean() ? CoinState.Heads : CoinState.Tails;
    }

    private void handleFlipCoins(){
        for(int i=0; i < numOfFlips; i++){
            if(flipCoin().equals(CoinState.Heads)){
                totalHeads++;
            }
            else{
                totalTails++;
            }
        }
    }

    private void updateUIResults(){
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                View view = getView();
                view.findViewById(R.id.btnFlip).setEnabled(true);
                txtHeads.setText("Heads: " + totalHeads);
                txtTails.setText("Tails: " + totalTails);
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private enum CoinState{
        Heads,
        Tails
    }

    /*
    https://developer.android.com/reference/android/os/AsyncTask
     */
    private class FlipCoinsAsync extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... args) {
            handleFlipCoins();
            return null;
        }

        @Override
        protected void onProgressUpdate(String... progress) { }

        @Override
        protected void onPostExecute(String status) {
            updateUIResults();
            totalHeads = 0;
            totalTails = 0;
        }
    }
}
