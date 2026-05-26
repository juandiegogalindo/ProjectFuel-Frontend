package co.edu.unipiloto.scrumbacklog.model;

public class DashboardDistribuidor {

    private Long pendientes;

    private Long aceptados;

    private Long entregados;

    private Long cancelados;

    private Double totalGalones;

    public Long getPendientes() {
        return pendientes;
    }

    public void setPendientes(Long pendientes) {
        this.pendientes = pendientes;
    }

    public Long getAceptados() {
        return aceptados;
    }

    public void setAceptados(Long aceptados) {
        this.aceptados = aceptados;
    }

    public Long getEntregados() {
        return entregados;
    }

    public void setEntregados(Long entregados) {
        this.entregados = entregados;
    }

    public Long getCancelados() {
        return cancelados;
    }

    public void setCancelados(Long cancelados) {
        this.cancelados = cancelados;
    }

    public Double getTotalGalones() {
        return totalGalones;
    }

    public void setTotalGalones(Double totalGalones) {
        this.totalGalones = totalGalones;
    }
}