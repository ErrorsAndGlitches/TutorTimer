package com.TutorTimer.ui.adapters;

import android.app.Activity;
import android.view.View;
import com.TutorTimer.Logger.Logger;
import com.TutorTimer.students.Student;
import com.TutorTimer.students.StudentManager.StudentListType;

import java.util.List;

/**
 * Adapter for the inactive countdown students list
 */
public class InactiveStudentsArrayAdapter extends StudentsArrayAdapter
{
    private static final String START_STRING = "Start";

    public InactiveStudentsArrayAdapter(Activity activity,
                                        int resource,
                                        List<Student> studentList)
    {
        super(activity, resource, studentList);
    }

    @Override
    String getStartPauseButtonText()
    {
        return START_STRING;
    }

    @Override
    View.OnClickListener getResetClickListener()
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ViewHolder viewHolder = (ViewHolder) v.getTag();
                Logger.log(View.OnClickListener.class, "Resetting the timer for student %s", viewHolder.student);
                viewHolder.student.setTimeLeft(viewHolder.student.getResetTime());
                resetBackgroundColor(viewHolder);
                resetTimeLeftTextView(viewHolder);
            }
        };
    }

    @Override
    View.OnClickListener getStartPauseClickListener()
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ViewHolder viewHolder = (ViewHolder) v.getTag();
                Logger.log(View.OnClickListener.class, "Starting the timer for student %s", viewHolder.student);
                m_studentManager.moveStudent(StudentListType.INACTIVE, StudentListType.ACTIVE, viewHolder.student);
            }
        };
    }

    @Override
    View.OnClickListener getRemoveClickListener()
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ViewHolder viewHolder = (ViewHolder) v.getTag();
                Logger.log(View.OnClickListener.class, "Removing student %s from inactive to import", viewHolder.student);
                m_studentManager.moveStudent(StudentListType.INACTIVE, StudentListType.IMPORT, viewHolder.student);
            }
        };
    }
}
