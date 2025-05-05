package com.lephiha.do_an.Helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.lephiha.do_an.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class Tooltip {

    public static String getToday() {
        TimeZone timeZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh");
        Calendar calendar = Calendar.getInstance(timeZone);

        int date = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);

        String dateValue = String.valueOf(date);
        if (date < 10) {
            dateValue = "0" + dateValue;
        }

        String monthValue = String.valueOf(month);
        if (month < 10) {
            monthValue = "0" + monthValue;
        }
        String yearValue = String.valueOf(year);

        return yearValue + "-" + monthValue + "-" + dateValue;
    }

    public static String getReadableToday(Context context) {
        TimeZone timeZone = TimeZone.getTimeZone("Asia/Ho_Chi_Minh");
        Calendar calendar = Calendar.getInstance(timeZone);

        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int date = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);

        // Lấy ngày tuần theo ngôn ngữ hiện tại
        String dayValue = getDayOfWeek(context, day);

        // Thêm số 0 vào trước ngày và tháng < 10
        String dateValue = String.valueOf(date);
        if (date < 10) {
            dateValue = "0" + dateValue;
        }
        String monthValue = String.valueOf(month);
        if (month < 10) {
            monthValue = "0" + monthValue;
        }
        String yearValue = String.valueOf(year);

        return dayValue + ", " + dateValue + "/" + monthValue + "/" + yearValue;
    }

    private static String getDayOfWeek(Context context, int day) {
        switch (day) {
            case 1:
                return context.getString(R.string.sunday);
            case 2:
                return context.getString(R.string.monday);
            case 3:
                return context.getString(R.string.tuesday);
            case 4:
                return context.getString(R.string.wednesday);
            case 5:
                return context.getString(R.string.thursday);
            case 6:
                return context.getString(R.string.friday);
            case 7:
                return context.getString(R.string.saturday);
            default:
                return context.getString(R.string.monday); // Giá trị mặc định
        }
    }

    @SuppressLint("SimpleDateFormat")
    public static String beautifierDatetime(Context context, String input) {
        if (input.length() != 19) {
            return "Tooltip - beautifierDatetime - error: value is not valid " + input.length();
        }
        String output = "";

        String dateInput = input.substring(0, 10);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        String dateOutput = "";
        DateFormat dateFormat = new SimpleDateFormat("EE, dd-MM-yyyy");

        try {
            Date dateFormated = formatter.parse(dateInput);
            assert dateFormated != null;
            dateOutput = dateFormat.format(dateFormated);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String timeOutput = input.substring(11, 16);
        output = dateOutput + " " + context.getString(R.string.at) + " " + timeOutput;

        return output;
    }

    public static long getDataDifference(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    public static void setLocale(Context context, SharedPreferences sharedPreferences) {
        // Lấy ngôn ngữ từ SharedPreferences
        String language = sharedPreferences.getString("language", context.getString(R.string.vietnamese));

        // Xác định Locale dựa trên ngôn ngữ
        Locale myLocale;
        if (Objects.equals(language, context.getString(R.string.vietnamese))) {
            myLocale = new Locale("vi");
        } else if (Objects.equals(language, context.getString(R.string.deutsch))) {
            myLocale = new Locale("de");
        } else {
            myLocale = new Locale("en"); // Mặc định là tiếng Anh
        }

        // Cập nhật Locale
        Locale.setDefault(myLocale);
        Resources resources = context.getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(myLocale);
        resources.updateConfiguration(configuration, displayMetrics);
    }
}