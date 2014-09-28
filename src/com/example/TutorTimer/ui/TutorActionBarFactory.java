package com.example.TutorTimer.ui;

import android.app.ActionBar;
import android.app.Activity;
import com.example.TutorTimer.R;

import java.util.concurrent.ThreadPoolExecutor;

public class TutorActionBarFactory
{
    public static ActionBar createActionBar(Activity activity, ThreadPoolExecutor threadPool)
    {
        activity.setContentView(R.layout.main);
        ActionBar actionBar = activity.getActionBar();

        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(false);

        CurrentStudentsTab.addCurrentStudentsTabToActionBar(actionBar, activity, threadPool);
        ImportStudentsTab.addImportStudentsTabToActionBar(actionBar, activity, threadPool);
        DebugTab.addDebugTabToActionBar(actionBar, activity, threadPool);

        return actionBar;
    }
}
