package com.TutorTimer.ui.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.TutorTimer.Logger.Logger;
import com.TutorTimer.students.Student;
import com.TutorTimer.students.Student.TimerState;
import com.TutorTimer.students.StudentManager.StudentListType;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Adapter for the active countdown students list
 */
public class ActiveStudentsArrayAdapter extends StudentsArrayAdapter
{
    private static final String PAUSE_STRING     = "Pause";
    private static final int    NO_TIME_LEFT_SEC = 15;

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
        ActiveViewHolder viewHolder = (ActiveViewHolder) view.getTag();

        // check if the timer has been started
        if (viewHolder.countDownTimer == null)
        {
            // start the new timer
            startCountdown(m_activity, viewHolder);
        }

        return view;
    }

    @Override
    ViewHolder createViewHolder()
    {
        return new ActiveViewHolder();
    }

    @Override
    String getStartPauseButtonText()
    {
        return PAUSE_STRING;
    }

    @Override
    View.OnClickListener getResetClickListener()
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ActiveViewHolder viewHolder = (ActiveViewHolder) v.getTag();
                stopCountdown(viewHolder);
                viewHolder.student.setTimeLeft(viewHolder.student.getResetTime());
                resetBackgroundColor(viewHolder);
                startCountdown(m_activity, viewHolder);
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
                ActiveViewHolder viewHolder = (ActiveViewHolder) v.getTag();
                stopCountdown(viewHolder);
                m_studentManager.moveStudent(StudentListType.ACTIVE, StudentListType.INACTIVE, viewHolder.student);
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
                ActiveViewHolder viewHolder = (ActiveViewHolder) v.getTag();
                stopCountdown(viewHolder);
                m_studentManager.moveStudent(StudentListType.ACTIVE, StudentListType.IMPORT, viewHolder.student);
            }
        };
    }

    private static void stopCountdown(ActiveViewHolder viewHolder)
    {
        Logger.log(ActiveStudentsArrayAdapter.class, "Stopping countdown for student %s", viewHolder.student);
        if (viewHolder.countDownTimer != null)
        {
            viewHolder.countDownTimer.cancel();
            viewHolder.countDownTimer = null;
        }
        viewHolder.student.setTimerState(TimerState.STOPPED);
    }

    private static void startCountdown(Activity activity, ActiveViewHolder viewHolder)
    {
        Logger.log(ActiveStudentsArrayAdapter.class, "Starting countdown for student %s", viewHolder.student);
        viewHolder.countDownTimer = new StudentCountDownTimer(activity,
                                                              viewHolder.entryView,
                                                              viewHolder.timeLeftTextView,
                                                              viewHolder.student,
                                                              viewHolder.defaultColor);
        viewHolder.student.setTimerState(TimerState.STARTED);
        viewHolder.countDownTimer.start();
    }

    private static class ActiveViewHolder extends StudentsArrayAdapter.ViewHolder
    {
        // countdown timer
        StudentCountDownTimer countDownTimer;
    }

    private static final class StudentCountDownTimer extends CountDownTimer
    {
        private static final int RUNNING_OUT_OF_TIME_COLOR = Color.RED;

        private final Activity       m_activity;
        private final View           m_entryView;
        private final TextView       m_timeLeftTextView;
        private final Student m_student;
        private final int            m_defaultColor;

        public StudentCountDownTimer(Activity activity,
                                     View entryView,
                                     TextView timeLeftTextView,
                                     Student student,
                                     int defaultColor)
        {
            super(student.getTimeLeft(), TimeUnit.SECONDS.toMillis(1L));
            m_activity = activity;
            m_entryView = entryView;
            m_timeLeftTextView = timeLeftTextView;
            m_student = student;
            m_defaultColor = defaultColor;
        }

        @Override
        public void onTick(long millisUntilFinished)
        {
            // there is some issue where the count down timer is continuing to go even though it should have been canceled
            /*
            if (!m_student.isTimerStopped())
            {
            }
            else
            {
                cancel();
            }
            */
            Logger.log(this, "Updating the tick time: %d", millisUntilFinished);
            m_student.setTimeLeft(millisUntilFinished);
            updateTimeLeft(millisUntilFinished);
        }

        @Override
        public void onFinish()
        {
            Logger.log(this, "Count down timer has finished");
            m_student.setTimeLeft(0L);
            updateTimeLeft(0L);
            m_entryView.setBackgroundColor(RUNNING_OUT_OF_TIME_COLOR);
            updateTextView();
        }

        private void updateTimeLeft(long millis)
        {
            setTextViewToTime(m_timeLeftTextView, millis);

            // update the color if it's running out of time
            long timeLeftSec = TimeUnit.MILLISECONDS.toSeconds(millis);
            if (timeLeftSec <= NO_TIME_LEFT_SEC)
            {
                if (timeLeftSec % 2 == 0)
                {
                    m_entryView.setBackgroundColor(m_defaultColor);
                }
                else
                {
                    m_entryView.setBackgroundColor(RUNNING_OUT_OF_TIME_COLOR);
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
