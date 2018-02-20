package winlytics.io.survey;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

/**
 * Created by Umur Kaya on 2/20/18.
 */

public interface WinlyticsBuilder {

    @RequiresApi(api = 19)
    WinlyticsBuilder withGeneratedUI(@Nullable Context context, boolean generateUI);

    @RequiresApi(api = 19)
    WinlyticsBuilder withModificationOption(boolean ableToChange);
}
