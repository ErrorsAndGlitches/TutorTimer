package com.example.TutorTimer.students;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.example.TutorTimer.database.Database;
import com.example.TutorTimer.database.DbUtils;

import java.util.LinkedList;
import java.util.List;

public class StudentManager
{
    private final Database m_database;

    public StudentManager(Context context)
    {
        m_database = new Database(context);
    }

    public Student addStudent(String name)
    {
        long id;

        Database.Transaction transaction = m_database.beginTransaction();
        try
        {
            ContentValues values = new ContentValues();
            values.put("name", name);
            id = transaction.insertOrThrow("students", values);
            transaction.setSuccessful();
        }
        finally
        {
            transaction.endTransaction();
        }

        return new Student(id, name);
    }

    public List<Student> getStudents()
    {
        final List<Student> students = new LinkedList<Student>();

        DbUtils.databaseQuery(m_database, new DbUtils.QueryProcessor()
        {
            @Override
            public Cursor performQuery(Database.Transaction transaction)
            {
                return transaction.query("SELECT id, name FROM students", null);
            }

            @Override
            public void process(Cursor cursor)
            {
                students.add(new Student(cursor.getLong(0), cursor.getString(1)));
            }
        });

        return students;
    }
}
