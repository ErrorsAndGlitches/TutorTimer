package com.TutorTimer.ui;

import android.content.Context;
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
import com.TutorTimer.ui.CurrentStudentsArrayAdapter.CurrentStudentEntry;
import com.TutorTimer.timer.Timer;
import com.TutorTimer.timer.TimerFactory;

import java.util.List;

public class CurrentStudentsArrayAdapter extends ArrayAdapter<CurrentStudentEntry>
{
    private final StudentManager m_studentManager;
    private final TimerFactory   m_timerFactory;

    public CurrentStudentsArrayAdapter(Context context, int resource, List<CurrentStudentEntry> objects)
    {
        super(context, resource, objects);
        m_studentManager = StudentManager.getInstance(context);
        m_timerFactory = TimerFactory.getInstance(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
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
                    Timer timer = (Timer) v.getTag();
                    timer.setResetDurationSec(m_timerFactory.getResetDurationSec());
                    timer.reset();
                }
            });

            viewHolder.removeFromCurrentStudentsButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Student student = (Student) v.getTag();
                    Logger.log(this, "Removing %s from current student list", student);
                    m_studentManager.removeFromCurrentStudents(student);
                }
            });

            rowView.setTag(viewHolder);
        }
        else
        {
            rowView = convertView;
        }

        ViewHolder viewHolder = (ViewHolder) rowView.getTag();

        CurrentStudentEntry currentStudentEntry = getItem(position);
        Student student = currentStudentEntry.student;

        TextView studentNameTextView = viewHolder.studentName;
        studentNameTextView.setText(student.toString());

        Timer timer = currentStudentEntry.timer;
        TextView timeLeftTextView = viewHolder.timeLeft;
        timeLeftTextView.setText(timer.toString());

        // add the student to the button's tag since the info will be needed in the onClick()
        viewHolder.resetTimerButton.setTag(timer);
        viewHolder.removeFromCurrentStudentsButton.setTag(student);

        return rowView;
    }

    public static class CurrentStudentEntry
    {
        public final Student student;
        public final Timer   timer;

        public CurrentStudentEntry(Student student, Timer timer)
        {
            this.student = student;
            this.timer = timer;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            CurrentStudentEntry that = (CurrentStudentEntry) o;

            if (student != null ? !student.equals(that.student) : that.student != null) return false;

            return true;
        }

        @Override
        public int hashCode()
        {
            return student != null ? student.hashCode() : 0;
        }
    }

    private static final class ViewHolder
    {
        TextView studentName;
        TextView timeLeft;
        Button   resetTimerButton;
        Button   removeFromCurrentStudentsButton;
    }
}
