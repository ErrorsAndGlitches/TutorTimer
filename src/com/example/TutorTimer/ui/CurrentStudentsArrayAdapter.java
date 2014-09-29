package com.example.TutorTimer.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import com.example.TutorTimer.Logger.Logger;
import com.example.TutorTimer.R;
import com.example.TutorTimer.students.Student;

import java.util.List;

public class CurrentStudentsArrayAdapter extends ArrayAdapter<Student>
{
    public CurrentStudentsArrayAdapter(Context context, int resource, List<Student> objects)
    {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView;
        if (convertView == null)
        {
            rowView = inflater.inflate(R.layout.current_student_entry, parent, false);

            ViewHolder viewHolder = new ViewHolder();
            viewHolder.studentName = (TextView) rowView.findViewById(R.id.timer_student_name);
            viewHolder.timeLeft = (TextView) rowView.findViewById(R.id.time_left_for_student);
            viewHolder.resetTimerButton = (Button) rowView.findViewById(R.id.reset_timer_button);
            viewHolder.removeFromCurrentStudentsButton = (Button) rowView.findViewById(R.id.remove_current_student);

            viewHolder.resetTimerButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Logger.log(this, "Resetting timer for %s", v.getTag());
                }
            });

            viewHolder.removeFromCurrentStudentsButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Logger.log(this, "Removing %s from current student list", v.getTag());
                }
            });

            rowView.setTag(viewHolder);
        }
        else
        {
            rowView = convertView;
        }

        ViewHolder viewHolder = (ViewHolder) rowView.getTag();

        Student student = getItem(position);
        viewHolder.student = student;

        TextView studentNameTextView = viewHolder.studentName;
        studentNameTextView.setText(student.toString());

        TextView timeLeftTextView = viewHolder.timeLeft;
        timeLeftTextView.setText("0:00");

        // add the student to the button's tag since the info will be needed in the onClick()
        viewHolder.resetTimerButton.setTag(student);
        viewHolder.removeFromCurrentStudentsButton.setTag(student);

        return rowView;
    }

    private static final class ViewHolder
    {
        Student  student;
        TextView studentName;
        TextView timeLeft;
        Button   resetTimerButton;
        Button   removeFromCurrentStudentsButton;
    }
}
