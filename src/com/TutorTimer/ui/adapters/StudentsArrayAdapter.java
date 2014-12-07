package com.TutorTimer.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import com.TutorTimer.Logger.Logger;
import com.TutorTimer.R;
import com.TutorTimer.students.Student;
import com.TutorTimer.students.StudentManager;
import com.TutorTimer.ui.ChangeResetTimeDialogueFactory;
import com.TutorTimer.ui.ChangeResetTimeDialogueFactory.DialogueCallbacks;
import com.TutorTimer.utils.TimerFactory;
import com.TutorTimer.utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

abstract class StudentsArrayAdapter extends ArrayAdapter<Student>
{
    private static final float FLING_DISTANCE = 100.0f;

    static interface FlingListener
    {
        boolean isLeftFlingable();

        boolean isRightFlingable();

        void onFlingLeftAnimationEnd(View view);

        void onFlingRightAnimationEnd(View view);
    }

    static class ViewHolder
    {
        Student student;

        // the "student_entry" view
        View entryView;

        // text shown on the UI
        TextView studentNameTextView;
        TextView timeLeftTextView;

        // reset time button - tap to open reset time dialogue
        Button resetTimeButton;

        // default color
        int defaultColor;
    }

    final         Activity               m_activity;
    final         StudentManager         m_studentManager;
    final         TimerFactory           m_timerFactory;
    private final Map<Student, TextView> m_currentTextViewUpdated;

    public StudentsArrayAdapter(Activity activity,
                                int resource,
                                List<Student> studentList)
    {
        super(activity, resource, studentList);
        m_activity = activity;
        m_studentManager = StudentManager.getInstance(activity);
        m_timerFactory = TimerFactory.getInstance(activity);
        m_currentTextViewUpdated = new HashMap<Student, TextView>();
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

            // button to click to change the reset time
            viewHolder.resetTimeButton = (Button) rowView.findViewById(R.id.set_reset_time_button);

            // create the click listeners
            viewHolder.timeLeftTextView.setOnClickListener(getResetTimeClickListener());
            viewHolder.resetTimeButton.setOnClickListener(getChangeResetTimeClickListener());

            // add the tags to all of the view's members
            viewHolder.timeLeftTextView.setTag(viewHolder);
            viewHolder.resetTimeButton.setTag(viewHolder);

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

        // check if the text view is already being used by a student from view recycling
        TextView textView = m_currentTextViewUpdated.get(viewHolder.student);
        if (textView != null && textView == viewHolder.timeLeftTextView)
        {
            viewHolder.student.setTimerCallback(null);
        }

        // set the student & count down timer
        Student student = getItem(position);
        viewHolder.student = student;

        // set the student name
        viewHolder.studentNameTextView.setText(student.toString());

        // set the time left and reset time
        Utils.setTextViewToTime(viewHolder.timeLeftTextView, student.getTimeLeft());
        Utils.setTextViewToTime(viewHolder.resetTimeButton, student.getResetTime());

        // set the color of the parent view and save in view holder
        setViewColor(position, rowView, viewHolder);

        rowView.setOnTouchListener(new FlingTouchListener());

        // update the text view to student mapping
        m_currentTextViewUpdated.put(student, viewHolder.timeLeftTextView);

        return rowView;
    }

    abstract View.OnClickListener getResetTimeClickListener();

    abstract FlingListener getFlingListener();

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

    private View.OnClickListener getChangeResetTimeClickListener()
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final ViewHolder holder = (ViewHolder) v.getTag();
                DialogueCallbacks callbacks = new DialogueCallbacks()
                {
                    @Override
                    public void onOkayClicked(long min, long sec)
                    {
                        long newTime = TimeUnit.SECONDS.toMillis(TimeUnit.MINUTES.toSeconds(min) + sec);
                        Logger.log(StudentsArrayAdapter.class,
                                   "Changing the reset time for student %s to %dms",
                                   holder.student, newTime);
                        holder.student.setResetTime(newTime);
                        Utils.setTextViewToTime(holder.resetTimeButton, newTime);
                    }

                    @Override
                    public void onCancelClicked()
                    {
                        // do nothing
                    }
                };

                ChangeResetTimeDialogueFactory.getResetDialogue(m_activity,
                                                                holder.student.getResetTime(),
                                                                callbacks).show();
            }
        };
    }

    private class FlingTouchListener implements View.OnTouchListener
    {
        private final FlingListener m_flingListener = getFlingListener();
        private float m_startX;
        private int   m_index;

        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            switch (event.getAction())
            {
            case MotionEvent.ACTION_DOWN:
                m_index = event.getPointerId(0);
                m_startX = event.getX(m_index);
                break;
            case MotionEvent.ACTION_MOVE:
                float diff = event.getX(m_index) - m_startX;
                if (diff > FLING_DISTANCE && m_flingListener.isRightFlingable())
                {
                    v.startAnimation(getRightFlingAnimation(v));
                }
                else if (diff < -FLING_DISTANCE && m_flingListener.isLeftFlingable())
                {
                    v.startAnimation(getLeftFlingAnimation(v));
                }

                break;
            }

            return true;
        }

        private Animation getLeftFlingAnimation(final View view)
        {
            Animation slideLeftAnim = AnimationUtils.loadAnimation(m_activity, R.anim.slide_left);
            slideLeftAnim.setAnimationListener(new Animation.AnimationListener()
            {
                @Override
                public void onAnimationStart(Animation animation)
                {
                }

                @Override
                public void onAnimationRepeat(Animation animation)
                {
                }

                @Override
                public void onAnimationEnd(Animation animation)
                {
                    m_flingListener.onFlingLeftAnimationEnd(view);
                }
            });

            return slideLeftAnim;
        }

        private Animation getRightFlingAnimation(final View view)
        {
            Animation slideRightAnim = AnimationUtils.loadAnimation(m_activity, R.anim.slide_right);
            slideRightAnim.setAnimationListener(new Animation.AnimationListener()
            {
                @Override
                public void onAnimationStart(Animation animation)
                {
                }

                @Override
                public void onAnimationRepeat(Animation animation)
                {
                }

                @Override
                public void onAnimationEnd(Animation animation)
                {
                    m_flingListener.onFlingRightAnimationEnd(view);
                }
            });

            return slideRightAnim;
        }
    }
}
