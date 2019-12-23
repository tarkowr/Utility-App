package com.rt.utility.helpers;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpHelper {
    private URL url;

    public HttpHelper(String _url) throws IOException {
        this.url = new URL(_url);
    }

    /*
    Makes a GET Web API request and returns the response (not parsed)
    Learned how to call an API in Java from https://stackoverflow.com/questions/1485708/how-do-i-do-a-http-get-in-java
        and https://www.baeldung.com/java-http-request
     */
    public StringBuffer Get() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");

        int status = connection.getResponseCode();
        Log.d("Status: ", String.valueOf(status));

        BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        StringBuffer content = new StringBuffer();
        while ((line = input.readLine()) != null) {
            content.append(line);
        }

        input.close();
        return content;
    }
}
