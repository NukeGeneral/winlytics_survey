package winlytics.io.survey;

import android.support.annotation.RequiresApi;

/**
 * Created by Umur Kaya on 2/20/18.
 */

public interface WinlyticsBuilder {

    @RequiresApi(api = 19)
    WinlyticsBuilder withGeneratedUI(boolean generateUI);

    @RequiresApi(api = 19)
    WinlyticsBuilder withModificationOption(boolean ableToChange);
}
