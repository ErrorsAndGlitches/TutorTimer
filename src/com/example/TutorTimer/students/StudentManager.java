package com.example.TutorTimer.students;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.example.TutorTimer.Logger.Logger;
import com.example.TutorTimer.database.Database;
import com.example.TutorTimer.database.DbUtils;

import java.util.LinkedList;
import java.util.List;

public class StudentManager
{
    private final Database                     m_database;
    private final List<StudentManagerObserver> m_observers;

    public interface StudentManagerObserver
    {
        public void onRosterChange();
    }

    public StudentManager(Context context)
    {
        m_database = new Database(context);
        m_observers = new LinkedList<StudentManagerObserver>();
    }

    public void addStudent(final String name)
    {
        long id = -1;

        Database.Transaction transaction = m_database.beginTransaction();
        try
        {
            // first check if the student exists already
            final boolean[] studentExists = {false};
            DbUtils.databaseQuery(transaction, new DbUtils.QueryProcessor()
            {
                @Override
                public Cursor performQuery(Database.Transaction transaction)
                {
                    return transaction.query("SELECT id FROM students WHERE name = ?", new String[]{name});
                }

                @Override
                public void process(Cursor cursor)
                {
                    studentExists[0] = true;
                }
            });

            if (studentExists[0])
            {
                Logger.log(this, "Attempting to insert a student that already exists - ignoring");
            }
            else
            {
                // add the student
                ContentValues values = new ContentValues();
                values.put("name", name);
                id = transaction.insertOrThrow("students", values);
            }

            transaction.setSuccessful();
        }
        finally
        {
            transaction.endTransaction();
        }

        if (id != -1)
        {
            notifyObservers();
        }
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

    public void deleteStudent(String name)
    {
        Database.Transaction transaction = m_database.beginTransaction();
        try
        {
            transaction.execSql("DELETE FROM students WHERE name = ?", new String[]{name});
            transaction.setSuccessful();
        }
        finally
        {
            transaction.endTransaction();
        }

        notifyObservers();
    }

    public void clearStudents()
    {
        Database.Transaction transaction = m_database.beginTransaction();
        try
        {
            transaction.execSql("DELETE FROM students");
            transaction.setSuccessful();
        }
        finally
        {
            transaction.endTransaction();
        }

        notifyObservers();
    }

    public void registerObserver(StudentManagerObserver observer)
    {
        m_observers.add(observer);
    }

    private void notifyObservers()
    {
        for (StudentManagerObserver observer : m_observers)
        {
            observer.onRosterChange();
        }
    }
}
