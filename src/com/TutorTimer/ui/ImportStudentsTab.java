package com.TutorTimer.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.widget.ListView;
import com.TutorTimer.R;
import com.TutorTimer.students.StudentManager;
import com.TutorTimer.students.StudentManager.StudentListType;
import com.TutorTimer.ui.adapters.ImportStudentsArrayAdapter;

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
        m_activity.setContentView(R.layout.import_view);

        ListView studentList = (ListView) m_activity.findViewById(R.id.import_student_list);
        studentList.setAdapter(m_importStudentsArrayAdapter);
    }

    final ImportStudentsArrayAdapter m_importStudentsArrayAdapter;

    private ImportStudentsTab(final Activity activity, ThreadPoolExecutor threadPool)
    {
        super(activity, threadPool);

        StudentManager studentManager = StudentManager.getInstance(activity);
        m_importStudentsArrayAdapter = new ImportStudentsArrayAdapter(activity,
                                                                      R.layout.import_student_entry,
                                                                      studentManager.getStudentListForType(StudentListType.IMPORT));
        studentManager.registerObserver(StudentListType.IMPORT, new StudentManager.StudentListObserver()
        {
            @Override
            public void onListChanged()
            {
                activity.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        m_importStudentsArrayAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }
}
