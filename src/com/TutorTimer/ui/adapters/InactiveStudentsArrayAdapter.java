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
    public InactiveStudentsArrayAdapter(Activity activity,
                                        int resource,
                                        List<Student> studentList)
    {
        super(activity, resource, studentList);
    }

    @Override
    View.OnClickListener getResetTimeClickListener()
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
    FlingListener getFlingListener()
    {
        return new FlingListener()
        {
            @Override
            public boolean isLeftFlingable()
            {
                return true;
            }

            @Override
            public boolean isRightFlingable()
            {
                return true;
            }

            @Override
            public void onFlingLeftAnimationEnd(View view)
            {
                ViewHolder viewHolder = (ViewHolder) view.getTag();
                Logger.log(View.OnClickListener.class, "Starting the timer for student %s", viewHolder.student);
                m_studentManager.moveStudent(StudentListType.INACTIVE, StudentListType.ACTIVE, viewHolder.student);
            }

            @Override
            public void onFlingRightAnimationEnd(View view)
            {
                ViewHolder viewHolder = (ViewHolder) view.getTag();
                Logger.log(View.OnClickListener.class, "Deleting the student %s", viewHolder.student);
                m_studentManager.removeImportStudent(viewHolder.student);
            }
        };
    }
}
