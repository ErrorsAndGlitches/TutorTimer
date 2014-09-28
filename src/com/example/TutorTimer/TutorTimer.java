package com.example.TutorTimer;

import android.app.Activity;
import android.os.Bundle;
import com.example.TutorTimer.Logger.Logger;
import com.example.TutorTimer.ui.TutorActionBarFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TutorTimer extends Activity
{
    private static final int THREAD_POOL_SIZE               = 10;
    private static final int THREAD_POOL_KEEP_ALIVE_TIME_MS = 5000;

    private final ThreadPoolExecutor m_threadPool;

    public TutorTimer()
    {
        m_threadPool = new ThreadPoolExecutor(THREAD_POOL_SIZE, THREAD_POOL_SIZE, THREAD_POOL_KEEP_ALIVE_TIME_MS,
                                              TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Logger.log(this, "Tutor Timer has started");
        TutorActionBarFactory.createActionBar(this, m_threadPool);
    }
}
