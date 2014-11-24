package com.TutorTimer.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.widget.ListView;
import com.TutorTimer.R;
import com.TutorTimer.students.StudentManager;
import com.TutorTimer.students.StudentManager.StudentListType;
import com.TutorTimer.ui.adapters.InactiveStudentsArrayAdapter;

import java.util.concurrent.ThreadPoolExecutor;

class InactiveStudentsTab extends TutorTab
{
    static ActionBar.Tab addImportStudentsTabToActionBar(ActionBar actionBar,
                                                         Activity activity,
                                                         ThreadPoolExecutor threadPool)
    {
        ActionBar.Tab tab = actionBar.newTab();
        tab.setText(activity.getResources().getString(R.string.inactive_students_tab_name));
        tab.setTabListener(new InactiveStudentsTab(activity, threadPool));
        actionBar.addTab(tab);
        return tab;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft)
    {
        m_activity.setContentView(R.layout.inactive_students);

        ListView studentList = (ListView) m_activity.findViewById(R.id.inactive_student_list);
        studentList.setAdapter(m_inactiveStudentsArrayAdapter);
    }

    final InactiveStudentsArrayAdapter m_inactiveStudentsArrayAdapter;

    private InactiveStudentsTab(final Activity activity, ThreadPoolExecutor threadPool)
    {
        super(activity, threadPool);

        StudentManager studentManager = StudentManager.getInstance(activity);
        m_inactiveStudentsArrayAdapter = new InactiveStudentsArrayAdapter(activity,
                                                                          R.layout.inactive_students,
                                                                          studentManager.getStudentListForType(StudentListType.INACTIVE));
        studentManager.registerObserver(StudentListType.INACTIVE, new StudentManager.StudentListObserver()
        {
            @Override
            public void onListChanged()
            {
                activity.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        m_inactiveStudentsArrayAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }
}
