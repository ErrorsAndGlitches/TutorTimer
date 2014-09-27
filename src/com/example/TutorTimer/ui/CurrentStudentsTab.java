package com.example.TutorTimer.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import com.example.TutorTimer.Logger.Logger;
import com.example.TutorTimer.R;

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
    }

    private class LoadStudentsTask implements Runnable
    {
        @Override
        public void run()
        {
            Logger.log(this, "Loading students");

            m_studentNames.clear();

            for (int i = 0; i < 10; ++i)
            {
                m_studentNames.add(String.format("Student_%s", i));
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
