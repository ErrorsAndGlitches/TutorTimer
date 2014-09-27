package com.example.TutorTimer.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.example.TutorTimer.Logger.Logger;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

abstract class TutorTab implements ActionBar.TabListener
{
    final Activity             m_activity;
    final ThreadPoolExecutor   m_threadPool;
    final ArrayAdapter<String> m_studentAdapter;
    final List<String>         m_studentNames;
    final ListView m_view;

    protected TutorTab(Activity activity, ThreadPoolExecutor threadPool)
    {
        m_activity = activity;
        m_threadPool = threadPool;

        m_studentNames = new LinkedList<String>();
        m_studentAdapter = new ArrayAdapter<String>(m_activity, android.R.layout.simple_list_item_1, m_studentNames);

        m_view = new ListView(activity);
        m_view .setAdapter(m_studentAdapter);
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
