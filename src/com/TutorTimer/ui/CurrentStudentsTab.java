package com.TutorTimer.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.widget.ListView;
import com.TutorTimer.R;
import com.TutorTimer.students.Student;
import com.TutorTimer.students.StudentManager;
import com.TutorTimer.timer.TimerFactory;
import com.TutorTimer.ui.CurrentStudentsArrayAdapter.CurrentStudentEntry;

import java.util.LinkedList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

class CurrentStudentsTab extends TutorTab
{
    private static final long UI_UPDATE_RATE_MS = 500; // 0.5 seconds so the count down is smooth

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

    private final CurrentStudentsArrayAdapter     m_currentStudentsAdapter;
    private final LinkedList<CurrentStudentEntry> m_currentStudents;
    private final TimerFactory                    m_timerFactory;
    private final ScheduledThreadPoolExecutor     m_scheduledThreadPoolExecutor;
    private       ScheduledFuture<?>              m_uiUpdater;

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft)
    {
        m_activity.setContentView(R.layout.current_students_view);
        ListView currentStudentList = (ListView) m_activity.findViewById(R.id.current_student_list);
        currentStudentList.setAdapter(m_currentStudentsAdapter);
        m_uiUpdater = m_scheduledThreadPoolExecutor.scheduleAtFixedRate(new LoadCurrentStudentsTask(), UI_UPDATE_RATE_MS, UI_UPDATE_RATE_MS, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft)
    {
        super.onTabUnselected(tab, ft);
        m_uiUpdater.cancel(true);
    }

    private CurrentStudentsTab(Activity activity, ThreadPoolExecutor threadPool)
    {
        super(activity, threadPool);

        m_timerFactory = TimerFactory.getInstance(activity);

        m_scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
        m_scheduledThreadPoolExecutor.allowCoreThreadTimeOut(false);
        m_scheduledThreadPoolExecutor.setMaximumPoolSize(1);

        m_currentStudents = new LinkedList<CurrentStudentEntry>();
        m_currentStudentsAdapter = new CurrentStudentsArrayAdapter(activity, R.layout.current_students_view, m_currentStudents);

        ListView currentStudentList = (ListView) activity.findViewById(R.id.current_student_list);
        currentStudentList.setAdapter(m_currentStudentsAdapter);

        m_studentManager.registerObserver(new StudentManager.CurrentStudentObserver()
        {
            @Override
            public void onStudentAdded(Student student)
            {
                m_currentStudents.add(new CurrentStudentEntry(student, m_timerFactory.newTimer()));
                m_threadPool.submit(new LoadCurrentStudentsTask());
            }

            @Override
            public void onStudentRemoved(Student student)
            {
                m_currentStudents.remove(new CurrentStudentEntry(student, null));
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
