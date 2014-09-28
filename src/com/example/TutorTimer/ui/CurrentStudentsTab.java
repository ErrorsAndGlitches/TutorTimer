package com.example.TutorTimer.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.example.TutorTimer.Logger.Logger;
import com.example.TutorTimer.R;
import com.example.TutorTimer.TutorTimer;
import com.example.TutorTimer.students.Student;
import com.example.TutorTimer.students.StudentManager;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

class CurrentStudentsTab extends TutorTab
{
    static ActionBar.Tab addCurrentStudentsTabToActionBar(ActionBar actionBar, Activity activity, ThreadPoolExecutor threadPool)
    {
        ActionBar.Tab tab = actionBar.newTab();
        tab.setText(activity.getResources().getString(R.string.current_students_tab_name));
        tab.setTabListener(new CurrentStudentsTab(activity, threadPool));
        actionBar.addTab(tab);
        return tab;
    }

    private final ArrayAdapter<Student> m_currentStudentsAdapter;
    private final List<Student>         m_currentStudents;

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft)
    {
        Logger.log(this, "Received onTabSelected()");
        m_activity.setContentView(R.layout.current_students_view);
    }

    private CurrentStudentsTab(Activity activity, ThreadPoolExecutor threadPool)
    {
        super(activity, threadPool);

        m_currentStudents = new LinkedList<Student>();
        m_currentStudentsAdapter = new ArrayAdapter<Student>(activity, R.layout.current_students_view, m_currentStudents);

        ListView currentStudentList = (ListView) activity.findViewById(R.id.current_student_list);
        currentStudentList.setAdapter(m_currentStudentsAdapter);

        ((TutorTimer) activity).getStudentManager().registerObserver(new StudentManager.CurrentStudentObserver()
        {
            @Override
            public void onStudentAdded(Student student)
            {
                m_currentStudents.add(student);
                m_threadPool.submit(new LoadCurrentStudentsTask());
            }

            @Override
            public void onStudentRemoved(Student student)
            {
                m_currentStudents.remove(student);
                m_threadPool.submit(new LoadCurrentStudentsTask());
            }
        });
    }

    private class LoadCurrentStudentsTask implements Runnable
    {
        @Override
        public void run()
        {
            m_activity.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    m_currentStudentsAdapter.notifyDataSetChanged();
                }
            });
        }
    }
}
