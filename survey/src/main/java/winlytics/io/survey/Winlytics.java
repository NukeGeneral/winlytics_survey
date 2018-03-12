package winlytics.io.survey;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Umur Kaya on 2/19/18.
 */

public class Winlytics{
    private static String WINLYTICS_URL = "http://www.winlytics.io/api/responses";
    private static Winlytics winlytics;
    private static WinlyticsSurvey survey = new WinlyticsSurvey();
    private static AtomicBoolean mutex = new AtomicBoolean(false);
    private static String SURVEYID = "";
    static String UNIQUE_COOKIE_ID = "";
    private static String USERID = "";
    private static String USERNAME = "";
    private static String EMAIL = "";
    static String WINLYTICS_CATEGORY_TAG = "";
    private String response;
    private WinlyticsAdapter mLayout;
    private boolean UIReady = false;
    private static SharedPreferences sharedPreferences;
    static boolean isTesting = false;
    static boolean isAppOnScreen = false;
    private static boolean isSuccessfulResult = false;
    private static int requestCount = 0;

    private Winlytics(){
        super();
    }

    @RequiresPermission(android.Manifest.permission.INTERNET)
    /**
     * @param surveyId Winlytics survey ID provided in registration
     * @param userId To identify users from each other(It should be unique for each user)
     * @param userName Optional parameter to see user name in dashboard,it can be empty string
     * @param email Optional parameter to see emails in dashboard,it can be empty string
     * @param categoryTags This makes easier to analyse dashboard,it's like filter or activity/fragment name
     * @param context This is activity or fragment context
     * @param isTest is this a test request or not(default false)
     */
    public static void createSurvey(@NonNull String surveyId,@NonNull String userId,@Nullable String userName,
            @Nullable String email,@NonNull String categoryTags , @NonNull Context context,boolean isTest){
        sharedPreferences = context.getSharedPreferences("io.winlytics.prefs",Context.MODE_PRIVATE);
        if(!isTest && (sharedPreferences.getLong("io.winlytics.lastcalled",System.currentTimeMillis()  + 86400000L) < System.currentTimeMillis())){
            return;
        }
        else{
            sharedPreferences.edit().putLong("io.winlytics.lastcalled",System.currentTimeMillis()).apply();
        }
        SURVEYID = surveyId;
        USERID  = userId;
        USERNAME = (userName == null) ? "" : userName;
        EMAIL = (email == null) ? "" : email;
        WINLYTICS_CATEGORY_TAG = categoryTags;
        isTesting = isTest;
        String temp = sharedPreferences.getString("io.winlytics.uniquecookieid",null);
        UNIQUE_COOKIE_ID = (temp == null) ? "" : temp;
        if(winlytics == null){
            if(!mutex.getAndSet(true)){
                requestCount++;
                winlytics = new Winlytics();
                new SetWinlyticsSurvey().execute();
            }
            else{
                throw new WinlyticsException("Winlytics should be called once per instance");
            }
        }
        else{
            if(mutex.get()){
                throw new WinlyticsException("Winlytics should be called once per instance");
            }
            else{
                if(survey == null){
                    requestCount++;
                    new SetWinlyticsSurvey().execute();
                }
            }
        }
        if(winlytics.mLayout == null){
            winlytics.mLayout = new WinlyticsAdapter(new WinlyticsAdapter.WinlyticsAdapterNotifier() {
                @Override
                public void notifyAdapterIsReady() {
                    winlytics.UIReady = true;
                    if(isSuccessfulResult){
                        winlytics.setSurveyItems();
                    }
                }
            },context);
        }
    }

    private void setSurveyItems(){
        if(UIReady && !mutex.get()){
            mLayout.setCanYouAdvice(survey.getCanYouAdvice());
            mLayout.setSorryToHearThat(survey.getSorryToHearThat());
            mLayout.setThanks(survey.getThanks());
            mLayout.setWhyDidYouChoose(survey.getWhyDidYouChoose());
            mLayout.setWeWillUseYourFeedback(survey.getWeWillUseYourFeedback());
            mLayout.setFeedbackPlaceholder(survey.getFeedbackPlaceholder());
            mLayout.setSubmit(survey.getSubmit());
            mLayout.setThankYou(survey.getThankYou());
            mLayout.setThanksAgain(survey.getThanksAgain());
            mLayout.setWeReallyAppreciateYourFeedback(survey.getWeReallyAppreciateYourFeedback());
            mLayout.setBrandColor(survey.getBrandColor());
            mLayout.setSubmitToken(survey.getSubmitToken());
            mLayout.dialog.show();
        }
    }

