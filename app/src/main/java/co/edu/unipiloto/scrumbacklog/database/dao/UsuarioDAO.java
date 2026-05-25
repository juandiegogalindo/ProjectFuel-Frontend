package co.edu.unipiloto.scrumbacklog.database.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import co.edu.unipiloto.scrumbacklog.model.Usuario;

public class UsuarioDAO {

    private SQLiteDatabase db;

    public UsuarioDAO(SQLiteDatabase db) {
        this.db = db;
    }

    // ==============================
    // INSERTAR USUARIO
    // ==============================
    public long insertarUsuario(Usuario usuario) {
        long resultado = -1;

        try {
            ContentValues values = new ContentValues();
            values.put("nombre", usuario.getNombre());
            values.put("usuario", usuario.getUsuario());
            values.put("correo", usuario.getCorreo());
            values.put("direccion", usuario.getDireccion());
            values.put("password", usuario.getPassword());
            values.put("rol", usuario.getRol());
            values.put("id_ubicacion", usuario.getIdUbicacion());
            values.put("fecha_nacimiento", usuario.getFechaNacimiento());
            values.put("genero", usuario.getGenero());
            values.put("latitud", usuario.getLatitud());
            values.put("longitud", usuario.getLongitud());
            values.put("verificado", usuario.getVerificado());
            values.put("codigo_verificacion", usuario.getCodigoVerificacion());

            resultado = db.insert("usuario", null, values);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultado;
    }

    // ==============================
    // VALIDAR SI EXISTE CORREO
    // ==============================
    public boolean existeCorreo(String correo) {
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(
                    "SELECT 1 FROM usuario WHERE correo = ?",
                    new String[]{correo}
            );
            return cursor.moveToFirst();

        } finally {
            if (cursor != null) cursor.close();
        }
    }

    // ==============================
    // LOGIN POR CORREO
    // ==============================
    public boolean validarLogin(String correo, String password) {
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(
                    "SELECT 1 FROM usuario WHERE correo = ? AND password = ?",
                    new String[]{correo, password}
            );
            return cursor.moveToFirst();

        } finally {
            if (cursor != null) cursor.close();
        }
    }

    // ==============================
    // LOGIN POR USUARIO
    // ==============================
    public boolean loginPorUsuario(String usuario, String password) {
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(
                    "SELECT 1 FROM usuario WHERE usuario = ? AND password = ?",
                    new String[]{usuario, password}
            );
            return cursor.moveToFirst();

        } finally {
            if (cursor != null) cursor.close();
        }
    }

    // ==============================
    // OBTENER ROL
    // ==============================
    public String obtenerRol(String correo) {
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(
                    "SELECT rol FROM usuario WHERE correo = ?",
                    new String[]{correo}
            );

            if (cursor.moveToFirst()) {
                return cursor.getString(0);
            }

        } finally {
            if (cursor != null) cursor.close();
        }

        return null;
    }

    // ==============================
    // OBTENER USUARIO COMPLETO
    // ==============================
    public Usuario obtenerUsuario(String correo) {
        Cursor cursor = null;
        Usuario usuario = null;

        try {
            cursor = db.rawQuery(
                    "SELECT * FROM usuario WHERE correo = ?",
                    new String[]{correo}
            );

            if (cursor.moveToFirst()) {
                usuario = new Usuario();

                usuario.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                usuario.setNombre(cursor.getString(cursor.getColumnIndexOrThrow("nombre")));
                usuario.setUsuario(cursor.getString(cursor.getColumnIndexOrThrow("usuario")));
                usuario.setCorreo(cursor.getString(cursor.getColumnIndexOrThrow("correo")));
                usuario.setDireccion(cursor.getString(cursor.getColumnIndexOrThrow("direccion")));
                usuario.setPassword(cursor.getString(cursor.getColumnIndexOrThrow("password")));
                usuario.setRol(cursor.getString(cursor.getColumnIndexOrThrow("rol")));
                usuario.setIdUbicacion(cursor.getInt(cursor.getColumnIndexOrThrow("id_ubicacion")));
                usuario.setFechaNacimiento(cursor.getString(cursor.getColumnIndexOrThrow("fecha_nacimiento")));
                usuario.setGenero(cursor.getString(cursor.getColumnIndexOrThrow("genero")));
                usuario.setLatitud(cursor.getDouble(cursor.getColumnIndexOrThrow("latitud")));
                usuario.setLongitud(cursor.getDouble(cursor.getColumnIndexOrThrow("longitud")));
                usuario.setVerificado(cursor.getInt(cursor.getColumnIndexOrThrow("verificado")));
                usuario.setCodigoVerificacion(cursor.getString(cursor.getColumnIndexOrThrow("codigo_verificacion")));
            }

        } finally {
            if (cursor != null) cursor.close();
        }

        return usuario;
    }

    // ==============================
    // OBTENER ID UBICACION
    // ==============================
    public int obtenerIdUbicacion(String correo){
        Cursor cursor = db.rawQuery(
                "SELECT id_ubicacion FROM usuario WHERE correo=?",
                new String[]{correo}
        );

        if(cursor.moveToFirst()){
            int id = cursor.getInt(0);
            cursor.close();
            return id;
        }

        cursor.close();
        return -1;
    }

    // ==============================
    // OBTENER UBICACIÓN DEL USUARIO
    // ==============================
    public String[] obtenerUbicacionUsuario(int idUbicacion) {
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(
                    "SELECT ciudad, localidad FROM ubicacion WHERE id_ubicacion = ?",
                    new String[]{String.valueOf(idUbicacion)}
            );

            if (cursor.moveToFirst()) {
                String ciudad = cursor.getString(0);
                String zona = cursor.getString(1);

                return new String[]{ciudad, zona};
            }

        } finally {
            if (cursor != null) cursor.close();
        }

        return null;
    }
}
