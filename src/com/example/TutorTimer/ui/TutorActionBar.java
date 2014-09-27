package com.example.TutorTimer.ui;

import android.app.ActionBar;
import android.app.Activity;
import com.example.TutorTimer.R;

import java.util.concurrent.ThreadPoolExecutor;

public class TutorActionBar
{
    private static ActionBar s_actionBar;

    public static ActionBar initializeActionBar(Activity activity, ThreadPoolExecutor threadPool)
    {
        if (s_actionBar == null)
        {
            synchronized (TutorActionBar.class)
            {
                if (s_actionBar == null)
                {
                    activity.setContentView(R.layout.main);
                    s_actionBar = activity.getActionBar();

                    s_actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
                    s_actionBar.setDisplayShowTitleEnabled(false);

                    CurrentStudentsTab.addCurrentStudentsTabToActionBar(s_actionBar, activity, threadPool);
                    ImportStudentsTab.addImportStudentsTabToActionBar(s_actionBar, activity, threadPool);
                    DebugTab.addDebugTabToActionBar(s_actionBar, activity, threadPool);
                }
            }
        }

        return s_actionBar;
    }
}
