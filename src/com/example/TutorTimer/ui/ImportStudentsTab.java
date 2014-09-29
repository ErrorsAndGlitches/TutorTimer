package com.example.TutorTimer.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.widget.ListView;
import com.example.TutorTimer.Logger.Logger;
import com.example.TutorTimer.R;
import com.example.TutorTimer.students.Student;
import com.example.TutorTimer.students.StudentManager;

import java.util.LinkedList;
import java.util.List;
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

        ListView studentList = (ListView) m_activity.findViewById(R.id.import_student_list);
        studentList.setAdapter(m_importStudentsArrayAdapter);

        m_threadPool.submit(new LoadStudentsTask());
    }

    final ImportStudentsArrayAdapter m_importStudentsArrayAdapter;
    final List<Student>              m_importableStudents;

    private ImportStudentsTab(final Activity activity, ThreadPoolExecutor threadPool)
    {
        super(activity, threadPool);

        m_importableStudents = new LinkedList<Student>();
        m_importStudentsArrayAdapter = new ImportStudentsArrayAdapter(activity, R.layout.import_student_entry, m_importableStudents);

        m_studentManager.registerObserver(new StudentManager.RosterChangeObserver()
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

            final List<Student> allStudents = m_studentManager.getStudents();

            m_activity.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    m_importableStudents.clear();

                    for (Student student : allStudents)
                    {
                        m_importableStudents.add(student);
                    }

                    m_importStudentsArrayAdapter.notifyDataSetChanged();
                }
            });
        }
    }
}
