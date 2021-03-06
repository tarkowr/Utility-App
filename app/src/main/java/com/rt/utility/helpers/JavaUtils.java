package com.rt.utility.helpers;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.rt.utility.R;

import java.util.Random;

public class JavaUtils {
    private static Random random = new Random();

    /*
    Return a random integer between 0 and a maximum value
     */
    public static Integer returnRandomInt(int max){
        return random.nextInt(max);
    }

    /*
    Pause the user interface of the app
     */
    public static void pauseUI(int delay){
        try{
            Thread.sleep(delay);
        }
        catch (InterruptedException ex){ }
    }

    /*
    Display a snackbar on the screen
     */
    public static void ShowSnackbar(View rootView, String msg, Integer fontSize) {
        Snackbar sb = Snackbar.make(rootView, msg, Snackbar.LENGTH_LONG);

        TextView sbTextView = sb.getView().findViewById(R.id.snackbar_text);
        sbTextView.setTextSize( fontSize );
        sbTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        sb.show();
    }

    /*
    Concatenate the app's name to the default action bar text
     */
    public static String FormatActionBarText(String appName, Context context){
        return context.getResources().getString(R.string.app_name) + " - " + appName;
    }

    /*
    Determines whether a string is empty
     */
    public static boolean CheckIfEmptyString(String str){
        return str == null || str.isEmpty();
    }

    /*
    Determines whether a value is within an acceptable range
     */
    public static boolean ValidRange(double value, double lower, double upper){
        return value > lower && value < upper;
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

    /*
    Long parse with try/catch for exception handling
     */
    public static long LongTryParse(String amount){
        try{
            long parsed = Long.parseLong(amount);
            if (parsed == 0){
                throw new Exception("Invalid Long");
            }
            return parsed;
        }
        catch (Exception e){
            return 0;
        }
    }

    /*
    Retrieves the text from a EditText widget
     */
    public static String GetWidgetText(EditText widget){
        try{
            return widget.getText().toString();
        } catch (Exception e){
            return null;
        }
    }
}
