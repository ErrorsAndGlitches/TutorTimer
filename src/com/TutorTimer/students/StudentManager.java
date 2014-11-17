package com.TutorTimer.students;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.TutorTimer.Logger.Logger;
import com.TutorTimer.database.Database;
import com.TutorTimer.database.DbUtils;
import com.TutorTimer.utils.TimerFactory;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Managers the list of imported, active, and inactive students
 */
public class StudentManager
{
    private static StudentManager s_studentManager;
    private static final Map<StudentListType, Comparator<Student>> s_studentListComparators = new HashMap<StudentListType, Comparator<Student>>();

    static
    {
        s_studentListComparators.put(StudentListType.IMPORT, new StudentNameComparator());
        s_studentListComparators.put(StudentListType.INACTIVE, new StudentNameComparator());
        s_studentListComparators.put(StudentListType.ACTIVE, new StudentTimeLeftComparator());
    }

    private final Database                                        m_database;
    private final TimerFactory                                    m_timerFactory;
    private final Map<StudentListType, List<Student>>             m_studentLists;
    private final Map<StudentListType, List<StudentListObserver>> m_studentObserversList;

    public interface StudentListObserver
    {
        void onListChanged();
    }

    public enum StudentListType
    {
        IMPORT,
        ACTIVE,
        INACTIVE
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

    public List<Student> getStudentListForType(StudentListType type)
    {
        return Collections.unmodifiableList(m_studentLists.get(type));
    }

    public void registerObserver(StudentListType type, StudentListObserver observer)
    {
        m_studentObserversList.get(type).add(observer);
    }

    public void moveStudent(StudentListType fromType, StudentListType toType, Student student)
    {
        if (fromType == toType)
        {
            return;
        }

        // get the student that is being removed
        boolean wasStudentRemoved = removeStudent(fromType, student);
        if (!wasStudentRemoved)
        {
            return;
        }

        // put the student in its new location
        addStudentToListType(toType, student);
    }

    public boolean removeStudent(StudentListType type, Student student)
    {
        boolean wasStudentRemoved = m_studentLists.get(type).remove(student);
        if (wasStudentRemoved)
        {
            notifyStudentListObserversForType(type);
        }
        return wasStudentRemoved;
    }

    public void addStudentToImportList(final String name)
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
            addStudentToListType(StudentListType.IMPORT, new Student(id, name, m_timerFactory.getResetDuration()));
        }
    }

    public boolean removeImportStudent(Student student)
    {
        boolean studentRemoved = m_studentLists.get(StudentListType.IMPORT).remove(student);

        if (studentRemoved)
        {
            Database.Transaction transaction = m_database.beginTransaction();
            try
            {
                transaction.execSql("DELETE FROM students WHERE id = ?", new String[]{String.valueOf(student.getId())});
                transaction.setSuccessful();
            }
            finally
            {
                transaction.endTransaction();
            }

            notifyStudentListObserversForType(StudentListType.IMPORT);
        }

        return studentRemoved;
    }

    public void clearImportStudents()
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

        m_studentLists.get(StudentListType.IMPORT).clear();
        notifyStudentListObserversForType(StudentListType.IMPORT);
    }

    public void resortActiveStudentList()
    {
        Collections.sort(m_studentLists.get(StudentListType.ACTIVE), s_studentListComparators.get(StudentListType.ACTIVE));
        notifyStudentListObserversForType(StudentListType.ACTIVE);
    }

    private void addStudentToListType(StudentListType type, Student student)
    {
        List<Student> studentList = m_studentLists.get(type);
        studentList.add(student);
        Collections.sort(studentList, s_studentListComparators.get(type));
        notifyStudentListObserversForType(type);
    }

    private void notifyStudentListObserversForType(StudentListType type)
    {
        Logger.log(this, "Notifying list changed for type: %s", type);
        for (StudentListObserver observer : m_studentObserversList.get(type))
        {
            observer.onListChanged();
        }
    }

    private StudentManager(Context context)
    {
        m_database = new Database(context);
        m_timerFactory = TimerFactory.getInstance(context);

        m_studentLists = new HashMap<StudentListType, List<Student>>();
        m_studentObserversList = new HashMap<StudentListType, List<StudentListObserver>>();
        for (StudentListType type : StudentListType.values())
        {
            m_studentLists.put(type, new LinkedList<Student>());
            m_studentObserversList.put(type, new LinkedList<StudentListObserver>());
        }

        // populate the import students list
        loadStudentsFromDb();
    }

    private void loadStudentsFromDb()
    {
        final List<Student> studentList = m_studentLists.get(StudentListType.IMPORT);

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
                studentList.add(new Student(cursor.getLong(0), cursor.getString(1), m_timerFactory.getResetDuration()));
            }
        });

        Collections.sort(studentList, s_studentListComparators.get(StudentListType.IMPORT));
    }

    private static class StudentNameComparator implements Comparator<Student>
    {
        @Override
        public int compare(Student lhs, Student rhs)
        {
            return lhs.getName().toLowerCase().compareTo(rhs.getName().toLowerCase());
        }
    }

    private static class StudentTimeLeftComparator implements Comparator<Student>
    {
        @Override
        public int compare(Student lhs, Student rhs)
        {
            return (int) (lhs.getTimeLeft() - rhs.getTimeLeft());
        }
    }
}
