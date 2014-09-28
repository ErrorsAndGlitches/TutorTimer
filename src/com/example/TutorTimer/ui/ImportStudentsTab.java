package com.example.TutorTimer.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.widget.ListView;
import com.example.TutorTimer.Logger.Logger;
import com.example.TutorTimer.R;
import com.example.TutorTimer.TutorTimer;
import com.example.TutorTimer.students.Student;
import com.example.TutorTimer.students.StudentManager;

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
        m_activity.setContentView(R.layout.import_view);
        ListView studentList = (ListView) m_activity.findViewById(R.id.student_list);
        studentList.setAdapter(m_studentAdapter);

        m_threadPool.submit(new LoadStudentsTask());
    }

    private ImportStudentsTab(final Activity activity, ThreadPoolExecutor threadPool)
    {
        super(activity, threadPool);

        ((TutorTimer) activity).getStudentManager().registerObserver(new StudentManager.StudentManagerObserver()
        {
            @Override
            public void onRosterChange()
            {
                m_threadPool.submit(new LoadStudentsTask());
            }
        });
    }

    private class LoadStudentsTask implements Runnable
    {
        @Override
        public void run()
        {
            Logger.log(this, "Loading students");

            m_studentNames.clear();

            for (Student student : ((TutorTimer) m_activity).getStudentManager().getStudents())
            {
                m_studentNames.add(student.toString());
            }

            m_activity.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    m_studentAdapter.notifyDataSetChanged();
                }
            });
        }
    }
}
