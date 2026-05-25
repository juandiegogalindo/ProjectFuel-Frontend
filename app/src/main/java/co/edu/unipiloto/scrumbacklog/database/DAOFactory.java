package co.edu.unipiloto.scrumbacklog.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import co.edu.unipiloto.scrumbacklog.database.dao.AlertaDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.CombustibleDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.DistribuidorDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.InventarioDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.MovimientoDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.PedidoDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.PrecioDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.UbicacionDAO;
import co.edu.unipiloto.scrumbacklog.database.dao.UsuarioDAO;

public class DAOFactory {

    private SQLiteDatabase db;

    public DAOFactory(Context context) {
        DatabaseManager manager = DatabaseManager.getInstance(context);
        db = manager.openDatabase();
    }

    public SQLiteDatabase getDatabase() {
        return db;
    }

    public CombustibleDAO getCombustibleDAO() {
        return new CombustibleDAO(db);
    }

    public InventarioDAO getInventarioDAO() {
        return new InventarioDAO(db);
    }

    public MovimientoDAO getMovimientoDAO() {
        return new MovimientoDAO(db);
    }

    public PrecioDAO getPrecioDAO() {
        return new PrecioDAO(db);
    }

    public UbicacionDAO getUbicacionDAO() {
        return new UbicacionDAO(db);
    }

    public AlertaDAO getAlertaDAO(){
        return new AlertaDAO(db);
    }

    public PedidoDAO getPedidoDAO(){
        return new PedidoDAO(db);
    }

    public DistribuidorDAO getDistribuidorDAO(){
        return new DistribuidorDAO(db);
    }

    public UsuarioDAO getUsuarioDAO(){
        return new UsuarioDAO(db);
    }
}