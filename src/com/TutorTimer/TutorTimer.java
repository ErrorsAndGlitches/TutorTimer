package com.TutorTimer;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.TutorTimer.Logger.Logger;
import com.TutorTimer.students.StudentManager;
import com.TutorTimer.utils.TimerFactory;
import com.TutorTimer.ui.TutorActionBarFactory;
import com.TutorTimer.utils.Utils;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TutorTimer extends Activity
{
    private static final int THREAD_POOL_SIZE               = 10;
    private static final int THREAD_POOL_KEEP_ALIVE_TIME_MS = 5000;

    private final ThreadPoolExecutor m_threadPool;

    private StudentManager m_studentManager;

    public TutorTimer()
    {
        m_threadPool = new ThreadPoolExecutor(THREAD_POOL_SIZE, THREAD_POOL_SIZE, THREAD_POOL_KEEP_ALIVE_TIME_MS,
                                              TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Logger.log(this, "Tutor Timer has started");
        m_studentManager = StudentManager.getInstance(this);
        TutorActionBarFactory.createActionBar(this, m_threadPool);
    }

    public void addDebugStudents(View view)
    {
        m_threadPool.submit(new Runnable()
        {
            @Override
            public void run()
            {
                Logger.log(this, "Adding debug students");

                final EditText numStudentsView = (EditText) findViewById(R.id.num_debug_students);
                String numStudentsStr = numStudentsView.getText().toString();

                int numStudentsToAdd = 0;
                if (numStudentsStr != null && numStudentsStr.length() != 0)
                {
                    try
                    {
                        numStudentsToAdd = Integer.parseInt(numStudentsStr);
                    }
                    catch (NumberFormatException e)
                    {
                        Utils.longToast(TutorTimer.this, "Could not create debug students; invalid arg %s", numStudentsStr);
                    }

                    numStudentsToAdd = Math.max(numStudentsToAdd, 0);
                }
                else
                {
                    numStudentsToAdd = getResources().getInteger(R.integer.default_num_debug_students);
                }

                final String msg = String.format("Adding %d debug students", numStudentsToAdd);
                Logger.log(this, msg);
                for (int i = 0; i < numStudentsToAdd; ++i)
                {
                    m_studentManager.addStudent(String.format("Student_%d", System.nanoTime()));
                }

                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        numStudentsView.setText("");
                        Utils.shortToast(TutorTimer.this, msg);
                    }
                });
            }
        });
    }

    public void clearStudents(View view)
    {
        m_studentManager.clearStudents();
    }

    public void addNewStudent(View view)
    {
        EditText newStudentText = (EditText) findViewById(R.id.new_student_name);
        String newStudentName = newStudentText.getText().toString();

        if (newStudentName != null && newStudentName.length() != 0)
        {
            m_studentManager.addStudent(newStudentName);
        }

        newStudentText.setText("");
    }

    public void onSetResetDuration(View view)
    {
        EditText resetEditTextView = (EditText) findViewById(R.id.reset_duration_seconds);
        String newResetDurationStr = resetEditTextView.getText().toString();

        if (newResetDurationStr.length() != 0)
        {
            try
            {
                long newResetDuration = Long.parseLong(newResetDurationStr);

                if (newResetDuration >= 0)
                {
                    TimerFactory.getInstance(this).setResetDuration(TimeUnit.SECONDS.toMillis(newResetDuration));
                    Utils.shortToast(this, "Set reset duration to %d seconds", newResetDuration);
                }
                else
                {
                    Logger.log(this, "User tried to set the reset duration to a negative number %d", newResetDuration);
                }
            }
            catch (NumberFormatException e)
            {
                Logger.log(this, "Could not update the reset duration with input %s", newResetDurationStr);
            }
        }
    }
}
