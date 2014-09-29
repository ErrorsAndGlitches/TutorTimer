package com.example.TutorTimer.utils;

import android.content.Context;
import android.widget.Toast;

public final class Utils
{
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
