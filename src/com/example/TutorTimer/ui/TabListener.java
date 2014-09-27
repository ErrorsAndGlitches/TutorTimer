package com.example.TutorTimer.ui;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import com.example.TutorTimer.Logger.Logger;

public class TabListener implements ActionBar.TabListener
{
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft)
    {
        Logger.log(TabListener.class, "Received %s", Thread.currentThread().getStackTrace()[2].getMethodName());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft)
    {
        Logger.log(TabListener.class, "Received %s", Thread.currentThread().getStackTrace()[2].getMethodName());
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft)
    {
        Logger.log(TabListener.class, "Received %s", Thread.currentThread().getStackTrace()[2].getMethodName());
    }
}
