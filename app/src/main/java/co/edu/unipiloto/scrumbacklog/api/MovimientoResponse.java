package co.edu.unipiloto.scrumbacklog.api;

public class MovimientoResponse {

    private Integer idMovimiento;
    private String combustible;
    private String tipoMovimiento;
    private Double galones;
    private Double total;
    private String fecha;

    public Integer getIdMovimiento() {
        return idMovimiento;
    }

    public String getCombustible() {
        return combustible;
    }

    public String getTipoMovimiento() {
        return tipoMovimiento;
    }

    public Double getGalones() {
        return galones;
    }

    public Double getTotal() {
        return total;
    }

    public String getFecha() {
        return fecha;
    }
}