package co.edu.unipiloto.scrumbacklog.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseManager {

    private static DatabaseManager instance;
    private static SQLiteDatabase database;
    private static DatabaseHelper dbHelper;

    private DatabaseManager(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public static synchronized DatabaseManager getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseManager(context.getApplicationContext());
        }
        return instance;
    }

    public SQLiteDatabase openDatabase() {
        if (database == null || !database.isOpen()) {
            database = dbHelper.getWritableDatabase();
        }
        return database;
    }

    public void closeDatabase() {
        if (database != null && database.isOpen()) {
            database.close();
        }
    }
}