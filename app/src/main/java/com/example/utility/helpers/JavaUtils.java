package com.example.utility.helpers;

import android.content.Context;
import android.widget.Toast;

import java.util.Random;

public class JavaUtils {
    private static Random random = new Random();

    public static Integer returnRandomInt(int max){
        return random.nextInt(max);
    }

    public static void pauseUI(int delay){
        try{
            Thread.sleep(delay);
        }
        catch (InterruptedException ex){ }
    }

    public static void ShowToast(Context context, int stringResource){
        Toast.makeText(context, stringResource, Toast.LENGTH_SHORT).show();
    }

    /*
    Double parse with try/catch for exception handling
     */
    public static double DoubleTryParse(String amount){
        try{
            double parsed = Double.parseDouble(amount);
            if (parsed == 0){
                throw new Exception("Invalid Double");
            }
            return parsed;
        }
        catch (Exception e){
            return 0;
        }
    }
}
