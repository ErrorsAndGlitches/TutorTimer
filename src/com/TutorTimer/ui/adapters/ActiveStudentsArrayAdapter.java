package com.TutorTimer.ui.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.TutorTimer.Logger.Logger;
import com.TutorTimer.students.Student;
import com.TutorTimer.students.StudentManager;
import com.TutorTimer.students.StudentManager.StudentListType;
import com.TutorTimer.utils.Utils;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Adapter for the active countdown students list
 */
public class ActiveStudentsArrayAdapter extends StudentsArrayAdapter
{
    private static final int NO_TIME_LEFT_SEC = 15;

    public ActiveStudentsArrayAdapter(Activity activity,
                                      int resource,
                                      List<Student> studentList)
    {
        super(activity, resource, studentList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = super.getView(position, convertView, parent);
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        Student student = viewHolder.student;

        Logger.log(this,
                   "Getting view for position %d, view %s, viewholder %s, student %s",
                   position,
                   view,
                   viewHolder,
                   student);

        // check if the timer has been started
        ActiveStudentCountdownCallback callback = new ActiveStudentCountdownCallback(m_activity,
                                                                                     view,
                                                                                     viewHolder.timeLeftTextView,
                                                                                     viewHolder.defaultColor);
        if (student.isTimerRunning())
        {
            student.setTimerCallback(callback);
        }
        else
        {
            student.startTimer(callback);
        }

        return view;
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
                Student student = viewHolder.student;

                Logger.log(View.OnClickListener.class, "Resetting the timer for student %s", student);
                student.stopTimer();
                student.setTimeLeft(student.getResetTime());
                resetBackgroundColor(viewHolder);
                student.startTimer(new ActiveStudentCountdownCallback(m_activity,
                                                                      viewHolder.entryView,
                                                                      viewHolder.timeLeftTextView,
                                                                      viewHolder.defaultColor));

                // the reset time may rearrange the entries in the backing list thus it needs to be resorted
                StudentManager.getInstance(m_activity).resortActiveStudentList();
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
                Student student = viewHolder.student;

                Logger.log(View.OnClickListener.class, "Removing student %s from active to import", student);
                student.stopTimer();
                m_studentManager.moveStudent(StudentListType.ACTIVE, StudentListType.IMPORT, student);
            }
        };
    }

    private static class ActiveStudentCountdownCallback implements Student.CountDownCallback
    {
        private static final int RUNNING_OUT_OF_TIME_COLOR = Color.RED;

        private final Activity m_activity;
        private final View     m_entryView;
        private final TextView m_timeLeftTextView;
        private final int      m_defaultColor;

        private ActiveStudentCountdownCallback(Activity activity,
                                               View entryView,
                                               TextView timeLeftTextView,
                                               int defaultColor)
        {
            m_activity = activity;
            m_entryView = entryView;
            m_timeLeftTextView = timeLeftTextView;
            m_defaultColor = defaultColor;
        }

        @Override
        public void onTick(long millisUntilFinished)
        {
            updateTimeLeft(millisUntilFinished);
        }

        @Override
        public void onFinish()
        {
            m_entryView.setBackgroundColor(RUNNING_OUT_OF_TIME_COLOR);
            updateTimeLeft(0L);
        }

        private void updateTimeLeft(long millis)
        {
            Utils.setTextViewToTime(m_timeLeftTextView, millis);

            // update the color if it's running out of time
            long timeLeftSec = TimeUnit.MILLISECONDS.toSeconds(millis);
            if (timeLeftSec <= NO_TIME_LEFT_SEC)
            {
                if (timeLeftSec % 2 == 0)
                {
                    m_entryView.setBackgroundColor(RUNNING_OUT_OF_TIME_COLOR);
                }
                else
                {
                    m_entryView.setBackgroundColor(m_defaultColor);
                }
            }

            updateTextView();
        }

        private void updateTextView()
        {
            m_activity.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    m_timeLeftTextView.invalidate();
                }
            });
        }
    }
}
