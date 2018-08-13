package com.creatively.grapeSalesApp.grapeapplication.manager;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;

import android.util.Log;
import android.widget.DatePicker;
import android.widget.TextView;

import com.creatively.grapeSalesApp.grapeapplication.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {
    private TextView textView;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog =
                new DatePickerDialog(Objects.requireNonNull(getActivity()),
                        this, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
        return datePickerDialog;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onDateSet(DatePicker view, int year, int month, int day) {
        Log.e("yea", year + "");
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0);
        cal.set(year, month, day, 0, 0, 0);
        Date chosenDate = cal.getTime();

        // Format the date using style medium and US locale
        Locale loc = new Locale("en", "US");
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", loc);
        String df_medium_us_str = format.format(chosenDate);
        Log.e("yea//", df_medium_us_str + "");

        if (getArguments() != null) {
            switch (Objects.requireNonNull(getArguments().getString("first"))) {
                case "start":
                    textView = Objects.requireNonNull(getActivity()).findViewById(R.id.startEditText);
                    textView.setText(df_medium_us_str);
                    textView.setError(null);
                    break;
                case "end":
                    textView = Objects.requireNonNull(getActivity()).findViewById(R.id.endEditText);
                    textView.setText(df_medium_us_str);
                    textView.setError(null);
                    break;
            }
        }

    }
}