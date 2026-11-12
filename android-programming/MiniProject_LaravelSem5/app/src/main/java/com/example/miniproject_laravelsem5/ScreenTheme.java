package com.example.miniproject_laravelsem5;

import android.app.Activity;
import android.content.res.Configuration;

import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

final class ScreenTheme {
    private ScreenTheme() {}

    static void updateSystemBars(Activity activity) {
        int nightMode = activity.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        boolean light = nightMode != Configuration.UI_MODE_NIGHT_YES;
        WindowInsetsControllerCompat controller =
                WindowCompat.getInsetsController(activity.getWindow(), activity.getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(light);
        controller.setAppearanceLightNavigationBars(light);
    }
}
