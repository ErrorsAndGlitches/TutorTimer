package com.TutorTimer.ui.adapters;

import android.app.Activity;
import android.view.View;
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
    ViewHolder createViewHolder()
    {
        return new InactiveViewHolder();
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
                InactiveViewHolder viewHolder = (InactiveViewHolder) v.getTag();
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
                InactiveViewHolder viewHolder = (InactiveViewHolder) v.getTag();
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
                InactiveViewHolder viewHolder = (InactiveViewHolder) v.getTag();
                m_studentManager.moveStudent(StudentListType.INACTIVE, StudentListType.IMPORT, viewHolder.student);
            }
        };
    }

    private static final class InactiveViewHolder extends ViewHolder
    {
    }
}
