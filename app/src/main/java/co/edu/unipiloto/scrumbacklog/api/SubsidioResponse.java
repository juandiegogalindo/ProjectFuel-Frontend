package co.edu.unipiloto.scrumbacklog.api;

public class SubsidioResponse {

    private boolean aprobado;

    private String mensaje;

    private double monto;

    private String estado;

    private String fechaVencimiento;

    public SubsidioResponse() {
    }

    public boolean isAprobado() {

        return aprobado;
    }

    public void setAprobado(boolean aprobado) {

        this.aprobado = aprobado;
    }

    public String getMensaje() {

        return mensaje;
    }

    public void setMensaje(String mensaje) {

        this.mensaje = mensaje;
    }

    public double getMonto() {

        return monto;
    }

    public void setMonto(double monto) {

        this.monto = monto;
    }

    public String getEstado() {

        return estado;
    }

    public void setEstado(String estado) {

        this.estado = estado;
    }

    public String getFechaVencimiento() {

        return fechaVencimiento;
    }

    public void setFechaVencimiento(String fechaVencimiento) {

        this.fechaVencimiento = fechaVencimiento;
    }
}