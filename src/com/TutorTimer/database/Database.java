package com.TutorTimer.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.TutorTimer.R;

public class Database
{
    private final SQLiteDatabase m_database;

    public Database(Context context)
    {
        String dbName = context.getResources().getString(R.string.db_name);
        int dbVersion = context.getResources().getInteger(R.integer.database_version);
        m_database = new DatabaseOpener(context, dbName, null, dbVersion).getWritableDatabase();
    }

    public Transaction beginTransaction()
    {
        return new Transaction();
    }

    // all transactions are write transactions for the moment
    public class Transaction
    {
        Transaction()
        {
            m_database.beginTransaction();
        }

        public void execSql(String sql)
        {
            m_database.execSQL(sql);
        }

        public void execSql(String sql, String[] args)
        {
            m_database.execSQL(sql, args);
        }

        public long insertOrThrow(String table, ContentValues values)
        {
            return m_database.insertOrThrow(table, null, values);
        }

        public Cursor query(String sql, String[] args)
        {
            return m_database.rawQuery(sql, args);
        }

        public void setSuccessful()
        {
            m_database.setTransactionSuccessful();
        }

        public void endTransaction()
        {
            m_database.endTransaction();
        }
    }
}
