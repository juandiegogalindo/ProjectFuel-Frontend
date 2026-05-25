package co.edu.unipiloto.scrumbacklog.database.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class UbicacionDAO {
    private SQLiteDatabase db;

    public UbicacionDAO(SQLiteDatabase db) {
        this.db = db;
    }

    public ArrayList<String> obtenerCiudades(){
        ArrayList<String> lista = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT DISTINCT ciudad FROM ubicacion", null);

        while(cursor.moveToNext()){
            lista.add(cursor.getString(0).trim());
        }
        cursor.close();
        return lista;
    }

    public ArrayList<String> obtenerZonas(String ciudad){
        ArrayList<String> lista = new ArrayList<>();
        Cursor cursor = db.rawQuery(
                "SELECT localidad FROM ubicacion WHERE ciudad=?",
                new String[]{ciudad}
        );

        while(cursor.moveToNext()){
            lista.add(cursor.getString(0).trim());
        }

        cursor.close();
        return lista;
    }

    public int obtenerIdUbicacion(String ciudad, String zona){
        Cursor cursor = db.rawQuery(
                "SELECT id_ubicacion FROM ubicacion WHERE ciudad=? AND localidad=?",
                new String[]{ciudad, zona}
        );

        if(cursor.moveToFirst()){
            int id = cursor.getInt(0);
            cursor.close();
            return id;
        }

        cursor.close();
        return -1;
    }

    public Cursor obtenerHorarios() {
        return db.rawQuery(
                "SELECT id_ubicacion AS _id, * FROM ubicacion",
                null
        );
    }
    }