package com.example.TutorTimer.Logger;

public class Logger
{
    public static void log(Class className, String format, Object ... args)
    {
        SharedLogger.getInstance().log(className, format, args);
    }

    public static void log(Class className, String msg)
    {
        SharedLogger.getInstance().log(className, msg);
    }
}