    private void setConnectionStatus(WinlyticsError error){
        switch (error){
            case OK:
                break;
            case UNKNOWN_ERROR:
                isSuccessfulResult = false;
                //TODO Currently not implemented
                break;
            case SERVICE_UNAVAILABLE:
                isSuccessfulResult = false;
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if(isAppOnScreen && requestCount < 3) {
                            requestCount++;
                            new SetWinlyticsSurvey().execute();
                        }
                    }
                }, 10000);
                break;
            case MALFORMED_RESPONSE:
                isSuccessfulResult = false;
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if(isAppOnScreen && requestCount < 3) {
                            requestCount++;
                            new SetWinlyticsSurvey().execute();
                        }
                    }
                }, 500);
                break;
            case PROTOCOL_ERROR:
                isSuccessfulResult = false;
                //Open an issue
                break;
            case SETUP_ERROR:
                //TODO Currently not implemented
                throw new WinlyticsException("Authenication failed,information which passed is wrong");
        }
    }

    private void loadSurvey() {
        winlytics.response = null;
        try {
            URL url = new URL(WINLYTICS_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Accept-Language", Locale.getDefault().getLanguage());
            String urlParameters = "surveyId="+SURVEYID+"&userId="+USERID+"&name="+USERNAME
                    +"&email="+EMAIL+"&category="+WINLYTICS_CATEGORY_TAG+"&token="+ UNIQUE_COOKIE_ID;
            if(isTesting){
                urlParameters += "&referrer=winlytics=test";
            }
            byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
            try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
                wr.write(postData);
            }
            int statusCode = conn.getResponseCode();
            if(statusCode == 201){
                InputStream in = new BufferedInputStream(conn.getInputStream());
                setConnectionStatus(WinlyticsError.OK);
                convertStreamToString(in);
            }
            else if(statusCode == 204){
                //Means no content,we won't show anything
                setConnectionStatus(WinlyticsError.OK);
                winlytics.response = null;
            }
            else{
                setConnectionStatus(WinlyticsError.UNKNOWN_ERROR);
            }
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

        SetWinlyticsSurvey(){
            super();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            winlytics.loadSurvey();
            try {
                if(winlytics.response == null){
                    isSuccessfulResult = false;
                    return null;
                }
                isSuccessfulResult = true;
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
                sharedPreferences.edit().putString("io.winlytics.uniquecookieid",jsonObj.getString("token")).apply();
                survey.setBrandColor(jsonObj.getString("brandColor"));
                survey.setSubmitToken(jsonObj.getString("submitToken"));
                JSONObject translations = jsonObj.getJSONObject("translations");
                survey.setCanYouAdvice(translations.getString("canYouAdvice"));
                survey.setSorryToHearThat(translations.getString("sorryToHearThat"));
                survey.setThanks(translations.getString("thanks"));
                survey.setWhyDidYouChoose(translations.getString("whyDidYouChoose"));
                survey.setWeWillUseYourFeedback(translations.getString("weWillUseYourFeedback"));
                survey.setFeedbackPlaceholder(translations.getString("feedbackPlaceholder"));
                survey.setSubmit(translations.getString("submit"));
                survey.setThankYou(translations.getString("thankYou"));
                survey.setThanksAgain(translations.getString("thanksAgain"));
                survey.setWeReallyAppreciateYourFeedback(translations.getString("weReallyAppreciateYourFeedback"));
            } catch (JSONException e) {
                winlytics.setConnectionStatus(WinlyticsError.MALFORMED_RESPONSE);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mutex.set(false);
            if(isSuccessfulResult){
                winlytics.setSurveyItems();
            }
        }
    }

    public static void onResume(){
        isAppOnScreen = true;
    }

    public static void onPause(){
        isAppOnScreen = false;
    }
}
