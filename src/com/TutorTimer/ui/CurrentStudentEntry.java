package com.TutorTimer.ui;

import com.TutorTimer.students.Student;
import com.TutorTimer.timer.Timer;

/**
 * Contains a student and a timer
 */
public class CurrentStudentEntry
{
    public final Student student;
    public final Timer   timer;

    public CurrentStudentEntry(Student student, Timer timer)
    {
        this.student = student;
        this.timer = timer;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CurrentStudentEntry that = (CurrentStudentEntry) o;

        if (student != null ? !student.equals(that.student) : that.student != null) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        return student != null ? student.hashCode() : 0;
    }
}
