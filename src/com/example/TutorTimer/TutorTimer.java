package com.example.TutorTimer;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.example.TutorTimer.Logger.Logger;
import com.example.TutorTimer.students.StudentManager;
import com.example.TutorTimer.ui.TabListener;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TutorTimer extends Activity
{
    private static final int THREAD_POOL_SIZE               = 10;
    private static final int THREAD_POOL_KEEP_ALIVE_TIME_MS = 5000;

    private final ThreadPoolExecutor   m_executor;
    private       StudentManager       m_studentManager;
    private       ArrayAdapter<String> m_studentAdapter;
    private final List<String>         m_studentNames;

    public TutorTimer()
    {
        m_executor = new ThreadPoolExecutor(THREAD_POOL_SIZE, THREAD_POOL_SIZE, THREAD_POOL_KEEP_ALIVE_TIME_MS,
                                            TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        m_studentNames = new LinkedList<String>();
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Logger.log(TutorTimer.class, "Tutor Timer has started");
        m_studentManager = new StudentManager(this);
        m_studentAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, m_studentNames);

        setContentView(R.layout.main);
        ListView studentList = (ListView) findViewById(R.id.student_list);
        studentList.setAdapter(m_studentAdapter);

        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(false);

        addCurrentStudentsTab(actionBar);
        addPastStudentsTab(actionBar);
        addDebugOptionsTab(actionBar);
    }

    private void addCurrentStudentsTab(ActionBar actionBar)
    {
        ActionBar.Tab tab = actionBar.newTab()
                                    .setText("Students")
                                    .setTabListener(new TabListener()
                                    {
                                        @Override
                                        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft)
                                        {
                                            Logger.log(TutorTimer.class, "Students onTabSelected()");
                                            m_executor.submit(new LoadStudentsTask());
                                        }
                                    });
        actionBar.addTab(tab);
    }

    private void addPastStudentsTab(ActionBar actionBar)
    {
        ActionBar.Tab tab = actionBar.newTab()
                                    .setText("Import")
                                    .setTabListener(new TabListener()
                                    {
                                        @Override
                                        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft)
                                        {
                                            Logger.log(TutorTimer.class, "Import onTabSelected()");
                                            m_studentNames.clear();
                                            m_studentNames.add("Import students options");
                                            m_studentAdapter.notifyDataSetChanged();
                                        }
                                    });
        actionBar.addTab(tab);
    }

    private void addDebugOptionsTab(ActionBar actionBar)
    {
        ActionBar.Tab tab = actionBar.newTab()
                                    .setText("Debug")
                                    .setTabListener(new TabListener()
                                    {
                                        @Override
                                        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft)
                                        {
                                            Logger.log(TutorTimer.class, "Debug onTabSelected()");
                                            m_studentNames.clear();
                                            m_studentNames.add("Debug options");
                                            m_studentAdapter.notifyDataSetChanged();
                                        }
                                    });
        actionBar.addTab(tab);
    }

    private class LoadStudentsTask implements Runnable
    {
        @Override
        public void run()
        {
            Logger.log(LoadStudentsTask.class, "Loading students");

            m_studentNames.clear();

            for (int i = 0; i < 10; ++i)
            {
                m_studentNames.add(String.format("Student_%s", i));
            }

            runOnUiThread(new Runnable()
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
