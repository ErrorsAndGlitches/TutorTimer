package com.example.TutorTimer.utils;

import android.content.Context;
import android.widget.Toast;

public final class Utils
{
    public static void shortToast(Context context, String format, Object... args)
    {
        toast(context, Toast.LENGTH_SHORT, format, args);
    }

    public static void longToast(Context context, String format, Object... args)
    {
        toast(context, Toast.LENGTH_LONG, format, args);
    }

    private static void toast(Context context, int duration, String format, Object... args)
    {
        Toast.makeText(context, String.format(format, args), duration).show();
    }

    private Utils()
    {
    }
}
