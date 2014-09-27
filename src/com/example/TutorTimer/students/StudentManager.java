package com.example.TutorTimer.students;

import android.content.Context;
import com.example.TutorTimer.database.Database;

public class StudentManager
{
    private final Database m_database;

    public StudentManager(Context context)
    {
        m_database = new Database(context);
    }

    public void addStudent(String student)
    {
    }
}
