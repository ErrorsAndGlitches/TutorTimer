package com.TutorTimer.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.widget.ListView;
import com.TutorTimer.R;
import com.TutorTimer.students.StudentManager;
import com.TutorTimer.students.StudentManager.StudentListType;
import com.TutorTimer.ui.adapters.ActiveStudentsArrayAdapter;

import java.util.concurrent.ThreadPoolExecutor;

class CurrentStudentsTab extends TutorTab
{
    static ActionBar.Tab addCurrentStudentsTabToActionBar(ActionBar actionBar,
                                                          Activity activity,
                                                          ThreadPoolExecutor threadPool)
    {
        ActionBar.Tab tab = actionBar.newTab();
        tab.setText(activity.getResources().getString(R.string.current_students_tab_name));
        tab.setTabListener(new CurrentStudentsTab(activity, threadPool));
        actionBar.addTab(tab);
        return tab;
    }

    private final ActiveStudentsArrayAdapter m_activeStudentsAdapter;

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft)
    {
        m_activity.setContentView(R.layout.current_students_view);

        ListView activeStudentList = (ListView) m_activity.findViewById(R.id.active_student_list);
        activeStudentList.setAdapter(m_activeStudentsAdapter);
    }

    private CurrentStudentsTab(Activity activity, ThreadPoolExecutor threadPool)
    {
        super(activity, threadPool);

        StudentManager studentManager = StudentManager.getInstance(activity);

        // set the active students list
        m_activeStudentsAdapter = new ActiveStudentsArrayAdapter(activity,
                                                                 R.layout.current_students_view,
                                                                 studentManager.getStudentListForType(StudentListType.ACTIVE));
        ListView activeStudentList = (ListView) activity.findViewById(R.id.active_student_list);
        activeStudentList.setAdapter(m_activeStudentsAdapter);

        // register callbacks
        m_studentManager.registerObserver(StudentListType.ACTIVE, new StudentManager.StudentListObserver()
        {
            @Override
            public void onListChanged()
            {
                m_activity.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        m_activeStudentsAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }
}
