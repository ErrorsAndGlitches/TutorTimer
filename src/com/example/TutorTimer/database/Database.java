package com.example.TutorTimer.database;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.TutorTimer.R;

/**
 * Opens the Database
 */
public class Database extends SQLiteOpenHelper
{
    public Database(Context context,
                    String name,
                    SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, name, factory, version);
    }

    public Database(Context context,
                    String name,
                    SQLiteDatabase.CursorFactory factory,
                    int version,
                    DatabaseErrorHandler errorHandler)
    {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onConfigure(SQLiteDatabase db)
    {
        super.onConfigure(db);
        db.enableWriteAheadLogging();
        db.setVersion(R.integer.database_version);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        initializeDatabaseTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // shouldn't be getting upgrades
    }

    private void initializeDatabaseTables(SQLiteDatabase db)
    {
        db.beginTransaction();
        try
        {
            db.execSQL("CREATE TABLE students(id INTEGER PRIMARY KEY, name TEXT NOT NULL UNIQUE);");
            db.setTransactionSuccessful();
        }
        finally
        {
            db.endTransaction();
        }
    }
}
