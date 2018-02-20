package winlytics.io.survey;


/**
 * Created by Umur Kaya on 2/19/18.
 */

class WinlyticsSurvey {
    /**
     * Generating Winlytics Survey from API
     */
    private int userId;
    private int surveyId;

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    private String temp;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(int surveyId) {
        this.surveyId = surveyId;
    }

    public boolean resetSurvey(){
        try{
            userId = 0;
            surveyId = 0;
            temp = null;
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
