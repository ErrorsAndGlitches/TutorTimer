package com.TutorTimer.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import com.TutorTimer.Logger.Logger;
import com.TutorTimer.R;
import com.TutorTimer.students.Student;
import com.TutorTimer.students.StudentManager;
import com.TutorTimer.utils.TimerFactory;
import com.TutorTimer.utils.CurrentStudentEntry;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class CurrentStudentsArrayAdapter extends ArrayAdapter<CurrentStudentEntry>
{
    private final Activity       m_activity;
    private final StudentManager m_studentManager;
    private final TimerFactory   m_timerFactory;

    public CurrentStudentsArrayAdapter(Activity activity, int resource, List<CurrentStudentEntry> objects)
    {
        super(activity, resource, objects);
        m_activity = activity;
        m_studentManager = StudentManager.getInstance(activity);
        m_timerFactory = TimerFactory.getInstance(activity);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View rowView;
        if (convertView == null)
        {
            rowView = inflater.inflate(R.layout.current_student_entry, parent, false);

            // create a view holder with all the links
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.studentNameTextView = (TextView) rowView.findViewById(R.id.timer_student_name);
            viewHolder.timeLeftTextView = (TextView) rowView.findViewById(R.id.time_left_for_student);
            viewHolder.resetTimeTextView = (TextView) rowView.findViewById(R.id.reset_time);

            // start, stop buttons
            viewHolder.startPauseButton = (Button) rowView.findViewById(R.id.start_pause_timer_button);

            // increase and decrease reset time buttons
            viewHolder.incTimeButton = (Button) rowView.findViewById(R.id.increase_time_button);
            viewHolder.decTimeButton = (Button) rowView.findViewById(R.id.decrease_time_button);

            // reset, remove buttons
            viewHolder.resetTimerButton = (Button) rowView.findViewById(R.id.reset_timer_button);
            viewHolder.removeFromCurrentStudentsButton = (Button) rowView.findViewById(R.id.remove_current_student);

            // create the click listeners
            viewHolder.startPauseButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ViewHolder holder = (ViewHolder) v.getTag();

                    // if the timer is stopped, start the timer
                    if (viewHolder.studentEntry.isTimerStopped())
                    {
                        viewHolder.countDownTimer = new StudentCountDownTimer(m_activity,
                                                                              holder.entryView,
                                                                              viewHolder.timeLeftTextView,
                                                                              viewHolder.studentEntry,
                                                                              holder.defaultColor);
                        viewHolder.studentEntry.setTimerStarted();
                        viewHolder.countDownTimer.start();
                    }
                    else
                    {
                        viewHolder.studentEntry.setTimerStopped();
                        viewHolder.countDownTimer.cancel();
                        viewHolder.countDownTimer = null;
                    }

                    setStartPauseButtonText(viewHolder.startPauseButton, viewHolder.studentEntry);
                }
            });

            viewHolder.incTimeButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    final ViewHolder holder = (ViewHolder) v.getTag();
                    Logger.log(CurrentStudentsArrayAdapter.class,
                               "Increasing the reset time for student %s",
                               holder.studentEntry.getStudent());
                    holder.studentEntry.addToResetTime(m_timerFactory.getIncTimeAmount());
                    setTextViewToTime(viewHolder.resetTimeTextView, holder.studentEntry.getResetTime());
                }
            });

            viewHolder.decTimeButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    final ViewHolder holder = (ViewHolder) v.getTag();
                    Logger.log(CurrentStudentsArrayAdapter.class,
                               "Decreasing the reset time for student %s",
                               holder.studentEntry.getStudent());
                    holder.studentEntry.addToResetTime(m_timerFactory.getDecTimeAmount());
                    setTextViewToTime(viewHolder.resetTimeTextView, holder.studentEntry.getResetTime());
                }
            });

            viewHolder.resetTimerButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    final ViewHolder holder = (ViewHolder) v.getTag();

                    // stop the timer, set the base, start the timer
                    Logger.log(CurrentStudentsArrayAdapter.class,
                               "Resetting the timer for student %s",
                               holder.studentEntry.getStudent());

                    if (holder.countDownTimer != null)
                    {
                        holder.countDownTimer.cancel();
                        holder.countDownTimer = null;
                    }

                    holder.studentEntry.setTimerStopped();
                    holder.studentEntry.setTimeLeft(holder.studentEntry.getResetTime());

                    setTextViewToTime(holder.timeLeftTextView, holder.studentEntry.getResetTime());
                    setStartPauseButtonText(holder.startPauseButton, holder.studentEntry);
                    holder.entryView.setBackgroundColor(holder.defaultColor);
                }
            });

            viewHolder.removeFromCurrentStudentsButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ViewHolder holder = (ViewHolder) v.getTag();
                    Logger.log(CurrentStudentsArrayAdapter.class,
                               "Removing %s from current student list",
                               holder.studentEntry.getStudent());
                    m_studentManager.removeFromCurrentStudents(holder.studentEntry.getStudent());

                    if (holder.countDownTimer != null)
                    {
                        holder.countDownTimer.cancel();
                        holder.countDownTimer = null;
                    }
                }
            });

            rowView.setTag(viewHolder);
        }
        else
        {
            rowView = convertView;
        }

        //
        // update the properties of the view
        //
        ViewHolder viewHolder = (ViewHolder) rowView.getTag();

        // set the student & count down timer
        CurrentStudentEntry studentEntry = getItem(position);
        viewHolder.studentEntry = studentEntry;

        Student student = studentEntry.getStudent();
        viewHolder.entryView = rowView;

        // set the student name
        TextView studentNameTextView = viewHolder.studentNameTextView;
        studentNameTextView.setText(student.toString());

        // set the start/pause button to the write text
        setStartPauseButtonText(viewHolder.startPauseButton, studentEntry);

        // set the time left to the reset time
        setTextViewToTime(viewHolder.timeLeftTextView, studentEntry.getResetTime());

        // set the reset time
        setTextViewToTime(viewHolder.resetTimeTextView, studentEntry.getResetTime());

        // add the student to the button's tag since the info will be needed in the onClick()
        viewHolder.startPauseButton.setTag(viewHolder);
        viewHolder.incTimeButton.setTag(viewHolder);
        viewHolder.decTimeButton.setTag(viewHolder);
        viewHolder.resetTimerButton.setTag(viewHolder);
        viewHolder.removeFromCurrentStudentsButton.setTag(viewHolder);

        if (position % 2 == 0)
        {
            int defaultColor = Color.DKGRAY;
            viewHolder.defaultColor = defaultColor;
            rowView.setBackgroundColor(defaultColor);
        }
        else
        {
            int defaultColor = Color.BLACK;
            viewHolder.defaultColor = defaultColor;
            rowView.setBackgroundColor(defaultColor);
        }

        return rowView;
    }

    private static void setStartPauseButtonText(Button startPauseButton, CurrentStudentEntry studentEntry)
    {
        if (studentEntry.isTimerStopped())
        {
            startPauseButton.setText("Start");
        }
        else
        {
            startPauseButton.setText("Pause");
        }
    }

    private static void setTextViewToTime(TextView textView, long resetTime)
    {
        long resetTimeSec = TimeUnit.MILLISECONDS.toSeconds(resetTime);
        long minutes = TimeUnit.SECONDS.toMinutes(resetTimeSec);
        long seconds = resetTimeSec - TimeUnit.MINUTES.toSeconds(minutes);
        textView.setText(String.format("%02d:%02d", minutes, seconds));
    }

    private static final class ViewHolder
    {
        CurrentStudentEntry studentEntry;

        // the top-most view for the entry
        View entryView;

        // objects shown on the UI
        TextView studentNameTextView;
        TextView timeLeftTextView;
        TextView resetTimeTextView;

        // buttons
        Button startPauseButton;
        Button incTimeButton;
        Button decTimeButton;
        Button resetTimerButton;
        Button removeFromCurrentStudentsButton;

        // count down timer
        StudentCountDownTimer countDownTimer;

        // default color
        int defaultColor;
    }

    private static final class StudentCountDownTimer extends CountDownTimer
    {
        private static final int RUNNING_OUT_OF_TIME_COLOR = Color.RED;

        private final Activity            m_activity;
        private final View                m_entryView;
        private final TextView            m_timeLeftTextView;
        private final CurrentStudentEntry m_studentEntry;
        private final int                 m_defaultColor;

        // this is the time to be saved if the clock is stopped - not reset

        public StudentCountDownTimer(Activity activity,
                                     View entryView,
                                     TextView timeLeftTextView,
                                     CurrentStudentEntry studentEntry,
                                     int defaultColor)
        {
            super(studentEntry.getTimeLeft(), TimeUnit.SECONDS.toMillis(1L));
            m_activity = activity;
            m_entryView = entryView;
            m_timeLeftTextView = timeLeftTextView;
            m_studentEntry = studentEntry;
            m_defaultColor = defaultColor;
        }

        @Override
        public void onTick(long millisUntilFinished)
        {
            Logger.log(this, "Updating the tick time: %d", millisUntilFinished);
            m_studentEntry.setTimeLeft(millisUntilFinished);
            updateTimeLeft(millisUntilFinished);
        }

        @Override
        public void onFinish()
        {
            Logger.log(this, "Count down timer has finished");
            m_studentEntry.setTimeLeft(0L);
            updateTimeLeft(0L);
            m_entryView.setBackgroundColor(RUNNING_OUT_OF_TIME_COLOR);
            updateTextView();
        }

        private void updateTimeLeft(long millis)
        {
            setTextViewToTime(m_timeLeftTextView, millis);

            // update the color if it's running out of time
            long timeLeftSec = TimeUnit.MILLISECONDS.toSeconds(millis);
            if (timeLeftSec <= 30)
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
