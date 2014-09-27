package com.example.TutorTimer.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import com.example.TutorTimer.Logger.Logger;
import com.example.TutorTimer.R;

import java.util.concurrent.ThreadPoolExecutor;

class ImportStudentsTab extends TutorTab
{
    static ActionBar.Tab addImportStudentsTabToActionBar(ActionBar actionBar, Activity activity, ThreadPoolExecutor threadPool)
    {
        ActionBar.Tab tab = actionBar.newTab();
        tab.setText(activity.getResources().getString(R.string.import_students_tab_name));
        tab.setTabListener(new ImportStudentsTab(activity, threadPool));
        actionBar.addTab(tab);
        return tab;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft)
    {
        Logger.log(this, "Received onTabSelected()");
        m_activity.setContentView(m_view);
        m_studentNames.clear();
        m_studentNames.add("Import students options");
        m_studentAdapter.notifyDataSetChanged();
    }

    private ImportStudentsTab(Activity activity, ThreadPoolExecutor threadPool)
    {
        super(activity, threadPool);
    }
}
