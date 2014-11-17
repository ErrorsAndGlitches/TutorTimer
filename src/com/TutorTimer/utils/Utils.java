package com.TutorTimer.utils;

import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

public final class Utils
{
    public static void setTextViewToTime(TextView textView, long time)
    {
        long resetTimeSec = TimeUnit.MILLISECONDS.toSeconds(time);
        long minutes = TimeUnit.SECONDS.toMinutes(resetTimeSec);
        long seconds = resetTimeSec - TimeUnit.MINUTES.toSeconds(minutes);
        textView.setText(String.format("%02d:%02d", minutes, seconds));
    }

    public static void shortToast(Context context, String format, Object... args)
    {
        toast(context, Toast.LENGTH_SHORT, String.format(format, args));
    }

    public static void longToast(Context context, String format, Object... args)
    {
        toast(context, Toast.LENGTH_LONG, String.format(format, args));
    }

    public static void shortToast(Context context, String msg)
    {
        toast(context, Toast.LENGTH_SHORT, msg);
    }

    public static void longToast(Context context, String msg)
    {
        toast(context, Toast.LENGTH_LONG, msg);
    }

    private static void toast(Context context, int duration, String msg)
    {
        Toast.makeText(context, msg, duration).show();
    }

    private Utils()
    {
    }
}
