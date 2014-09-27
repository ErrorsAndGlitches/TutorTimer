package com.example.TutorTimer;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import com.example.TutorTimer.Logger.Logger;
import com.example.TutorTimer.ui.TabListener;

public class TutorTimer extends Activity
{
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Logger.log(TutorTimer.class, "Tutor Timer has started");

        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(false);

        ActionBar.Tab tab = actionBar.newTab()
                                    .setText("Artists")
                                    .setTabListener(new TabListener());
        actionBar.addTab(tab);

        tab = actionBar.newTab()
                      .setText("Albums")
                      .setTabListener(new TabListener());
        actionBar.addTab(tab);
    }
}
