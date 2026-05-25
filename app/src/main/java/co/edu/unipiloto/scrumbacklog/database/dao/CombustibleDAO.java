package co.edu.unipiloto.scrumbacklog.database.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class CombustibleDAO {
    private SQLiteDatabase db;

    public CombustibleDAO(SQLiteDatabase db) {
        this.db = db;
    }

    public ArrayList<String> obtenerCombustibles() {
        ArrayList<String> lista = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT nombre FROM combustible", null);

        while(cursor.moveToNext()){
            lista.add(cursor.getString(0));
        }
        cursor.close();
        return lista;
    }

    public int obtenerIdCombustible(String nombre){
        Cursor cursor = db.rawQuery(
                "SELECT id_combustible FROM combustible WHERE nombre=?",
                new String[]{nombre}
        );

        if(cursor.moveToFirst()){
            int id = cursor.getInt(0);
            cursor.close();
            return id;
        }
        cursor.close();
        return -1;
    }
}