package com.TutorTimer.timer;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Timer
{
    private Date m_timeOutTime;
    private long m_resetDuration;

    Timer(long resetDuration)
    {
        m_resetDuration = resetDuration;
        reset();
    }

    public void reset()
    {
        m_timeOutTime = new Date(System.currentTimeMillis() + m_resetDuration);
    }

    @Override
    public String toString()
    {
        long diff = Math.max(0L, m_timeOutTime.getTime() - new Date().getTime());

        long diffSec = TimeUnit.MILLISECONDS.toSeconds(diff);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
        long seconds = diffSec - TimeUnit.MINUTES.toSeconds(minutes);
        return String.format("%02d:%02d", minutes, seconds);
    }
}
