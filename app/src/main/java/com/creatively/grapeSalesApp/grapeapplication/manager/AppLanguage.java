package com.creatively.grapeSalesApp.grapeapplication.manager;

import android.app.Activity;
import android.content.Context;

import java.util.Locale;

public class AppLanguage {

    public final static String ARABIC = "ar";
    public final static String PERSIAN = "fa";

    public static String getLanguage(Context context) {
        return AppPreferences.getString(context, "language", Locale.getDefault().getLanguage());
    }

    public static void saveLanguage(Context context, String language) {
        AppPreferences.saveString(context, "language", language);
    }

    private static void changeLang(Context context, String lang) {
        if (lang.equalsIgnoreCase(""))
            return;
        Locale myLocale = new Locale(lang);
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        ((Activity) context).getBaseContext().getResources().updateConfiguration(config,
                ((Activity) context).getBaseContext().getResources().getDisplayMetrics());
//        updateTexts();
    }
}
