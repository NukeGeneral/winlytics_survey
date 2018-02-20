package winlytics.io.survey;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Locale;
import java.util.concurrent.Semaphore;

/**
 * Created by Umur Kaya on 2/19/18.
 */

public class Winlytics implements WinlyticsBuilder{
    private static String WINLYTICS_URL = "http://www.heydate.com/api/v4/references/google_play?winlytics_test_id=";
    private static Winlytics winlytics;
    private static WinlyticsSurvey survey = new WinlyticsSurvey();
    private final static Semaphore mutex = new Semaphore(1,true);
    private static String AUTH_TOKEN = "";
    private String response;

    private interface WinlyticsResponse {
        int OK = 0;
        int SERVICE_UNAVAILABLE = 1;
        int MALFORMED_RESPONSE = 2;
        int PROTOCOL_ERROR = 3;
        int SETUP_ERROR = 4;
        int UNKNOWN_ERROR = -1;
    }

    private Winlytics(){
        try{
            synchronized (mutex){
                mutex.acquire();
                new SetWinlyticsSurvey().execute();
            }
        }catch (InterruptedException e){
            //Prevent multiple execution
            throw new WinlyticsException("Illegal execution,already started Winlytics instance");
        }
    }

    @RequiresPermission(android.Manifest.permission.INTERNET)
    public static WinlyticsBuilder createSurvey(@NonNull String authToken){
        AUTH_TOKEN = authToken;
        if(winlytics == null){
            winlytics = new Winlytics();
        }
        else{
            //Do nothing
        }
        return winlytics;
    }

    @Override public WinlyticsBuilder withGeneratedUI(boolean generateUI){
        if(generateUI){

        }
        return this;
    }

    @Override public WinlyticsBuilder withModificationOption(boolean withOption){
        if(withOption){

        }
        return this;
    }

    private void setConnectionStatus(int connectionStatus){
        switch (connectionStatus){
            case WinlyticsResponse.OK:
                //Initialize Adapter
                break;
            case WinlyticsResponse.UNKNOWN_ERROR:
                //Report StackTrace to API
                break;
            case WinlyticsResponse.SERVICE_UNAVAILABLE:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                       new SetWinlyticsSurvey().execute();
                    }
                },2000);
                break;
            case WinlyticsResponse.MALFORMED_RESPONSE:
                //Try again
                break;
            case WinlyticsResponse.PROTOCOL_ERROR:
                //Open an issue
                break;
            case WinlyticsResponse.SETUP_ERROR:
                //Check credentials
                break;
        }
    }

    private void getSurvey() {
        winlytics.response = null;
        try {
            URL url = new URL(WINLYTICS_URL+AUTH_TOKEN);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setRequestProperty("User-Agent","OkHttp Winlytics");
            conn.setRequestProperty("Accept-Language", Locale.getDefault().getLanguage());
            InputStream in = new BufferedInputStream(conn.getInputStream());
            setConnectionStatus(WinlyticsResponse.OK);
            convertStreamToString(in);
        } catch (MalformedURLException e) {
            setConnectionStatus(WinlyticsResponse.MALFORMED_RESPONSE);
        } catch (ProtocolException e) {
            setConnectionStatus(WinlyticsResponse.PROTOCOL_ERROR);
        } catch (IOException e) {
            setConnectionStatus(WinlyticsResponse.SERVICE_UNAVAILABLE);
        } catch (Exception e) {
            setConnectionStatus(WinlyticsResponse.UNKNOWN_ERROR);
        }
    }

    private void convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        SystemClock.sleep(500);
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        winlytics.response = sb.toString();
    }

    /**
     * Async task class to get data by making HTTP call
     */
    private static class SetWinlyticsSurvey extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... arg0) {
            winlytics.getSurvey();
            try {
                JSONObject jsonObj = new JSONObject(winlytics.response);
                // Example Json Parsing
                if(survey == null){
                    survey = new WinlyticsSurvey();
                }
                else{
                    if(!survey.resetSurvey()){
                        survey = new WinlyticsSurvey();
                    }
                }
                JSONObject contacts = jsonObj.getJSONObject("debug");
                survey.setTemp(contacts.getString("warnings"));
                // adding contact to contact list
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mutex.release();
            return null;
        }
    }
}
