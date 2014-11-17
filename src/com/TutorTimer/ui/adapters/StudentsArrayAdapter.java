package com.TutorTimer.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
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
import com.TutorTimer.utils.Utils;

import java.util.List;

abstract class StudentsArrayAdapter extends ArrayAdapter<Student>
{
    static class ViewHolder
    {
        Student student;

        // the "current_student_entry" view
        View entryView;

        // text shown on the UI
        TextView studentNameTextView;
        TextView timeLeftTextView;
        TextView resetTimeTextView;

        // buttons
        Button startPauseButton;
        Button incTimeButton;
        Button decTimeButton;
        Button resetTimerButton;
        Button removeFromStudentsButton;

        // default color
        int defaultColor;
    }

    final Activity             m_activity;
    final StudentManager       m_studentManager;
    final TimerFactory         m_timerFactory;

    public StudentsArrayAdapter(Activity activity,
                                int resource,
                                List<Student> studentList)
    {
        super(activity, resource, studentList);
        m_activity = activity;
        m_studentManager = StudentManager.getInstance(activity);
        m_timerFactory = TimerFactory.getInstance(activity);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final View rowView;
        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.current_student_entry, parent, false);

            // create a view holder with all the links
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.entryView = rowView;

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
            viewHolder.removeFromStudentsButton = (Button) rowView.findViewById(R.id.remove_current_student);

            // create the click listeners
            viewHolder.startPauseButton.setOnClickListener(getStartPauseClickListener());
            viewHolder.resetTimerButton.setOnClickListener(getResetClickListener());
            viewHolder.incTimeButton.setOnClickListener(getIncResetTimeClickListener());
            viewHolder.decTimeButton.setOnClickListener(getDecResetTimeClickListener());
            viewHolder.removeFromStudentsButton.setOnClickListener(getRemoveClickListener());

            // set the text of the start/pause button
            viewHolder.startPauseButton.setText(getStartPauseButtonText());

            // add the tags to all of the view's members
            viewHolder.startPauseButton.setTag(viewHolder);
            viewHolder.incTimeButton.setTag(viewHolder);
            viewHolder.decTimeButton.setTag(viewHolder);
            viewHolder.resetTimerButton.setTag(viewHolder);
            viewHolder.removeFromStudentsButton.setTag(viewHolder);

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
        Student student = getItem(position);
        viewHolder.student = student;

        // set the student name
        viewHolder.studentNameTextView.setText(student.toString());

        // set the time left and reset time
        Utils.setTextViewToTime(viewHolder.timeLeftTextView, student.getTimeLeft());
        Utils.setTextViewToTime(viewHolder.resetTimeTextView, student.getResetTime());

        // set the color of the parent view and save in view holder
        setViewColor(position, rowView, viewHolder);

        return rowView;
    }

    abstract View.OnClickListener getStartPauseClickListener();

    abstract View.OnClickListener getResetClickListener();

    abstract View.OnClickListener getRemoveClickListener();

    abstract String getStartPauseButtonText();

    static void resetTimeLeftTextView(ViewHolder viewHolder)
    {
        Utils.setTextViewToTime(viewHolder.timeLeftTextView, viewHolder.student.getTimeLeft());
    }

    static void resetBackgroundColor(ViewHolder viewHolder)
    {
        viewHolder.entryView.setBackgroundColor(viewHolder.defaultColor);
    }

    private void setViewColor(int position, View rowView, ViewHolder viewHolder)
    {
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
    }

    private View.OnClickListener getIncResetTimeClickListener()
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final ViewHolder holder = (ViewHolder) v.getTag();
                Logger.log(StudentsArrayAdapter.class,
                           "Increasing the reset time for student %s",
                           holder.student);
                holder.student.addToResetTime(m_timerFactory.getIncTimeAmount());
                Utils.setTextViewToTime(holder.resetTimeTextView, holder.student.getResetTime());
            }
        };
    }

    private View.OnClickListener getDecResetTimeClickListener()
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final ViewHolder holder = (ViewHolder) v.getTag();
                Logger.log(StudentsArrayAdapter.class,
                           "Decreasing the reset time for student %s",
                           holder.student);
                holder.student.addToResetTime(m_timerFactory.getDecTimeAmount());
                Utils.setTextViewToTime(holder.resetTimeTextView, holder.student.getResetTime());
            }
        };
    }
}
