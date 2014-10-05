package com.TutorTimer.timer;

import java.util.Date;

public class Timer
{
    private static final int SEC_TO_MS  = 1000;
    private static final int MIN_TO_SEC = 60;

    private Date m_timeOutTime;
    private int  m_resetDurationSec;

    Timer(int resetDurationSec)
    {
        m_resetDurationSec = resetDurationSec;
        reset();
    }

    public void setResetDurationSec(int resetDurationSec)
    {
        m_resetDurationSec = resetDurationSec;
    }

    public void reset()
    {
        m_timeOutTime = new Date(System.currentTimeMillis() + m_resetDurationSec * SEC_TO_MS);
    }

    @Override
    public String toString()
    {
        int diff = (int) (Math.max(0, m_timeOutTime.getTime() - new Date().getTime()) / SEC_TO_MS);
        int minutes = diff / MIN_TO_SEC;
        int seconds = diff % MIN_TO_SEC;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
