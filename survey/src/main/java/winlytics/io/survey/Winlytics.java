package winlytics.io.survey;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Umur Kaya on 2/19/18.
 */

public class Winlytics implements WinlyticsBuilder{
    private static String WINLYTICS_URL = "http://www.heydate.com/api/v4/references/google_play?winlytics_test_id=";
    private static Winlytics winlytics;
    private static WinlyticsSurvey survey = new WinlyticsSurvey();
    private static AtomicBoolean mutex = new AtomicBoolean(false);
    private static String AUTH_TOKEN = "";
    private String response;
    private WinlyticsAdapter mLayout;

    private Winlytics(){
        if(!mutex.getAndSet(true)){
            new SetWinlyticsSurvey().execute();
        }
        else{
            throw new WinlyticsException("Winlytics should be called once per instance");
        }
    }

    @RequiresPermission(android.Manifest.permission.INTERNET)
    public static WinlyticsBuilder createSurvey(@NonNull String authToken){
        AUTH_TOKEN = authToken;
        if(winlytics == null){
            winlytics = new Winlytics();
        }
        else{
            if(mutex.get()){
                throw new WinlyticsException("Winlytics should be called once per instance");
            }
            else{
                if(survey != null){

                }
                else{
                    winlytics.getSurvey();
                }
            }
        }
        return winlytics;
    }

    /**
     *
     * @param context Should be non-null if requested with Default UI
     * @param generateUI Should passed as true if requested Default UI,else false
     * @return
     */

    @Override public WinlyticsBuilder withGeneratedUI(@Nullable Context context, boolean generateUI){
        if(generateUI){
            mLayout = new WinlyticsAdapter(context);
        }
        return this;
    }

    /**
     *
     * @param withOption Should passed as true if developer wants to make some modifications on Default UI
     * @return
     */
    @Override public WinlyticsBuilder withModificationOption(boolean withOption){
        if(withOption){

        }
        return this;
    }

    private void setConnectionStatus(WinlyticsError error){
        switch (error){
            case OK:
                //Initialize Adapter
                break;
            case UNKNOWN_ERROR:
                //Report StackTrace to API
                break;
            case SERVICE_UNAVAILABLE:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                       new SetWinlyticsSurvey().execute();
                    }
                },2000);
                break;
            case MALFORMED_RESPONSE:
                //Try again
                break;
            case PROTOCOL_ERROR:
                //Open an issue
                break;
            case SETUP_ERROR:
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
            setConnectionStatus(WinlyticsError.OK);
            convertStreamToString(in);
        } catch (MalformedURLException e) {
            setConnectionStatus(WinlyticsError.MALFORMED_RESPONSE);
        } catch (ProtocolException e) {
            setConnectionStatus(WinlyticsError.PROTOCOL_ERROR);
        } catch (IOException e) {
            setConnectionStatus(WinlyticsError.SERVICE_UNAVAILABLE);
        } catch (Exception e) {
            setConnectionStatus(WinlyticsError.UNKNOWN_ERROR);
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
            } catch (JSONException e) {
                winlytics.setConnectionStatus(WinlyticsError.MALFORMED_RESPONSE);
            }
            mutex.set(false);
            return null;
        }
    }
}
