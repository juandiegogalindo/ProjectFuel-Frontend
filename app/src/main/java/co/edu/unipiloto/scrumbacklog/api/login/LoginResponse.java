package co.edu.unipiloto.scrumbacklog.api.login;

import co.edu.unipiloto.scrumbacklog.model.Usuario;

public class LoginResponse {

    private boolean success;

    private String mensaje;

    private Usuario usuario;

    public boolean isSuccess() {
        return success;
    }

    public String getMensaje() {
        return mensaje;
    }

    public Usuario getUsuario() {
        return usuario;
    }
}