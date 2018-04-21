package com.example.fatima.grapeapplication.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;


public class FontManager {

    private static final String ROOT = "fonts/",
            FONTAWESOME = ROOT + "droid_kufi_regular.otf";
    public static final String URL = "http://grapessales.com/grapeApplication/api/";
    public static final String IMAGE_URL = "http://grapessales.com/grapeApplication/public/uploads/";
    public static final String KEY_LANG_AR = "ar";
    private static final String KEY_LANG_EN_US = "en-us";
    private static final String KEY_LANG_EN = "en";

    public static Typeface getTypeface(Context context) {
        return Typeface.createFromAsset(context.getAssets(), FONTAWESOME);

    }

    public static Typeface getTypefaceTextInputRegular(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "fonts/droid_kufi_regular.ttf");
    }

    public static Typeface getTypefaceTextInputBold(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "fonts/droid_kufi_bold.ttf");
    }

    public static void logOut(Context context) {
        AppPreferences.clearAll(context);
//        Intent intent = new Intent(context, SelectActivity.class);
//        intent.setAction("logout");
//        context.startActivity(intent);
        ((Activity) context).finish();

    }

    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * (metrics.densityDpi / 160f);
    }

    public static boolean isEnglish(Context context) {
        String lang = context.getResources().getConfiguration().locale.toString();
        if (lang.equals(KEY_LANG_EN_US) || lang.equals(KEY_LANG_EN))
            return true;
        else
            return false;
    }

    public static void strikeThroughText(TextView price) {
        price.setPaintFlags(price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    }

    public static void applyFont(final Context context, final View v) {
        Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/droid_kufi_regular.ttf");
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    applyFont(context, child);
                }
            } else if (v instanceof TextView) {
                ((TextView) v).setTypeface(font);
            } else if (v instanceof EditText) {
                ((EditText) v).setTypeface(font);
            } else if (v instanceof Button) {
                ((Button) v).setTypeface(font);
            } else if (v instanceof RadioButton) {
                ((RadioButton) v).setTypeface(font);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // ignore
        }
    }

    public static boolean checkPermission(Context context, String permission) {
        if (context == null) return false;
        int res;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            res = context.checkSelfPermission(permission);
            return (res == PackageManager.PERMISSION_GRANTED);
        } else {
            return true;
        }
    }

    public static void hideKeyboard(Context ctx) {
        InputMethodManager inputManager = (InputMethodManager) ctx
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View v = ((Activity) ctx).getCurrentFocus();
        if (v == null)
            return;

        inputManager.hideSoftInputFromWindow(v.getRootView().getWindowToken(), 0);
    }
}