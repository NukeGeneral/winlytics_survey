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

public class Winlytics{
    private static String WINLYTICS_URL = "http://www.heydate.com/api/v4/references/google_play?winlytics_test_id=";
    private static Winlytics winlytics;
    private static WinlyticsSurvey survey = new WinlyticsSurvey();
    private static AtomicBoolean mutex = new AtomicBoolean(false);
    private static String AUTH_TOKEN = "";
    private String response;
    private WinlyticsAdapter mLayout;
    private boolean UIReady = false;

    private Winlytics(){
        if(!mutex.getAndSet(true)){
            new SetWinlyticsSurvey().execute();
        }
        else{
            throw new WinlyticsException("Winlytics should be called once per instance");
        }
    }

    @RequiresPermission(android.Manifest.permission.INTERNET)
    /**
     * @param authToken This is a token which provided to you from winlytics.io dashboard
     * @param context Must be non-null if requested with Default UI,it can be Activity or Fragment Context
     * @return Winlytics instance object
     */
    public static Winlytics createSurvey(@NonNull String authToken,@NonNull Context context){
        AUTH_TOKEN = authToken;
        if(winlytics == null){
            winlytics = new Winlytics();
        }
        else{
            if(mutex.get()){
                throw new WinlyticsException("Winlytics should be called once per instance");
            }
            else{
                if(survey == null){
                    winlytics.loadSurvey();
                }
            }
        }
        if(winlytics.mLayout == null){
            winlytics.mLayout = new WinlyticsAdapter(new WinlyticsAdapter.WinlyticsAdapterNotifier() {
                @Override
                public void notifyAdapterIsReady() {
                    winlytics.UIReady = true;
                    winlytics.setSurveyItems();
                }
            },context);
        }
        return winlytics;
    }

    private void setSurveyItems(){
        if(UIReady && !mutex.get()){
            mLayout.setOptionalTextAreaText(survey.getTemp());
            mLayout.setBrandName(survey.getBrandName());
            mLayout.setSubmitButtonText("Submit");
            mLayout.setImage(survey.getImageUrl());
            mLayout.setBrandColor(0xFF48A9D7);
            mLayout.setButtonAnswerWelcomingText("sdadsdsa dsadsad sadasasdasdsadsad dasdsadas");
            mLayout.dialog.show();
        }
    }

    private void setConnectionStatus(WinlyticsError error){
        switch (error){
            case OK:
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
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new SetWinlyticsSurvey().execute();
                    }
                },2000);
                break;
            case PROTOCOL_ERROR:
                //Open an issue
                break;
            case SETUP_ERROR:
                throw new WinlyticsException("Authenication failed,information which passed is wrong");
        }
    }

    private void loadSurvey() {
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
        SystemClock.sleep(100);
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
                setConnectionStatus(WinlyticsError.UNKNOWN_ERROR);
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
            winlytics.loadSurvey();
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
                survey.setImageUrl("https://www.logodesignlove.com/images/classic/apple-logo-rob-janoff-01.jpg");
                survey.setBrandName("How likely are you recommend Apple to your friends?");
            } catch (JSONException e) {
                winlytics.setConnectionStatus(WinlyticsError.MALFORMED_RESPONSE);
            }
            mutex.set(false);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            winlytics.setSurveyItems();
        }
    }
}
