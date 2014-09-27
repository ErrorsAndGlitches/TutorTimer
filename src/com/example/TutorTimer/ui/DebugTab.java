package com.example.TutorTimer.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import com.example.TutorTimer.Logger.Logger;
import com.example.TutorTimer.R;

import java.util.concurrent.ThreadPoolExecutor;

class DebugTab extends TutorTab
{
    static ActionBar.Tab addDebugTabToActionBar(ActionBar actionBar, Activity activity, ThreadPoolExecutor threadPool)
    {
        ActionBar.Tab tab = actionBar.newTab();
        tab.setText(activity.getResources().getString(R.string.debug_tab_name));
        tab.setTabListener(new DebugTab(activity, threadPool));
        actionBar.addTab(tab);
        return tab;
    }

    private DebugTab(Activity activity, ThreadPoolExecutor threadPool)
    {
        super(activity, threadPool);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft)
    {
        Logger.log(this, "Received onTabSelected()");
        m_activity.setContentView(m_view);
        m_studentNames.clear();
        m_studentNames.add("Debug options");
        m_studentAdapter.notifyDataSetChanged();
    }
}
