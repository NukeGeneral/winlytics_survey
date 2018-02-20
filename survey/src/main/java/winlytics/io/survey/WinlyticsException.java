package winlytics.io.survey;

/**
 * Created by Umur Kaya on 2/20/18.
 */

class WinlyticsException extends IllegalStateException {

    WinlyticsException(String detailMessage) throws IllegalStateException{
        super(detailMessage);
    }
}
