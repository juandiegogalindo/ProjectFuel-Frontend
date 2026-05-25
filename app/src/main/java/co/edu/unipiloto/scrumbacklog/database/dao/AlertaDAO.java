package co.edu.unipiloto.scrumbacklog.database.dao;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class AlertaDAO {

    private SQLiteDatabase db;

    public AlertaDAO(SQLiteDatabase db){
        this.db = db;
    }

    public void crearAlerta(int idUbicacion, int idCombustible, double nivelMin){

        ContentValues values = new ContentValues();
        values.put("id_ubicacion", idUbicacion);
        values.put("id_combustible", idCombustible);
        values.put("nivel_minimo", nivelMin);

        db.insert("alerta", null, values);
    }
}