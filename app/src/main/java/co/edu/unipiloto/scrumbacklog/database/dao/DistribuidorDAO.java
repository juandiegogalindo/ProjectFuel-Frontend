package co.edu.unipiloto.scrumbacklog.database.dao;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class DistribuidorDAO {

    private SQLiteDatabase db;

    public DistribuidorDAO(SQLiteDatabase db){
        this.db = db;
    }

    public void insertarDistribuidor(String nombre){
        ContentValues values = new ContentValues();
        values.put("nombre", nombre);
        db.insert("distribuidor", null, values);
    }
}