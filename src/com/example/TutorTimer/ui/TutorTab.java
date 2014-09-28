package com.example.TutorTimer.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import com.example.TutorTimer.Logger.Logger;

import java.util.concurrent.ThreadPoolExecutor;

abstract class TutorTab implements ActionBar.TabListener
{
    final Activity             m_activity;
    final ThreadPoolExecutor   m_threadPool;

    protected TutorTab(Activity activity, ThreadPoolExecutor threadPool)
    {
        m_activity = activity;
        m_threadPool = threadPool;
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft)
    {
        Logger.log(this, "Received %s", Thread.currentThread().getStackTrace()[2].getMethodName());
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft)
    {
        Logger.log(this, "Received %s", Thread.currentThread().getStackTrace()[2].getMethodName());
    }
}
