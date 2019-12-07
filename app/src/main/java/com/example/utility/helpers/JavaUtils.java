package com.example.utility.helpers;

import android.content.Context;
import android.widget.EditText;
import android.widget.Toast;

import com.example.utility.R;

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
    Display toast on the screen
     */
    public static void ShowToast(Context context, int stringResource){
        Toast.makeText(context, stringResource, Toast.LENGTH_SHORT).show();
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
