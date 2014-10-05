package com.TutorTimer.Logger;

public class Logger
{
    public static void log(Object object, String format, Object ... args)
    {
        SharedLogger.getInstance().log(object, format, args);
    }

    public static void log(Object object, String msg)
    {
        SharedLogger.getInstance().log(object, msg);
    }
}
