package co.edu.unipiloto.scrumbacklog.api;

public class SubsidioRequest {

    private String codigo;

    public SubsidioRequest(String codigo) {

        this.codigo = codigo;
    }

    public String getCodigo() {

        return codigo;
    }

    public void setCodigo(String codigo) {

        this.codigo = codigo;
    }
}