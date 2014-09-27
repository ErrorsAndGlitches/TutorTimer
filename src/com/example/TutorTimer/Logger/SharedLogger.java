package com.example.TutorTimer.Logger;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
 * Logger shared by all classes
 */
class SharedLogger
{
    private static final String OUTPUT_FILE = "tutor.log";

    private static SharedLogger s_sharedLogger;
    private        FileWriter   m_output;

    static SharedLogger getInstance()
    {
        if (s_sharedLogger == null)
        {
            synchronized (SharedLogger.class)
            {
                if (s_sharedLogger == null)
                {
                    s_sharedLogger = new SharedLogger();
                }
            }
        }

        return s_sharedLogger;
    }

    public void log(Class className, String format, Object ... args)
    {
        if (m_output != null)
        {
            String msg = String.format("%s [%s] %s: %s\n", new Date(), Thread.currentThread().getId(),
                                       className.getSimpleName(), String.format(format, args));
            try
            {
                m_output.write(msg);
                m_output.flush();
            }
            catch (IOException e)
            {
                Log.e("TutorTimer", "Failed to write to log file.", e);
            }
        }
    }

    private SharedLogger()
    {
        m_output = null;

        File fileDir = Environment.getExternalStorageDirectory();
        if (!fileDir.exists())
        {
            if (fileDir.mkdirs())
            {
                Log.e("TutorTimer", "Failed to make directory to store log files.");
                return;
            }
        }

        String outputLogFileName = fileDir.getAbsoluteFile().toString() + "/" + OUTPUT_FILE;
        File outputFile = new File(outputLogFileName);
        try
        {
            m_output = new FileWriter(outputFile, true);
        }
        catch (IOException e)
        {
            Log.e("TutorTimer", "Failed to open log file for writing.", e);
        }
    }
}
