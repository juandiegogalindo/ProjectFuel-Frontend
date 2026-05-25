package co.edu.unipiloto.scrumbacklog.api;

public class UsuarioResponse {

    private Integer id;
    private String nombre;
    private String usuario;
    private String correo;
    private String rol;
    private Integer idUbicacion;

    public Integer getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getCorreo() {
        return correo;
    }

    public String getRol() {
        return rol;
    }

    public Integer getIdUbicacion() {
        return idUbicacion;
    }
}