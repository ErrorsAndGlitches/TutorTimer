package com.TutorTimer.database;

import android.database.Cursor;

public class DbUtils
{
    public interface QueryProcessor
    {
        Cursor performQuery(Database.Transaction transaction);
        void process(Cursor cursor);
    }

    public static void databaseQuery(Database database, QueryProcessor processor)
    {
        Database.Transaction transaction = database.beginTransaction();
        try
        {
            databaseQuery(transaction, processor);
            transaction.setSuccessful();
        }
        finally
        {
            transaction.endTransaction();
        }
    }

    public static void databaseQuery(Database.Transaction transaction, QueryProcessor processor)
    {
        Cursor cursor = processor.performQuery(transaction);
        try
        {
            if (cursor != null && cursor.moveToFirst())
            {
                while (!cursor.isAfterLast())
                {
                    processor.process(cursor);
                    cursor.moveToNext();
                }
            }
        }
        finally
        {
            if (cursor != null)
            {
                cursor.close();
            }
        }
    }
}
