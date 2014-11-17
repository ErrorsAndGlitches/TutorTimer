package com.TutorTimer.students;

import com.TutorTimer.Logger.Logger;
import com.TutorTimer.utils.CountDownTimer;

import java.util.concurrent.TimeUnit;

public class Student
{
    private final String                m_name;
    private final long                  m_id;
    private       long                  m_resetTime;
    private       long                  m_timeLeft;
    private       StudentCountDownTimer m_countDownTimer;

    public interface CountDownCallback
    {
        public void onTick(long millisUntilFinished);
        public void onFinish();
    }

    Student(long id, String name, long resetTime)
    {
        m_id = id;
        m_name = name;
        m_resetTime = resetTime;
        m_timeLeft = m_resetTime;
        m_countDownTimer = null;
    }

    public long getId()
    {
        return m_id;
    }

    public String getName()
    {
        return m_name;
    }

    public long getResetTime()
    {
        return m_resetTime;
    }

    public void addToResetTime(long delta)
    {
        long newResetTime = m_resetTime + delta;
        if (newResetTime > 0)
        {
            m_resetTime = newResetTime;
        }
    }

    public long getTimeLeft()
    {
        return m_timeLeft;
    }

    public void setTimeLeft(long timeLeft)
    {
        m_timeLeft = timeLeft;
    }

    public void startTimer(CountDownCallback callback)
    {
        if (m_countDownTimer == null)
        {
            Logger.log(this, "Starting countdown for student %s", this);
            m_countDownTimer = new StudentCountDownTimer(callback);
            m_countDownTimer.start();
        }
        else
        {
            Logger.log(this, "Failed to start countdown for student %s because countdown already exists", this);
        }
    }

    public void stopTimer()
    {
        if (m_countDownTimer != null)
        {
            Logger.log(this, "Stopping countdown for student %s", this);
            m_countDownTimer.cancel();
            m_countDownTimer = null;
        }
        else
        {
            Logger.log(this, "Failed to stop countdown for student %s because countdown does not exist", this);
        }
    }

    public boolean isTimerRunning()
    {
        return m_countDownTimer != null;
    }

    public void setTimerCallback(CountDownCallback callback)
    {
        if (m_countDownTimer != null)
        {
            m_countDownTimer.m_callback = callback;
        }
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Student student = (Student) o;

        if (m_id != student.m_id) return false;
        if (m_name != null ? !m_name.equals(student.m_name) : student.m_name != null) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = m_name != null ? m_name.hashCode() : 0;
        result = 31 * result + (int) (m_id ^ (m_id >>> 32));
        return result;
    }

    @Override
    public String toString()
    {
        return m_name;
    }

    private final class StudentCountDownTimer extends CountDownTimer
    {
        private CountDownCallback m_callback;

        private StudentCountDownTimer(CountDownCallback callback)
        {
            super(m_timeLeft, TimeUnit.SECONDS.toMillis(1L));
            m_callback = callback;
        }

        @Override
        public void onTick(long millisUntilFinished)
        {
            Logger.log(this, "Updating the tick time: %d", millisUntilFinished);
            setTimeLeft(millisUntilFinished);
            if (m_callback != null)
            {
                m_callback.onTick(millisUntilFinished);
            }
        }

        @Override
        public void onFinish()
        {
            Logger.log(this, "Count down timer has finished");
            setTimeLeft(0L);
            if (m_callback != null)
            {
                m_callback.onFinish();
            }
        }
    }
}
