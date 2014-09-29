package com.example.TutorTimer.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
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

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft)
    {
        m_activity.setContentView(R.layout.debug_view);
    }

    private DebugTab(Activity activity, ThreadPoolExecutor threadPool)
    {
        super(activity, threadPool);
    }
}
