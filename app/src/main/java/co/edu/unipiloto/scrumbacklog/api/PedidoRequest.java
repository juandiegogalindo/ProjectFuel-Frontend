package co.edu.unipiloto.scrumbacklog.api;

public class PedidoRequest {

    private Integer idUbicacion;
    private Integer idCombustible;
    private Double cantidad;
    private String fecha;

    public PedidoRequest(
            Integer idUbicacion,
            Integer idCombustible,
            Double cantidad,
            String fecha
    ) {
        this.idUbicacion = idUbicacion;
        this.idCombustible = idCombustible;
        this.cantidad = cantidad;
        this.fecha = fecha;
    }

    public Integer getIdUbicacion() {
        return idUbicacion;
    }

    public Integer getIdCombustible() {
        return idCombustible;
    }

    public Double getCantidad() {
        return cantidad;
    }

    public String getFecha() {
        return fecha;
    }
}