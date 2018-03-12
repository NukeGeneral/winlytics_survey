package winlytics.io.survey;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.res.ResourcesCompat;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

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

/**
 * Created by Umur Kaya on 2/20/18.
 */

class WinlyticsAdapter extends Dialog{

    private static String WINLYTICS_SUBMIT_URL = "http://www.winlytics.io/api/responses/{submitToken}";
    private final TextView winlytics_head_question,winlytics_optional_text_title_area, winlytics_afteranswer_head,powered_by_text;
    private Button referenceHolder;
    private final Button button0,button1,button2,button3,button4,button5,button6,button7,button8,button9,button10,winlytics_submit;
    private final LinearLayout winlytics_optional_text_area;
    private final EditText winlytics_optional_edit_text_area;
    private final ScrollView winlytics_scroll;
    private String resultNumber,sorryToHearThat,thanks,whyDidYouChoose,weWillUseYourFeedback
            ,thankYou,thanksAgain,weReallyAppreciateYourFeedback,submitToken,concatenatedFeedback = null;
    private Context context;
    Dialog dialog;
    private int selectionColor;
    private GradientDrawable solidDrawable = new GradientDrawable();
    private GradientDrawable withBorderDrawable = new GradientDrawable();
    private int requestCount = 0;
    private String resultText = "";
    private boolean isFromSubmit = false;
    private boolean isEdittextRequestFocus = false;

    interface WinlyticsAdapterNotifier{
        void notifyAdapterIsReady();
    }

