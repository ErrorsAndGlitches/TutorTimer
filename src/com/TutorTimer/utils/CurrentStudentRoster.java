package com.TutorTimer.utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * A roster of the students sorted in the following levels:
 *   1) students with a running countdown timer
 *   2) time left on the countdown timer
 */
public class CurrentStudentRoster
{
    private final static CurrentStudentComparator s_studentComparator = new CurrentStudentComparator();
    private final List<CurrentStudentEntry> m_currentStudents;

    public CurrentStudentRoster()
    {
        m_currentStudents = new LinkedList<CurrentStudentEntry>();
    }

    public List<CurrentStudentEntry> getRoster()
    {
        // only allow the roster to modify the backing list
        return Collections.unmodifiableList(m_currentStudents);
    }

    public void addStudent(CurrentStudentEntry entry)
    {
        m_currentStudents.add(entry);
        resortRoster();
    }

    public void removeStudent(CurrentStudentEntry entry)
    {
        if (m_currentStudents.remove(entry))
        {
            resortRoster();
        }
    }

    public void notifyStudentStateChange()
    {
        resortRoster();
    }

    private void resortRoster()
    {
        Collections.sort(m_currentStudents, s_studentComparator);
    }

    private static class CurrentStudentComparator implements Comparator<CurrentStudentEntry>
    {
        @Override
        public int compare(CurrentStudentEntry lhs, CurrentStudentEntry rhs)
        {
            // if both timers are stopped, sort alphabetically
            if (lhs.isTimerStopped() && rhs.isTimerStopped())
            {
                return lhs.getStudent().getName().compareTo(rhs.getStudent().getName());
            }
            else if (lhs.isTimerStopped())
            {
                // this means that rhs's timer is not stopped
                return 1;
            }
            else if (rhs.isTimerStopped())
            {
                return -1;
            }
            else
            {
                return (int) (lhs.getTimeLeft() - rhs.getTimeLeft());
            }
        }
    }
}
