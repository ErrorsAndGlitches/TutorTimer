package com.example.TutorTimer.timer;

import android.content.Context;
import com.example.TutorTimer.R;

public class TimerFactory
{
    private static TimerFactory s_timerFactory;

    private int m_resetDurationSec;

    public static TimerFactory getInstance(Context context)
    {
        if (s_timerFactory == null)
        {
            synchronized (TimerFactory.class)
            {
                if (s_timerFactory == null)
                {
                    s_timerFactory = new TimerFactory(context);
                }
            }
        }

        return s_timerFactory;
    }

    public int getResetDurationSec()
    {
        return m_resetDurationSec;
    }

    public void setResetDurationSec(int resetDurationSec)
    {
        m_resetDurationSec = resetDurationSec;
    }

    public Timer newTimer()
    {
        return new Timer(m_resetDurationSec);
    }

    private TimerFactory(Context context)
    {
        m_resetDurationSec = context.getResources().getInteger(R.integer.default_timer_duration_sec);
    }
}
