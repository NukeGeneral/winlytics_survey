package winlytics.io.survey;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

/**
 * Created by Umur Kaya on 2/20/18.
 */

public interface WinlyticsBuilder{

    @RequiresApi(api = 19)
    WinlyticsBuilder withGeneratedUI(@NonNull Context context, boolean isAbleToModify);
}
