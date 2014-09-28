package com.example.TutorTimer.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.widget.ListView;
import com.example.TutorTimer.Logger.Logger;
import com.example.TutorTimer.R;
import com.example.TutorTimer.TutorTimer;
import com.example.TutorTimer.students.Student;

import java.util.concurrent.ThreadPoolExecutor;

class CurrentStudentsTab extends TutorTab
{
    static ActionBar.Tab addCurrentStudentsTabToActionBar(ActionBar actionBar, Activity activity, ThreadPoolExecutor threadPool)
    {
        ActionBar.Tab tab = actionBar.newTab();
        tab.setText(activity.getResources().getString(R.string.current_students_tab_name));
        tab.setTabListener(new CurrentStudentsTab(activity, threadPool));
        actionBar.addTab(tab);
        return tab;
    }

    private final ListView m_view;

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft)
    {
        Logger.log(this, "Received onTabSelected()");
        m_activity.setContentView(m_view);
        m_threadPool.submit(new LoadStudentsTask());
    }

    private CurrentStudentsTab(Activity activity, ThreadPoolExecutor threadPool)
    {
        super(activity, threadPool);

        m_view = new ListView(activity);
        m_view.setAdapter(m_studentAdapter);
    }

    private class LoadStudentsTask implements Runnable
    {
        @Override
        public void run()
        {
            Logger.log(this, "Loading students");

            m_studentNames.clear();

            for (Student student : ((TutorTimer) m_activity).getStudentManager().getStudents())
            {
                m_studentNames.add(student.toString());
            }

            m_activity.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    m_studentAdapter.notifyDataSetChanged();
                }
            });
        }
    }
}