    @SuppressLint("ClickableViewAccessibility")
    WinlyticsAdapter(final WinlyticsAdapterNotifier mListener, final Context context){
        super(context);
        dialog = new Dialog(context,R.style.DialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setContentView(R.layout.winlytics_default);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        this.context = context;
        //Dialog setup
        winlytics_head_question = (TextView) dialog.findViewById(R.id.winlytics_head_question);
        button0 = (Button) dialog.findViewById(R.id.winlytics_button_0);
        button1 = (Button) dialog.findViewById(R.id.winlytics_button_1);
        button2 = (Button) dialog.findViewById(R.id.winlytics_button_2);
        button3 = (Button) dialog.findViewById(R.id.winlytics_button_3);
        button4 = (Button) dialog.findViewById(R.id.winlytics_button_4);
        button5 = (Button) dialog.findViewById(R.id.winlytics_button_5);
        button6 = (Button) dialog.findViewById(R.id.winlytics_button_6);
        button7 = (Button) dialog.findViewById(R.id.winlytics_button_7);
        button8 = (Button) dialog.findViewById(R.id.winlytics_button_8);
        button9 = (Button) dialog.findViewById(R.id.winlytics_button_9);
        button10 = (Button) dialog.findViewById(R.id.winlytics_button_10);
        winlytics_optional_text_area = (LinearLayout) dialog.findViewById(R.id.winlytics_optional_text_area);
        winlytics_optional_text_title_area = (TextView) dialog.findViewById(R.id.winlytics_optional_text_title_area);
        winlytics_optional_edit_text_area = (EditText) dialog.findViewById(R.id.winlytics_optional_edit_text_area);
        winlytics_afteranswer_head = (TextView) dialog.findViewById(R.id.winlytics_afteranswer_head);
        winlytics_submit = (Button) dialog.findViewById(R.id.winlytics_submit);
        winlytics_scroll = (ScrollView) dialog.findViewById(R.id.winlytics_scroll);
        ImageButton winlytics_cancel_action = (ImageButton) dialog.findViewById(R.id.winlytics_cancel_action);
        powered_by_text = (TextView) dialog.findViewById(R.id.powered_by_text);
        //Sad and Happy Buttons are based on NPS system

        //Set Button UnHappy Listener

        winlytics_optional_edit_text_area.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    isFromSubmit = true;
                    submitResult();
                    return true;
                }
                return false;
            }
        });

        winlytics_optional_edit_text_area.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    isEdittextRequestFocus = true;
                    return true;
                }
                else if(event.getAction() == MotionEvent.ACTION_UP && isEdittextRequestFocus){
                    //Call my onClick method
                    isEdittextRequestFocus = false;
                    winlytics_optional_edit_text_area.requestFocus();
                    InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,0);
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            winlytics_scroll.smoothScrollTo(0,powered_by_text.getBottom());
                        }
                    },300);
                    return true;
                }
                else{
                    if(winlytics_optional_edit_text_area.hasFocus()){
                        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        winlytics_optional_edit_text_area.clearFocus();
                    }
                    return event.getAction() == MotionEvent.ACTION_MOVE;
                }
            }
        });

        View.OnClickListener buttonSad = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(referenceHolder != null){
                    referenceHolder.setBackground(solidDrawable);
                    referenceHolder.setTextColor(selectionColor);
                }
                referenceHolder = (Button) v;
                resultNumber = ((String) referenceHolder.getTag());
                winlytics_optional_text_area.setVisibility(View.VISIBLE);
                v.post(new Runnable() {
                    @Override
                    public void run() {
                        if(winlytics_scroll.canScrollVertically(1)){
                            winlytics_scroll.smoothScrollTo(0,powered_by_text.getBottom());
                        }
                    }
                });
                referenceHolder.setBackground(withBorderDrawable);
                referenceHolder.setTextColor(context.getResources().getColor(R.color.white));
                winlytics_afteranswer_head.setText(sorryToHearThat);
                String temp = whyDidYouChoose.replace("%number%",resultNumber);
                concatenatedFeedback = temp + " " + weWillUseYourFeedback;
                winlytics_optional_text_title_area.setText(concatenatedFeedback);
                submitResult();
            }
        };

        //Set Button Happy Listener

        View.OnClickListener buttonHappy = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(referenceHolder != null){
                    referenceHolder.setBackground(solidDrawable);
                    referenceHolder.setTextColor(selectionColor);
                }
                referenceHolder = (Button)v;
                resultNumber = ((String) referenceHolder.getTag());
                winlytics_optional_text_area.setVisibility(View.VISIBLE);
                v.post(new Runnable() {
                    @Override
                    public void run() {
                        if(winlytics_scroll.canScrollVertically(1)){
                            winlytics_scroll.smoothScrollTo(0,powered_by_text.getBottom());
                        }
                    }
                });
                referenceHolder.setBackground(withBorderDrawable);
                referenceHolder.setTextColor(context.getResources().getColor(R.color.white));
                winlytics_afteranswer_head.setText(thanks);
                String temp = whyDidYouChoose.replace("%number%",resultNumber);
                concatenatedFeedback = temp + " " + weWillUseYourFeedback;
                winlytics_optional_text_title_area.setText(concatenatedFeedback);
                isFromSubmit = false;
                submitResult();
            }
        };

        //Set OnClickListener

        button0.setOnClickListener(buttonSad);
        button1.setOnClickListener(buttonSad);
        button2.setOnClickListener(buttonSad);
        button3.setOnClickListener(buttonSad);
        button4.setOnClickListener(buttonSad);
        button5.setOnClickListener(buttonSad);
        button6.setOnClickListener(buttonSad);
        button7.setOnClickListener(buttonHappy);
        button8.setOnClickListener(buttonHappy);
        button9.setOnClickListener(buttonHappy);
        button10.setOnClickListener(buttonHappy);

        winlytics_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFromSubmit = true;
                submitResult();
            }
        });
        winlytics_cancel_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        mListener.notifyAdapterIsReady();
    }
    void setSubmitToken(String submitToken){this.submitToken = submitToken;}
    void setCanYouAdvice(String canYouAdvice) {winlytics_head_question.setText(canYouAdvice);}
    void setSorryToHearThat(String sorryToHearThat) {this.sorryToHearThat = sorryToHearThat;}
    void setThanks(String thanks) {this.thanks = thanks;}
    void setWhyDidYouChoose(String whyDidYouChoose) {this.whyDidYouChoose = whyDidYouChoose;}
    void setWeWillUseYourFeedback(String weWillUseYourFeedback) {this.weWillUseYourFeedback = weWillUseYourFeedback;}
    void setFeedbackPlaceholder(String feedbackPlaceholder) {winlytics_optional_edit_text_area.setHint(feedbackPlaceholder);}
    void setSubmit(String submit) {winlytics_submit.setText(submit);}
    void setThankYou(String thankYou) {this.thankYou = thankYou;}
    void setThanksAgain(String thanksAgain) {this.thanksAgain = thanksAgain;}
    void setWeReallyAppreciateYourFeedback(String weReallyAppreciateYourFeedback) {this.weReallyAppreciateYourFeedback = weReallyAppreciateYourFeedback;}
    void setBrandColor(String color){
        selectionColor = Color.parseColor(color);
        button0.setTextColor(selectionColor);
        button1.setTextColor(selectionColor);
        button2.setTextColor(selectionColor);
        button3.setTextColor(selectionColor);
        button4.setTextColor(selectionColor);
        button5.setTextColor(selectionColor);
        button6.setTextColor(selectionColor);
        button7.setTextColor(selectionColor);
        button8.setTextColor(selectionColor);
        button9.setTextColor(selectionColor);
        button10.setTextColor(selectionColor);

        // prepare button background borders
        solidDrawable.setColor(0xFFFFFFFF);
        solidDrawable.setShape(GradientDrawable.RECTANGLE);
        solidDrawable.setStroke((int)pxFromDp(context,.5f),0xFF363636);
        solidDrawable.setCornerRadius(pxFromDp(context,5));

        withBorderDrawable.setColor(selectionColor);
        withBorderDrawable.setCornerRadius(pxFromDp(context,5));
        withBorderDrawable.setStroke((int)pxFromDp(context,.5f),selectionColor);

        button0.setBackground(solidDrawable);
        button1.setBackground(solidDrawable);
        button2.setBackground(solidDrawable);
        button3.setBackground(solidDrawable);
        button4.setBackground(solidDrawable);
        button5.setBackground(solidDrawable);
        button6.setBackground(solidDrawable);
        button7.setBackground(solidDrawable);
        button8.setBackground(solidDrawable);
        button9.setBackground(solidDrawable);
        button10.setBackground(solidDrawable);

        winlytics_submit.setBackground(withBorderDrawable);
    }

    private static float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    private void submitResult(){
        if(resultText.length() == 0){
            resultText = winlytics_optional_edit_text_area.getText().toString();
        }
        String temp = WINLYTICS_SUBMIT_URL.replace("{submitToken}",submitToken);
        requestCount++;
        new SendResult(resultNumber,temp,resultText,Winlytics.WINLYTICS_CATEGORY_TAG,isFromSubmit).execute();
        if(dialog.isShowing() && isFromSubmit){
            dialog.dismiss();
        }
    }

    @SuppressLint("StaticFieldLeak")
    //This should run no matter application lives or not to send results correctly
    private class SendResult extends AsyncTask<Void,Void,Void>{

        private String url,comment,category,number;
        private boolean fromSubmit;

        SendResult(String number,String url,String comment,String category,boolean fromSubmit){
            this.url = url;
            this.comment = comment;
            this.category = category;
            this.fromSubmit = fromSubmit;
            this.number = number;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(fromSubmit){
                new WinlyticsBottomDialog(context);
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try{
                URL url = new URL(this.url);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("PUT");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("charset", "utf-8");
                conn.setRequestProperty("Accept-Language", Locale.getDefault().getLanguage());
                String urlParameters = "type=web"+"&score="+number+"&comment="+comment
                        +"&autoSubmitted="+fromSubmit+"&category="+category+"&token="+Winlytics.UNIQUE_COOKIE_ID;
                if(Winlytics.isTesting){
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
                }
                else if(statusCode == 400){
                    setConnectionStatus(WinlyticsError.AUTHENICATION_ERROR);
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
            return null;
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
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if(Winlytics.isAppOnScreen && requestCount < 3 && isFromSubmit) {
                            requestCount++;
                            submitResult();
                        }
                    }
                }, 10000);
                break;
            case PROTOCOL_ERROR:
                //Open an issue
                break;
            case MALFORMED_RESPONSE:
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if(Winlytics.isAppOnScreen && requestCount < 3 && isFromSubmit) {
                            requestCount++;
                            submitResult();
                        }
                    }
                }, 500);
                break;
            case AUTHENICATION_ERROR:
                //Token expired
                break;
            case SETUP_ERROR:
                throw new WinlyticsException("Authenication failed,information which passed is wrong");
        }
    }

    @NonNull
    private void convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        SystemClock.sleep(100);
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
            try {
                JSONObject jsonObj = new JSONObject(sb.toString());
                // Example Json Parsing
                jsonObj.get("resultCode");
                jsonObj.get("errors");
            }
            catch (JSONException e) {
                setConnectionStatus(WinlyticsError.MALFORMED_RESPONSE);
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
    }

    private class WinlyticsBottomDialog extends BottomSheetDialog {

        private TextView winlytics_thankyou_simple,winlytics_thankyou_detailed;
        private ImageView winlytics_thankyou_image;
        private ImageButton winlytics_thankyou_cancel;

        WinlyticsBottomDialog(Context context){
            super(context);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.winlytics_bottomdialog);
            setCanceledOnTouchOutside(true);
            winlytics_thankyou_simple = (TextView) findViewById(R.id.winlytics_thankyou_simple);
            winlytics_thankyou_detailed = (TextView) findViewById(R.id.winlytics_thankyou_detailed);
            winlytics_thankyou_image = (ImageView) findViewById(R.id.winlytics_thankyou_image);
            winlytics_thankyou_cancel = (ImageButton) findViewById(R.id.winlytics_thankyou_cancel);
            if(Integer.parseInt(resultNumber) > 6){
                winlytics_thankyou_simple.setText(thanksAgain);
                winlytics_thankyou_image.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(),R.drawable.happyheart,null));
            }
            else{
                winlytics_thankyou_simple.setText(thankYou);
                winlytics_thankyou_image.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(),R.drawable.sadheart,null));
            }
            winlytics_thankyou_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isShowing()){
                        dismiss();
                    }
                }
            });
            winlytics_thankyou_image.setColorFilter(selectionColor);
            winlytics_thankyou_detailed.setText(weReallyAppreciateYourFeedback);
            if(Winlytics.isAppOnScreen){
                show();
                final Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    public void run() {
                       if(isShowing()){
                           dismiss();
                       }
                        timer.cancel(); //this will cancel the timer of the system
                    }
                }, 2500);
            }
        }
    }
}
