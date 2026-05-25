package co.edu.unipiloto.scrumbacklog.api;

public class InventarioResponse {

    private int idInventario;
    private String combustible;
    private int idUbicacion;
    private String nombreUbicacion;
    private double cantidad;

    public int getIdInventario() {
        return idInventario;
    }

    public String getCombustible() {
        return combustible;
    }

    public int getIdUbicacion() {
        return idUbicacion;
    }

    public String getNombreUbicacion() {
        return nombreUbicacion;
    }

    public double getCantidad() {
        return cantidad;
    }
}