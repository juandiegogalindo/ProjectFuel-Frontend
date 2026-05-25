package co.edu.unipiloto.scrumbacklog.api;

public class PrecioResponse {

    private Integer idPrecio;

    private String combustible;

    private Integer idUbicacion;

    private String ubicacion;

    private Double precio;

    public Integer getIdPrecio() {
        return idPrecio;
    }

    public String getCombustible() {
        return combustible;
    }

    public Integer getIdUbicacion() {
        return idUbicacion;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public Double getPrecio() {
        return precio;
    }
}