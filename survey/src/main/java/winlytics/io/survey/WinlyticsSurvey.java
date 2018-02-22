package winlytics.io.survey;


/**
 * Created by Umur Kaya on 2/19/18.
 */

public class WinlyticsSurvey {
    /**
     * Generating Winlytics Survey from API
     */
    private int userId;
    private int surveyId;
    private String imageUrl;
    private String temp;

    String getBrandName() {return brandName;}
    void setBrandName(String brandName) {this.brandName = brandName;}
    private String brandName;
    String getTemp() {
        return temp;
    }
    void setTemp(String temp) {
        this.temp = temp;
    }
    int getUserId() {
        return userId;
    }
    void setUserId(int userId) {
        this.userId = userId;
    }
    int getSurveyId() {
        return surveyId;
    }
    void setSurveyId(int surveyId) {
        this.surveyId = surveyId;
    }
    String getImageUrl() {
        return imageUrl;
    }
    void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    boolean resetSurvey(){
        try{
            userId = 0;
            surveyId = 0;
            temp = null;
            imageUrl = null;
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
