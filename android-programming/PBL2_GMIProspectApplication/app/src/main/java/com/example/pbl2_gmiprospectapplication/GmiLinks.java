package com.example.pbl2_gmiprospectapplication;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

final class GmiLinks {
    static final String DIPLOMA_PROGRAMMES = "https://www.gmi.edu.my/diploma-programmes/";
    static final String DIPLOMA_ADMISSION = "https://www.gmi.edu.my/diploma-programme-admission/";
    static final String GAPP_ADMISSION = "https://www.gmi.edu.my/a-level-programme-admission/";
    static final String GUFP = "https://www.gmi.edu.my/gmi-utp-foundation-studies-programme/";
    static final String APPLICATION = "https://gmi.vialing.com/oa/login";
    static final String WEBSITE = "https://www.gmi.edu.my/";

    private GmiLinks() { }

    static void openWeb(Context context, String url) {
        launch(context, new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    static void call(Context context) {
        launch(context, new Intent(Intent.ACTION_DIAL, Uri.parse("tel:+60389219000")));
    }

    static void email(Context context) {
        launch(context, new Intent(Intent.ACTION_SENDTO,
                Uri.parse("mailto:marketing@gmi.edu.my")));
    }

    private static void launch(Context context, Intent intent) {
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException exception) {
            Toast.makeText(context, R.string.no_app_to_open, Toast.LENGTH_SHORT).show();
        }
    }
}
