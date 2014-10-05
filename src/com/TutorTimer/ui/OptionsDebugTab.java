package com.TutorTimer.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import com.TutorTimer.R;

import java.util.concurrent.ThreadPoolExecutor;

class OptionsDebugTab extends TutorTab
{
    static ActionBar.Tab addOptionsDebugTabToActionBar(ActionBar actionBar, Activity activity, ThreadPoolExecutor threadPool)
    {
        ActionBar.Tab tab = actionBar.newTab();
        tab.setText(activity.getResources().getString(R.string.options_debug_tab_name));
        tab.setTabListener(new OptionsDebugTab(activity, threadPool));
        actionBar.addTab(tab);
        return tab;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft)
    {
        m_activity.setContentView(R.layout.options_debug_view);
    }

    private OptionsDebugTab(Activity activity, ThreadPoolExecutor threadPool)
    {
        super(activity, threadPool);
    }
}
