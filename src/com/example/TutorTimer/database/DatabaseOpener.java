package com.example.TutorTimer.database;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.TutorTimer.Logger.Logger;

/**
 * Opens the Database
 */
class DatabaseOpener extends SQLiteOpenHelper
{
    DatabaseOpener(Context context,
                   String name,
                   SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, name, factory, version);
    }

    DatabaseOpener(Context context,
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
        Logger.log(DatabaseOpener.class, "Configuring database %s", db.getPath());
        db.enableWriteAheadLogging();
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        Logger.log(DatabaseOpener.class, "Creating database %s", db.getPath());
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
