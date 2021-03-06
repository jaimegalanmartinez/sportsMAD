package com.mdp.sportsmad.utils;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;


public class DownloadRunnable implements Runnable{
    Handler creator;
    Context context;
    private SportCenterParser sportCenterParser;
    private String expectedContent_type;
    private String string_URL;
    public DownloadRunnable(Context c,Handler handler, String cnt_type, String strURL) {
        // The constructor accepts 3 arguments:
        // The handler to the creator of this object
        // The content type expected (e.g. "application/vnd.google-earth.kml+xml").
        // The URL to load.
        context =c;
        creator = handler;
        expectedContent_type = cnt_type;
        string_URL = strURL;
    }
    public void setParser(SportCenterParser scp){
        sportCenterParser =scp;
    }
    @Override
    public void run() {
        Message msg = creator.obtainMessage();
        Bundle msg_data = msg.getData();

        /////////////////////////////
        try {
            String response = ""; // This string will contain the loaded contents of a text resource
            HttpURLConnection urlConnection;

            // Build the logTag with the Thread and Class names (to identify logs):
            String logTag = "Thread = " + Thread.currentThread().getName() + ", Class = " +
                    this.getClass().getName().substring(this.getClass().getName().lastIndexOf(".") + 1);

            Log.d(logTag, "run() called, starting load");

            try {
                URL url = new URL(string_URL);
                urlConnection = (HttpURLConnection) url.openConnection();
                String actualContentType = urlConnection.getContentType(); // content-type header from HTTP server
                InputStream is = urlConnection.getInputStream();

                // Extract MIME type (get rid of the possible parameters present in the content-type header
                // Content-type: type/subtype;parameter1=value1;parameter2=value2...
                if ((actualContentType != null) && (actualContentType.contains(";"))) {
                    int beginparam = actualContentType.indexOf(";", 0);
                    Log.d(logTag, "Complete HTTP content-type header from server = " + actualContentType);
                    actualContentType = actualContentType.substring(0, beginparam);
                }
                Log.d(logTag, "MIME type reported by server = " + actualContentType);

                if (expectedContent_type.equals(actualContentType)) {
                    // We check that the actual content type got from the server is the expected one
                    // and if it is, download text
                    InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
                    BufferedReader in = new BufferedReader(reader);
                    // We read the text contents line by line and add them to the response:
                    String line = in.readLine();
                    while (line != null) {
                        response += line + "\n";
                        line = in.readLine();
                    }
                } else { // content type not supported
                    response = "Actual content type different from expected (" +
                            actualContentType + " vs " + expectedContent_type;
                }
                urlConnection.disconnect();
            } catch (Exception e) {//Error at downloading

            }

            Log.d(logTag, "load complete, sending message to UI thread");

            sportCenterParser.parse(response);
            Log.d(logTag, "parsing complete, sending message ok to UI thread");
            if ("".equals(response) == false) {
                msg_data.putBoolean("result", true);
            }else{
                msg_data.putBoolean("result", false);
                msg_data.putString("error","Content received is empty");
            }
        }catch (Exception e){
            msg_data.putBoolean("result", false);
            msg_data.putString("error", "Error at downloading");
        }
        msg.sendToTarget();
    }
}
