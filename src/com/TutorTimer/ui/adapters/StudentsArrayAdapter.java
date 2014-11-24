package com.TutorTimer.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import com.TutorTimer.Logger.Logger;
import com.TutorTimer.R;
import com.TutorTimer.students.Student;
import com.TutorTimer.students.StudentManager;
import com.TutorTimer.utils.TimerFactory;
import com.TutorTimer.utils.Utils;

import java.util.List;
import java.util.concurrent.TimeUnit;

abstract class StudentsArrayAdapter extends ArrayAdapter<Student>
{
    private static final String[] SECOND_RESET_STRINGS = {"00", "30"};
    private static final long[]   SECOND_RESET_VALUES  = {0, 30};

    static class ViewHolder
    {
        Student student;

        // the "student_entry" view
        View entryView;

        // text shown on the UI
        TextView studentNameTextView;
        TextView timeLeftTextView;

        // reset time pickers
        NumberPicker minPicker;
        NumberPicker secPicker;

        // buttons
        Button importRemoveButton;
        Button resetTimerButton;

        // default color
        int defaultColor;
    }

    final Activity       m_activity;
    final StudentManager m_studentManager;
    final TimerFactory   m_timerFactory;

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
            rowView = inflater.inflate(R.layout.student_entry, parent, false);

            // create a view holder with all the links
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.entryView = rowView;

            viewHolder.studentNameTextView = (TextView) rowView.findViewById(R.id.timer_student_name);
            viewHolder.timeLeftTextView = (TextView) rowView.findViewById(R.id.time_left_for_student);

            // reset time pickers
            viewHolder.minPicker = (NumberPicker) rowView.findViewById(R.id.min_reset_time_picker);
            viewHolder.secPicker = (NumberPicker) rowView.findViewById(R.id.sec_reset_time_picker);

            viewHolder.minPicker.setMinValue(0);
            viewHolder.minPicker.setMaxValue(60);
            viewHolder.secPicker.setMinValue(0);
            viewHolder.secPicker.setMaxValue(1);
            viewHolder.secPicker.setDisplayedValues(SECOND_RESET_STRINGS);

            // reset, remove buttons
            viewHolder.importRemoveButton = (Button) rowView.findViewById(R.id.import_remove_current_student);
            viewHolder.resetTimerButton = (Button) rowView.findViewById(R.id.reset_timer_button);

            // create the click listeners
            viewHolder.minPicker.setOnValueChangedListener(getNumberPickerListener());
            viewHolder.secPicker.setOnValueChangedListener(getNumberPickerListener());
            viewHolder.resetTimerButton.setOnClickListener(getResetClickListener());
            viewHolder.importRemoveButton.setOnClickListener(getImportRemoveClickListener());

            // set the text of the start/pause button
            viewHolder.importRemoveButton.setText(getImportRemoveButtonText());

            // add the tags to all of the view's members
            viewHolder.minPicker.setTag(viewHolder);
            viewHolder.secPicker.setTag(viewHolder);
            viewHolder.importRemoveButton.setTag(viewHolder);
            viewHolder.resetTimerButton.setTag(viewHolder);

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
        setResetPickers(viewHolder);

        // set the color of the parent view and save in view holder
        setViewColor(position, rowView, viewHolder);

        return rowView;
    }

    abstract View.OnClickListener getResetClickListener();

    abstract View.OnClickListener getImportRemoveClickListener();

    abstract String getImportRemoveButtonText();

    static void resetTimeLeftTextView(ViewHolder viewHolder)
    {
        Utils.setTextViewToTime(viewHolder.timeLeftTextView, viewHolder.student.getTimeLeft());
    }

    static void resetBackgroundColor(ViewHolder viewHolder)
    {
        viewHolder.entryView.setBackgroundColor(viewHolder.defaultColor);
    }

    private static void setViewColor(int position, View rowView, ViewHolder viewHolder)
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

    private static void setResetPickers(ViewHolder viewHolder)
    {
        long timeSeconds = TimeUnit.MILLISECONDS.toSeconds(viewHolder.student.getResetTime());
        viewHolder.minPicker.setValue((int) TimeUnit.SECONDS.toMinutes(timeSeconds));

        long remainderSeconds = timeSeconds % 60;
        for (int i = 0; i < SECOND_RESET_VALUES.length; ++i)
        {
            if (remainderSeconds == SECOND_RESET_VALUES[i])
            {
                viewHolder.secPicker.setValue(i);
                break;
            }
        }
    }

    private NumberPicker.OnValueChangeListener getNumberPickerListener()
    {
        return new NumberPicker.OnValueChangeListener()
        {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal)
            {
                final ViewHolder holder = (ViewHolder) picker.getTag();
                Logger.log(StudentsArrayAdapter.class,
                           "Changing the reset time for student %s",
                           holder.student);

                NumberPicker minPicker = holder.minPicker;
                NumberPicker secPicker = holder.secPicker;

                long newTime = TimeUnit.MINUTES.toSeconds(minPicker.getValue()) + SECOND_RESET_VALUES[secPicker.getValue()];
                holder.student.setResetTime(TimeUnit.SECONDS.toMillis(newTime));
            }
        };
    }
}
