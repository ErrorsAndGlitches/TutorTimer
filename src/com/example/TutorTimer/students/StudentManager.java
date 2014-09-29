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
    private static StudentManager s_studentManager;

    private final Database                     m_database;
    private final List<StudentManagerObserver> m_observers;
    private final List<Student>                m_currentStudents;

    public interface StudentManagerObserver
    {
    }

    public interface RosterChangeObserver extends StudentManagerObserver
    {
        public void onRosterChange();
    }

    public interface CurrentStudentObserver extends StudentManagerObserver
    {
        public void onStudentAdded(Student student);

        public void onStudentRemoved(Student student);
    }

    public static StudentManager getInstance(Context context)
    {
        if (s_studentManager == null)
        {
            synchronized (StudentManager.class)
            {
                if (s_studentManager == null)
                {
                    s_studentManager = new StudentManager(context);
                }
            }
        }

        return s_studentManager;
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
            notifyRosterChange();
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

    public void deleteStudent(long id)
    {
        Database.Transaction transaction = m_database.beginTransaction();
        try
        {
            transaction.execSql("DELETE FROM students WHERE id = ?", new String[]{String.valueOf(id)});
            transaction.setSuccessful();
        }
        finally
        {
            transaction.endTransaction();
        }

        notifyRosterChange();
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

        notifyRosterChange();
    }

    public void addToCurrentStudents(Student student)
    {
        if (!m_currentStudents.contains(student))
        {
            m_currentStudents.add(student);
            notifyCurrentStudentAdded(student);
        }
    }

    public void removeFromCurrentStudents(Student student)
    {
        if (m_currentStudents.contains(student))
        {
            m_currentStudents.remove(student);
            notifyCurrentStudentRemoved(student);
        }
    }

    public List<Student> getCurrentStudents()
    {
        return new LinkedList<Student>(m_currentStudents);
    }

    public void registerObserver(StudentManagerObserver observer)
    {
        m_observers.add(observer);
    }

    private StudentManager(Context context)
    {
        m_database = new Database(context);
        m_observers = new LinkedList<StudentManagerObserver>();
        m_currentStudents = new LinkedList<Student>();
    }

    private void notifyRosterChange()
    {
        for (StudentManagerObserver observer : m_observers)
        {
            if (observer instanceof RosterChangeObserver)
            {
                ((RosterChangeObserver) observer).onRosterChange();
            }
        }
    }

    private void notifyCurrentStudentAdded(Student student)
    {
        for (StudentManagerObserver observer : m_observers)
        {
            if (observer instanceof CurrentStudentObserver)
            {
                ((CurrentStudentObserver) observer).onStudentAdded(student);
            }
        }
    }

    private void notifyCurrentStudentRemoved(Student student)
    {
        for (StudentManagerObserver observer : m_observers)
        {
            if (observer instanceof CurrentStudentObserver)
            {
                ((CurrentStudentObserver) observer).onStudentRemoved(student);
            }
        }
    }
}
