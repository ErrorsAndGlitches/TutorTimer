package com.TutorTimer.utils;

import com.TutorTimer.students.Student;

/**
 * Contains a student and a timer
 */
public class CurrentStudentEntry
{
    private final Student m_student;
    private       long    m_resetTime;
    private       long    m_timeLeft;
    private       boolean m_isStopped;

    public CurrentStudentEntry(Student student, long resetTime)
    {
        m_student = student;
        m_resetTime = resetTime;
        m_timeLeft = m_resetTime;
        m_isStopped = true;
    }

    public Student getStudent()
    {
        return m_student;
    }

    public long getResetTime()
    {
        return m_resetTime;
    }

    public void addToResetTime(long delta)
    {
        long newResetTime = m_resetTime + delta;
        if (newResetTime >= 0)
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

    public void setTimerStarted()
    {
        m_isStopped = false;
    }

    public void setTimerStopped()
    {
        m_isStopped = true;
    }

    public boolean isTimerStopped()
    {
        return m_isStopped;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CurrentStudentEntry that = (CurrentStudentEntry) o;

        if (m_student != null ? !m_student.equals(that.m_student) : that.m_student != null) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        return m_student != null ? m_student.hashCode() : 0;
    }
}
