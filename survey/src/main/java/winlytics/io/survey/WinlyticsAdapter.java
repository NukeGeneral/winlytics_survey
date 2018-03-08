package winlytics.io.survey;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Created by Umur Kaya on 2/20/18.
 */

class WinlyticsAdapter extends Dialog{

    private final TextView winlytics_head_question,winlytics_optional_text_title_area, winlytics_afteranswer_head,powered_by_text;
    private Button referenceHolder;
    private final Button button0,button1,button2,button3,button4,button5,button6,button7,button8,button9,button10,winlytics_submit;
    private final LinearLayout winlytics_optional_text_area;
    private final EditText winlytics_optional_edit_text_area;
    private final ImageButton winlytics_cancel_action;
    private final ScrollView winlytics_scroll;
    private String resultNumber,sorryToHearThat,thanks,whyDidYouChoose,weWillUseYourFeedback
            ,thankYou,thanksAgain,weReallyAppreciateYourFeedback,concatenatedFeedback = null;
    private Context context;
    Dialog dialog;
    private int selectionColor;
    private GradientDrawable solidDrawable = new GradientDrawable();
    private GradientDrawable withBorderDrawable = new GradientDrawable();

    interface WinlyticsAdapterNotifier{
        void notifyAdapterIsReady();
    }

    WinlyticsAdapter(WinlyticsAdapterNotifier mListener,final Context context){
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
        winlytics_cancel_action = (ImageButton) dialog.findViewById(R.id.winlytics_cancel_action);
        powered_by_text = (TextView) dialog.findViewById(R.id.powered_by_text);
        //Sad and Happy Buttons are based on NPS system

        //Set Button UnHappy Listener

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
                //Send result
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
                //Send result

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
                String resultText = winlytics_optional_edit_text_area.getText().toString();
                //Send resultText and resultNumber to service
                dialog.dismiss();
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

        winlytics_optional_edit_text_area.setBackground(solidDrawable);
        winlytics_submit.setBackground(withBorderDrawable);
    }

    private static float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }
}
