package com.TutorTimer.students;

public class Student
{
    private final String     m_name;
    private final long       m_id;
    private       long       m_resetTime;
    private       long       m_timeLeft;
    private       TimerState m_timerState;

    public enum TimerState
    {
        STARTED,
        STOPPED
    }

    Student(long id, String name, long resetTime)
    {
        m_id = id;
        m_name = name;
        m_resetTime = resetTime;
        m_timeLeft = m_resetTime;
        m_timerState = TimerState.STOPPED;
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

    public void setTimerState(TimerState newState)
    {
        m_timerState = newState;
    }

    public boolean isTimerStopped()
    {
        return TimerState.STOPPED == m_timerState;
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
}
