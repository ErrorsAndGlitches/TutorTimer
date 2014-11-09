package com.TutorTimer.timer;

import android.content.Context;
import android.content.res.Resources;
import com.TutorTimer.R;

public class TimerFactory
{
    private static TimerFactory s_timerFactory;

    private final long m_incTimeAmount;
    private final long m_decTimeAmount;
    private       long m_resetDuration;

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

    public long getResetDuration()
    {
        return m_resetDuration;
    }

    public void setResetDuration(long resetDuration)
    {
        m_resetDuration = resetDuration;
    }

    public long getIncTimeAmount()
    {
        return m_incTimeAmount;
    }

    public long getDecTimeAmount()
    {
        return m_decTimeAmount;
    }

    public Timer newTimer()
    {
        return new Timer(m_resetDuration);
    }

    private TimerFactory(Context context)
    {
        Resources resources = context.getResources();
        m_incTimeAmount = resources.getInteger(R.integer.inc_time_amt);
        m_decTimeAmount = resources.getInteger(R.integer.dec_time_amt);
        m_resetDuration = resources.getInteger(R.integer.default_timer_duration);
    }
}
