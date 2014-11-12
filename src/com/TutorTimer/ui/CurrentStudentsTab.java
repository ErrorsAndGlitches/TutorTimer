package com.TutorTimer.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.widget.ListView;
import com.TutorTimer.R;
import com.TutorTimer.students.Student;
import com.TutorTimer.students.StudentManager;
import com.TutorTimer.utils.CurrentStudentEntry;
import com.TutorTimer.utils.CurrentStudentRoster;
import com.TutorTimer.utils.TimerFactory;

import java.util.concurrent.ThreadPoolExecutor;

class CurrentStudentsTab extends TutorTab
{
    static ActionBar.Tab addCurrentStudentsTabToActionBar(ActionBar actionBar,
                                                          Activity activity,
                                                          ThreadPoolExecutor threadPool)
    {
        ActionBar.Tab tab = actionBar.newTab();
        tab.setText(activity.getResources().getString(R.string.current_students_tab_name));
        tab.setTabListener(new CurrentStudentsTab(activity, threadPool));
        actionBar.addTab(tab);
        return tab;
    }

    private final CurrentStudentsArrayAdapter m_currentStudentsAdapter;
    private final CurrentStudentRoster        m_studentRoster;
    private final TimerFactory                m_timerFactory;

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft)
    {
        m_activity.setContentView(R.layout.current_students_view);
        ListView currentStudentList = (ListView) m_activity.findViewById(R.id.current_student_list);
        currentStudentList.setAdapter(m_currentStudentsAdapter);
    }

    private CurrentStudentsTab(Activity activity, ThreadPoolExecutor threadPool)
    {
        super(activity, threadPool);

        m_timerFactory = TimerFactory.getInstance(activity);
        m_studentRoster = new CurrentStudentRoster();
        m_currentStudentsAdapter = new CurrentStudentsArrayAdapter(activity,
                                                                   R.layout.current_students_view,
                                                                   m_studentRoster);

        ListView currentStudentList = (ListView) activity.findViewById(R.id.current_student_list);
        currentStudentList.setAdapter(m_currentStudentsAdapter);

        m_studentManager.registerObserver(new StudentManager.CurrentStudentObserver()
        {
            @Override
            public void onStudentAdded(Student student)
            {
                m_studentRoster.addStudent(new CurrentStudentEntry(student, m_timerFactory.getResetDuration()));
                m_threadPool.submit(new LoadCurrentStudentsTask());
            }

            @Override
            public void onStudentRemoved(Student student)
            {
                m_studentRoster.removeStudent(new CurrentStudentEntry(student, 0L));
                m_threadPool.submit(new LoadCurrentStudentsTask());
            }
        });
    }

    private class LoadCurrentStudentsTask implements Runnable
    {
        @Override
        public void run()
        {
            m_activity.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    m_currentStudentsAdapter.notifyDataSetChanged();
                }
            });
        }
    }
}
