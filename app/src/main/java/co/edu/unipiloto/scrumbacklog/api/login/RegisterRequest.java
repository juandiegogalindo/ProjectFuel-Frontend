package co.edu.unipiloto.scrumbacklog.api.login;

public class RegisterRequest {

    private String nombre;
    private String usuario;
    private String correo;
    private String password;
    private String direccion;
    private String rol;
    private Integer idUbicacion;
    private String fechaNacimiento;
    private String genero;
    private Double latitud;
    private Double longitud;

    public RegisterRequest(
            String nombre,
            String usuario,
            String correo,
            String password,
            String direccion,
            String rol,
            Integer idUbicacion,
            String fechaNacimiento,
            String genero,
            Integer verificado
    ) {

        this.nombre = nombre;
        this.usuario = usuario;
        this.correo = correo;
        this.password = password;
        this.direccion = direccion;
        this.rol = rol;
        this.idUbicacion = idUbicacion;
        this.fechaNacimiento = fechaNacimiento;
        this.genero = genero;

        // por ahora null
        this.latitud = null;
        this.longitud = null;
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

    public String getPassword() {
        return password;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getRol() {
        return rol;
    }

    public Integer getIdUbicacion() {
        return idUbicacion;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public String getGenero() {
        return genero;
    }

    public Double getLatitud() {
        return latitud;
    }

    public Double getLongitud() {
        return longitud;
    }
}