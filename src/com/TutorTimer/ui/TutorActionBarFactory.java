package com.TutorTimer.ui;

import android.app.ActionBar;
import android.app.Activity;
import com.TutorTimer.R;

import java.util.concurrent.ThreadPoolExecutor;

public class TutorActionBarFactory
{
    public static ActionBar createActionBar(Activity activity, ThreadPoolExecutor threadPool)
    {
        activity.setContentView(R.layout.active_students);
        ActionBar actionBar = activity.getActionBar();

        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(false);

        ActiveStudentsTab.addCurrentStudentsTabToActionBar(actionBar, activity, threadPool);
        InactiveStudentsTab.addImportStudentsTabToActionBar(actionBar, activity, threadPool);
        OptionsDebugTab.addOptionsDebugTabToActionBar(actionBar, activity, threadPool);

        return actionBar;
    }
}
