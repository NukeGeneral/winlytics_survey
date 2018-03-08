package winlytics.io.survey;


/**
 * Created by Umur Kaya on 2/19/18.
 */

class WinlyticsSurvey {
    /**
     * Generating Winlytics Survey from API
     */

    private String submitToken;
    private String brandColor;
    private String canYouAdvice;
    private String sorryToHearThat;
    private String thanks;
    private String whyDidYouChoose;
    private String weWillUseYourFeedback;
    private String feedbackPlaceholder;
    private String submit;
    private String thankYou;
    private String thanksAgain;
    private String weReallyAppreciateYourFeedback;

    String getCanYouAdvice() {
        return canYouAdvice;
    }

    void setCanYouAdvice(String canYouAdvice) {
        this.canYouAdvice = canYouAdvice;
    }

    String getSorryToHearThat() {
        return sorryToHearThat;
    }

    void setSorryToHearThat(String sorryToHearThat) {
        this.sorryToHearThat = sorryToHearThat;
    }

    String getThanks() {
        return thanks;
    }

    void setThanks(String thanks) {
        this.thanks = thanks;
    }

    String getWhyDidYouChoose() {
        return whyDidYouChoose;
    }

    void setWhyDidYouChoose(String whyDidYouChoose) {
        this.whyDidYouChoose = whyDidYouChoose;
    }

    String getWeWillUseYourFeedback() {
        return weWillUseYourFeedback;
    }

    void setWeWillUseYourFeedback(String weWillUseYourFeedback) {
        this.weWillUseYourFeedback = weWillUseYourFeedback;
    }

    String getFeedbackPlaceholder() {
        return feedbackPlaceholder;
    }

    void setFeedbackPlaceholder(String feedbackPlaceholder) {
        this.feedbackPlaceholder = feedbackPlaceholder;
    }

    String getSubmit() {
        return submit;
    }

    void setSubmit(String submit) {
        this.submit = submit;
    }

    String getThankYou() {
        return thankYou;
    }

    void setThankYou(String thankYou) {
        this.thankYou = thankYou;
    }

    String getThanksAgain() {
        return thanksAgain;
    }

    void setThanksAgain(String thanksAgain) {
        this.thanksAgain = thanksAgain;
    }

    String getWeReallyAppreciateYourFeedback() {
        return weReallyAppreciateYourFeedback;
    }

    void setWeReallyAppreciateYourFeedback(String weReallyAppreciateYourFeedback) {
        this.weReallyAppreciateYourFeedback = weReallyAppreciateYourFeedback;
    }

    String getSubmitToken() {
        return submitToken;
    }

    void setSubmitToken(String submitToken) {
        this.submitToken = submitToken;
    }

    String getBrandColor() {
        return brandColor;
    }

    void setBrandColor(String brandColor) {
        this.brandColor = brandColor;
    }

    boolean resetSurvey(){
        try{
            setCanYouAdvice(null);
            setSorryToHearThat(null);
            setThanks(null);
            setWhyDidYouChoose(null);
            setWeWillUseYourFeedback(null);
            setFeedbackPlaceholder(null);
            setSubmit(null);
            setThankYou(null);
            setThanksAgain(null);
            setWeReallyAppreciateYourFeedback(null);
            setSubmitToken(null);
            setBrandColor(null);
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
