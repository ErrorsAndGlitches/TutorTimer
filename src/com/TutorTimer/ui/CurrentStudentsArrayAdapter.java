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
import com.TutorTimer.timer.TimerFactory;

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
            viewHolder.studentName = (TextView) rowView.findViewById(R.id.timer_student_name);
            viewHolder.timeLeft = (TextView) rowView.findViewById(R.id.time_left_for_student);
            viewHolder.resetTime = (TextView) rowView.findViewById(R.id.reset_time);

            // start, stop buttons
            viewHolder.startButton = (Button) rowView.findViewById(R.id.start_timer_button);
            viewHolder.stopButton = (Button) rowView.findViewById(R.id.stop_timer_button);

            // increase and decrease reset time buttons
            viewHolder.incTimeButton = (Button) rowView.findViewById(R.id.increase_time_button);
            viewHolder.decTimeButton = (Button) rowView.findViewById(R.id.decrease_time_button);

            // reset, remove buttons
            viewHolder.resetTimerButton = (Button) rowView.findViewById(R.id.reset_timer_button);
            viewHolder.removeFromCurrentStudentsButton = (Button) rowView.findViewById(R.id.remove_current_student);

            // create the click listeners
            viewHolder.startButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ViewHolder holder = (ViewHolder) v.getTag();

                    // check if the timer is actually already running
                    if (viewHolder.countDownTimer != null && !viewHolder.countDownTimer.isStopped())
                    {
                        Logger.log(CurrentStudentsArrayAdapter.class,
                                   "Not starting timer for student %s because timer is already running",
                                   holder.student);
                        return;
                    }

                    long timeLeft;
                    if (viewHolder.countDownTimer == null)
                    {
                        Logger.log(CurrentStudentsArrayAdapter.class,
                                   "Starting the timer for student %s",
                                   holder.student);
                        timeLeft = holder.student.getResetTime();
                    }
                    else
                    {
                        Logger.log(CurrentStudentsArrayAdapter.class,
                                   "Resuming the countdown for student %s",
                                   holder.student);
                        timeLeft = viewHolder.countDownTimer.getTimeRemaining();
                    }

                    viewHolder.countDownTimer = new StudentCountDownTimer(holder.entryView,
                                                                          holder.defaultColor,
                                                                          timeLeft,
                                                                          TimeUnit.SECONDS.toMillis(1L),
                                                                          m_activity,
                                                                          viewHolder.timeLeft);

                    viewHolder.countDownTimer.start();
                }
            });

            viewHolder.stopButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ViewHolder holder = (ViewHolder) v.getTag();
                    if (viewHolder.countDownTimer != null)
                    {
                        Logger.log(CurrentStudentsArrayAdapter.class,
                                   "Stopping the timer for student %s",
                                   holder.student);
                        viewHolder.countDownTimer.stop();
                    }
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
                               holder.student);
                    holder.student.addToResetTime(m_timerFactory.getIncTimeAmount());
                    setResetTime(viewHolder.resetTime, viewHolder.student);
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
                               holder.student);
                    holder.student.addToResetTime(m_timerFactory.getDecTimeAmount());
                    setResetTime(viewHolder.resetTime, viewHolder.student);
                }
            });

            viewHolder.resetTimerButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    final ViewHolder holder = (ViewHolder) v.getTag();

                    // stop the timer, set the base, start the timer
                    Logger.log(CurrentStudentsArrayAdapter.class, "Resetting the timer for student %s", holder.student);

                    if (holder.countDownTimer != null)
                    {
                        holder.countDownTimer.cancel();
                        holder.countDownTimer = null;
                    }

                    setResetTime(holder.timeLeft, holder.student);
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
                               holder.student);
                    m_studentManager.removeFromCurrentStudents(holder.student);

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
        CurrentStudentEntry currentStudentEntry = getItem(position);
        Student student = currentStudentEntry.student;
        viewHolder.student = student;
        viewHolder.entryView = rowView;

        // set the student name
        TextView studentNameTextView = viewHolder.studentName;
        studentNameTextView.setText(student.toString());

        // set the time left to the reset time
        setResetTime(viewHolder.timeLeft, student);

        // set the reset time
        setResetTime(viewHolder.resetTime, student);

        // add the student to the button's tag since the info will be needed in the onClick()
        viewHolder.startButton.setTag(viewHolder);
        viewHolder.stopButton.setTag(viewHolder);
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

    private static void setResetTime(TextView textView, Student student)
    {
        setTextViewTimeSec(textView, TimeUnit.MILLISECONDS.toSeconds(student.getResetTime()));
    }

    private static void setTextViewTimeSec(TextView textView, long resetTimeSec)
    {
        long minutes = TimeUnit.SECONDS.toMinutes(resetTimeSec);
        long seconds = resetTimeSec - TimeUnit.MINUTES.toSeconds(minutes);
        textView.setText(String.format("%02d:%02d", minutes, seconds));
    }

    private static final class ViewHolder
    {
        Student student;

        // the top-most view for the entry
        View entryView;

        // objects shown on the UI
        TextView studentName;
        TextView timeLeft;
        TextView resetTime;

        // buttons
        Button startButton;
        Button stopButton;
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

        private final Activity m_activity;
        private final TextView m_timeLeft;
        private final View     m_entryView;
        private final int      m_defaultColor;

        // this is the time to be saved if the clock is stopped - not reset
        private long    m_timeLeftMs;
        private boolean m_isStopped;

        public StudentCountDownTimer(View entryView,
                                     int defaultColor,
                                     long millisInFuture,
                                     long countDownInterval,
                                     Activity activity,
                                     TextView timeLeft)
        {
            super(millisInFuture, countDownInterval);
            m_entryView = entryView;
            m_defaultColor = defaultColor;
            m_activity = activity;
            m_timeLeft = timeLeft;
            m_isStopped = false;
        }

        @Override
        public void onTick(long millisUntilFinished)
        {
            Logger.log(this, "Updating the tick time: %d", millisUntilFinished);
            m_timeLeftMs = millisUntilFinished;
            updateTimeLeft(millisUntilFinished);
        }

        @Override
        public void onFinish()
        {
            Logger.log(this, "Count down timer has finished");
            updateTimeLeft(0L);
            m_entryView.setBackgroundColor(RUNNING_OUT_OF_TIME_COLOR);
            updateTextView();
        }

        long getTimeRemaining()
        {
            return m_timeLeftMs;
        }

        boolean isStopped()
        {
            return m_isStopped;
        }

        // don't call cancel
        public void stop()
        {
            m_isStopped = true;
            cancel();
        }

        private void updateTimeLeft(long millis)
        {
            setTextViewTimeSec(m_timeLeft, TimeUnit.MILLISECONDS.toSeconds(millis));

            // update the color if it's running out of time
            long timeLeftSec = TimeUnit.MILLISECONDS.toSeconds(m_timeLeftMs);
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
                    m_timeLeft.invalidate();
                }
            });
        }
    }
}
